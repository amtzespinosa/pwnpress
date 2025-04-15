package org.pwnpress.scanner;

import java.io.IOException;
import java.util.Scanner;
import org.pwnpress.framework.WPFramework;
import org.pwnpress.framework.WPFrameworkSettings;

public class WPScanner {

    public static void scan() throws IOException {

        Scanner scanner = new Scanner(System.in);
        // printHelp();

        String url = null;
        if (WPFrameworkSettings.getConstantUrl() != null) {
            url = WPFrameworkSettings.getConstantUrl();
        }

        while (true) {
            System.out.print("\nPwnPress (scanner) > ");
            String command = scanner.nextLine();

            if (command.equalsIgnoreCase("exit")) {
                System.out.println("Exiting PwnPress Framework. Goodbye and Happy Hacking!");
                scanner.close();
                System.exit(0);
            } else if (command.equalsIgnoreCase("back")) {
                WPFramework.framework();
            } else if (command.toLowerCase().startsWith("scan")) {
                String[] parts = command.split("\\s+");
                if (parts.length == 1) {
                    if (url != null) {
                        WPAutoscan.scan(url);
                    } else {
                        System.out.println("Usage: scan <url>");
                        System.out.println("You can also set a constant <url> in settings.");
                    }
                } else if (parts.length == 2) {
                    url = parts[1];
                    WPAutoscan.scan(url);
                } else {
                    System.out.println("Usage: scan <url>");
                    System.out.println("You can also set a constant <url> in settings.");
                }
            } else if (command.toLowerCase().startsWith("settings")) {
                WPFrameworkSettings.settings();
            } else if (command.equalsIgnoreCase("help")) {
                printHelp();
            } else {
                System.out.println("\nUnknown command. Type 'help' for available commands.");
            }
        }
    }

    private static void printHelp() {
        System.out.println("\nAvailable commands:\n");
        System.out.println("  scan <url>           - Perform a regular scan over the specified WordPress URL.");
        System.out.println("  deep-scan <url>      - [Not implemented yet] Perform a deep scan over the specified WordPress URL (requires user interaction).");
        System.out.println();
        System.out.println("  settings");
        System.out.println("  back");
        System.out.println("  exit");

    }
}