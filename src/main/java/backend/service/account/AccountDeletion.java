package main.java.backend.service.account;

import main.java.backend.model.UserAccount;
import main.java.backend.repository.AccountRepository;
import main.java.backend.service.GeneralService;
import main.java.records.OperationResult;

public class AccountDeletion<T extends UserAccount> implements GeneralService {

    // Generics is used so can accept either UserAccount or StaffAccount in this variable
    // The repository instance is stored in AccountRepository interface, to eliminate the need of checking type
    private final AccountRepository<T> accountRepo;

    // Existing customer, technician, counter-staff, or manager account
    private final T existingAcc;
    private final String accountID;

    public AccountDeletion(AccountRepository<T> accountRepo, String accountID) {
        this.accountRepo = accountRepo;
        this.existingAcc = accountRepo.getAccountByAccId(accountID);
        this.accountID = accountID;
    }

    public OperationResult executeProcess() {

        if (existingAcc == null) {
            return OperationResult.failed("The account with ID: " + accountID +
                    " does not exist in the system.");
        }

        return accountRepo.deleteAccount(existingAcc);
    }
}
