package main.java.backend.service.account;

import main.java.backend.repository.AccountRepository;
import main.java.backend.service.ServiceOperation;
import main.java.backend.util.DataValidation;
import main.java.backend.util.PswdHashing;
import main.java.backend.model.UserAccount;
import main.java.enums.AccountStatus;
import main.java.enums.Role;
import main.java.records.OperationResult;

public class AccountLogin implements ServiceOperation {

    // Wildcard is used so can accept either customer or staff repository in one variable
    private final AccountRepository<? extends UserAccount> accountRepo;

    private final String emailAddress;
    private final String password;

    public AccountLogin(Role userRole, String emailAddress, String password) {
        this.emailAddress = emailAddress;
        this.password = password;
        this.accountRepo = AccountRepository.getInstance(userRole);
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
