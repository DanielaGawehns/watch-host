package util;

import java.util.regex.Pattern;

/**
 * Class for validating IP address strings
 */
public class IpValidator {

    /**
     * Regex to validate a substring of a number between 0 and 255
     */
    private static final String zeroTo255
            = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])";

    /**
     * Regex to validate string layout for IP adress
     */
    private static final String IP_REGEXP
            = zeroTo255 + "\\." + zeroTo255 + "\\."
            + zeroTo255 + "\\." + zeroTo255;


    /**
     * Compiles the regex to check for proper layout
     */
    private static final Pattern IP_PATTERN
            = Pattern.compile(IP_REGEXP);


    /**
     * Checks if the string contains a valid IP address
     * @param address String containing the address
     * @return True if valid. False if not
     */
    public boolean isValid(String address) {
        return IP_PATTERN.matcher(address).matches();
    }
}
