package org.pwnpress.scanner.modules;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class WpCronChecker {

    public static void checkWpCron(String urlStr) {
        try {
            URL url = new URL(urlStr + "wp-cron.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("\n[+] The external WP-Cron seems to be enabled: " + url);
                System.out.println(" |- Found By: Direct Access (Aggressive Detection)");
                System.out.println(" |- Confidence: 60%");
                System.out.println(" |- References:");
                System.out.println("    - https://www.iplocation.net/defend-wordpress-from-ddos");
                System.out.println("    - https://github.com/wpscanteam/wpscan/issues/1299");
            }
        } catch (IOException e) {
            System.err.println("Error checking wp-cron.php: " + e.getMessage());
        }
    }
}
