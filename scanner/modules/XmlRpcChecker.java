package org.pwnpress.scanner.modules;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class XmlRpcChecker {

    public static void checkXmlRpc(String urlStr) {
        try {
            URL url = new URL(urlStr + "xmlrpc.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("\n[+] XML-RPC endpoint found at: " + url);
            }
        } catch (IOException e) {
            System.err.println("Error checking XML-RPC endpoint: " + e.getMessage());
        }
    }
}
