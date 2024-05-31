package org.pwnpress.scanner.modules;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;

public class ErrorLogChecker {

    public static boolean checkErrorLogs(String url) {
        try {
            URL errorLogURL = new URL(url + "wp-content/debug.log");
            HttpURLConnection connection = (HttpURLConnection) errorLogURL.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Error logs exposed by plugins found.");
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }
}
