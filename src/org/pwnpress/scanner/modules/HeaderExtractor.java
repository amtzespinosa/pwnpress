package org.pwnpress.scanner.modules;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HeaderExtractor {

    public static void extractHeaders(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");

            Map<String, java.util.List<String>> headers = connection.getHeaderFields();
            System.out.println("\n[+] Headers:");
            for (Map.Entry<String, java.util.List<String>> entry : headers.entrySet()) {
                String headerName = entry.getKey();
                for (String value : entry.getValue()) {
                	System.out.println(" |- " + padRight(headerName + ":", 20) + value);
                }
            }
            connection.disconnect();
        } catch (IOException e) {
            System.err.println("Error fetching headers: " + e.getMessage());
        }
    }
    
    // Method to pad string to the right with spaces to given length
    private static String padRight(String s, int length) {
        return String.format("%-" + length + "s", s);
    }
}
