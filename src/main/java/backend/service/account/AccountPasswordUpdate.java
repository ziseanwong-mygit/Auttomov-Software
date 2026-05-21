package main.java.backend.service.account;

import main.java.backend.model.UserAccount;
import main.java.backend.repository.impl.account.AccountRepository;
import main.java.backend.service.RecordWriter;
import main.java.backend.util.PswdHasher;
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

        // Check if the account exists with the given ID
        UserAccount existingAcc = accountRepository.getRecordById(userAccountDTO.getAccountID());
        if (existingAcc == null) {
            return OperationResult.failed("The account with ID: " + userAccountDTO.getAccountID() +
                    " does not exist in the system.");
        }

        // Hash password to prevent storing plain text password
        PasswordHash passwordHash = PswdHasher.hashNewPassword(userAccountDTO.getPassword());

        // Set the new password hash into the existing account record
        existingAcc.setPasswordHash(passwordHash);

        /*
            Regardless of whether the new password is the same as the old one,
            begin updating account record with new status in the repository
            Cast the existingAcc to specific account type, then only update the record; base implementation
         */
        return accountRepository.updateRecordBase(existingAcc);
    }
}
