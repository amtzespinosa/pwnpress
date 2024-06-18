package org.pwnpress.scanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import org.pwnpress.framework.WPFrameworkSettings;
import org.pwnpress.proxy.ProxyManager;
import org.pwnpress.utils.CustomFormat;
import org.pwnpress.utils.NormalizeURL;

import org.pwnpress.scanner.modules.*;

public class WPScanner {

    // private static int requestsDone = 0;
    // private static int cachedRequests = 0;
    // private static long dataSent = 0;
    // private static long dataReceived = 0;
    // private static double memoryUsed = 0.0;
    private static LocalDateTime startDateTime;
    private static LocalDateTime finishDateTime;

    public static void scan(String urlStr) {
    	
    	urlStr = NormalizeURL.normalize(urlStr);
    	
    	// Change proxy if enabled
        if (WPFrameworkSettings.isProxy()==true) ProxyManager.changeProxy(); 

        if (isWordPressSite(urlStr)) {
            System.out.println("\n[+] " + CustomFormat.padRight("Scanning:", 20) + urlStr);

            // Print the start time
            startDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            System.out.println("[+] " + CustomFormat.padRight("Time started:", 20) + startDateTime.format(formatter));

            // Change proxy if enabled
            if (WPFrameworkSettings.isProxy()==true) ProxyManager.changeProxy(); 
            
            // Extract headers
            HeaderExtractor.extractHeaders(urlStr);

            // Check robots.txt
            RobotsChecker.checkRobotsTxt(urlStr);
         
            // Change proxy if enabled
            if (WPFrameworkSettings.isProxy()==true) ProxyManager.changeProxy(); 

            // Check sitemap.xml
            SitemapChecker.checkSitemap(urlStr);

            // Check readme.html
            ReadmeChecker.checkReadme(urlStr);
            
            // Change proxy if enabled
            if (WPFrameworkSettings.isProxy()==true) ProxyManager.changeProxy();

            // Check XML-RPC endpoint
            XmlRpcChecker.checkXmlRpc(urlStr);

            // Check 'Must Use Plugins' directory
            MustUsePluginsChecker.checkMustUsePlugins(urlStr);
            
            // Change proxy if enabled
            if (WPFrameworkSettings.isProxy()==true) ProxyManager.changeProxy();

            // Check wp-cron.php
            WpCronChecker.checkWpCron(urlStr);

            // Check if user registration is enabled
            UserRegistrationChecker.checkUserRegistration(urlStr);
            
            // Change proxy if enabled
            if (WPFrameworkSettings.isProxy()==true) ProxyManager.changeProxy();

            // Check for full path disclosure
            FullPathDisclosureChecker.checkFullPathDisclosure(urlStr);

            // Check for upload directory
            UploadDirectoryListingChecker.checkUploadDirectoryListing(urlStr);
            
            // Change proxy if enabled
            if (WPFrameworkSettings.isProxy()==true) ProxyManager.changeProxy(); 

            // Check for publicly accessible wp-config.php files
            WpConfigChecker.checkWpConfigFiles(urlStr);

            // Check for PHP
            PhpVersionChecker.checkPhpVersion(urlStr);
            
            // Change proxy if enabled
            if (WPFrameworkSettings.isProxy()==true) ProxyManager.changeProxy(); 

            // Check for publicly accessible database dumps
            DatabaseDumpChecker.checkDatabaseDumps(urlStr);

            // Check for exposed error logs by plugins
            ErrorLogChecker.checkErrorLogs(urlStr);
            
            // Change proxy if enabled
            if (WPFrameworkSettings.isProxy()==true) ProxyManager.changeProxy(); 

            // User enumeration
            UserEnumerationChecker.checkUserEnumeration(urlStr);

            // Extract WordPress version
            WPVersion.extractWordPressVersion(urlStr);
            
            // Change proxy if enabled
            if (WPFrameworkSettings.isProxy()==true) ProxyManager.changeProxy(); 

            // Check core version vulnerabilities
            CoreVulnerabilityChecker.checkVersionVulnerabilities(urlStr);
            
            // Change proxy if enabled
            if (WPFrameworkSettings.isProxy()==true) ProxyManager.changeProxy(); 

            // Extract site themes
            WPThemeScanner.scanThemes(urlStr);
            
            // Change proxy if enabled
            if (WPFrameworkSettings.isProxy()==true) ProxyManager.changeProxy(); 

            // Check theme vulnerabilities
            WPThemeVulnerabilityChecker.checkThemeVulnerabilities(urlStr);
            
            // Change proxy if enabled
            if (WPFrameworkSettings.isProxy()==true) ProxyManager.changeProxy(); 

            // Extract site plugins
            WPPluginScanner.scanPlugins(urlStr);
            
            // Change proxy if enabled
            if (WPFrameworkSettings.isProxy()==true) ProxyManager.changeProxy(); 
            
            // Check plugins vulnerabilities
            WPPluginsVulnerabilityChecker.checkPluginVulnerabilities(urlStr);

            // Check for media file enumeration
            // MediaEnumerationChecker.checkMediaEnumeration(urlStr);
            
            // Print footer with info
            // TODO: Implement statistics measurement in modules
            printScanSummary();

        } else {
            System.out.println("Not a WordPress site.");
        }
    }

