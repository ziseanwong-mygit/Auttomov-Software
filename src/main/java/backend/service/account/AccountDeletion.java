package main.java.backend.service.account;

import main.java.backend.model.UserAccount;
import main.java.backend.repository.AccountRepository;
import main.java.backend.repository.CustomerAccountRepository;
import main.java.backend.repository.StaffAccountRepository;
import main.java.backend.service.ServiceOperation;
import main.java.enums.Role;
import main.java.records.OperationResult;

public class AccountDeletion implements ServiceOperation {

    // Wildcard is used so can accept either customer or staff repository in one variable
    private final AccountRepository<? extends UserAccount> accountRepo;
    private final UserAccount existingAcc;

    private final String accountID;

    public AccountDeletion(String accountID, Role userRole) {
        this.accountID = accountID;

        this.accountRepo  = AccountRepository.getInstance(userRole);
        this.existingAcc  = accountRepo.getAccountByAccId(accountID);
    }

    public OperationResult executeProcess() {

        if (existingAcc == null) {
            return OperationResult.failed("The account with ID: " + accountID +
                    " does not exist in the system.");
        }

        // Cast the existingAcc to specific account type, then only delete the record; base implementation
        return accountRepo.deleteAccountBase(existingAcc);
    }
}
