package main.java.backend.service.account;

import main.java.backend.repository.AccountRepository;
import main.java.backend.service.GeneralService;
import main.java.backend.util.DataValidation;
import main.java.backend.util.PswdHashing;
import main.java.backend.model.UserAccount;
import main.java.enums.AccountStatus;
import main.java.records.OperationResult;

public class AccountLogin<T extends UserAccount>  implements GeneralService {

    // Generics is used so can accept either UserAccount or StaffAccount in this variable
    // The repository instance is stored in AccountRepository interface, to eliminate the need of checking type
    private final AccountRepository<T> accountRepo;

    private final String emailAddress;
    private final String password;

    public AccountLogin(String emailAddress, String password, AccountRepository<T> accountRepo) {
        this.emailAddress = emailAddress;
        this.password = password;
        this.accountRepo = accountRepo;
    }

    /**
     * Account login for customer, technician, counter-staff, and manager
     * Check email first to verify if the account exists
     * Then verify the password and account status before enabling access to the system
     */
    public OperationResult executeProcess() {

        // Check email address format
        // If email address format is invalid, return error message
        OperationResult emailFormatValidation = DataValidation.validateEmailAddress(emailAddress);
        if (!emailFormatValidation.isValid()) {
            return emailFormatValidation;
        }

        // Check if the customer/staff account exists with the given email address
        UserAccount existingAcc = accountRepo.getAccountByEmail(emailAddress);
        if (existingAcc == null) {
            return OperationResult.failed("Sorry! We could not find your account with this " +
                    "email address: " + emailAddress + " Please sign up a new account first.");
        }

        // Verify input password
        boolean pswdVerification = PswdHashing.verifyPassword(password, existingAcc);
        if (!pswdVerification) {
            return OperationResult.failed(
                    "Sorry! The password you entered is incorrect. Please try again.");
        }

        // Check the account's status. If the account is in pending, denied or suspended status, return error message
        if (existingAcc.getAccountStatus() == AccountStatus.PENDING) {
            return OperationResult.failed("Your account is still pending approval. " +
                    "Please wait for the manager's approval.");
        } else if (existingAcc.getAccountStatus() == AccountStatus.DENIED) {
            return OperationResult.failed("Your account has been denied access. " +
                    "Please contact the manager for more information.");
        } else if (existingAcc.getAccountStatus() == AccountStatus.SUSPENDED) {
            return OperationResult.failed("Your account has been suspended. " +
                    "Please contact the manager for more information.");
        }

        return OperationResult.success();
    }

}
