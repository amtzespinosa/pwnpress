package org.pwnpress.scanner.modules;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WPVersion {

    public static String extractWordPressVersion(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder htmlContent = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                htmlContent.append(line);
            }
            reader.close();

            String html = htmlContent.toString();
            String version = extractVersionFromMetaTags(html);

            if (version != null) {
                System.out.println("\n[+] WordPress version " + version + " identified.");
                System.out.println(" |- Found By: Meta Generator (Passive Detection)");
                System.out.println("  - Match: " + getVersionMatch(html, version));
                if (version != null) {
                	String versionConf = extractVersionFromSource(html);
                	if (versionConf != null && versionConf.equals(version)) {
	                	System.out.println("    - Confirmed By: Source Code (Passive Detection)");
	                    return versionConf;
                	} else {
                		versionConf = extractVersionFromRSS(html);
                		if (versionConf != null && versionConf.equals(version)) {
    	                	System.out.println("    - Confirmed By: Source Code (Passive Detection)");
    	                    return versionConf;
                		} else {
                			System.out.println("    - Version detected but not confirmed.");
                		}
                	}
                }
            }

            if (version != null) {
            	version = extractVersionFromSource(html);
                System.out.println("\n[+] WordPress version " + version + " identified.");
                System.out.println(" |- Found By: Source Code (Passive Detection)");
                System.out.println("  - Match: " + getVersionMatch(html, version));
                if (version != null) {
                	String versionConf = extractVersionFromMetaTags(html);
                	if (versionConf != null && versionConf.equals(version)) {
	                	System.out.println(" |- Confirmed By: Source Code (Passive Detection)");
	                    return versionConf;
                	} else {
                		versionConf = extractVersionFromRSS(html);
                		if (versionConf != null && versionConf.equals(version)) {
    	                	System.out.println(" |- Confirmed By: Source Code (Passive Detection)");
    	                    return versionConf;
                		} else {
                			System.out.println(" |- Version detected but not confirmed.");
                		}
                	}
                }
            }

            if (version != null) {
            	version = extractVersionFromRSS(html);
                System.out.println("\n[+] WordPress version " + version + " identified.");
                System.out.println(" |- Found By: RSS (Passive Detection)");
                System.out.println("  - Match: " + getVersionMatch(html, version));
                if (version != null) {
                	String versionConf = extractVersionFromMetaTags(html);
                	if (versionConf != null && versionConf.equals(version)) {
	                	System.out.println(" |- Confirmed By: Source Code (Passive Detection)");
	                    return versionConf;
                	} else {
                		versionConf = extractVersionFromSource(html);
                		if (versionConf != null && versionConf.equals(version)) {
    	                	System.out.println(" |- Confirmed By: Source Code (Passive Detection)");
    	                    return versionConf;
                		} else {
                			System.out.println(" |- Version detected but not confirmed.");
                		}
                	}
                }
            }

            return null;
        } catch (IOException e) {
            System.err.println("Error fetching URL content: " + e.getMessage());
            return null;
        }
    }

    public static String extractVersionFromMetaTags(String html) {
        Pattern pattern = Pattern.compile("<meta name=\"generator\" content=\"WordPress (\\d+\\.\\d+\\.\\d+)");
        Matcher matcher = pattern.matcher(html);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public static String extractVersionFromRSS(String html) {
        Pattern pattern = Pattern.compile("<generator>https?://wordpress.org/?\\?v=(\\d+\\.\\d+\\.\\d+)");
        Matcher matcher = pattern.matcher(html);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public static String extractVersionFromSource(String html) {
        Pattern pattern = Pattern.compile("\\?ver=([0-9]+\\.[0-9]+\\.[0-9]+)");
        Matcher matcher = pattern.matcher(html);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public static String getVersionMatch(String html, String version) {
        Pattern patternMetaTags = Pattern.compile("<meta name=\"generator\" content=\"WordPress " + version + "[^\"]*\"");
        Matcher matcherMetaTags = patternMetaTags.matcher(html);

        if (matcherMetaTags.find()) {
            return matcherMetaTags.group();
        }

        Pattern patternRSS = Pattern.compile("<generator>https?://wordpress.org/\\?v=" + version);
        Matcher matcherRSS = patternRSS.matcher(html);

        if (matcherRSS.find()) {
            return matcherRSS.group();
        }

        Pattern patternSource = Pattern.compile("\\?ver=" + version);
        Matcher matcherSource = patternSource.matcher(html);

        if (matcherSource.find()) {
            return matcherSource.group();
        }

        return null;
    }

    public static String extractWordPressVersionOnly(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder htmlContent = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                htmlContent.append(line);
            }
            reader.close();

            String html = htmlContent.toString();
            
            String version = extractVersionFromMetaTags(html);
            if (version == null) {
            	version = extractVersionFromRSS(html);
            	if (version == null) {
                	version = extractVersionFromSource(html);
                }
            }
            return version;
            
            
        } catch (IOException e) {
            System.err.println("Error fetching URL content: " + e.getMessage());
            return null;
        }
        

    }


}
