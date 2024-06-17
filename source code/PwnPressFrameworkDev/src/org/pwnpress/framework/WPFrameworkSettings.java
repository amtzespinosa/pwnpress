package org.pwnpress.framework;

import java.util.Scanner;

public class WPFrameworkSettings {
	
    private static String constantUrl = null;

    public static void settings() {
        Scanner scanner = new Scanner(System.in);
        // printHelp();
        
        // boolean settings = true;

        while (/*settings=*/true) {
        	System.out.print("\nPwnPress (settings) > ");
            String command = scanner.nextLine();

            if (command.equalsIgnoreCase("exit")) {
                System.out.println("\nExiting PwnPress Framework. Goodbye and Happy Hacking!");
                break;
                // settings=false;
            } else if (command.equalsIgnoreCase("back")) {
                WPFramework.framework();
                break;
                // settings=false;
            } else if (command.toLowerCase().startsWith("set url")) {
                setConstantUrl(command);
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
        System.out.println("  set url <url>        - Set constant URL to use with PwnPress Framework.");
        System.out.println("  WordFence vuln feed  - Default: production.");
        System.out.println("  autopwn		       - Try to exploit any vulnerability found.");
        System.out.println();
        System.out.println("  back");
        System.out.println("  exit");
    }

    private static void setConstantUrl(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length != 3) {
            System.out.println("Usage: set <url>");
        } else {
            String url = parts[2];
            constantUrl = url;
            System.out.println("Constant URL set to: " + constantUrl);
        }
    }

    public static String getConstantUrl() {
        return constantUrl;
    }
}
