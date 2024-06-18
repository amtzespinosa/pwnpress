package org.pwnpress.scanner.modules;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pwnpress.utils.CustomFormat;

public class UserEnumerationChecker {

    public static void checkUserEnumeration(String url) {
        boolean enumerationPossible = false;

        // Method 1: Access WP JSON API endpoint for users
        if (checkUserEnumerationViaJSONAPI(url)) {
            enumerationPossible = true;
        }

        if (!enumerationPossible) {
            System.out.println("\n[-] User enumeration not possible.");
        }
    }

    private static boolean checkUserEnumerationViaJSONAPI(String url) {
        try {
            URL userURL = new URL(url + "wp-json/wp/v2/users");
            HttpURLConnection connection = (HttpURLConnection) userURL.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONArray usersArray = new JSONArray(response.toString());
                System.out.println("\n[+] User enumeration via WP JSON API:");

                for (int i = 0; i < usersArray.length(); i++) {
                    JSONObject userObject = usersArray.getJSONObject(i);
                    String slug = userObject.getString("slug");
                    String name = userObject.getString("name");
                    System.out.println(" |- User found:");
                    System.out.println(CustomFormat.padRight("    - Name:", 20) + name);
                    System.out.println(CustomFormat.padRight("    - Username:", 20) + slug);

                    // Check weak passwords for this user
                    // checkWeakPasswords(url, slug);
                }
                return true; // User enumeration is possible
            }
        } catch (IOException | JSONException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return false; // User enumeration failed
    }

    private static void checkWeakPasswords(String url, String username) {
        try {
            String[] passwords = {"password1", "password123", "123456", "admin", "qwerty", "letmein", "abc123"}; // Example weak passwords
            boolean weakPasswordDetected = false;
            for (String password : passwords) {
                if (attemptLogin(url, username, password)) {
                    weakPasswordDetected = true;
                    String color = CustomFormat.getColor("great"); // Red color ANSI escape code for console
                    System.out.println("[+] Weak password detected for username " +
                            color + username+ CustomFormat.resetColor() + ":" +color+ password + CustomFormat.resetColor());
                    break; // Stop checking passwords once a weak one is found
                }
            }
            if (!weakPasswordDetected) {
                System.out.println("[-] No weak passwords found for username " + username);
            }
        } catch (IOException e) {
            System.out.println("[!] Error checking weak passwords: " + e.getMessage());
        }
    }

    private static boolean attemptLogin(String url, String username, String password) throws IOException {
        String authString = username + ":" + password;
        String encodedAuthString = Base64.getEncoder().encodeToString(authString.getBytes());
        URL loginURL = new URL(url + "wp-login.php");
        HttpURLConnection connection = (HttpURLConnection) loginURL.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic " + encodedAuthString);

        int responseCode = connection.getResponseCode();
        // Check for valid HTTP response codes indicating successful login
        return responseCode == HttpURLConnection.HTTP_OK ||
                responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                responseCode == HttpURLConnection.HTTP_MOVED_PERM;
    }

}
