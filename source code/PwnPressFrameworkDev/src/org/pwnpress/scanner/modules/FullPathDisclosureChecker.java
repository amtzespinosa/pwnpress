package org.pwnpress.scanner.modules;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;

public class FullPathDisclosureChecker {

    public static boolean checkFullPathDisclosure(String url) {
        try {
            URL fullPathURL = new URL(url + "wp-includes/rss-functions.php");
            HttpURLConnection connection = (HttpURLConnection) fullPathURL.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("\n[+] Full path disclosure may be present.");
                System.out.println(" |- " + fullPathURL);
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }
}
