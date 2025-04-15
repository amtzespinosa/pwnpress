package org.pwnpress.pingbacker;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;

class PingbackServer {
    private HttpServer server;
    private static final List<String> RECEIVED_PINGS = new LinkedList<>();

    public PingbackServer(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/pingback", new PingbackHandler());
        server.createContext("/", new RootHandler());
    }

    public void start() {
        server.start();
    }

    static class PingbackHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                // Read the request body containing the XML-RPC payload
                InputStreamReader reader = new InputStreamReader(exchange.getRequestBody());
                StringBuilder requestBody = new StringBuilder();
                int character;
                while ((character = reader.read()) != -1) {
                    requestBody.append((char) character);
                }

                // Log the raw XML-RPC request for debugging
                System.out.println("Received XML-RPC Request: " + requestBody.toString());

                // Extract the URLs from the XML payload
                String pingbackUrl = extractPingbackUrl(requestBody.toString());

                if (pingbackUrl != null) {
                    String forwardedFor = exchange.getRequestHeaders().getFirst("x-pingback-forwarded-for");
                    if (forwardedFor != null) {
                        System.out.println("Pingback forwarded from: " + forwardedFor);
                    } else {
                        System.out.println("No forwarded-for header present.");
                    }

                    RECEIVED_PINGS.add(pingbackUrl);
                    System.out.println("Received pingback for: " + pingbackUrl);
                }
            }

            String response = "OK";
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }

        // Extract the target post URL from the XML-RPC request
        private String extractPingbackUrl(String xmlPayload) {
            String targetUrlTag = "<value>" + "</value>"; // The XML tag enclosing the target URL
            int startIndex = xmlPayload.indexOf("<methodName>pingback.ping</methodName>");
            if (startIndex != -1) {
                // Find the post URL (this is a simplified way to extract the URL)
                int targetUrlStart = xmlPayload.indexOf("<value>", startIndex);
                int targetUrlEnd = xmlPayload.indexOf("</value>", targetUrlStart);
                if (targetUrlStart != -1 && targetUrlEnd != -1) {
                    return xmlPayload.substring(targetUrlStart + 7, targetUrlEnd); // Extract the target URL
                }
            }
            return null;
        }
    }


    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder htmlResponse = new StringBuilder("<html><body><h1>Received Pingbacks</h1><ul>");
            for (String pingback : RECEIVED_PINGS) {
                htmlResponse.append("<li>").append(pingback).append("</li>");
            }
            htmlResponse.append("</ul></body></html>");

            String response = htmlResponse.toString();
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    public static void startServer() throws IOException {
        int port = 666;
        PingbackServer server = new PingbackServer(port);
        server.start();
        System.out.println("Pingback Server started on port " + port);
    }

    public static String getLastReceivedPingback() {
        return RECEIVED_PINGS.isEmpty() ? null : RECEIVED_PINGS.remove(0);
    }
}
