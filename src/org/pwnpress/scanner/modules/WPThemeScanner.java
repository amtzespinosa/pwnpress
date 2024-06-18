package org.pwnpress.scanner.modules;

import org.pwnpress.framework.WPFrameworkSettings;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WPThemeScanner {

    private static List<Map<String, String>> foundThemes = new ArrayList<>(); // Static variable to store found themes

    public static void scanThemes(String url) {
        String themesFilePath = WPFrameworkSettings.getThemesFilePath(); // Get themes file path from settings
        System.out.println("\n[+] Themes enumeration:");

        List<String> themesToScan = loadThemesFromSettings(themesFilePath);
        if (themesToScan.isEmpty()) {
            System.out.println(" |- No themes specified in the settings to scan.");
            return;
        }

        // Configure thread pool based on settings
        int numThreads = WPFrameworkSettings.getNumThreads();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        // Submit tasks for each theme to be scanned
        for (String theme : themesToScan) {
            executor.submit(() -> {
                if (isThemeInstalled(url, theme)) {
                    String themeUrl = url + "/wp-content/themes/" + theme + "/style.css";
                    String themeVersion = getThemeVersion(themeUrl);
                    Map<String, String> themeInfo = new HashMap<>();
                    themeInfo.put("slug", theme);
                    themeInfo.put("version", themeVersion != null ? themeVersion : "Version not found");
                    foundThemes.add(themeInfo);
                    if (themeVersion != null) {
                        System.out.println(" |- " + theme + " (Version: " + themeVersion + ")");
                    } else {
                        System.out.println(" |- " + theme + " (Version not found)");
                    }
                }
            });
        }

        // Shutdown the executor after all tasks are submitted
        executor.shutdown();
        // Wait for all threads to finish
        while (!executor.isTerminated()) {
            // Optionally, you can add a delay here if needed
        }
    }

    public static List<Map<String, String>> getFoundThemes() {
        return foundThemes;
    }

    private static List<String> loadThemesFromSettings(String themesFilePath) {
        List<String> themesList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(themesFilePath))) {
            String theme;
            while ((theme = br.readLine()) != null) {
                themesList.add(theme.trim());
            }
        } catch (IOException e) {
            System.out.println(" |- An error occurred while loading themes from settings: " + e.getMessage());
        }
        return themesList;
    }

    private static boolean isThemeInstalled(String url, String theme) {
        String themeUrl = url + "/wp-content/themes/" + theme + "/style.css";
        try {
            URL urlObj = new URL(themeUrl);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            return false;
        }
    }

    private static String getThemeVersion(String themeUrl) {
        try {
            URL url = new URL(themeUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new java.io.InputStreamReader(connection.getInputStream()))) {
                StringBuilder cssContent = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    cssContent.append(line);
                }
                return extractVersionFromCss(cssContent.toString());
            }
        } catch (IOException e) {
            // Uncomment the line below to log the error, if needed
            // System.err.println("Error fetching theme version: " + themeUrl);
            return null;
        }
    }

    private static String extractVersionFromCss(String cssContent) {
        Pattern pattern = Pattern.compile("Version: ([\\d.]+)");
        Matcher matcher = pattern.matcher(cssContent);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }
}