    private static void printScanSummary() {
        // Calculate elapsed time
        finishDateTime = LocalDateTime.now();
        Duration duration = Duration.between(startDateTime, finishDateTime);

        // Print summary
        System.out.println("\n[+] " + CustomFormat.padRight("Finished:", 20) + finishDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        // System.out.println(" |- " + CustomFormat.padRight("Requests Done:", 20) + requestsDone);
        // System.out.println(" |- " + CustomFormat.padRight("Cached Requests:", 20) + cachedRequests);
        // System.out.println(" |- " + CustomFormat.padRight("Data Sent:", 20) + formatDataSize(dataSent));
        // System.out.println(" |- " + CustomFormat.padRight("Data Received:", 20) + formatDataSize(dataReceived));
        // System.out.println(" |- " + CustomFormat.padRight("Memory used:", 20) + String.format("%.3f", memoryUsed) + " MB");
        System.out.println(" |- " + CustomFormat.padRight("Elapsed time:", 20) + formatDuration(duration.toMillis()));
    }

    /*
    private static String formatDataSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.3f", bytes / 1024.0) + " KB";
        } else {
            return String.format("%.3f", bytes / (1024.0 * 1024.0)) + " MB";
        }
    }
    */

    private static String formatDuration(long millis) {
        long seconds = millis / 1000;
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    // Proxy stuff not working
    public static boolean isWordPressSite(String urlStr) {
        boolean isWordPress = false;

        // Attempt up to 3 times using different proxies
        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                URL url = new URL(urlStr);

                // Get current proxy
                Proxy proxy = ProxyManager.getCurrentProxy();
                HttpURLConnection connection;

                if (WPFrameworkSettings.isProxy()==true) {
                    connection = (HttpURLConnection) url.openConnection(proxy);
                } else {
                    connection = (HttpURLConnection) url.openConnection();
                }

                connection.setRequestMethod("GET");

                // Set connection timeout and read timeout
                connection.setConnectTimeout(10000); // 10 seconds timeout
                connection.setReadTimeout(10000); // 10 seconds timeout

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder htmlContent = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        htmlContent.append(line);
                    }
                    reader.close();

                    String html = htmlContent.toString();
                    isWordPress = html.contains("wp-content") || html.contains("wp-json");
                    break; // Exit loop if successful
                } else {
                    System.err.println("Failed to fetch URL content. Response code: " + responseCode);
                }
            } catch (IOException e) {
                System.err.println("Error fetching URL content: " + e.getMessage());
            }

            // Change proxy for the next attempt
            ProxyManager.changeProxy();
        }

        return isWordPress;
    }

}
