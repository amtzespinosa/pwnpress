package org.pwnpress.pingbacker;

import org.pwnpress.framework.WPFramework;
import org.pwnpress.framework.WPFrameworkSettings;
import org.pwnpress.scanner.WPAutoscan;

import java.io.IOException;
import java.util.Scanner;

public class WPPingbacker {

    public static void pingbacker() throws IOException {

        Scanner scanner = new Scanner(System.in);
        // printHelp();

        while (true) {
            System.out.print("\nPwnPress (pingbacker) > ");
            String command = scanner.nextLine();

            if (command.equalsIgnoreCase("exit")) {
                System.out.println("Exiting PwnPress Framework. Goodbye and Happy Hacking!");
                scanner.close();
                System.exit(0);
            } else if (command.equalsIgnoreCase("back")) {
                WPFramework.framework();
            } else if (command.toLowerCase().startsWith("webnet")) {
                String[] parts = command.split("\\s+");
                if (parts.length == 2) {
                    String file = parts[1];
                    WPWebnet.build(file);
                } else {
                    System.out.println("Usage: webnet <file>");
                }
            } else if (command.toLowerCase().startsWith("ddos")) {
                String[] parts = command.split("\\s+");
                if (parts.length == 2) {
                    String url = parts[1];
                    DDoS.attack(url);
                } else {
                    System.out.println("Usage: ddos <url>");
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
        System.out.println("  webnet <file>        - Build a file of sites that can perform requests to other sites.");
        System.out.println("  ddos <url>           - Use a webnet file to perform a DDoS attack.");
        System.out.println();
        System.out.println("  settings");
        System.out.println("  back");
        System.out.println("  exit");

    }
}