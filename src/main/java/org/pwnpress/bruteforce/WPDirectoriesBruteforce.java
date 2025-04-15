package org.pwnpress.bruteforce;

import org.pwnpress.framework.WPFrameworkSettings;
import org.pwnpress.utils.HttpRequest;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class WPDirectoriesBruteforce {

    private static final AtomicBoolean stopBruteforce = new AtomicBoolean(false);
    private static final AtomicInteger requestCounter = new AtomicInteger(0);
    private static boolean debugMode = false;
    private static boolean verboseMode = true;
    private static final List<String> foundPaths = Collections.synchronizedList(new ArrayList<>());

    public static void wpDirectoriesBrute() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("[!] WARNING: This part of the tool is not fine tuned, it tends to show many false positives/negatives.\n");
        System.out.print("Enter target website URL: ");
        String targetUrl = scanner.nextLine().trim();

        if (!isSiteAccessible(targetUrl)) {
            System.out.println("[-] Target site is not accessible. Aborting.");
            return;
        }

        System.out.println("[+] Site is accessible. Starting brute-force attack...");
        System.out.println("    Press [s] for status, [q] to quit, [v] to toggle verbose output, [d] to toggle debug info.");

        List<String> paths = loadWordlist();
        if (paths.isEmpty()) {
            System.out.println("[-] No entries found in wordlist. Exiting.");
            return;
        }

        startUserInputListener();
        bruteForce(targetUrl, paths);
        printFoundPaths();
    }

    private static boolean isSiteAccessible(String targetUrl) {
        try {
            HttpURLConnection connection = HttpRequest.getRequest(targetUrl);
            return connection.getResponseCode() == 200;
        } catch (IOException e) {
            System.err.println("Error accessing site: " + e.getMessage());
            return false;
        }
    }

    private static void bruteForce(String targetUrl, List<String> paths) {
        for (String path : paths) {
            if (stopBruteforce.get()) {
                System.out.println("[*] Brute force stopped by user.");
                return;
            }

            // Strip all leading slashes from the path
            path = path.replaceAll("^/+", "");

            // Ensure proper URL formatting (no double slashes)
            String fullUrl = targetUrl.endsWith("/") ? targetUrl + path : targetUrl + "/" + path;
            fullUrl = fullUrl.replaceAll("([^:])//", "$1/");

            if (verboseMode) {
                System.out.println("[+] Trying: " + fullUrl);
            }

            if (attemptPath(fullUrl)) {
                foundPaths.add(fullUrl);
                System.out.println("[!] Valid directory/file found: " + fullUrl);  // Print immediately when found
            }

            requestCounter.incrementAndGet();

            try {
                Thread.sleep(500);  // Avoid overwhelming the server
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("[+] Brute-force completed.");
    }

    private static boolean attemptPath(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            String finalUrl = url;

            // Follow all redirections until we get the final response
            while (responseCode >= 300 && responseCode < 400) {
                String location = connection.getHeaderField("Location");
                if (location == null) break;

                finalUrl = new URL(new URL(url), location).toString();
                connection = (HttpURLConnection) new URL(finalUrl).openConnection();
                connection.setInstanceFollowRedirects(false);
                responseCode = connection.getResponseCode();
            }

            if (debugMode) {
                System.out.println("[DEBUG] Request to " + url + " -> Final URL: " + finalUrl + " | Response Code: " + responseCode);
            }

            return responseCode == 200;  // Only consider a valid result if it's 200 OK

        } catch (IOException e) {
            if (debugMode) {
                System.err.println("[DEBUG] Error checking " + url + ": " + e.getMessage());
            }
        }
        return false;
    }

    private static List<String> loadWordlist() {
        List<String> paths = new ArrayList<>();
        String wordlistFile = WPFrameworkSettings.getWpDirectoriesWordlist();

        if (wordlistFile == null || wordlistFile.isEmpty()) {
            System.err.println("[-] Error: Wordlist file is not set.");
            return paths;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(wordlistFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                paths.add(line.trim());
            }
        } catch (IOException e) {
            System.err.println("[-] Error reading wordlist: " + e.getMessage());
        }

        return paths;
    }

    private static void startUserInputListener() {
        Thread inputThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (!stopBruteforce.get()) {
                String input = scanner.nextLine().trim().toLowerCase();
                switch (input) {
                    case "s":
                        System.out.println("[STATUS] Requests Sent: " + requestCounter.get() + " | Paths Found: " + foundPaths.size());
                        break;
                    case "q":
                        stopBruteforce.set(true);
                        System.out.println("[*] Stopping brute-force attack...");
                        return;
                    case "v":
                        verboseMode = !verboseMode;
                        System.out.println("[*] Verbose mode: " + (verboseMode ? "Enabled" : "Disabled"));
                        break;
                    case "d":
                        debugMode = !debugMode;
                        System.out.println("[*] Debug mode: " + (debugMode ? "Enabled" : "Disabled"));
                        break;
                }
            }
        });

        inputThread.setDaemon(true);
        inputThread.start();
    }

    private static void printFoundPaths() {
        if (foundPaths.isEmpty()) {
            System.out.println("[-] No valid directories or files found.");
        } else {
            System.out.println("[+] Brute-force completed. Valid directories/files found:");
            for (String path : foundPaths) {
                System.out.println("    - " + path);
            }
        }
    }
}
