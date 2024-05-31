package org.pwnpress.scanner.modules;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;

public class UserRegistrationChecker {

    public static boolean checkUserRegistration(String url) {
        try {
            URL registrationURL = new URL(url + "wp-login.php?action=register");
            HttpURLConnection connection = (HttpURLConnection) registrationURL.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("\n[+] User registration is enabled.");
                System.out.println(" |- " + registrationURL);
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }
}
