package org.pwnpress.scraper;

import org.pwnpress.framework.WPFramework;
import org.pwnpress.framework.WPFrameworkSettings;
import java.io.IOException;
import java.util.Scanner;

public class WPScraper {

    public static void scraper() throws IOException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("\nPwnPress (scraper) > ");

            if (!scanner.hasNextLine()) {
                System.out.println("\nNo input found. Exiting.");
                break;
            }

            String command = scanner.nextLine();

            if (command.equalsIgnoreCase("exit")) {
                System.out.println("\nExiting PwnPress Framework. Goodbye and Happy Hacking!");
                scanner.close();
                System.exit(0);
            } else if (command.equalsIgnoreCase("back")) {
                WPFramework.framework();
            } else if (command.toLowerCase().startsWith("scrape")) {
                DirectoryScraper.scrapeDirectory();
            } else if (command.toLowerCase().startsWith("settings")) {
                WPFrameworkSettings.settings();
            } else if (command.equalsIgnoreCase("help")) {
                printHelp();
            } else {
                System.out.println("\nUnknown command. Type 'help' for available commands.");
            }
        }

        // scanner.close();
    }

    private static void printHelp() {
        System.out.println("\nAvailable commands:\n");
        System.out.println("  scrape              - Recursively scrapes a given directory and prints all files found.");
        System.out.println();
        System.out.println("  settings");
        System.out.println("  back");
        System.out.println("  exit");

    }
}
