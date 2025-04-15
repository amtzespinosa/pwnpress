package org.pwnpress.framework;

import java.io.IOException;
import java.util.Scanner;

/**
 * Handles the configurable runtime settings for the PwnPress framework.
 * Provides a command-line interface to update options such as target URL,
 * bruteforce toggles, dictionary paths, autopwn settings, proxy usage, and more.
 */
public class WPFrameworkSettings {

    /** Default port used by the framework's internal server */
    private static final int PORT = 666;

    /** Target URL for the scan */
    private static String URL = null;

    /** Path to default password list for XML-RPC bruteforce */
    private static String XMLRPC_BRUTEFORCE_PASSWORDS_WORDLIST = "dictionaries/default-passwords.txt";

    /** Path to the file containing known WP directories */
    private static String WPDIRECTORIES_WORDLIST = "dictionaries/wpdirectories.txt";

    /** Toggle for bruteforcing themes */
    private static boolean THEMES_BRUTEFORCE = false;

    /** Toggle for bruteforcing plugins */
    private static boolean PLUGINS_BRUTEFORCE = false;

    /** Path to the themes dictionary file */
    private static String themesFilePath = "dictionaries/themes.txt";

    /** Path to the plugins dictionary file */
    private static String pluginsFilePath = "dictionaries/plugins.txt";

    /** Wordfence vulnerability feed type: "production" or "scanner" */
    private static String FEED = "production";

    /** Publicly exposed API endpoint based on the selected feed */
    public static String API_URL = "https://www.wordfence.com/api/intelligence/v2/vulnerabilities/" + FEED;

    /** Local cache file for storing Wordfence API data */
    public static String CACHE_FILE_PATH = "wordfence_cache.json";

    /** Toggle for automatic exploitation mode */
    private static boolean autopwn = false;

    /** Number of threads to use during scanning operations */
    private static int numThreads = 15;

    /** Toggle for enabling proxy */
    private static boolean proxy = false;

    /** Toggle for error logging (currently unused) */
    private static boolean ERROR_LOGGING = false;

    /**
     * Main interactive method for handling settings via CLI.
     * Accepts commands and updates settings accordingly.
     */
    public static void settings() throws IOException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("\nPwnPress (settings) > ");
            String command = scanner.nextLine();

