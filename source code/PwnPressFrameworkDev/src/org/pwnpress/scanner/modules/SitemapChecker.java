package org.pwnpress.scanner.modules;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class SitemapChecker {

    public static void checkSitemap(String urlStr) {
        try {
            URL url = new URL(urlStr + "sitemap.xml");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("\n[+] sitemap.xml found at: " + url);
                System.out.println(" |- Found By: Sitemap XML (Aggressive Detection)");
                System.out.println(" |- Confidence: 100%");
            }
        } catch (IOException e) {
            System.err.println("Error checking sitemap.xml: " + e.getMessage());
        }
    }
}
