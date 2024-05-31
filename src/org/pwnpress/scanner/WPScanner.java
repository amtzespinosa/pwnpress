package org.pwnpress.scanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.pwnpress.scanner.modules.DatabaseDumpChecker;
import org.pwnpress.scanner.modules.ErrorLogChecker;
import org.pwnpress.scanner.modules.FullPathDisclosureChecker;
import org.pwnpress.scanner.modules.HeaderExtractor;
import org.pwnpress.scanner.modules.MustUsePluginsChecker;
import org.pwnpress.scanner.modules.PhpVersionChecker;
import org.pwnpress.scanner.modules.ReadmeChecker;
import org.pwnpress.scanner.modules.RobotsChecker;
import org.pwnpress.scanner.modules.SitemapChecker;
import org.pwnpress.scanner.modules.UploadDirectoryListingChecker;
import org.pwnpress.scanner.modules.UserEnumerationChecker;
import org.pwnpress.scanner.modules.UserRegistrationChecker;
import org.pwnpress.scanner.modules.WPVersion;
import org.pwnpress.scanner.modules.WordfenceVulnerabilityChecker;
import org.pwnpress.scanner.modules.WpConfigChecker;
import org.pwnpress.scanner.modules.WpCronChecker;
import org.pwnpress.scanner.modules.XmlRpcChecker;
import org.pwnpress.utils.CustomFormat;

public class WPScanner {

    public static void scan(String urlStr) {

        if (isWordPressSite(urlStr)) {
			System.out.println("\n[+] " + CustomFormat.padRight("Scanning:", 20) + urlStr);
		    
		    // Print the start time
		    LocalDateTime startDateTime = LocalDateTime.now();
		    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		    System.out.println("[+] " + CustomFormat.padRight("Time started:", 20) + startDateTime.format(formatter));

		    // Extract headers
		    HeaderExtractor.extractHeaders(urlStr);

		    // Check robots.txt
		    RobotsChecker.checkRobotsTxt(urlStr);

		    // Check sitemap.xml
		    SitemapChecker.checkSitemap(urlStr);

		    // Check readme.html
		    ReadmeChecker.checkReadme(urlStr);
		    
		    // Check XML-RPC endpoint
		    XmlRpcChecker.checkXmlRpc(urlStr);

		    // Check 'Must Use Plugins' directory
		    MustUsePluginsChecker.checkMustUsePlugins(urlStr);

		    // Check wp-cron.php
		    WpCronChecker.checkWpCron(urlStr);
		    
		    // Check if user registration is enabled
		    UserRegistrationChecker.checkUserRegistration(urlStr);
		    
		    // Check for full path disclosure
		    FullPathDisclosureChecker.checkFullPathDisclosure(urlStr);
		    
		    // Check for upload directory
		    UploadDirectoryListingChecker.checkUploadDirectoryListing(urlStr);
		    
		    // Check for publicly accessible wp-config.php files
		    WpConfigChecker.checkWpConfigFiles(urlStr);
		    
		    // Check for PHP
		    PhpVersionChecker.checkPhpVersion(urlStr);

		    // Check for publicly accessible database dumps
		    DatabaseDumpChecker.checkDatabaseDumps(urlStr);

		    // Check for exposed error logs by plugins
		    ErrorLogChecker.checkErrorLogs(urlStr);
		    
		    // User enumeration
		    UserEnumerationChecker.checkUserEnumeration(urlStr);
		    
		    // Extract WordPress version
		    WPVersion.extractWordPressVersion(urlStr);
		    
		    // Check version vulnerabilities
		    WordfenceVulnerabilityChecker.checkVersionVulnerabilities(urlStr);
		    
		    // TODO
		    // Themes enumeration & vulnerabilities
		    // Plugins enumeration & vulnerabilities
		    // Print footer with info

		    // Check for media file enumeration
		    // MediaEnumerationChecker.checkMediaEnumeration(urlStr);
		    
		} else {
		    System.out.println("Not a WordPress site.");
		}
    }

    public static boolean isWordPressSite(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder htmlContent = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                htmlContent.append(line);
            }
            reader.close();

            String html = htmlContent.toString();
            return html.contains("wp-content") || html.contains("wp-json");
        } catch (IOException e) {
            System.err.println("Error fetching URL content: " + e.getMessage());
            return false;
        }
    }
    
}
