package org.pwnpress.scanner.modules;
import org.pwnpress.framework.WPFrameworkSettings;
import org.pwnpress.utils.HttpRequest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class XmlRpcChecker {

    public static void checkXmlRpc(String urlStr) {
        try {
            HttpURLConnection connection = HttpRequest.postRequest(urlStr + "xmlrpc.php", "<methodCall><methodName>system.listMethods</methodName></methodCall>");
            String response = HttpRequest.readResponse(connection);

            if (HttpRequest.requestResponseCode(connection) && response.contains("wp.getUsersBlogs")) {
                System.out.println("\n[+] XML-RPC endpoint found.");
                System.out.println(" ├─ " + urlStr + "xmlrpc.php");
                System.out.println(" └─ It seems vulnerable to System.Multicall brute force amplification attack");
                System.out.println("  - Test it in bruteforce section");
                System.out.println("  - https://blog.sucuri.net/2015/10/brute-force-amplification-attacks-against-wordpress-xmlrpc.html");
            }
        } catch (IOException e) {
            if (WPFrameworkSettings.isErrorLogging()) {
                System.err.println("Error checking XML-RPC endpoint: " + e.getMessage());
            }
        }
    }

}
