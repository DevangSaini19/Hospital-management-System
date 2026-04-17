package utils;

import java.util.regex.Pattern;

/**
 * Utility class for input validation
 * Provides methods to validate various input types used in the Hospital Management System
 */
public class ValidationUtil {
    
    // Regular expressions for validation
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[0-9]{10}$");
    private static final Pattern NAME_PATTERN = 
        Pattern.compile("^[a-zA-Z\\s'-]{2,50}$");

    /**
     * Validate patient first name
     * @param firstName First name to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidName(String firstName) {
        return firstName != null && NAME_PATTERN.matcher(firstName).matches();
    }

    /**
     * Validate phone number (10 digits)
     * @param phone Phone number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Validate email address
     * @param email Email to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validate age (between 1 and 150)
     * @param age Age to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidAge(int age) {
        return age > 0 && age < 150;
    }

    /**
     * Validate blood group
     * @param bloodGroup Blood group to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidBloodGroup(String bloodGroup) {
        String[] validGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        for (String group : validGroups) {
            if (group.equals(bloodGroup)) return true;
        }
        return false;
    }

    /**
     * Validate gender
     * @param gender Gender to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidGender(String gender) {
        return gender != null && (gender.equals("Male") || gender.equals("Female") || gender.equals("Other"));
    }

    /**
     * Trim and validate non-empty string
     * @param str String to validate
     * @return trimmed string if valid, null otherwise
     */
    public static String sanitizeString(String str) {
        if (str == null) return null;
        String trimmed = str.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
