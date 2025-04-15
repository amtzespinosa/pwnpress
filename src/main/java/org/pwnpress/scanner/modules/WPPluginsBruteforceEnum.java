package org.pwnpress.scanner.modules;

import org.pwnpress.framework.WPFrameworkSettings;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WPPluginsBruteforceEnum {

    private static List<Map<String, String>> foundPlugins = new ArrayList<>(); // List to store found plugins (slug and version)

    public static void bruteforcePlugins(String url) {
        String pluginsFilePath = WPFrameworkSettings.getPluginsFilePath(); // Get plugins file path from settings
        System.out.println("\n[+] Plugins enumeration (via bruteforce):");

        List<String> pluginsToScan = loadPluginsFromSettings(pluginsFilePath);
        if (pluginsToScan.isEmpty()) {
            System.out.println("No plugins wordlist specified in settings.");
            return;
        }

        // Create a thread pool with a fixed number of threads (adjust as needed)
        int numThreads = WPFrameworkSettings.getNumThreads(); // Number of threads for concurrent scanning
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        for (String plugin : pluginsToScan) {
            executor.submit(() -> {
                boolean installed = isPluginInstalled(url, plugin);
                if (installed) {
                    String pluginUrl = url + "/wp-content/plugins/" + plugin + "/";
                    String pluginVersion = getPluginVersion(pluginUrl);
                    if (pluginVersion != null) {
                        Map<String, String> pluginInfo = new HashMap<>();
                        pluginInfo.put("slug", plugin);
                        pluginInfo.put("version", pluginVersion);
                        synchronized (foundPlugins) {
                            foundPlugins.add(pluginInfo); // Add plugin map to foundPlugins list
                        }
                    }
                } // If not installed, do nothing (omit printing)
            });
        }

        // Shutdown the executor and wait until all tasks are completed
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            System.out.println("Error waiting for threads to complete: " + e.getMessage());
        }
    }

    public static List<Map<String, String>> getFoundPlugins() {
        return foundPlugins;
    }

    private static List<String> loadPluginsFromSettings(String pluginsFilePath) {
        List<String> pluginsList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(pluginsFilePath))) {
            String plugin;
            while ((plugin = br.readLine()) != null) {
                pluginsList.add(plugin.trim());
            }
        } catch (IOException e) {
            System.out.println("An error occurred while loading plugins from settings: " + e.getMessage());
        }
        return pluginsList;
    }

    private static boolean isPluginInstalled(String url, String plugin) {
        String pluginUrl = url + "/wp-content/plugins/" + plugin + "/";
        try {
            URL urlObj = new URL(pluginUrl);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            // Check if plugin directory returns HTTP OK (200) or Forbidden (403)
            return responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_FORBIDDEN;
        } catch (IOException e) {
            // Log the error if needed
            // System.err.println("Error checking plugin URL: " + pluginUrl + " - " + e.getMessage());
            return false;
        }
    }

    private static String getPluginVersion(String pluginUrl) {
        try {
            URL url = new URL(pluginUrl + "readme.txt"); // Example: readme.txt path
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String version = extractVersionFromReadme(line);
                    if (version != null) {
                        return version;
                    }
                }
            }
        } catch (IOException e) {
            // Uncomment the line below to log the error, if needed
            // System.err.println("Error fetching plugin version: " + pluginUrl);
            return null;
        }
        return null;
    }

    private static String extractVersionFromReadme(String line) {
        // Example: Extract version from line starting with "Stable tag:"
        Pattern pattern = Pattern.compile("Stable tag:\\s*([\\d.]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(line);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

}
