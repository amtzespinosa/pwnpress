package org.pwnpress.scanner.modules;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MediaEnumerationChecker {

    public static boolean checkMediaEnumeration(String url) {
        try {
            URL mediaURL = new URL(url + "wp-json/wp/v2/media");
            HttpURLConnection connection = (HttpURLConnection) mediaURL.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("\n[+] Media file enumeration possible.");
                System.out.print(" |- Do you want to enumerate media files? (y/n): ");

                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                String input = reader.readLine().trim().toLowerCase();

                if (input.equals("y")) {
                    System.out.println(" |- Enumerating media files...");
                    List<String> mediaFiles = getMediaFiles(connection);
                    if (mediaFiles.isEmpty()) {
                        System.out.println("    No media files found.");
                    } else {
                        System.out.println("    Media files found:");
                        for (String mediaFile : mediaFiles) {
                            System.out.println(mediaFile);
                        }
                    }
                } else {
                    System.out.println(" |- Media files enumeration skipped.");
                }
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    private static List<String> getMediaFiles(HttpURLConnection connection) throws IOException {
        List<String> mediaFiles = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            String jsonResponse = response.toString();
            int index = jsonResponse.indexOf("\"guid\":{\"rendered\":\"");
            while (index != -1) {
                int startUrl = index + "\"guid\":{\"rendered\":\"".length();
                int endUrl = jsonResponse.indexOf("\"", startUrl);
                String mediaUrl = "  - " + jsonResponse.substring(startUrl, endUrl);
                mediaFiles.add(mediaUrl);
                index = jsonResponse.indexOf("\"guid\":{\"rendered\":\"", endUrl);
            }
        }
        return mediaFiles;
    }
}
