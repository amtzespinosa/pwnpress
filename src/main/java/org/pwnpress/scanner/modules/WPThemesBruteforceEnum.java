package org.pwnpress.scanner.modules;

import org.pwnpress.framework.WPFrameworkSettings;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WPThemesBruteforceEnum {

    public static void bruteforceThemes(String url) {

    }
    private static List<String> loadThemesFromSettings(String themesFilePath) {
        List<String> themesList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(themesFilePath))) {
            String theme;
            while ((theme = br.readLine()) != null) {
                themesList.add(theme.trim());
            }
        } catch (IOException e) {
            System.out.println(" |- An error occurred while loading themes from settings: " + e.getMessage());
        }
        return themesList;
    }

}
