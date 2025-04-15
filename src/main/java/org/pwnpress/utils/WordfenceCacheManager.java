package org.pwnpress.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.pwnpress.framework.WPFrameworkSettings;

public class WordfenceCacheManager {

    private static final long CACHE_EXPIRY_TIME = 86400 * 1000; // 1 hour in milliseconds

    /**
     * Retrieves the Wordfence data, either from cache or by downloading fresh data.
     */
    public static JSONObject getWordfenceData() {
        try {
            File cacheFile = new File(WPFrameworkSettings.CACHE_FILE_PATH);

            // Check if cache exists and is fresh
            if (cacheFile.exists() && (System.currentTimeMillis() - cacheFile.lastModified() < CACHE_EXPIRY_TIME)) {
                System.out.println("\n[+] Using cached Wordfence data.");
                return readFromCache(cacheFile);
            } else {
                System.out.println("\n[+] Downloading fresh Wordfence data...");
                JSONObject data = fetchFromApi();
                saveToCache(data, cacheFile);
                return data;
            }
        } catch (IOException e) {
            System.out.println("[!] Error retrieving Wordfence data: " + e.getMessage());
            return null;
        }
    }

    /**
     * Fetches data from the Wordfence API.
     */
    private static JSONObject fetchFromApi() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(WPFrameworkSettings.API_URL).openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return new JSONObject(response.toString());
            }
        } else {
            throw new IOException("Failed to fetch Wordfence data. HTTP code: " + connection.getResponseCode());
        }
    }

    /**
     * Reads data from the local cache file.
     */
    private static JSONObject readFromCache(File cacheFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(cacheFile))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return new JSONObject(response.toString());
        }
    }

    /**
     * Saves data to the local cache file.
     */
    private static void saveToCache(JSONObject data, File cacheFile) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(cacheFile))) {
            writer.write(data.toString());
        }
        System.out.println("[+] Wordfence data cached successfully.");
    }
}