            if (command.equalsIgnoreCase("exit")) {
                System.out.println("\nExiting PwnPress Framework. Goodbye and Happy Hacking!");
                break;
            } else if (command.equalsIgnoreCase("back")) {
                WPFramework.framework();
                break;
            } else if (command.toLowerCase().startsWith("set url")) {
                setConstantUrl(command);
            } else if (command.toLowerCase().startsWith("set bruteforce themes")) {
                setThemesBrutefoce(command);
            } else if (command.toLowerCase().startsWith("set themes dictionary")) {
                setThemesFilePath(command);
            } else if (command.toLowerCase().startsWith("set bruteforce plugins")) {
                setPluginsBrutefoce(command);
            } else if (command.toLowerCase().startsWith("set plugins dictionary")) {
                setPluginsFilePath(command);
            } else if (command.toLowerCase().startsWith("set feed")) {
                setFeed(command);
            } else if (command.toLowerCase().startsWith("set autopwn")) {
                setAutopwn(command);
            } else if (command.toLowerCase().startsWith("set threads")) {
                setNumThreads(command);
            } else if (command.toLowerCase().startsWith("set proxy")) {
                setProxy(command);
            } else if (command.equalsIgnoreCase("help")) {
                printHelp();
            } else {
                System.out.println("\nUnknown command. Type 'help' for available commands.");
            }
        }

        scanner.close();
    }

    /** Displays all available settings and their current values. */
    private static void printHelp() {
        System.out.println("\nAvailable settings:\n");
        if (URL != null) {
            System.out.println("  set url <url>                   - Current constant URL: " + URL);
        } else {
            System.out.println("  set url <url>                   - Set constant URL to use with PwnPress Framework.");
        }
        System.out.println("  set bruteforce themes           - Enable or disable themes bruteforce. Current: " + (THEMES_BRUTEFORCE ? "enabled" : "disabled"));
        System.out.println("  set themes dictionary <path>    - Set the path to the themes dictionary file. Current: " + themesFilePath);
        System.out.println("  set plugins dictionary <path>   - Set the path to the plugins dictionary file. Current: " + pluginsFilePath);
        System.out.println("  set feed <production/scanner>   - Set which WordFence vulnerability feed to use. Current: " + FEED);
        System.out.println("  set autopwn <enabled/disabled>  - Enable or disable autopwn. Current: " + (autopwn ? "enabled" : "disabled"));
        System.out.println("  set threads <number>            - Set the number of threads for plugin scanning. Current: " + numThreads);
        System.out.println("  set proxy <enabled/disabled>    - Enable or disable proxy. Current: " + (proxy ? "enabled" : "disabled"));
        System.out.println();
        System.out.println("  back");
        System.out.println("  exit");
    }

    // ----------- Individual setting handlers ------------

    /** Parses and sets the constant URL used during scanning. */
    private static void setConstantUrl(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length != 3) {
            System.out.println("Usage: set url <url>");
        } else {
            URL = parts[2];
            System.out.println("Constant URL set to: " + URL);
        }
    }

    /** Enables or disables theme bruteforce mode. */
    private static void setThemesBrutefoce(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length != 4) {
            System.out.println("Usage: set bruteforce themes <enabled/disabled>");
        } else {
            THEMES_BRUTEFORCE = parts[3].equalsIgnoreCase("enabled");
            System.out.println("Themes bruteforce " + (THEMES_BRUTEFORCE ? "enabled." : "disabled."));
        }
    }

    /** Updates the file path for the themes dictionary. */
    private static void setThemesFilePath(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length != 4) {
            System.out.println("Usage: set themes dictionary <path>");
        } else {
            themesFilePath = parts[3];
            System.out.println("Themes dictionary path set to: " + themesFilePath);
        }
    }

    /** Enables or disables plugin bruteforce mode. */
    private static void setPluginsBrutefoce(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length != 4) {
            System.out.println("Usage: set bruteforce plugins <enabled/disabled>");
        } else {
            PLUGINS_BRUTEFORCE = parts[3].equalsIgnoreCase("enabled");
            System.out.println("Plugins bruteforce " + (PLUGINS_BRUTEFORCE ? "enabled." : "disabled."));
        }
    }

    /** Updates the file path for the plugins dictionary. */
    private static void setPluginsFilePath(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length != 4) {
            System.out.println("Usage: set plugins dictionary <path>");
        } else {
            pluginsFilePath = parts[3];
            System.out.println("Plugins dictionary path set to: " + pluginsFilePath);
        }
    }

    /** Sets the vulnerability feed source (production or scanner). */
    private static void setFeed(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length != 3) {
            System.out.println("Usage: set feed <production/scanner>");
        } else {
            String newFeed = parts[2];
            if (newFeed.equalsIgnoreCase("production") || newFeed.equalsIgnoreCase("scanner")) {
                FEED = newFeed;
                System.out.println("Feed set to: " + FEED);
            } else {
                System.out.println("Invalid feed value. Use 'production' or 'scanner'.");
            }
        }
    }

    /** Enables or disables autopwn mode. */
    private static void setAutopwn(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length != 3) {
            System.out.println("Usage: set autopwn <enabled/disabled>");
        } else {
            autopwn = parts[2].equalsIgnoreCase("enabled");
            System.out.println("Autopwn " + (autopwn ? "enabled." : "disabled."));
        }
    }

    /** Sets the number of threads for scanning tasks. */
    private static void setNumThreads(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length != 3) {
            System.out.println("Usage: set threads <number>");
        } else {
            try {
                int threads = Integer.parseInt(parts[2]);
                if (threads > 0) {
                    numThreads = threads;
                    System.out.println("Number of threads set to: " + numThreads);
                } else {
                    System.out.println("Number of threads must be greater than zero.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please enter a valid integer.");
            }
        }
    }

    /** Enables or disables proxy mode. */
    private static void setProxy(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length != 3) {
            System.out.println("Usage: set proxy <enabled/disabled>");
        } else {
            proxy = parts[2].equalsIgnoreCase("enabled");
            System.out.println("Proxy " + (proxy ? "enabled." : "disabled."));
        }
    }

    // ----------- Getters (used by other components) ------------

    public static String getConstantUrl() { return URL; }

    public static boolean isThemesBruteforce() { return THEMES_BRUTEFORCE; }

    public static boolean isPluginsBrutefoce() { return PLUGINS_BRUTEFORCE; }

    public static String getThemesFilePath() { return themesFilePath; }

    public static String getXmlrpcBruteforcePasswordsWordlist() { return XMLRPC_BRUTEFORCE_PASSWORDS_WORDLIST; }

    public static String getWpDirectoriesWordlist() { return WPDIRECTORIES_WORDLIST; }

    public static String getPluginsFilePath() { return pluginsFilePath; }

    public static String getFeed() { return FEED; }

    public static boolean isAutopwn() { return autopwn; }

    public static int getNumThreads() { return numThreads; }

    public static boolean isProxy() { return proxy; }

    public static boolean isErrorLogging() { return ERROR_LOGGING; }

    public static int getServerPort() { return PORT; }
}
