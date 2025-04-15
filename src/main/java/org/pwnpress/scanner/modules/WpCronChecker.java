package org.pwnpress.scanner.modules;
import org.pwnpress.framework.WPFrameworkSettings;
import org.pwnpress.utils.HttpRequest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class WpCronChecker {

    public static void checkWpCron(String urlStr) {
        try {
            HttpURLConnection connection = HttpRequest.getRequest(urlStr + "wp-cron.php");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("\n[+] The external WP-Cron seems to be enabled: ");
                System.out.println(" ├─ Check: " + urlStr + "wp-cron.php");
                System.out.println(" └─ References:");
                System.out.println("    - https://www.iplocation.net/defend-wordpress-from-ddos");
                System.out.println("    - https://github.com/wpscanteam/wpscan/issues/1299");
            }
        } catch (IOException e) {
            if (WPFrameworkSettings.isErrorLogging()) {
                System.err.println("Error checking wp-cron.php: " + e.getMessage());
            }
        }
    }
}
