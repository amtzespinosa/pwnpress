package org.pwnpress.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequest {

    public static HttpURLConnection getRequest(String baseUrl) throws IOException {

        URL fullPathURL = new URL(baseUrl);
        HttpURLConnection connection = (HttpURLConnection) fullPathURL.openConnection();
        connection.setRequestMethod("GET");

        String userAgent = UserAgentRotation.getNextUserAgent();
        setHeaders(connection, baseUrl, userAgent);
        RequestsCounter.addRequest();

        return connection;
    }

    public static HttpURLConnection getRequest(String baseUrl, String requestPath) throws IOException {

        URL fullPathURL = new URL(baseUrl + requestPath);
        HttpURLConnection connection = (HttpURLConnection) fullPathURL.openConnection();
        connection.setRequestMethod("GET");

        String userAgent = UserAgentRotation.getNextUserAgent();
        setHeaders(connection, baseUrl, userAgent);
        RequestsCounter.addRequest();

        return connection;
    }

    public static HttpURLConnection postRequest(String baseUrl, String payload) throws IOException {
        URL fullPathURL = new URL(baseUrl);
        HttpURLConnection connection = (HttpURLConnection) fullPathURL.openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true); // Enables sending a request body
        connection.setRequestProperty("Content-Type", "text/xml");
        connection.setRequestProperty("Content-Length", String.valueOf(payload.getBytes().length));

        String userAgent = UserAgentRotation.getNextUserAgent();
        setHeaders(connection, baseUrl, userAgent);
        RequestsCounter.addRequest();

        // Write payload to request body
        try (OutputStream os = connection.getOutputStream()) {
            os.write(payload.getBytes("UTF-8"));
            os.flush();
        }

        return connection;
    }

    public static String readResponse(HttpURLConnection connection) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }

    public static HttpURLConnection headRequest(String baseUrl) throws IOException {

        URL fullPathURL = new URL(baseUrl);
        HttpURLConnection connection = (HttpURLConnection) fullPathURL.openConnection();
        connection.setRequestMethod("HEAD");

        String userAgent = UserAgentRotation.getNextUserAgent();
        setHeaders(connection, baseUrl, userAgent);
        RequestsCounter.addRequest();

        return connection;
    }

    public static HttpURLConnection headRequest(String baseUrl, String requestPath) throws IOException {

        URL fullPathURL = new URL(baseUrl + requestPath);
        HttpURLConnection connection = (HttpURLConnection) fullPathURL.openConnection();
        connection.setRequestMethod("HEAD");

        String userAgent = UserAgentRotation.getNextUserAgent();
        setHeaders(connection, baseUrl, userAgent);
        RequestsCounter.addRequest();

        return connection;
    }

    public static boolean requestResponseCode(HttpURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            return true;
        }
        return false;
    }

    private static void setHeaders(HttpURLConnection connection, String baseUrl, String userAgent) {

        connection.setRequestProperty("User-Agent", userAgent);
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setRequestProperty("Referer", baseUrl);

    }

}
