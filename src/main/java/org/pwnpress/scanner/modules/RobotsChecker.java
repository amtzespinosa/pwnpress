package org.pwnpress.scanner.modules;
import org.pwnpress.framework.WPFrameworkSettings;
import org.pwnpress.scanner.WPScanner;
import org.pwnpress.utils.HttpRequest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class RobotsChecker {

    public static void checkRobotsTxt(String urlStr) {
        try {
            String robotsPath = "robots.txt";
            HttpURLConnection connection = HttpRequest.getRequest(urlStr, robotsPath);

            if (HttpRequest.requestResponseCode(connection)) {
                System.out.println("\n[+] robots.txt found: ");
                System.out.println(" └─ " + urlStr + robotsPath);
            }
        } catch (IOException e) {
            if (WPFrameworkSettings.isErrorLogging()) {
                System.err.println("\nError checking robots.txt: " + e.getMessage());
            }
        }
    }
}
