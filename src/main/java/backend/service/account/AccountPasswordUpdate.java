package main.java.backend.service.account;

import main.java.backend.model.UserAccount;
import main.java.backend.repository.account.AccountRepository;
import main.java.backend.service.RecordWriter;
import main.java.backend.util.PswdHashing;
import main.java.dtos.UserAccountDTO;
import main.java.records.OperationResult;
import main.java.records.PasswordHash;

public class AccountPasswordUpdate implements RecordWriter {

    // Wildcard is used so can accept either customer or staff repository in one variable
    private final AccountRepository<? extends UserAccount> accountRepository;
    private final UserAccountDTO userAccountDTO;
    private final UserAccount existingAcc;

    public AccountPasswordUpdate(AccountRepository<? extends UserAccount> accountRepository,
                                 UserAccountDTO userAccountDTO) {
        this.accountRepository = accountRepository;
        this.userAccountDTO = userAccountDTO;
        this.existingAcc = accountRepository.getRecordById(userAccountDTO.getAccountID());
    }

    @Override
    public OperationResult execute() {

        String accountID = userAccountDTO.getAccountID();
        String password = userAccountDTO.getPassword();

        // If the account is not found, return an error message
        if (existingAcc == null) {
            return OperationResult.failed("The account with ID: " + accountID +
                    " does not exist in the system.");
        }

        // Hash password to prevent storing plain text password
        PasswordHash passwordHash = PswdHashing.hashNewPassword(password);

        // Set the new password hash into the existing account record
        existingAcc.setPasswordHash(passwordHash);

        // Regardless of whether the new password is the same as the old one,
        // begin updating account record with new status in the repository
        try {
            // Cast the existingAcc to specific account type, then only update the record; base implementation
            return accountRepository.updateRecordBase(existingAcc);
        } catch (Exception e) {
            System.out.println(e.getMessage()); // Print exception message for debugging
            return OperationResult.failed("An unexpected error occurred while updating the account. " +
                    "Please try again.");
        }
    }
}
