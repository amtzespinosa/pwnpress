package org.pwnpress.scanner.modules;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;

public class DatabaseDumpChecker {

    public static boolean checkDatabaseDumps(String url) {
        try {
            URL dbDumpURL = new URL(url + "wp-content/uploads/database.sql");
            HttpURLConnection connection = (HttpURLConnection) dbDumpURL.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Database dump that may be publicly accessible found.");
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }
}
