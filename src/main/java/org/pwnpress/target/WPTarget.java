package org.pwnpress.target;

import org.pwnpress.framework.WPFramework;
import org.pwnpress.framework.WPFrameworkSettings;
import java.io.IOException;
import java.util.Scanner;

public class WPTarget {

    public static void target() throws IOException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("\nPwnPress (target) > ");

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

                // WordPress validation

            } else if (command.toLowerCase().startsWith("validate")) {
                String[] parts = command.split("\\s+");
                if (parts.length != 2) {
                    System.out.println("Usage: validate <url/domain/file>");
                } else {
                    String domain = parts[1];
                    if (!domain.isEmpty()) {
                        WPValidator.validate(domain);
                    } else {
                        System.out.println("[-] Please specify a domain to validate.");
                    }
                }

                // WordPress status

            } else if (command.toLowerCase().startsWith("status")) {
                // Capture the command and process the file and flags
                String[] parts = command.split("\\s+");

                // Ensure we have a file and optional flags
                if (parts.length < 2) {
                    System.out.println("Usage: status <domain_file> [flags]");
                } else {
                    String domainFile = parts[1]; // Domain file path

                    // Flags (if provided)
                    String flags = "";
                    if (parts.length > 2) {
                        flags = command.substring(command.indexOf(parts[2])); // Get flags after the domain file
                    }

                    // Validate the flags: ensure --all/-a is not combined with any other flag
                    if (flags.contains("-a") || flags.contains("--all")) {
                        if (flags.trim().length() > 2) {
                            System.out.println("[-] The '--all' or '-a' flag cannot be used with any other flags.");
                            continue;  // Skip the current iteration and ask again
                        }
                    }

                    // Call WPStatus to check the status with the file and flags
                    WPStatus.statusCheck(domainFile, flags);
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
        System.out.println("  validate <url/domain/file>     - Checks if a single url or domain uses WordPress.");
        System.out.println("  status <domain_file> [flags]   - Checks version status of WordPress for the domains in the file.");
        System.out.println("                                Flags: --latest, --outdated, --insecure, --all, -a");
        System.out.println("  settings                      - Displays settings menu.");
        System.out.println("  back                          - Goes back to the main menu.");
        System.out.println("  exit                          - Exits the application.");
        System.out.println();
    }
}
