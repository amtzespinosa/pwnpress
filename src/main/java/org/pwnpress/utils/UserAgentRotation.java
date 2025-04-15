package org.pwnpress.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class UserAgentRotation {

    private static final List<String> USER_AGENTS = new ArrayList<>();
    private static final AtomicInteger counter = new AtomicInteger(0);

    static {
        // Preload commonly used user agents
        USER_AGENTS.add("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");
        USER_AGENTS.add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");
        USER_AGENTS.add("Mozilla/5.0 (iPhone; CPU iPhone OS 15_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.5 Mobile/15E148 Safari/604.1");
        USER_AGENTS.add("Mozilla/5.0 (Linux; Android 11; SM-G991U) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36");
        USER_AGENTS.add("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");
        USER_AGENTS.add("WordPress/6.6.2;");
        USER_AGENTS.add("Mozilla/5.0 (compatible; Google-Site-Verification/1.0)");
        USER_AGENTS.add("Mozilla/5.0 (Linux; U; Android 4.4.2; en-US; HM NOTE 1W Build/KOT49H) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 UCBrowser/11.0.5.850 U3/0.8.0 Mobile Safari/534.30");
        USER_AGENTS.add("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)");
        USER_AGENTS.add("Mozilla/5.0 (Linux; Android 6.0.1; Nexus 5X Build/MMB29P) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)");
        USER_AGENTS.add("Mozilla/5.0 (X11; Linux x86_64; rv:40.0) Gecko/20100101 Firefox/40.0");
        // Add more user agents as needed
    }

    /**
     * Get the next user agent in rotation.
     *
     * @return A user agent string.
     */
    public static String getNextUserAgent() {
        if (USER_AGENTS.isEmpty()) {
            throw new IllegalStateException("User agent pool is empty. Add user agents before usage.");
        }
        int index = counter.getAndIncrement() % USER_AGENTS.size();
        return USER_AGENTS.get(index);
    }

    /**
     * Add a new user agent to the pool.
     *
     * @param userAgent The user agent string to add.
     */
    public static void addUserAgent(String userAgent) {
        if (userAgent != null && !userAgent.trim().isEmpty()) {
            USER_AGENTS.add(userAgent.trim());
        }
    }

    /**
     * Remove all user agents from the pool.
     */
    public static void clearUserAgents() {
        USER_AGENTS.clear();
    }

    /**
     * Add multiple user agents to the pool.
     *
     * @param userAgents A list of user agent strings to add.
     */
    public static void addUserAgents(List<String> userAgents) {
        for (String userAgent : userAgents) {
            addUserAgent(userAgent);
        }
    }

    /**
     * Get the total number of user agents in the pool.
     *
     * @return The number of user agents.
     */
    public static int getUserAgentCount() {
        return USER_AGENTS.size();
    }
}
