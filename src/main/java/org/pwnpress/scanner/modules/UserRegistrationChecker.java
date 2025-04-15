package org.pwnpress.scanner.modules;
import org.pwnpress.framework.WPFrameworkSettings;
import org.pwnpress.utils.HttpRequest;

import java.net.HttpURLConnection;
import java.io.IOException;

public class UserRegistrationChecker {

    public static void checkUserRegistration(String urlStr) {
        try {
            HttpURLConnection connection = HttpRequest.getRequest(urlStr, "wp-login.php?action=register");
            if (HttpRequest.requestResponseCode(connection)) {
                System.out.println("\n[+] User registration might be enabled.");
                System.out.println(" └─ " + urlStr + "wp-login.php?action=register");
            }
        } catch (IOException e) {
            if (WPFrameworkSettings.isErrorLogging()) {
                System.out.println("Error checking user registration: " + e.getMessage());
            }
        }
    }
}
