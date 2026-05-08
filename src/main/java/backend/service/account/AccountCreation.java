package main.java.backend.service.account;

import main.java.backend.model.StaffAccount;
import main.java.backend.model.UserAccount;
import main.java.backend.repository.account.AccountRepository;
import main.java.backend.service.GeneralProcess;
import main.java.backend.util.PswdHashing;
import main.java.dtos.StaffAccountDTO;
import main.java.dtos.UserAccountDTO;
import main.java.enums.data.AccountStatus;
import main.java.enums.data.Role;
import main.java.records.OperationResult;
import main.java.records.PasswordHash;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AccountCreation implements GeneralProcess {

    // Wildcard is used so can accept either customer or staff repository in one variable
    private final AccountRepository<? extends UserAccount> accountRepository;
    private final UserAccountDTO userAccountDTO;

    public AccountCreation(AccountRepository<? extends UserAccount> accountRepository, UserAccountDTO userAccountDTO) {
        this.accountRepository = accountRepository;
        this.userAccountDTO = userAccountDTO;
    }

    @Override
    public OperationResult executeProcess() {

        OperationResult verificationResult = verifyAccountDetails();
        if (!verificationResult.isValid()) {
            return verificationResult;
        }

        try {
            // Create a new account object based on the user role
            UserAccount newAccount = populateAccountDetails();
            // Cast the newAccount to specific account type, then only create the record; base implementation
            return accountRepository.createRecordBase(newAccount);

        } catch (RuntimeException e) {
            System.out.println(e.getMessage()); // Print exception message for debugging
            return OperationResult.failed("An unexpected error occurred while preparing the account. " +
                    "Please try again.");

        } catch (Exception e) {
            System.out.println(e.getMessage()); // Print exception message for debugging
            return OperationResult.failed("An unexpected error occurred while updating the account. " +
                    "Please try again.");
        }
    }

    /**
     * Verify user ID and email address are unique to prevent duplicate entries
     */
    private OperationResult verifyAccountDetails() {

        // Create a shared map to collect all error messages
        Map<String, String> allErrors = new HashMap<>();

        collectErrors(allErrors, AccountDataVerification.verifyUniqueUserId(
                userAccountDTO.getUserRole(), userAccountDTO.getUserID(), null));
        collectErrors(allErrors, AccountDataVerification.verifyUniqueEmail(userAccountDTO.getUserRole(),
                userAccountDTO.getEmailAddress(), null));

        // If all validations are successful, return the success message
        // Otherwise, return the map of error messages
        return allErrors.isEmpty()
                ? OperationResult.success(
                "Field verification is successful. Proceed to account creation.")
                : OperationResult.failed(
                "Field verification is unsuccessful. Please try again.", allErrors);
    }

    /**
     * For each verification, if there are errors, collect the field name and error message into the shared map
     */
    private void collectErrors(Map<String, String> allErrors, OperationResult result) {
        if (!result.isValid()) {
            allErrors.putAll(result.fieldErrors());
        }
    }

    /**
     * Create a new UserAccount/StaffAccount object for record creation
     */
    private UserAccount populateAccountDetails() {

        // Generate prerequisite account data
        String accountId = UUID.randomUUID().toString();
        LocalDate creationDate = LocalDate.now();
        AccountStatus accountStatus;

        String userID = userAccountDTO.getUserID();
        Role userRole = userAccountDTO.getUserRole();
        String firstName = userAccountDTO.getFirstName();
        String lastName = userAccountDTO.getLastName();
        String emailAddress = userAccountDTO.getEmailAddress();
        String phoneNumber = userAccountDTO.getPhoneNumber();
        String password = userAccountDTO.getPassword();

        // Hash password to prevent storing plain text password
        PasswordHash passwordHash = PswdHashing.hashNewPassword(password);

        // If the account is a staff type, set its status for manager's approval
        // Otherwise, automatically authorize the customer's account for immediate login
        if (userAccountDTO instanceof StaffAccountDTO staffAccountDTO) {
            accountStatus = AccountStatus.PENDING;
            LocalDate employmentDate = LocalDate.now();
            return new StaffAccount(accountId, creationDate, userID, userRole, firstName, lastName,
                    emailAddress, phoneNumber, passwordHash, accountStatus, staffAccountDTO.getJobPosition(),
                    employmentDate, staffAccountDTO.getEmploymentStatus(), staffAccountDTO.getSpecialization());
        } else {
            accountStatus = AccountStatus.ACTIVE;
            return new UserAccount(accountId, creationDate, userID, userRole, firstName, lastName,
                    emailAddress, phoneNumber, passwordHash, accountStatus);
        }
    }
}
