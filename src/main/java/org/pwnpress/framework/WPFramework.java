package org.pwnpress.framework;

import java.io.IOException;
import java.util.Scanner;

import org.pwnpress.bruteforce.WPBruteforce;
import org.pwnpress.exploit.WPExploit;
import org.pwnpress.phisher.WPPhisher;
import org.pwnpress.pingbacker.WPPingbacker;
import org.pwnpress.scanner.WPScanner;
import org.pwnpress.scraper.WPScraper;
import org.pwnpress.target.WPTarget;

/**
 * Entry point for the PwnPress Framework.
 *
 * This class contains the main method which acts as the launcher for
 * interacting with the available WordPress auditing and exploitation modules.
 *
 * Author: @amtzespinosa
 * Version: 1.0.0
 */
public class WPFramework {

    /**
     * Displays the framework banner and starts the command loop.
     *
     * @param args command-line arguments (not used)
     * @throws IOException if an input/output error occurs
     */
    public static void main(String[] args) throws IOException {
        // Framework ASCII banner
        System.out.print("""
                
                
                 ______             ______                      
                (_____ \\           (_____ \\                     
                 _____) ) _ _ ____  _____) )___ _____  ___  ___ 
                |  ____/ | | |  _ \\|  ____/ ___) ___ |/___)/___)
                | |    | | | | | | | |   | |   | ____|___ |___ |
                |_|     \\___/|_|FRAMEWORK V1.2.0_____|___/(___/ 
                                            by @amtzespinosa     
                                                          
                """);

        System.out.println("Type 'help' to see available commands.");
        framework(); // Enter command loop
    }

    /**
     * Starts the interactive framework command prompt loop.
     *
     * Allows users to run modules like scanner, phisher, bruteforce, etc.
     * Reads user input and dispatches to the corresponding module.
     */
    public static void framework() throws IOException {
        Scanner scanner = new Scanner(System.in);
        String url = WPFrameworkSettings.getConstantUrl();

        while (true) {
            System.out.print("\nPwnPress > ");
            String command = scanner.nextLine().trim();

            // Exit condition
            if (command.equalsIgnoreCase("exit")) {
                System.out.println("Exiting PwnPress Framework. Goodbye and Happy Hacking!");
                scanner.close();
                System.exit(0);

                // Command dispatch
            } else if (command.startsWith("scanner")) {
                WPScanner.scan();
            } else if (command.startsWith("phisher")) {
                WPPhisher.phish();
            } else if (command.startsWith("bruteforce")) {
                WPBruteforce.bruteforce();
            } else if (command.startsWith("pingbacker")) {
                WPPingbacker.pingbacker();
            } else if (command.startsWith("exploit")) {
                WPExploit.exploit();
            } else if (command.startsWith("target")) {
                WPTarget.target();
            } else if (command.startsWith("scraper")) {
                WPScraper.scraper();
            } else if (command.startsWith("settings")) {
                WPFrameworkSettings.settings();
            } else if (command.equalsIgnoreCase("help")) {
                printHelp();
            } else {
                System.out.println("\nUnknown command. Type 'help' for available commands.");
            }
        }
    }

    /**
     * Prints the available commands and module statuses.
     */
    private static void printHelp() {
        System.out.println("\n[!] WARNING:");
        System.out.println("Some modules are not usable yet but you can test them.");
        System.out.println("Working modules: scanner, target, bruteforce");
        System.out.println("Fairly working modules: scraper, phisher");
        System.out.println("Modules under heavy dev: exploit, pingbacker\n");

        System.out.println("Available commands:\n");
        System.out.println("  scanner      - Scan the specified WordPress URL/domain.");
        System.out.println("  target       - Validate and extract WP versions and status.");
        System.out.println("  scraper      - Scrape the specified WordPress directory URL.");
        System.out.println("  phisher      - Create a WordPress login phishing page.");
        System.out.println("  bruteforce   - Brute force the specified WordPress page.");
        System.out.println("  pingbacker   - Leverage XML-RPC pingback to perform DDoS attacks.");
        System.out.println("  exploit      - Try to exploit found or common vulnerabilities.");
        System.out.println();
        System.out.println("  settings     - Modify framework settings.");
        System.out.println("  help         - Show this help message.");
        System.out.println("  exit         - Exit the framework.");
    }
}
