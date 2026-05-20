package main.java.backend.service.account;

import main.java.backend.model.UserAccount;
import main.java.backend.repository.account.AccountRepository;
import main.java.backend.service.RecordWriter;
import main.java.dtos.UserAccountDTO;
import main.java.enums.data.AccountStatus;
import main.java.records.FieldComparison;
import main.java.records.OperationResult;

public class AccountStatusUpdate implements RecordWriter {

    // Wildcard is used so can accept either customer or staff repository in one variable
    private final AccountRepository<? extends UserAccount> accountRepository;
    private final UserAccountDTO userAccountDTO;
    private final UserAccount existingAcc;

    public AccountStatusUpdate(AccountRepository<? extends UserAccount> accountRepository,
                               UserAccountDTO userAccountDTO) {
        this.accountRepository = accountRepository;
        this.userAccountDTO = userAccountDTO;
        this.existingAcc = accountRepository.getRecordById(userAccountDTO.getAccountID());
    }

    @Override
    public OperationResult execute() {

        String accountID = userAccountDTO.getAccountID();

        // If the account is not found, return an error message
        if (existingAcc == null) {
            return OperationResult.failed("The account with ID: " + accountID +
                    " does not exist in the system.");
        }

        // Check if the input status is different from the current status
        FieldComparison<AccountStatus> statusChange = new FieldComparison<>(
                "accountStatus", existingAcc.getAccountStatus(), userAccountDTO.getAccountStatus(),
                null);

        if (!statusChange.isChanged()) {
            return OperationResult.failed("The input status provided is the same as the " +
                    "current status. Please set to a different status for update.");
        }

        // Set the new status into the existing account record
        existingAcc.setAccountStatus(userAccountDTO.getAccountStatus());

        // Begin updating account record with new status in the repository
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
