package org.pwnpress.scanner.modules;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MustUsePluginsChecker {

    public static void checkMustUsePlugins(String urlStr) {
        try {
            URL url = new URL(urlStr + "wp-content/mu-plugins/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("\n[+] 'Must Use Plugins' directory found at: " + url);
            } 
        } catch (IOException e) {
            System.err.println("Error checking 'Must Use Plugins' directory: " + e.getMessage());
        }
    }
}
