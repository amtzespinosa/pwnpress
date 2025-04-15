package org.pwnpress.scanner.modules;
import org.pwnpress.framework.WPFrameworkSettings;
import org.pwnpress.utils.HttpRequest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MustUsePluginsChecker {

    public static void checkMustUsePlugins(String urlStr) {
        try {
            HttpURLConnection connection = HttpRequest.getRequest(urlStr);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("\n[+] This site has 'Must Use Plugins'");
                System.out.println(" └─ Location: " + urlStr + "wp-content/mu-plugins/");
            } 
        } catch (IOException e) {
            if (WPFrameworkSettings.isErrorLogging()) {
                System.err.println("\nError checking 'Must Use Plugins' directory: " + e.getMessage());
            }
        }
    }
}
