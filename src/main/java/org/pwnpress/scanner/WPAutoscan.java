package org.pwnpress.scanner;

import org.json.JSONObject;
import org.pwnpress.framework.WPFrameworkSettings;
import org.pwnpress.scanner.modules.*;
import org.pwnpress.target.WPValidator;
import org.pwnpress.utils.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WPAutoscan {

    // private static int cachedRequests = 0;
    // private static long dataSent = 0;
    // private static long dataReceived = 0;
    // private static double memoryUsed = 0.0;
    private static LocalDateTime startDateTime;
    private static LocalDateTime finishDateTime;

    public static void scan(String urlStr) {
    	
    	urlStr = NormalizeURL.normalize(urlStr);

        if (WPValidator.singleValidate(urlStr)) {

            // Get and store Wordfence API
            JSONObject wfVulnFeed = WordfenceCacheManager.getWordfenceData();

            System.out.println("\n[+] " + CustomFormat.padRight("Scanning:", 20) + urlStr);

            // Print the start time
            startDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            System.out.println("[+] " + CustomFormat.padRight("Time started:", 20) + startDateTime.format(formatter));

            // ----------------------------------------
            // NETWORK
            // ----------------------------------------

            // Extract headers
            HeaderExtractor.extractHeaders(urlStr);

            // ----------------------------------------
            // INTERESTING FILES
            // ----------------------------------------

            // Check robots.txt
            RobotsChecker.checkRobotsTxt(urlStr);

            // Check sitemap.xml
            SitemapChecker.checkSitemap(urlStr);

            // Check readme.html
            ReadmeChecker.checkReadme(urlStr);

            // ----------------------------------------
            // SERVICES
            // ----------------------------------------

            // Check XML-RPC endpoint
            XmlRpcChecker.checkXmlRpc(urlStr);

            // Check for upload directory
            UploadDirectoryListingChecker.checkUploadDirectoryListing(urlStr);

            // Check wp-cron.php
            WpCronChecker.checkWpCron(urlStr);

            // ----------------------------------------
            // USERS
            // ----------------------------------------

            // Check if user registration is enabled
            UserRegistrationChecker.checkUserRegistration(urlStr);

            // User enumeration
            UserEnumerationChecker.checkUserEnumeration(urlStr);

            // Check for full path disclosure
            //FullPathDisclosureChecker.checkFullPathDisclosure(urlStr);

            // Check for publicly accessible wp-config.php files
            //WpConfigChecker.checkWpConfigFiles(urlStr);

            // Check for publicly accessible database dumps
            //DatabaseDumpChecker.checkDatabaseDumps(urlStr);

            // Check for exposed error logs by plugins
            //ErrorLogChecker.checkErrorLogs(urlStr);

            // ----------------------------------------
            // VULN
            // ----------------------------------------

            // Check for PHP
            PhpVersionChecker.getPhpVersion();

            // Extract WordPress version
            WPVersion.extractWordPressVersion(urlStr);

            // Check core version vulnerabilities
            WPCoreVulnerabilityChecker.checkVersionVulnerabilities(wfVulnFeed);

            // Extract site themes
            WPThemeScanner.scanThemes(urlStr);

            // Check if themes bruteforce is enabled and proceed or not
            if (WPFrameworkSettings.isThemesBruteforce()) {WPThemesBruteforceEnum.bruteforceThemes(urlStr);}

            // Check theme vulnerabilities
            WPThemeVulnerabilityChecker.checkThemeVulnerabilities(wfVulnFeed,urlStr);

            // Check 'Must Use Plugins' directory
            MustUsePluginsChecker.checkMustUsePlugins(urlStr);

            // Extract site plugins
            WPPluginScanner.scanPlugins(urlStr);

            // Check if themes bruteforce is enabled and proceed or not
            if (WPFrameworkSettings.isPluginsBrutefoce()) {WPPluginsBruteforceEnum.bruteforcePlugins(urlStr);}

            // Check plugins vulnerabilities
            WPPluginsVulnerabilityChecker.checkPluginVulnerabilities(wfVulnFeed, urlStr);

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
        System.out.println(" ├─ " + CustomFormat.padRight("Requests Done:", 20) + RequestsCounter.getRequestsCount());
        // System.out.println(" |- " + CustomFormat.padRight("Cached Requests:", 20) + cachedRequests);
        // System.out.println(" |- " + CustomFormat.padRight("Data Sent:", 20) + formatDataSize(dataSent));
        // System.out.println(" |- " + CustomFormat.padRight("Data Received:", 20) + formatDataSize(dataReceived));
        // System.out.println(" |- " + CustomFormat.padRight("Memory used:", 20) + String.format("%.3f", memoryUsed) + " MB");
        System.out.println(" └─ " + CustomFormat.padRight("Elapsed time:", 20) + formatDuration(duration.toMillis()));
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

}
