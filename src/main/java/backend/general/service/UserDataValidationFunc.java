package main.java.backend.general.service;

import main.java.records.ValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class UserDataValidationFunc {

    // Private constructor to prevent instantiation
    private UserDataValidationFunc() {}

    // Constants for data validation with regular expressions
    private static final Pattern ANY_UPPERCASE = Pattern.compile(".*[A-Z].*");
    private static final Pattern ANY_LOWERCASE = Pattern.compile(".*[a-z].*");
    private static final Pattern ANY_NUMBER = Pattern.compile(".*[0-9].*");
    private static final Pattern ANY_SPECIALCHAR = Pattern.compile(".*[!@#$%^&*(),.?:{}|<>].*");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^0[0-9]{8,10}$");
    private static final Pattern IC_PATTERN = Pattern.compile("^\\d{12}$");
    private static final Pattern PASSPORT_PATTERN = Pattern.compile("^[A-Z]{1,2}[0-9]{6,9}$");

    /**
     * Validation process begins here
     * If all validations are successful, return a ValidationResult record with true value
     * Otherwise, return a ValidationResult record with false value and a list of error messages
     */
    public static ValidationResult validateUserDetails(String userId, String emailAddress, String phoneNumber,
                                                       String password, String confirmPassword) {

        // Create a list of FieldError records to store all error messages with involved fields
        List<ValidationResult.FieldError> allErrors = new ArrayList<>();

        // Validate the user detail first, then if there are errors, collect them into the shared list
        collectErrors(allErrors, validateUserId(userId));
        collectErrors(allErrors, validateEmailAddress(emailAddress));
        collectErrors(allErrors, validatePhoneNumber(phoneNumber));
        collectErrors(allErrors, validatePassword(password, confirmPassword));

        // If there are no errors, all validations are successful
        // Otherwise, create a new record that contains false value and the complete list of error messages
        return allErrors.isEmpty()
                ? ValidationResult.success()
                : new ValidationResult(false, allErrors);
    }

    // Add error messages from a ValidationResult record into the shared list
    private static void collectErrors(List<ValidationResult.FieldError> allErrors, ValidationResult result) {
        if (!result.isValid())
            allErrors.addAll(result.fieldErrorsList());
    }

    // Validate user ID, email address, phone number, and password
    // Return a ValidationResult record with boolean and FieldError record with fieldName name and error errorMessage
    private static ValidationResult validateUserId(String userId) {

        if (userId == null || userId.isBlank()) {
            return ValidationResult.failed(
                    "userID", "User ID cannot be empty - either Malaysian IC or passport number");
        }

        if (!IC_PATTERN.matcher(userId).matches() && !PASSPORT_PATTERN.matcher(userId).matches()) {
            return ValidationResult.failed(
                    "userID", "User ID must be a valid Malaysian IC (e.g. 010101145678) " +
                            "or passport number (e.g. A123456789)");
        }

        return ValidationResult.success();
    }

    private static ValidationResult validateEmailAddress(String emailAddress) {

        if (emailAddress == null || emailAddress.isBlank()) {
            return ValidationResult.failed("email", "Email address cannot be empty");
        }

        if (!EMAIL_PATTERN.matcher(emailAddress).matches()) {
            return ValidationResult.failed("email",
                    "Email address must be in a valid format (e.g. example123@email.com)");
        }

        return ValidationResult.success();
    }

    private static ValidationResult validatePhoneNumber(String phoneNumber) {

        if (phoneNumber == null || phoneNumber.isBlank()) {
            return ValidationResult.failed("phoneNum", "Phone number cannot be empty");
        }

        if (!PHONE_PATTERN.matcher(phoneNumber).matches()) {
            return ValidationResult.failed("phoneNum",
                    "Phone number must be 9 to 11 digits and start with 0 (e.g. 0123456789)");
        }

        return ValidationResult.success();
    }

    private static ValidationResult validatePassword(String password, String confirmPassword) {

        if (password == null || password.isBlank()) {
            return ValidationResult.failed("password", "Password cannot be empty");
        }

        if (password.length() < 8 || password.length() > 16) {
            return ValidationResult.failed("password",
                    "Password must be between 8 and 16 characters");
        }

        if (!ANY_UPPERCASE.matcher(password).matches()) {
            return ValidationResult.failed("password",
                    "Password must contain at least one uppercase letter");
        }

        if (!ANY_LOWERCASE.matcher(password).matches()) {
            return ValidationResult.failed("password",
                    "Password must contain at least one lowercase letter");
        }

        if (!ANY_NUMBER.matcher(password).matches()) {
            return ValidationResult.failed("password",
                    "Password must contain at least one number");
        }

        if (!ANY_SPECIALCHAR.matcher(password).matches()) {
            return ValidationResult.failed("password",
                    "Password must contain at least one special character: !@#$%^&*(),.?:{}|<>");
        }

        if (!password.equals(confirmPassword)) {
            return ValidationResult.failed("password",
                    "Password do not match with confirm password");
        }

        return ValidationResult.success();
    }
}
