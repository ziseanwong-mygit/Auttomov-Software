package main.java.backend.service.account;

import main.java.backend.model.UserAccount;
import main.java.backend.repository.account.AccountRepository;
import main.java.backend.service.GeneralProcess;
import main.java.records.OperationResult;

public class AccountDeletion implements GeneralProcess {

    // Wildcard is used so can accept either customer or staff repository in one variable
    private final AccountRepository<? extends UserAccount> accountRepository;
    private final String accountID;
    private final UserAccount existingAcc;

    public AccountDeletion(AccountRepository<? extends UserAccount> accountRepository, String accountID) {
        this.accountRepository = accountRepository;
        this.accountID = accountID;
        this.existingAcc = accountRepository.getRecordById(accountID);
    }


    /**
     * Account deletion process for customer, technician, counter-staff, and manager
     */
    @Override
    public OperationResult executeProcess() {

        if (existingAcc == null) {
            return OperationResult.failed("The account with ID: " + accountID +
                    " does not exist in the system.");
        }

        try {
            // Cast the existingAcc to specific account type, then only delete the record; base implementation
            return accountRepository.deleteRecordBase(existingAcc);
        } catch (Exception e) {
            System.out.println(e.getMessage()); // Print exception message for debugging
            return OperationResult.failed(
                    "An unexpected error occurred while deleting your account. Please try again.");
        }
    }
}
