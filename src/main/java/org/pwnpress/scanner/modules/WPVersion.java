package org.pwnpress.scanner.modules;
import org.json.JSONObject;
import org.pwnpress.scanner.WPScanner;
import org.pwnpress.utils.HttpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WPVersion {

    public static String VERSION;

    public static void extractWordPressVersion(String urlStr) {
        try {
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
                connection = HttpRequest.getRequest(urlStr, "/feed/");

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                htmlContent = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    htmlContent.append(line);
                }
                reader.close();

                html = htmlContent.toString();
                version = extractVersionFromRSS(html);
            }

            VERSION = version;

        } catch (IOException e) {
            System.err.println("Error fetching URL content: " + e.getMessage());
        }
    }

    public static String extractVersionFromSource(String html) {
        Pattern pattern = Pattern.compile("<meta name=\"generator\" content=\"WordPress (\\d+\\.\\d+\\.\\d+)");
        Matcher matcher = pattern.matcher(html);

        if (matcher.find()) {
            String version = matcher.group(1);

            if (version != null) {
                String status = checkVersionStatus(version);
                System.out.println("\n[+] WordPress version " + version + " identified.");
                System.out.println(" ├─ Version status: " + status);
                System.out.println(" ├─ Found in page source meta tag");
                System.out.println(" └─ Match: " + getVersionMatch(html, version));
            }

            return version;
        }

        return null;
    }

    public static String extractVersionFromRSS(String html) {
        Pattern pattern = Pattern.compile("<generator>https?://wordpress.org/?\\?v=(\\d+\\.\\d+\\.\\d+)");
        Matcher matcher = pattern.matcher(html);

        if (matcher.find()) {
            String version = matcher.group(1);

            if (version != null) {
                String status = checkVersionStatus(version);
                System.out.println("\n[+] WordPress version " + version + " identified.");
                System.out.println(" ├─ Version status: " + status);
                System.out.println(" ├─ Found in rss tag:");
                System.out.println(" └─ Match: " + getVersionMatch(html, version));
            }

            return version;

        }

        return null;
    }

    private static String getVersionMatch(String html, String version) {
        Pattern patternMetaTags = Pattern.compile("<meta name=\"generator\" content=\"WordPress " + version + "[^\"]*\"");
        Matcher matcherMetaTags = patternMetaTags.matcher(html);

        if (matcherMetaTags.find()) {
            return matcherMetaTags.group();
        }

        Pattern patternRSS = Pattern.compile("<generator>https?://wordpress.org/\\?v=" + version);
        Matcher matcherRSS = patternRSS.matcher(html);

        if (matcherRSS.find()) {
            return matcherRSS.group();
        }

        return null;
    }

    private static String checkVersionStatus(String version) {
        String apiUrl = "https://api.wordpress.org/core/stable-check/1.0/";

        try {
            // Open a connection to the API
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    JSONObject versionStatus = new JSONObject(response.toString());
                    // Check the status of the given version
                    return versionStatus.optString(version);
                }
            } else {
                throw new IOException("Failed to fetch Wordfence data. HTTP code: " + connection.getResponseCode());
            }

        } catch (IOException e) {
            System.err.println("Error checking version status: " + e.getMessage());
        }

        return null;
    }

}
