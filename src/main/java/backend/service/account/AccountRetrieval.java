package main.java.backend.service.account;

import main.java.backend.model.UserAccount;
import main.java.backend.repository.account.AccountRepository;
import main.java.records.OperationResult;

import java.util.List;

public class AccountRetrieval {

    // Wildcard is used so can accept either customer or staff repository in one variable
    private final AccountRepository<? extends UserAccount> accountRepository;
    private String accountID;
    private UserAccount existingAcc;

    public AccountRetrieval(AccountRepository<? extends UserAccount> accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountRetrieval(AccountRepository<? extends UserAccount> accountRepository, String accountID) {
        this.accountRepository = accountRepository;
        this.accountID = accountID;
        this.existingAcc = accountRepository.getRecordById(accountID);
    }

    /**
     * Read and return all customer or staff accounts from the repository
     */
    public List<? extends UserAccount> getAllAccounts() {
        return accountRepository.getAllRecords();
    }

    /**
     * Read and return a single customer or staff account based on the given account ID
     */
    public OperationResult getAccountByAccId() {

        if (existingAcc == null) {
            return OperationResult.failed("The account with ID: " + accountID +
                    " does not exist in the system.");
        }

        return OperationResult.success("The account with ID: " + accountID +
                "has been found.", existingAcc);
    }
}
