package org.pwnpress.scanner.modules;

import org.pwnpress.framework.WPFrameworkSettings;
import org.pwnpress.utils.HttpRequest;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HeaderExtractor {

    public static void extractHeaders(String urlStr) {
        try {

            HttpURLConnection connection = HttpRequest.headRequest(urlStr);
            Map<String, List<String>> headers = connection.getHeaderFields();

            System.out.println("\n[+] Headers:");

            List<Map.Entry<String, List<String>>> headerList = new ArrayList<>(headers.entrySet());
            int lastIndex = headerList.size() - 1;

            for (int i = 0; i < headerList.size(); i++) {
                Map.Entry<String, List<String>> entry = headerList.get(i);
                String headerName = entry.getKey();
                List<String> values = entry.getValue();

                for (int j = 0; j < values.size(); j++) {
                    String headerValue = values.get(j);
                    boolean isLastEntry = (i == lastIndex) && (j == values.size() - 1); // Check if it's the last header value
                    String prefix = isLastEntry ? " └─ " : " ├─ "; // Different prefix for the last header

                    System.out.println(prefix + padRight(headerName + ":", 20) + headerValue);

                    if ("x-powered-by".equalsIgnoreCase(headerName) && headerValue != null) {
                        PhpVersionChecker.storeHeaderValue(headerValue);
                    }
                }
            }


            connection.disconnect();
        } catch (IOException e) {
            if (WPFrameworkSettings.isErrorLogging()) {
                System.err.println("\nError fetching headers: " + e.getMessage());
            }
        }
    }
    
    // Method to pad string to the right with spaces to given length
    private static String padRight(String s, int length) {
        return String.format("%-" + length + "s", s);
    }
}

