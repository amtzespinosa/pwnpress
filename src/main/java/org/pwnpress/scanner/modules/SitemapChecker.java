package org.pwnpress.scanner.modules;
import org.pwnpress.framework.WPFrameworkSettings;
import org.pwnpress.scanner.WPScanner;
import org.pwnpress.utils.HttpRequest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class SitemapChecker {

    public static void checkSitemap(String urlStr) {
        try {
            String sitemapPath = "sitemap.xml";
            HttpURLConnection connection = HttpRequest.getRequest(urlStr, sitemapPath);

            if (HttpRequest.requestResponseCode(connection)) {
                System.out.println("\n[+] Sitemap found: ");
                System.out.println(" └─ " + urlStr + sitemapPath);
            }
        } catch (IOException e) {
            if (WPFrameworkSettings.isErrorLogging()) {
                System.err.println("\nError checking sitemap.xml: " + e.getMessage());
            }
        }
    }
}
