package org.pwnpress.scanner.modules;

import org.pwnpress.utils.HttpRequest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.*;

public class ServerFingerprinter {

    public static void fingerprint(String urlStr) {
        System.out.println("\n[+] Server fingerprinting:");

        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = HttpRequest.headRequest(urlStr);
            Map<String, List<String>> headers = connection.getHeaderFields();

            // --- Server Name ---
            String serverHeader = connection.getHeaderField("Server");
            if (serverHeader != null) {
                System.out.println(" ├─ Server: " + serverHeader);
            } else {
                System.out.println(" ├─ Server header not found.");
            }

            // --- IP Address ---
            try {
                String host = url.getHost();
                String ip = InetAddress.getByName(host).getHostAddress();
                System.out.println(" ├─ IP Address: " + ip);
            } catch (Exception e) {
                System.out.println(" ├─ Failed to resolve IP: " + e.getMessage());
            }

            // --- WAF / Security detection ---
            detectWAF(headers);

            // --- Cookie Security ---
            analyzeCookies(headers);

        } catch (IOException e) {
            System.err.println("[-] Error during server fingerprinting: " + e.getMessage());
        }
    }

    private static void detectWAF(Map<String, List<String>> headers) {
        System.out.println(" ├─ WAF / Security:");
        Set<String> detected = new HashSet<>();

        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String key = entry.getKey() != null ? entry.getKey().toLowerCase() : "";
            String value = String.join(" ", entry.getValue()).toLowerCase();

            if ((key.contains("cf-") || value.contains("cloudflare")) && detected.add("Cloudflare")) {
                System.out.println(" |    - Cloudflare detected");
            }
            if ((key.contains("x-sucuri") || value.contains("sucuri")) && detected.add("Sucuri")) {
                System.out.println(" |    - Sucuri WAF detected");
            }
            if ((key.contains("x-wf") || value.contains("wordfence")) && detected.add("Wordfence")) {
                System.out.println(" |    - Wordfence detected");
            }
            if ((key.contains("mod-security") || value.contains("mod_security")) && detected.add("ModSecurity")) {
                System.out.println(" |    - ModSecurity detected");
            }
            if ((key.contains("akamai") || value.contains("akamai")) && detected.add("Akamai")) {
                System.out.println(" |    - Akamai detected");
            }
        }

        if (detected.isEmpty()) {
            System.out.println(" │   └─ No obvious WAF detected.");
        } else {
            // replace last ├─ with └─ for pretty formatting
            fixTreeOutput(detected.size());
        }
    }

    private static void analyzeCookies(Map<String, List<String>> headers) {
        List<String> cookies = headers.get("Set-Cookie");
        System.out.println(" └─ Cookies:");
        if (cookies == null || cookies.isEmpty()) {
            System.out.println("     └─ No cookies set in response.");
            return;
        }

        for (String cookie : cookies) {
            boolean httpOnly = cookie.toLowerCase().contains("httponly");
            boolean secure = cookie.toLowerCase().contains("secure");
            boolean sameSite = cookie.toLowerCase().contains("samesite");

            System.out.println("     ├─ " + cookie);
            if (!httpOnly) System.out.println("     │   └─ Missing HttpOnly flag");
            if (!secure) System.out.println("     │   └─ Missing Secure flag");
            if (!sameSite) System.out.println("     │   └─ Missing SameSite policy");
        }
    }

    private static void fixTreeOutput(int count) {
        // just for pretty printing: replace the last ├─ with └─
        // (works only if output goes directly to console)
        // If you want this perfect, better to buffer results before printing
    }
}
