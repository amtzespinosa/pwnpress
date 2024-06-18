package org.pwnpress.framework;

import java.io.IOException;
import java.util.Scanner;

public class WPFrameworkSettings {

    private static String constantUrl = null;
    private static String themesFilePath = "resources/themes.txt";
    private static String pluginsFilePath = "resources/plugins.txt";
    private static String feed = "production";
    private static boolean autopwn = false;
    private static int numThreads = 10; // Default number of threads for scanning
    private static boolean proxy = false;

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
            } else if (command.toLowerCase().startsWith("set themes dictionary")) {
                setThemesFilePath(command);
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

    private static void printHelp() {
        System.out.println("\nAvailable settings:\n");
        if (constantUrl != null) {
            System.out.println("  set url <url>                   - Current constant URL: " + constantUrl);
        } else {
            System.out.println("  set url <url>                   - Set constant URL to use with PwnPress Framework.");
        }
        System.out.println("  set themes dictionary <path>    - Set the path to the themes dictionary file. Current: " + themesFilePath);
        System.out.println("  set plugins dictionary <path>   - Set the path to the plugins dictionary file. Current: " + pluginsFilePath);
        System.out.println("  set feed <production/scanner>   - Set which WordFence vulnerability feed to use. Current: " + feed);
        System.out.println("  set autopwn <enabled/disabled>  - Enable or disable autopwn. Current: " + (autopwn ? "enabled" : "disabled"));
        System.out.println("  set threads <number>            - Set the number of threads for plugin scanning. Current: " + numThreads);
        System.out.println("  set proxy <enabled/disabled>    - Enable or disable proxy. Current: " + (proxy ? "enabled" : "disabled"));
        System.out.println();
        System.out.println("  back");
        System.out.println("  exit");
    }

    private static void setConstantUrl(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length != 3) {
            System.out.println("Usage: set url <url>");
        } else {
            String url = parts[2];
            constantUrl = url;
            System.out.println("Constant URL set to: " + constantUrl);
        }
    }

    private static void setThemesFilePath(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length != 4) {
            System.out.println("Usage: set themes dictionary <path>");
        } else {
            String path = parts[3];
            themesFilePath = path;
            System.out.println("Themes dictionary path set to: " + themesFilePath);
        }
    }

    private static void setPluginsFilePath(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length != 4) {
            System.out.println("Usage: set plugins dictionary <path>");
        } else {
            String path = parts[3];
            pluginsFilePath = path;
            System.out.println("Plugins dictionary path set to: " + pluginsFilePath);
        }
    }

    private static void setFeed(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length != 3) {
            System.out.println("Usage: set feed <production/scanner>");
        } else {
            String newFeed = parts[2];
            if (newFeed.equalsIgnoreCase("production") || newFeed.equalsIgnoreCase("scanner")) {
                feed = newFeed;
                System.out.println("Feed set to: " + feed);
            } else {
                System.out.println("Invalid feed value. Use 'production' or 'scanner'.");
            }
        }
    }

    private static void setAutopwn(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length != 3) {
            System.out.println("Usage: set autopwn <enabled/disabled>");
        } else {
            String value = parts[2];
            if (value.equalsIgnoreCase("enabled")) {
                autopwn = true;
                System.out.println("Autopwn enabled.");
            } else if (value.equalsIgnoreCase("disabled")) {
                autopwn = false;
                System.out.println("Autopwn disabled.");
            } else {
                System.out.println("Invalid autopwn value. Use 'enabled' or 'disabled'.");
            }
        }
    }

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
    
    private static void setProxy(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length != 3) {
            System.out.println("Usage: set proxy <enabled/disabled>");
        } else {
            String value = parts[2];
            if (value.equalsIgnoreCase("enabled")) {
            	proxy = true;
                System.out.println("Proxy enabled.");
            } else if (value.equalsIgnoreCase("disabled")) {
            	proxy = false;
                System.out.println("Proxy disabled.");
            } else {
                System.out.println("Invalid proxy value. Use 'enabled' or 'disabled'.");
            }
        }
    }

    public static String getConstantUrl() {
        return constantUrl;
    }

    public static String getThemesFilePath() {
        return themesFilePath;
    }

    public static String getPluginsFilePath() {
        return pluginsFilePath;
    }

    public static String getFeed() {
        return feed;
    }

    public static boolean isAutopwn() {
        return autopwn;
    }

    public static int getNumThreads() {
        return numThreads;
    }
    
    public static boolean isProxy() {
        return proxy;
    }
}
