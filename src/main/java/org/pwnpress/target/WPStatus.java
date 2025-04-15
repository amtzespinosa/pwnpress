package org.pwnpress.target;

import org.json.JSONArray;
import org.json.JSONObject;
import org.pwnpress.framework.WPFrameworkSettings;
import org.pwnpress.utils.HttpRequest;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.Scanner;

public class WPStatus {

    private static final String DEFAULT_OUTPUT_FILE = "wp_status.json";
    private static final int THREAD_POOL_SIZE = WPFrameworkSettings.getNumThreads();

    public static void statusCheck(String file, String flags) {
        List<String> domains = readDomainsFromFile(file);

        if (domains.isEmpty()) {
            System.out.println("No domains found in file.");
            return;
        }

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        JSONArray results = new JSONArray();

        for (String domain : domains) {
            executor.execute(() -> {
                JSONObject domainResult = extractWordPressVersion(domain, flags);
                if (domainResult != null) {
                    synchronized (results) {
                        results.put(domainResult);
                    }
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            System.err.println("Error waiting for threads to finish: " + e.getMessage());
        }

        // Prompt the user for the output file name
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the output file name (or press Enter for default '" + DEFAULT_OUTPUT_FILE + "'): ");
        String outputFile = scanner.nextLine().trim();
        if (outputFile.isEmpty()) {
            outputFile = DEFAULT_OUTPUT_FILE;
        }
        outputFile = outputFile + ".json";
        saveResultsToFile(results, outputFile);
        System.out.println("Results saved to: " + outputFile);
    }

    private static List<String> readDomainsFromFile(String filePath) {
        List<String> domains = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String domain;
            while ((domain = reader.readLine()) != null) {
                domain = domain.trim();
                if (!domain.isEmpty()) {
                    domains.add(domain);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return domains;
    }

    public static JSONObject extractWordPressVersion(String urlStr, String flags) {
        try {
            if (!urlStr.startsWith("http")) {
                urlStr = "https://" + urlStr;
            }

            HttpURLConnection connection = HttpRequest.getRequest(urlStr);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder htmlContent = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                htmlContent.append(line);
            }
            reader.close();

            String html = htmlContent.toString();
            String version = extractVersionFromSource(html);

            if (version == null) {
                connection = HttpRequest.getRequest(urlStr + "/feed/");
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                htmlContent = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    htmlContent.append(line);
                }
                reader.close();

                html = htmlContent.toString();
                version = extractVersionFromRSS(html);
            }

            if (version != null) {
                String status = checkVersionStatus(version, flags);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("website", urlStr);
                jsonObject.put("version", version);
                jsonObject.put("status", status);
                return jsonObject;
            }

        } catch (IOException e) {
            System.err.println("Error fetching " + urlStr + ": " + e.getMessage());
        }

        return null;
    }

    public static String extractVersionFromSource(String html) {
        Pattern pattern = Pattern.compile("<meta name=\"generator\" content=\"WordPress (\\d+\\.\\d+\\.\\d+)");
        Matcher matcher = pattern.matcher(html);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public static String extractVersionFromRSS(String html) {
        Pattern pattern = Pattern.compile("<generator>https?://wordpress.org/?\\?v=(\\d+\\.\\d+\\.\\d+)");
        Matcher matcher = pattern.matcher(html);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private static String checkVersionStatus(String version, String flags) {
        String apiUrl = "https://api.wordpress.org/core/stable-check/1.0/";

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject versionStatus = new JSONObject(response.toString());
                return versionStatus.optString(version, "Unknown");
            }

        } catch (IOException e) {
            System.err.println("Error checking version status: " + e.getMessage());
        }

        return "Unknown";
    }

    private static void saveResultsToFile(JSONArray results, String outputFile) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write(results.toString(4)); // Pretty print JSON
        } catch (IOException e) {
            System.err.println("Error saving results: " + e.getMessage());
        }
    }
}
