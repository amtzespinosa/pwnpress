package org.pwnpress.scraper;

import org.pwnpress.utils.HttpRequest;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class DirectoryScraper {

    private static final AtomicBoolean stopScraping = new AtomicBoolean(false);
    private static boolean debugMode = false; // Flag to control debug info
    private static boolean verboseMode = true; // Flag to control verbose info (default is true)

    public static void scrapeDirectory() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter target website URL: ");
        String targetUrl = scanner.nextLine().trim();

        System.out.print("Enter directory to scrape: ");
        String directory = scanner.nextLine().trim();

        // Start a thread to listen for user input during scraping
        startUserInputListener();

        System.out.println("[+] Starting to scrape the directory...");

        // Start the directory scraping process
        startScraping(targetUrl, directory);
    }

    private static void startScraping(String baseUrl, String directory) {
        String directoryUrl = baseUrl + "/" + directory;

        if (stopScraping.get()) {
            System.out.println("[*] Scraping stopped by user.");
            return;
        }

        if (isDirectoryListable(directoryUrl)) {
            extractUrlsFromHtml(directoryUrl);
        }
    }

    private static boolean isDirectoryListable(String directoryUrl) {
        try {
            HttpURLConnection connection = HttpRequest.getRequest(directoryUrl);
            int responseCode = connection.getResponseCode();

            // Follow redirects if any
            while (isRedirect(responseCode)) {
                String redirectUrl = connection.getHeaderField("Location");
                if (redirectUrl != null) {
                    connection = HttpRequest.getRequest(redirectUrl);
                    responseCode = connection.getResponseCode();
                    System.out.println("[*] Redirected to: " + redirectUrl);
                }
            }

            if (responseCode == HttpURLConnection.HTTP_OK) {
                return true;  // Directory is listable
            } else {
                System.out.println("[-] Unable to access the directory. HTTP " + responseCode);
                return false;
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    private static void extractUrlsFromHtml(String directoryUrl) {
        try {
            HttpURLConnection connection = HttpRequest.getRequest(directoryUrl);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("<a href=\"")) {
                    String fileUrl = extractUrl(line);
                    if (fileUrl != null) {
                        // Print the URL
                        System.out.println("[+] Found URL: " + fileUrl);

                        // If it's a directory, recursively scrape it
                        if (fileUrl.endsWith("/")) {
                            startScraping(directoryUrl, fileUrl);  // Recurse into the directory
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading the HTML response: " + e.getMessage());
        }
    }

    private static String extractUrl(String htmlLine) {
        int startIdx = htmlLine.indexOf("<a href=\"");
        if (startIdx != -1) {
            startIdx += "<a href=\"".length();
            int endIdx = htmlLine.indexOf("\"", startIdx);
            if (endIdx != -1) {
                return htmlLine.substring(startIdx, endIdx).trim();
            }
        }
        return null;
    }

    private static boolean isRedirect(int responseCode) {
        return responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
               responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
               responseCode == HttpURLConnection.HTTP_SEE_OTHER;
    }

    // Start listening for user input (commands for status, quitting, toggling verbose/debug modes)
    private static void startUserInputListener() {
        Thread inputThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (!stopScraping.get()) {
                String input = scanner.nextLine().trim().toLowerCase();
                if (input.equals("s")) {
                    System.out.println("[STATUS] Scraping in progress...");
                } else if (input.equals("q")) {
                    stopScraping.set(true);
                    System.out.println("[*] Stopping the scraper...");
                    return;
                } else if (input.equals("v")) {
                    verboseMode = !verboseMode; // Toggle verbose mode
                    System.out.println("[*] Verbose mode: " + (verboseMode ? "Enabled" : "Disabled"));
                } else if (input.equals("d")) {
                    debugMode = !debugMode; // Toggle debug mode
                    System.out.println("[*] Debug mode: " + (debugMode ? "Enabled" : "Disabled"));
                }
            }
        });

        inputThread.setDaemon(true);
        inputThread.start();
    }
}
