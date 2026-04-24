package main.java.backend.account;

import main.java.backend.account.service.PswdEncryption;
import main.java.backend.general.service.UserAccValidation;
import main.java.backend.pojo.UserAccount;
import main.java.backend.repository.UserAccRepository;
import main.java.enums.AccountStatus;
import main.java.enums.Role;
import main.java.enums.TextFilePath;
import main.java.records.OperationResult;

import java.util.Base64;

public class AccountLogin {

    private final Role userRole;
    private final String emailAddress;
    private final String password;

    public AccountLogin(Role userRole, String emailAddress, String password) {
        this.emailAddress = emailAddress;
        this.password = password;
        this.userRole = userRole;
    }

    public OperationResult validateLoginInputs() {

        UserAccRepository customerAccRepository = new UserAccRepository(TextFilePath.CUSTOMER_ACCOUNT_FILE_PATH);
        UserAccRepository staffAccRepository = new UserAccRepository(TextFilePath.STAFF_ACCOUNT_FILE_PATH);

        // Check email address format
        // If email address format is invalid, return error message
        OperationResult emailFormatValidation = UserAccValidation.validateEmailAddress(emailAddress);
        if (!emailFormatValidation.isValid()) {
            return emailFormatValidation;
        }

        // Check if the user account exists with the given email address
        UserAccount existingAcc = userRole == Role.CUSTOMER
                ? customerAccRepository.getUserAccByEmail(emailAddress)
                : staffAccRepository.getUserAccByEmail(emailAddress);

        if (existingAcc == null) {
            return OperationResult.failed("Sorry! We could not find your account with this " +
                    "email address. Please sign up a new account first.");
        }

        String pswdSalt = existingAcc.getPasswordHash().hashSalt(); // Get salt from the existing account
        String pswdHash = existingAcc.getPasswordHash().pswdHash(); // Get hash from the existing account

        // Compute hashing for password input
        byte[] saltBytes = Base64.getDecoder().decode(pswdSalt);
        String hashedPswdInput = PswdEncryption.computeHash(password, saltBytes);

        // If the input hash does not match the existing hash, return error message
        if (!hashedPswdInput.equals(pswdHash)) {
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
