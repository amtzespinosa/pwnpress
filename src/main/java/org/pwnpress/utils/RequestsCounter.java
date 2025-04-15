package org.pwnpress.utils;

public class RequestsCounter {

    private static int REQUESTS = 0;

    public static int getRequestsCount() {
        return REQUESTS;
    }

    public static void addRequest() {
        REQUESTS += 1;
    }

    public static void resetRequestCount() { REQUESTS = 0; }
}

// RequestsCounter.addRequest();