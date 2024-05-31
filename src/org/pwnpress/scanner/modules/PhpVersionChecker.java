package org.pwnpress.scanner.modules;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PhpVersionChecker {

    public static void checkPhpVersion(String url) {
        String path = "wp-includes/version.php";

        try {
            URL versionURL = new URL(url + path);
            HttpURLConnection connection = (HttpURLConnection) versionURL.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("$wp_version =")) {
                        System.out.println("[+] Version file found at: " + url + path);
                        System.out.println(" |- Confidence: 100");
                        break;
                    }
                }
                reader.close();
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
