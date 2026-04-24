package main.java.backend.general.service;

import main.java.backend.repository.UserAccRepository;
import main.java.enums.RegexPattern;
import main.java.enums.Role;
import main.java.enums.TextFilePath;
import main.java.records.OperationResult;

import java.util.HashMap;
import java.util.Map;

public class UserAccValidation {

    // Private constructor to prevent instantiation
    private UserAccValidation() {
        throw new UnsupportedOperationException("This class is a utility class, which cannot be instantiated.");
    }

    // Create a repository instance to collect all user accounts
    // Used to match the existing data with user's input to avoid duplicated data
    private static final UserAccRepository customerAccRepository =
            new UserAccRepository(TextFilePath.CUSTOMER_ACCOUNT_FILE_PATH);
    private static final UserAccRepository staffAccRepository =
            new UserAccRepository(TextFilePath.STAFF_ACCOUNT_FILE_PATH);

    /**
     * For signing up new user accounts
     * If all validations are successful, a true value and success message are returned.
     * Otherwise, a false value and a list of error messages are returned
     */
    public static OperationResult validateSignupData(String userId, Role userRole, String emailAddress,
                                                     String phoneNumber, String password, String confirmPassword) {

        // Create a shared map to collect all error messages
        Map<String, String> allErrors = new HashMap<>();

        // Validate the user detail first, then if there are errors, collect them into the shared list
        collectErrors(allErrors, validateUserId(userRole, userId));
        collectErrors(allErrors, validateEmailAddress(emailAddress));
        collectErrors(allErrors, checkEmailAlreadyTaken(userRole, emailAddress));
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
    private static OperationResult validateUserId(Role userRole, String userId) {

        if (userId == null || userId.isBlank()) {
            return OperationResult.failed("userID",
                    "User ID cannot be empty - either Malaysian IC or passport number");
        }

        if (!RegexPattern.IC_PATTERN.matches(userId) && !RegexPattern.PASSPORT_PATTERN.matches(userId)) {
            return OperationResult.failed("userID",
                    "User ID must be a valid Malaysian IC (e.g. 010101145678) " +
                            "or passport number (e.g. A123456789)");
        }

        if (userRole == Role.CUSTOMER) {
            if (customerAccRepository.getUserAccByUserID(userId) != null) {
                return OperationResult.failed("userID",
                        "User ID already exists in the records");
            }
        } else {
            if (staffAccRepository.getUserAccByUserID(userId) != null) {
                return OperationResult.failed("userID",
                        "User ID already exists in the records");
            }
        }

        return OperationResult.success();
    }

    public static OperationResult validateEmailAddress(String emailAddress) {

        if (emailAddress == null || emailAddress.isBlank()) {
            return OperationResult.failed("email",
                    "Email address cannot be empty");
        }

        if (!RegexPattern.EMAIL_PATTERN.matches(emailAddress)) {
            return OperationResult.failed("email",
                    "Email address must be in a valid format (e.g. example123@email.com)");
        }

        return OperationResult.success();
    }

    private static OperationResult checkEmailAlreadyTaken(Role userRole, String emailAddress) {

        if (userRole == Role.CUSTOMER) {
            if (customerAccRepository.getUserAccByEmail(emailAddress) != null) {
                return OperationResult.failed("email",
                        "Email address already exists in the system");
            }
        } else {
            if (staffAccRepository.getUserAccByEmail(emailAddress) != null) {
                return OperationResult.failed("email",
                        "Email address already exists in the system");
            }
        }

        return OperationResult.success();
    }

    private static OperationResult validatePhoneNumber(String phoneNumber) {

        if (phoneNumber == null || phoneNumber.isBlank()) {
            return OperationResult.failed("phoneNum",
                    "Phone number cannot be empty");
        }

        if (!RegexPattern.PHONE_PATTERN.matches(phoneNumber)) {
            return OperationResult.failed("phoneNum",
                    "Phone number must be 9 to 11 digits and start with 0 (e.g. 0123456789)");
        }

        return OperationResult.success();
    }

    public static OperationResult validatePassword(String password, String confirmPassword) {

        if (password == null || password.isBlank()) {
            return OperationResult.failed("password",
                    "Password cannot be empty");
        }

        if (password.length() < 8 || password.length() > 16) {
            return OperationResult.failed("password",
                    "Password must be between 8 and 16 characters");
        }

        if (!RegexPattern.ANY_UPPERCASE.matches(password)) {
            return OperationResult.failed("password",
                    "Password must contain at least one uppercase letter");
        }

        if (!RegexPattern.ANY_LOWERCASE.matches(password)) {
            return OperationResult.failed("password",
                    "Password must contain at least one lowercase letter");
        }

        if (!RegexPattern.ANY_NUMBER.matches(password)) {
            return OperationResult.failed("password",
                    "Password must contain at least one number");
        }

        if (!RegexPattern.ANY_SPECIALCHAR.matches(password)) {
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
