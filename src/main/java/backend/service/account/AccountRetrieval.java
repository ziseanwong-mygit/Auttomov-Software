package main.java.backend.service.account;

import main.java.backend.model.UserAccount;
import main.java.backend.repository.AccountRepository;
import main.java.backend.repository.CustomerAccountRepository;
import main.java.backend.repository.StaffAccountRepository;
import main.java.enums.Role;

import java.util.List;

public class AccountRetrieval {

    // Wildcard is used so can accept either customer or staff repository in one variable
    private final AccountRepository<? extends UserAccount> accountRepo;

    private String accountID;

    public AccountRetrieval(Role userRole) {
        this.accountRepo  = AccountRepository.getInstance(userRole);
    }

    public AccountRetrieval(Role userRole, String accountID) {
        this(userRole);
        this.accountID = accountID;
    }

    public List<? extends UserAccount> getAllAccounts() {
        return accountRepo.getAllAccounts();
    }

    public UserAccount getAccountByAccId() {
        return accountRepo.getAccountByAccId(accountID);
    }
}
