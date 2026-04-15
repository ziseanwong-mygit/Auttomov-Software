package main.java.backend.general.service;

import main.java.backend.repository.UserAccRepository;
import main.java.enums.TextFilePath;
import main.java.records.OperationResult;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class UserDataValidation {

    // Private constructor to prevent instantiation
    private UserDataValidation() {
        throw new UnsupportedOperationException("This class is a utility class, which cannot be instantiated.");
    }

    // Constants for data validation with regular expressions
    private static final Pattern ANY_UPPERCASE = Pattern.compile(".*[A-Z].*");
    private static final Pattern ANY_LOWERCASE = Pattern.compile(".*[a-z].*");
    private static final Pattern ANY_NUMBER = Pattern.compile(".*[0-9].*");
    private static final Pattern ANY_SPECIALCHAR = Pattern.compile(".*[!@#$%^&*(),.?:{}|<>].*");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^0[0-9]{8,10}$");
    private static final Pattern IC_PATTERN = Pattern.compile("^\\d{12}$");
    private static final Pattern PASSPORT_PATTERN = Pattern.compile("^[A-Z]{1,2}[0-9]{6,9}$");

    // Create a repository instance to collect all user accounts
    // Used to match the existing data with user's input to avoid duplicating records
    private static final UserAccRepository userAccRepository =
            new UserAccRepository(TextFilePath.CUSTOMER_ACCOUNT_FILE_PATH);

    /**
     * The validation process begins here
     * If all validations are successful, a true value and success message are returned.
     * Otherwise, a false value and a list of error messages are returned
     */
    public static OperationResult validateAllDetails(String userId, String emailAddress, String phoneNumber,
                                                     String password, String confirmPassword) {

        // Create a shared map to collect all error messages
        Map<String, String> allErrors = new HashMap<>();

        // Validate the user detail first, then if there are errors, collect them into the shared list
        collectErrors(allErrors, validateUserId(userId));
        collectErrors(allErrors, validateEmailAddress(emailAddress));
        collectErrors(allErrors, validatePhoneNumber(phoneNumber));
        collectErrors(allErrors, validatePassword(password, confirmPassword));

        // If all validations are successful, return the success message
        // Otherwise, return the map of error messages
        return allErrors.isEmpty()
                ? OperationResult.success("Field validation is successful. Proceed to account creation.")
                : OperationResult.failed("Field validation is unsuccessful. Please try again.", allErrors);
    }

    // For each validation, if there are errors, collect the field name and error message into the shared map
    private static void collectErrors(Map<String, String> allErrors, OperationResult result) {
        if (!result.isValid()) {
            allErrors.putAll(result.fieldErrors());
        }
    }

    // Validate user ID, email address, phone number, and password
    // Returns OperationResult record with result message
    private static OperationResult validateUserId(String userId) {

        if (userId == null || userId.isBlank()) {
            return OperationResult.failed("userID",
                    "User ID cannot be empty - either Malaysian IC or passport number");
        }

        if (!IC_PATTERN.matcher(userId).matches() && !PASSPORT_PATTERN.matcher(userId).matches()) {
            return OperationResult.failed("userID",
                    "User ID must be a valid Malaysian IC (e.g. 010101145678) " +
                            "or passport number (e.g. A123456789)");
        }

        if (userAccRepository.getUserAccByUserID(userId) != null) {
            return OperationResult.failed("userID",
                    "User ID already exists in the records");
        }

        return OperationResult.success();
    }

    private static OperationResult validateEmailAddress(String emailAddress) {

        if (emailAddress == null || emailAddress.isBlank()) {
            return OperationResult.failed("email",
                    "Email address cannot be empty");
        }

        if (!EMAIL_PATTERN.matcher(emailAddress).matches()) {
            return OperationResult.failed("email",
                    "Email address must be in a valid format (e.g. example123@email.com)");
        }

        if (userAccRepository.getUserAccByEmail(emailAddress) != null) {
            return OperationResult.failed("email",
                    "Email address already exists in the system");
        }

        return OperationResult.success();
    }

    private static OperationResult validatePhoneNumber(String phoneNumber) {

        if (phoneNumber == null || phoneNumber.isBlank()) {
            return OperationResult.failed("phoneNum",
                    "Phone number cannot be empty");
        }

        if (!PHONE_PATTERN.matcher(phoneNumber).matches()) {
            return OperationResult.failed("phoneNum",
                    "Phone number must be 9 to 11 digits and start with 0 (e.g. 0123456789)");
        }

        return OperationResult.success();
    }

    private static OperationResult validatePassword(String password, String confirmPassword) {

        if (password == null || password.isBlank()) {
            return OperationResult.failed("password",
                    "Password cannot be empty");
        }

        if (password.length() < 8 || password.length() > 16) {
            return OperationResult.failed("password",
                    "Password must be between 8 and 16 characters");
        }

        if (!ANY_UPPERCASE.matcher(password).matches()) {
            return OperationResult.failed("password",
                    "Password must contain at least one uppercase letter");
        }

        if (!ANY_LOWERCASE.matcher(password).matches()) {
            return OperationResult.failed("password",
                    "Password must contain at least one lowercase letter");
        }

        if (!ANY_NUMBER.matcher(password).matches()) {
            return OperationResult.failed("password",
                    "Password must contain at least one number");
        }

        if (!ANY_SPECIALCHAR.matcher(password).matches()) {
            return OperationResult.failed("password",
                    "Password must contain at least one special character: !@#$%^&*(),.?:{}|<>");
        }

        if (!password.equals(confirmPassword)) {
            return OperationResult.failed("password",
                    "Password do not match with confirm password");
        }

        return OperationResult.success();
    }
}
