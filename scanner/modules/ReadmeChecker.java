package org.pwnpress.scanner.modules;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReadmeChecker {

    public static void checkReadme(String urlStr) {
        try {
            URL url = new URL(urlStr + "readme.html");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("\n[+] readme.html found at: " + url);
                System.out.println(" |- Found By: Direct Access (Aggressive Detection)");
                System.out.println(" |- Confidence: 100%");
            }
        } catch (IOException e) {
            System.err.println("Error checking readme.html: " + e.getMessage());
        }
    }
}
