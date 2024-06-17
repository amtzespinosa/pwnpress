package org.pwnpress.scanner.modules;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;

public class UploadDirectoryListingChecker {

    public static boolean checkUploadDirectoryListing(String url) {
        try {
            URL uploadDirectoryURL = new URL(url + "wp-content/uploads/");
            HttpURLConnection connection = (HttpURLConnection) uploadDirectoryURL.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("\n[+]Upload directory listing may be enabled.");
                System.out.println(" |- " + uploadDirectoryURL);
            } 
            return true;
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }
}
