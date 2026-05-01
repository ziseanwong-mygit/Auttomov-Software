package main.java.backend.service.account;

import main.java.backend.repository.AccountRepository;
import main.java.backend.repository.CustomerAccountRepository;
import main.java.backend.service.GeneralService;
import main.java.backend.util.DataValidation;
import main.java.backend.util.PswdHashing;
import main.java.backend.model.StaffAccount;
import main.java.backend.repository.StaffAccountRepository;
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

public class AccountCreation implements GeneralService {

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

        OperationResult validationResult = validateInputValues(userID, userRole, emailAddress, phoneNumber,
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

        // Create new account record based on user role
        if (userRole == Role.STAFF) {
            LocalDate employmentDate = LocalDate.now();
            StaffAccount newAccount = new StaffAccount(accountId, creationDate, userID, userRole, firstName, lastName,
                    emailAddress, phoneNumber, passwordHash, accountStatus, jobPosition, employmentDate,
                    employmentStatus, specialization);

            AccountRepository<StaffAccount> accountRepo = StaffAccountRepository.getInstance();
            return accountRepo.createAccount(newAccount);
        } else {
            UserAccount newAccount = new UserAccount(accountId, creationDate, userID, userRole, firstName, lastName,
                    emailAddress, phoneNumber, passwordHash, accountStatus);

            AccountRepository<UserAccount> accountRepo = CustomerAccountRepository.getInstance();
            return accountRepo.createAccount(newAccount);
        }
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
        collectErrors(allErrors, DataValidation.validateUserId(userRole, userId));
        collectErrors(allErrors, AccountDataVerification.verifyUniqueUserId(userRole, userId));
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
    private static void collectErrors(Map<String, String> allErrors, OperationResult result) {
        if (!result.isValid()) {
            allErrors.putAll(result.fieldErrors());
        }
    }
}
