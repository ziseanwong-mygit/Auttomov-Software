package main.java.backend.general.service;

import main.java.backend.repository.StaffAccountRepository;
import main.java.backend.repository.UserAccountRepository;
import main.java.enums.RegexPattern;
import main.java.enums.Role;
import main.java.records.OperationResult;

public class UserAccountValidation {

    // Private constructor to prevent instantiation
    private UserAccountValidation() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    // Create repository instances to retrieve user account record
    // Used to match the existing data with user's input to avoid duplicated data
    private static final UserAccountRepository customerAccRepository = UserAccountRepository.getInstance();
    private static final StaffAccountRepository staffAccRepository = StaffAccountRepository.getInstance();

    public static OperationResult validateUserId(Role userRole, String userId) {

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
            if (customerAccRepository.getCustomerAccByUserId(userId) != null) {
                return OperationResult.failed("userID",
                        "User ID already exists in the records");
            }
        } else {
            if (staffAccRepository.getStaffAccByUserId(userId) != null) {
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

    public static OperationResult checkEmailAlreadyTaken(Role userRole, String emailAddress) {

        if (userRole == Role.CUSTOMER) {
            if (customerAccRepository.getCustomerAccByEmail(emailAddress) != null) {
                return OperationResult.failed("email",
                        "Email address already exists in the system");
            }
        } else {
            if (staffAccRepository.getStaffAccByEmail(emailAddress) != null) {
                return OperationResult.failed("email",
                        "Email address already exists in the system");
            }
        }

        return OperationResult.success();
    }

    public static OperationResult validatePhoneNumber(String phoneNumber) {

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
