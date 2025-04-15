package org.pwnpress.scanner.modules;
import org.pwnpress.framework.WPFrameworkSettings;
import org.pwnpress.utils.HttpRequest;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;

public class UploadDirectoryListingChecker {

    public static void checkUploadDirectoryListing(String urlStr) {
        try {

            HttpURLConnection connection = HttpRequest.getRequest(urlStr, "wp-content/uploads/");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("\n[+] Upload directory listing may be enabled.");
                System.out.println(" └─ " + urlStr + "wp-content/uploads/");
            }
        } catch (IOException e) {
            if (WPFrameworkSettings.isErrorLogging()) {
                System.out.println("\nError checking upload directory listing: " + e.getMessage());
            }
        }
    }
}
