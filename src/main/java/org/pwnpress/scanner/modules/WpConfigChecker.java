package org.pwnpress.scanner.modules;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;

public class WpConfigChecker {

    public static boolean checkWpConfigFiles(String url) {
        try {
            URL wpConfigURL = new URL(url + "wp-config.php~");
            HttpURLConnection connection = (HttpURLConnection) wpConfigURL.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Backed up and publicly accessible wp-config.php file found.");
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }
}
