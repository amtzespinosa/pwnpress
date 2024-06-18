package org.pwnpress.scanner.modules;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class RobotsChecker {

    public static void checkRobotsTxt(String urlStr) {
        try {
            URL url = new URL(urlStr + "robots.txt");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("\n[+] robots.txt found at: " + url);
                System.out.println(" |- Found By: Robots Txt (Aggressive Detection)");
                System.out.println(" |- Confidence: 100%");
            }
        } catch (IOException e) {
            System.err.println("Error checking robots.txt: " + e.getMessage());
        }
    }
}
