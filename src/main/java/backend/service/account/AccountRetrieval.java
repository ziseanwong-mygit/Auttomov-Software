package main.java.backend.service.account;

import main.java.backend.model.UserAccount;
import main.java.backend.repository.account.AccountRepository;
import main.java.backend.service.RecordReader;
import main.java.records.OperationResult;

import java.util.List;

public class AccountRetrieval implements RecordReader {

    // Wildcard is used so can accept either customer or staff repository in one variable
    private final AccountRepository<? extends UserAccount> accountRepository;

    public AccountRetrieval(AccountRepository<? extends UserAccount> accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Read and return all customer or staff accounts from the repository
     */
    @Override
    public List<? extends UserAccount> getAllRecords() {
        return accountRepository.getAllRecords();
    }

    /**
     * Read and return a single customer or staff account based on the given keyword like accID, userID, or email
     */
    @Override
    public OperationResult getRecordById(String accountID) {

        // Find the account by account ID
        UserAccount existingAcc = accountRepository.getRecordById(accountID);

        if (existingAcc == null) {
            return OperationResult.failed("The account with account ID: " + accountID +
                    " does not exist in the system.");
        }

        return OperationResult.success("The account with account ID: " + accountID +
                "has been found.", existingAcc);
    }

    public OperationResult getAccountByUserID(String userID) {

        // Find the account by user ID
        UserAccount existingAcc = accountRepository.getAccountByUserId(userID);

        if (existingAcc == null) {
            return OperationResult.failed("The account with user ID: " + userID +
                    " does not exist in the system.");
        }

        return OperationResult.success("The account with user ID: " + userID +
                "has been found.", existingAcc);
    }

    public OperationResult getAccountByEmail(String emailAddress) {

        // Find the account by email address
        UserAccount existingAcc = accountRepository.getAccountByEmail(emailAddress);

        if (existingAcc == null) {
            return OperationResult.failed("The account with email address: " + emailAddress +
                    " does not exist in the system.");
        }

        return OperationResult.success("The account with email address: " + emailAddress +
                "has been found.", existingAcc);
    }
}
