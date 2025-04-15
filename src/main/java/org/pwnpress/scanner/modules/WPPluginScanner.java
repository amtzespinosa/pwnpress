package org.pwnpress.scanner.modules;

import org.pwnpress.utils.HttpRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WPPluginScanner {

    private static final List<Map<String, String>> foundPlugins = new ArrayList<>();

    public static List<Map<String, String>> getFoundPlugins() {
        return foundPlugins;
    }

    public static void scanPlugins(String urlStr) {
            System.out.println("\n[+] Enumerating all plugins...");

        try {
            HttpURLConnection connection = HttpRequest.getRequest(urlStr);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder htmlContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                htmlContent.append(line);
            }
            reader.close();

            String html = htmlContent.toString();

            // Step 1: Extract all plugin names
            List<String> plugins = getPluginsFromSource(html);

            if (plugins.isEmpty()) {
                System.out.println("[-] No plugins detected.");
                return;
            }

            System.out.println("[+] Checking plugins version...");
            // Step 2: Extract versions for each plugin
            for (String pluginName : plugins) {

                String pluginVersion = extractVersionFromReadme(urlStr, pluginName);

                if (pluginVersion == null) {
                    pluginVersion = extractVersionFromSource(html, pluginName);

                }

                Map<String, String> pluginInfo = new HashMap<>();
                pluginInfo.put("slug", pluginName);
                pluginInfo.put("version", pluginVersion != null ? pluginVersion : "Version not found");
                foundPlugins.add(pluginInfo);
            }

        } catch (IOException e) {
            System.err.println("Error fetching URL content: " + e.getMessage());
        }
    }

    // Extracts all plugins from the HTML and returns a list of plugin names
    public static List<String> getPluginsFromSource(String html) {
        Pattern pattern = Pattern.compile("/wp-content/plugins/([^/]+)/");
        Matcher matcher = pattern.matcher(html);

        List<String> plugins = new ArrayList<>();
        Set<String> uniquePlugins = new HashSet<>();

        while (matcher.find()) {
            String pluginName = matcher.group(1);
            if (pluginName != null && uniquePlugins.add(pluginName)) {
                plugins.add(pluginName);
            }
        }

        return plugins;
    }

    // Extracts the plugin version from the source HTML (CSS/JS URLs)
    private static String extractVersionFromSource(String html, String pluginName) {
        Pattern pattern = Pattern.compile("/wp-content/plugins/" + pluginName + "/.*?\\?ver=(\\d+(?:\\.\\d+)*)");
        Matcher matcher = pattern.matcher(html);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    // Tries to extract version from readme.txt (if accessible)
    private static String extractVersionFromReadme(String urlStr, String pluginName) {
        String readmePath = urlStr + "wp-content/plugins/" + pluginName + "/readme.txt";

        try {
            HttpURLConnection connection = HttpRequest.getRequest(readmePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder readmeContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                readmeContent.append(line).append("\n");
            }
            reader.close();

            // Extract version from readme.txt
            Pattern pattern = Pattern.compile("Stable tag: (\\d+(?:\\.\\d+)*)");
            Matcher matcher = pattern.matcher(readmeContent.toString());

            if (matcher.find()) {
                return matcher.group(1);
            }

        } catch (IOException e) {
            return null;
        }

        return null;
    }
}
