package org.pwnpress.framework;

import java.io.IOException;
import java.util.Scanner;

import org.pwnpress.exploit.WPExploit;
import org.pwnpress.phishing.WPPhishing;
import org.pwnpress.scanner.WPScanner;

public class WPFramework {

    public static void main(String[] args) throws IOException {
        System.out.print("""
        		
        		
                 ______             ______                      
                (_____ \\           (_____ \\                     
                 _____) ) _ _ ____  _____) )___ _____  ___  ___ 
                |  ____/ | | |  _ \\|  ____/ ___) ___ |/___)/___)
                | |    | | | | | | | |   | |   | ____|___ |___ |
                |_|     \\___/|_|FRAMEWORK V1.0.0_____|___/(___/ 
                                            by @amtzespinosa	 
                                            
                                                          
                            """);
        System.out.println("Type 'help' to see available commands.");
        framework();
    }

    public static void framework() throws IOException {
        Scanner scanner = new Scanner(System.in);
        // printHelp();

        String url = null;
        if (WPFrameworkSettings.getConstantUrl() != null) {
        	url = WPFrameworkSettings.getConstantUrl();
        }
        
        while (true) {
            System.out.print("\nPwnPress > ");
            String command = scanner.nextLine();

            if (command.equalsIgnoreCase("exit")) {
                System.out.println("Exiting PwnPress Framework. Goodbye and Happy Hacking!");
                scanner.close();
                System.exit(0);
            } else if (command.toLowerCase().startsWith("scan")) {
                String[] parts = command.split("\\s+");
                if (parts.length == 1) {
                    if (url != null) {
                        WPScanner.scan(url);
                    } else {
                    	System.out.println("Usage: scan <url>");
                        System.out.println("You can also set a constant <url> in settings.");
                    }
                } else if (parts.length == 2) {
                    url = parts[1];
                    WPScanner.scan(url);
                } else {
                    System.out.println("Usage: scan <url>");
                    System.out.println("You can also set a constant <url> in settings.");
                }
            } else if (command.toLowerCase().startsWith("phish")) {
            	WPPhishing.phish();
            } else if (command.toLowerCase().startsWith("exploit")) {
                WPExploit.exploit();
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
        System.out.println("  scan <url>           - Scan the specified WordPress URL.");
        System.out.println();
        System.out.println("  phish                - Create a WordPress login phishing page.");
        System.out.println("  brute force          - Brute force the specified WordPress page.");
        System.out.println("  exploit              - Try to exploit found or common vulnerabilities.");
        System.out.println();
        System.out.println("  settings");
        System.out.println("  exit");
        
    }
}
