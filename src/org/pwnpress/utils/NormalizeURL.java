package org.pwnpress.utils;

public class NormalizeURL {
	public static String normalize(String url) {
        // Add https:// if no protocol is specified
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }

        // Add trailing slash if not present
        if (!url.endsWith("/")) {
            url += "/";
        }

        return url;
    }
}
