package org.pwnpress.bruteforce;

import org.pwnpress.framework.WPFramework;
import org.pwnpress.framework.WPFrameworkSettings;

import java.io.IOException;
import java.util.Scanner;

public class WPBruteforce {

    public static void bruteforce() throws IOException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("\nPwnPress (bruteforce) > ");

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
            } else if (command.toLowerCase().startsWith("xml-rpc")) {
                XmlRpcBruteforce.xmlrpcBrute();
            } else if (command.toLowerCase().startsWith("directories")) {
                WPDirectoriesBruteforce.wpDirectoriesBrute();
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
        System.out.println("  xml-rpc              - Bruteforce WordPress abusing XML-RPC system.Multicall if enabled.");
        System.out.println("  directories          - Bruteforce common WordPress directories.");
        System.out.println();
        System.out.println("  back");
        System.out.println("  exit");

    }
}
