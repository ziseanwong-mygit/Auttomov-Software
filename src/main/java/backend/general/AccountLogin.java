package main.java.backend.general;

import main.java.backend.general.service.PswdHashing;
import main.java.backend.general.service.UserAccountValidation;
import main.java.backend.dto.UserAccount;
import main.java.backend.repository.StaffAccountRepository;
import main.java.backend.repository.UserAccountRepository;
import main.java.enums.AccountStatus;
import main.java.enums.Role;
import main.java.records.OperationResult;

public class AccountLogin {

    // Private constructor to prevent instantiation
    private AccountLogin() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    /**
     * Account login for customer, technician, counter-staff, and manager
     * Check email first to verify if the account exists
     * Then verify the password and account status before enabling access to the system
     */
    public OperationResult executeProcess(Role userRole, String emailAddress, String password) {

        UserAccountRepository customerAccRepository = UserAccountRepository.getInstance();
        StaffAccountRepository staffAccRepository = StaffAccountRepository.getInstance();

        // Check email address format
        // If email address format is invalid, return error message
        OperationResult emailFormatValidation = UserAccountValidation.validateEmailAddress(emailAddress);
        if (!emailFormatValidation.isValid()) {
            return emailFormatValidation;
        }

        // Check if the user account exists with the given email address
        UserAccount existingAcc = userRole == Role.CUSTOMER
                ? customerAccRepository.getCustomerAccByEmail(emailAddress)
                : staffAccRepository.getStaffAccByEmail(emailAddress);

        if (existingAcc == null) {
            return OperationResult.failed("Sorry! We could not find your account with this " +
                    "email address. Please sign up a new account first.");
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
