package main.java.backend.general;

import main.java.backend.general.service.PswdHashing;
import main.java.backend.general.service.UserAccountValidation;
import main.java.backend.dto.StaffAccount;
import main.java.backend.repository.StaffAccountRepository;
import main.java.backend.repository.UserAccountRepository;
import main.java.backend.dto.UserAccount;
import main.java.enums.AccountStatus;
import main.java.enums.EmploymentStatus;
import main.java.enums.JobPosition;
import main.java.enums.Role;
import main.java.records.PasswordHash;
import main.java.records.OperationResult;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AccountCreate {

    // Private constructor to prevent instantiation
    private AccountCreate() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    /**
     * Account creation process for customer, technician, counter-staff, and manager
     * Validate user data first before storing data and creating new account
     */
    public static OperationResult executeProcess(String userId, Role userRole, String firstName,
                                                 String lastName, String emailAddress, String phoneNumber,
                                                 String password, String confirmPassword) {

        OperationResult validationResult = validateInputValues(userId, userRole, emailAddress, phoneNumber,
                password, confirmPassword);

        // If the validation fails, return the error message - method ends here
        if (!validationResult.isValid()) {
            return validationResult;
        }

        // Generate prerequisite account data
        String accountId = UUID.randomUUID().toString();
        LocalDate creationDate = LocalDate.now();
        AccountStatus accountStatus = AccountStatus.ACTIVE;

        // Hash password to prevent storing plain text password
        PasswordHash passwordHash = PswdHashing.hashNewPassword(password);

        UserAccount customerAcc = new UserAccount(accountId, creationDate, userId, userRole, firstName, lastName,
                emailAddress, phoneNumber, passwordHash, accountStatus);

        UserAccountRepository customerRepo = UserAccountRepository.getInstance();
        return customerRepo.createAccount(customerAcc);
    }

    public static OperationResult executeProcess(String userId, Role userRole, String firstName,
                                                 String lastName, String emailAddress, String phoneNumber,
                                                 String password, String confirmPassword,
                                                 JobPosition jobPosition, EmploymentStatus employmentStatus,
                                                 String specialization) {

        OperationResult validationResult = validateInputValues(userId, userRole, emailAddress, phoneNumber,
                password, confirmPassword);

        // If the validation fails, return the error message - method ends here
        if (!validationResult.isValid()) {
            return validationResult;
        }

        // Generate prerequisite account data
        String accountId = UUID.randomUUID().toString();
        LocalDate creationDate = LocalDate.now();
        AccountStatus accountStatus = AccountStatus.PENDING;
        LocalDate employmentDate = LocalDate.now();

        // Hash password to prevent storing plain text password
        PasswordHash passwordHash = PswdHashing.hashNewPassword(password);

        StaffAccount staffAcc = new StaffAccount(accountId, creationDate, userId, userRole, firstName, lastName,
                emailAddress, phoneNumber, passwordHash, accountStatus, jobPosition, employmentDate,
                employmentStatus, specialization);

        StaffAccountRepository staffRepo = StaffAccountRepository.getInstance();
        return staffRepo.createAccount(staffAcc);
    }

    /**
     * If all validations are successful, a true value and success message are returned.
     * Otherwise, a false value and a list of error messages are returned
     */
    private static OperationResult validateInputValues(String userId, Role userRole, String emailAddress,
                                                       String phoneNumber, String password, String confirmPassword) {

        // Create a shared map to collect all error messages
        Map<String, String> allErrors = new HashMap<>();

        // Validate the user detail first, then if there are errors, collect them into the shared list
        collectErrors(allErrors, UserAccountValidation.validateUserId(userRole, userId));
        collectErrors(allErrors, UserAccountValidation.validateEmailAddress(emailAddress));
        collectErrors(allErrors, UserAccountValidation.checkEmailAlreadyTaken(userRole, emailAddress));
        collectErrors(allErrors, UserAccountValidation.validatePhoneNumber(phoneNumber));
        collectErrors(allErrors, UserAccountValidation.validatePassword(password, confirmPassword));

        // If all validations are successful, return the success message
        // Otherwise, return the map of error messages
        return allErrors.isEmpty()
                ? OperationResult.success(
                        "Field validation is successful. Proceed to account creation.")
                : OperationResult.failed(
                        "Field validation is unsuccessful. Please try again.", allErrors);
    }

    /**
     * For each validation, if there are errors, collect the field name and error message into the shared map
     */
    private static void collectErrors(Map<String, String> allErrors, OperationResult result) {
        if (!result.isValid()) {
            allErrors.putAll(result.fieldErrors());
        }
    }
}
