package org.pwnpress.target;

import org.pwnpress.framework.WPFrameworkSettings;
import org.pwnpress.utils.HttpRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.util.*;
import java.util.concurrent.*;

public class WPValidator {

    private static final String[] WP_INDICATORS = {
            "wp-content", "wp-includes", "xmlrpc.php", "wp-json", "WordPress"
    };

    private static final int THREAD_POOL_SIZE = WPFrameworkSettings.getNumThreads();

    /**
     * Validates a single domain or a bulk file.
     * @param input The domain or file to validate.
     */
    public static void validate(String input) {
        if (isFile(input)) {
            bulkValidate(input);
        } else {
            singleValidate(input);
        }
    }

    /**
     * Determines if the input is a file.
     * @param input The input string.
     * @return True if input is a valid file, false otherwise.
     */
    private static boolean isFile(String input) {
        File file = new File(input);
        return input.toLowerCase().endsWith(".txt") || file.exists();
    }

    /**
     * Performs WordPress detection on a single domain.
     * @param domain The domain to check.
     */
    public static boolean singleValidate(String domain) {
        try {
            if (!domain.startsWith("https")) {
                domain = "https://" + domain; // Default to HTTPS
            }

            HttpURLConnection connection = HttpRequest.getRequest(domain);
            int responseCode = connection.getResponseCode();
            String response = HttpRequest.readResponse(connection);

            if (responseCode == 200 && containsWordPressIndicators(response)) {
                System.out.println("[+] WordPress detected: " + domain);
                return true;
            } else {
                System.out.println("[-] Not a WordPress site: " + domain);
            }

        } catch (IOException e) {
            System.out.println("[-] Error validating " + domain + ": " + e.getMessage());
        }
        return false;
    }

    /**
     * Validates multiple domains from a file using multithreading.
     * @param filePath The path to the file containing domains.
     */
    private static void bulkValidate(String filePath) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter output file name: ");
        String outputFile = scanner.nextLine().trim() + ".txt";
        scanner.close();

        List<String> domains = readDomainsFromFile(filePath);
        if (domains.isEmpty()) {
            System.out.println("[-] No valid domains found in the file.");
            return;
        }

        List<String> wpSites = Collections.synchronizedList(new ArrayList<>());
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        for (String domain : domains) {
            executor.submit(() -> {
                if (singleValidate(domain)) {
                    wpSites.add(domain);
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            System.err.println("[-] Error: Bulk validation interrupted.");
        }

        if (!wpSites.isEmpty()) {
            saveWordPressSites(wpSites, outputFile);
        }
    }

    /**
     * Checks if a domain is running WordPress.
     * @param domain The domain to check.
     * @return True if WordPress is detected, false otherwise.
     */
    private static boolean isWordPress(String domain) {
        try {
            if (!domain.startsWith("http")) {
                domain = "https://" + domain; // Default to HTTPS
            }

            HttpURLConnection connection = HttpRequest.getRequest(domain);
            String response = HttpRequest.readResponse(connection);

            return containsWordPressIndicators(response);

        } catch (IOException e) {
            System.err.println("[-] Error checking " + domain + ": " + e.getMessage());
        }
        return false;
    }

    /**
     * Reads domains from the file.
     * @param filePath The file containing domain names.
     * @return A list of domains.
     */
    private static List<String> readDomainsFromFile(String filePath) {
        List<String> domains = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String domain;
            while ((domain = reader.readLine()) != null) {
                domain = domain.trim();
                if (!domain.isEmpty()) {
                    domains.add(domain);
                }
            }
        } catch (IOException e) {
            System.err.println("[-] Error reading file: " + e.getMessage());
        }
        return domains;
    }

    /**
     * Saves the detected WordPress domains to an output file.
     * @param wpSites The list of WordPress domains.
     * @param outputFile The name of the output file.
     */
    private static void saveWordPressSites(List<String> wpSites, String outputFile) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (String site : wpSites) {
                writer.write(site);
                writer.newLine();
            }
            System.out.println("[*] WordPress sites saved to: " + outputFile);
        } catch (IOException e) {
            System.err.println("[-] Error saving WordPress sites: " + e.getMessage());
        }
    }

    /**
     * Checks if the response contains WordPress indicators.
     * @param response The HTTP response body.
     * @return True if WordPress-related strings are found.
     */
    private static boolean containsWordPressIndicators(String response) {
        for (String indicator : WP_INDICATORS) {
            if (response.contains(indicator)) {
                return true;
            }
        }
        return false;
    }
}
