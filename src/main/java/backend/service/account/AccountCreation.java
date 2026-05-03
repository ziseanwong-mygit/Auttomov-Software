package main.java.backend.service.account;

import main.java.backend.repository.AccountRepository;
import main.java.backend.service.ServiceOperation;
import main.java.backend.util.DataValidation;
import main.java.backend.util.PswdHashing;
import main.java.backend.model.StaffAccount;
import main.java.backend.model.UserAccount;
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

public class AccountCreation implements ServiceOperation {

    // Wildcard is used so can accept either customer or staff repository in one variable
    private final AccountRepository<? extends UserAccount> accountRepo;
    private UserAccount newAccount;

    private final String userID;
    private final Role userRole;
    private final String firstName;
    private final String lastName;
    private final String emailAddress;
    private final String phoneNumber;
    private final String password;
    private final String confirmPassword;

    // For staff only
    private JobPosition jobPosition;
    private EmploymentStatus employmentStatus;
    private String specialization;

    public AccountCreation(String userID, Role userRole, String firstName,
                           String lastName, String emailAddress, String phoneNumber, String password,
                           String confirmPassword) {
        this.userID = userID;
        this.userRole = userRole;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.accountRepo = AccountRepository.getInstance(userRole);
    }

    public AccountCreation(String userID, Role userRole, String firstName,
                           String lastName, String emailAddress, String phoneNumber, String password,
                           String confirmPassword, JobPosition jobPosition, EmploymentStatus employmentStatus,
                           String specialization) {
        this(userID, userRole, firstName, lastName, emailAddress, phoneNumber, password, confirmPassword);
        this.jobPosition = jobPosition;
        this.employmentStatus = employmentStatus;
        this.specialization = specialization;
    }

    /**
     * Account creation process for customer, technician, counter-staff, and manager
     * Validate user data first before storing data and creating new account
     */
    public OperationResult executeProcess() {

        OperationResult validationResult = validateInputValues();

        // If the validation fails, return the error message - method ends here
        if (!validationResult.isValid()) {
            return validationResult;
        }

        // Fetch and store the account details in an object
        populateAccountDetails();

        // Cast the newAccount to specific account type, then only create the record; base implementation
        return accountRepo.createAccountBase(newAccount);

    }

    /**
     * If all validations are successful, a true value and success message are returned.
     * Otherwise, a false value and a list of error messages are returned
     */
    private OperationResult validateInputValues() {

        // Create a shared map to collect all error messages
        Map<String, String> allErrors = new HashMap<>();

        // Validate the user detail first, then if there are errors, collect them into the shared list
        collectErrors(allErrors, DataValidation.validateUserId(userRole, userID));
        collectErrors(allErrors, AccountDataVerification.verifyUniqueUserId(userRole, userID));
        collectErrors(allErrors, DataValidation.validateEmailAddress(emailAddress));
        collectErrors(allErrors, AccountDataVerification.verifyUniqueEmail(userRole, emailAddress));
        collectErrors(allErrors, DataValidation.validatePhoneNumber(phoneNumber));
        collectErrors(allErrors, DataValidation.validatePassword(password, confirmPassword));

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
    private void collectErrors(Map<String, String> allErrors, OperationResult result) {
        if (!result.isValid()) {
            allErrors.putAll(result.fieldErrors());
        }
    }

    /**
     * Store the account details in UserAccount/StaffAccount class for account creation
     */
    private void populateAccountDetails() {

        // Generate prerequisite account data
        String accountId = UUID.randomUUID().toString();
        LocalDate creationDate = LocalDate.now();
        AccountStatus accountStatus = AccountStatus.ACTIVE;

        // Hash password to prevent storing plain text password
        PasswordHash passwordHash = PswdHashing.hashNewPassword(password);

        if (userRole == Role.STAFF) {
            LocalDate employmentDate = LocalDate.now();
            newAccount = new StaffAccount(accountId, creationDate, userID, userRole, firstName, lastName,
                    emailAddress, phoneNumber, passwordHash, accountStatus, jobPosition, employmentDate,
                    employmentStatus, specialization);
        } else {
            newAccount = new UserAccount(accountId, creationDate, userID, userRole, firstName, lastName,
                    emailAddress, phoneNumber, passwordHash, accountStatus);
        }
    }
}
