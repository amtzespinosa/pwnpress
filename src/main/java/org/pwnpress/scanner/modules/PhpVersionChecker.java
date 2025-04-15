package org.pwnpress.scanner.modules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhpVersionChecker {

    private static String headerValue;

    public static void storeHeaderValue(String header) {
        headerValue = header;
    }

    private static String extractPhpVersion(String headerValue) {
        return regexMatcher(headerValue, "PHP/(\\d+\\.\\d+\\.\\d+)");
    }

    private static String regexMatcher(String input, String pattern) {
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(input);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public static void getPhpVersion() {
        if (headerValue != null) {
            String phpVersion = extractPhpVersion(headerValue);
            System.out.println("\n[+] PHP version: " + phpVersion);
        }

    }
}
