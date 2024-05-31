package org.pwnpress.utils;

public class CustomFormat {
	
	// Method to pad string to the right with spaces to given length
    public static String padRight(String s, int length) {
        return String.format("%-" + length + "s", s);
    }
    
    // Method to determine color based on vulnerability rating
    public static String getColor(String rank) {

        // ANSI escape codes for text color
    	final String ANSI_GREEN = "\u001B[38;5;120m"; // Brighter Light Green
    	final String ANSI_BLUE = "\u001B[38;5;153m"; // Brighter Light Blue
        
        switch (rank.toLowerCase()) {
            case "low":
            	return "";
            case "manual":
            	return "";
            case "normal":
            	return "";
            case "great":
                return ANSI_BLUE;
            case "excellent":
                return ANSI_GREEN;
            default:
                return ""; // Default color
        }
    }
    
    // Method to determine color based on vulnerability rating
    public static String getColorForRating(String rating) {

        // ANSI escape codes for text color
    	final String ANSI_RED = "\u001B[91m"; // Bright Red
    	final String ANSI_ORANGE = "\u001B[38;5;208m"; // Orange
    	final String ANSI_GREEN = "\u001B[92m"; // Bright Green
    	final String ANSI_YELLOW = "\u001B[93m"; // Bright Yellow
        
        switch (rating.toLowerCase()) {
            case "critical":
                return ANSI_RED;
            case "high":
                return ANSI_ORANGE;
            case "medium":
                return ANSI_YELLOW;
            case "low":
                return ANSI_GREEN;
            default:
                return ""; // Default color
        }
    }
}
