package org.pwnpress.scanner.modules;

import org.pwnpress.utils.HttpRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WPThemeScanner {

    private static final List<Map<String, String>> foundThemes = new ArrayList<>(); // Stores found themes

    public static List<Map<String, String>> getFoundThemes() {
        return foundThemes;
    }

    public static void scanThemes(String urlStr) {
        System.out.println("\n[+] Enumerating themes...");

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

            // Step 1: Extract all theme names
            List<String> themes = getThemesFromSource(html);

            if (themes.isEmpty()) {
                System.out.println("[-] No themes detected.");
                return;
            }
            System.out.println("[+] Checking theme version...");
            // Step 2: Extract versions for each theme
            for (String themeName : themes) {


                String themeVersion = extractVersionFromSource(html, themeName);

                if (themeVersion == null) {
                    // If no version in HTML, try readme.txt
                    themeVersion = extractVersionFromReadme(urlStr, themeName);
                }

                Map<String, String> themeInfo = new HashMap<>();
                themeInfo.put("slug", themeName);
                themeInfo.put("version", themeVersion != null ? themeVersion : "Version not found");
                foundThemes.add(themeInfo);
            }

        } catch (IOException e) {
            System.err.println("Error fetching URL content: " + e.getMessage());
        }
    }

    // Extracts all themes from the HTML and returns a list of theme names
    public static List<String> getThemesFromSource(String html) {
        Pattern pattern = Pattern.compile("/wp-content/themes/([^/]+)/");
        Matcher matcher = pattern.matcher(html);

        List<String> themes = new ArrayList<>();
        Set<String> uniqueThemes = new HashSet<>(); // Avoid duplicates

        while (matcher.find()) {
            String themeName = matcher.group(1);
            if (themeName != null && uniqueThemes.add(themeName)) { // Prevent duplicates
                themes.add(themeName);
            }
        }

        return themes;
    }

    // Extracts the theme version from the source HTML (CSS/JS URLs)
    private static String extractVersionFromSource(String html, String themeName) {
        Pattern pattern = Pattern.compile("/wp-content/themes/" + themeName + "/.*?\\?ver=(\\d+(?:\\.\\d+)*)");
        Matcher matcher = pattern.matcher(html);

        if (matcher.find()) {
            String themeVersion = matcher.group(1);
            return themeVersion;
        }
        return null;
    }

    // Tries to extract version from readme.txt (if accessible)
    private static String extractVersionFromReadme(String urlStr, String themeName) {
        String readmePath = urlStr + "wp-content/themes/" + themeName + "/readme.txt";

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
                String themeVersion = matcher.group(1);

                return themeVersion;
            }

        } catch (IOException e) {
            return null;
        }

        return null;
    }
}
