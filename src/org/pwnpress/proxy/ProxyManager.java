package org.pwnpress.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ProxyManager {

    private static List<String> proxyList = new ArrayList<>();
    private static int currentProxyIndex = 0;

    // API endpoint to fetch proxies
    private static final String PROXY_API_URL = "https://api.proxyscrape.com/v2/?request=displayproxies&protocol=https&timeout=10000&country=all&ssl=yes&anonymity=elite";

    static {
        fetchProxies();
    }

    // Fetch the list of proxies from the API
    private static void fetchProxies() {
        try {
            URL url = new URL(PROXY_API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    proxyList.add(inputLine.trim());
                }
                in.close();
            } else {
                System.err.println("Failed to fetch proxies. Response code: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Get the current proxy as a Proxy object
    public static Proxy getCurrentProxy() {
        if (proxyList.isEmpty()) {
            fetchProxies();
        }
        if (proxyList.isEmpty()) {
            return null;
        }

        String proxyAddress = proxyList.get(currentProxyIndex);
        String[] parts = proxyAddress.split(":");
        String proxyHost = parts[0];
        int proxyPort = Integer.parseInt(parts[1]);

        Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyHost, proxyPort));
        return proxy;
    }

    // Rotate to the next proxy
    public static void changeProxy() {
        if (!proxyList.isEmpty()) {
            currentProxyIndex = (currentProxyIndex + 1) % proxyList.size();
        }
    }
}
