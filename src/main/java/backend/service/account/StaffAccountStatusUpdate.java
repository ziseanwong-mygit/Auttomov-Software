package main.java.backend.service.account;

import main.java.backend.model.StaffAccount;
import main.java.backend.repository.impl.account.AccountRepository;
import main.java.backend.service.RecordWriter;
import main.java.dtos.StaffAccountDTO;
import main.java.enums.data.AccountStatus;
import main.java.records.FieldComparison;
import main.java.records.OperationResult;

/**
 * Used for updating the status of newly registered staff accounts who currently have a pending status
 * Convenient for manager to approve staff accounts
 */
public class StaffAccountStatusUpdate implements RecordWriter {

    private final AccountRepository<StaffAccount> accountRepository;
    private final StaffAccountDTO staffAccountDTO;

    public StaffAccountStatusUpdate(AccountRepository<StaffAccount> accountRepository,
                                    StaffAccountDTO staffAccountDTO) {
        this.accountRepository = accountRepository;
        this.staffAccountDTO = staffAccountDTO;
    }

    @Override
    public OperationResult execute() {

        // Check if the staff account exists with the given ID
        StaffAccount existingAcc = accountRepository.getRecordById(staffAccountDTO.getAccountID());
        if (existingAcc == null) {
            return OperationResult.failed("The staff account with ID " + staffAccountDTO.getAccountID()  +
                    " does not exist in the system.");
        }

        // Check if the input status is different from the current status
        FieldComparison<AccountStatus> statusChange = new FieldComparison<>(
                "accountStatus", existingAcc.getAccountStatus(), staffAccountDTO.getAccountStatus(),
                null);

        if (!statusChange.isChanged()) {
            return OperationResult.failed("The input status provided is the same as the " +
                    "current status. Please set to a different status for update.");
        }

        // Set the new status into the existing account record
        existingAcc.setAccountStatus(staffAccountDTO.getAccountStatus());

        // Begin updating staff account record with new status in the repository
        // Cast the existingAcc to specific account type, then only update the record; base implementation
        return accountRepository.updateRecordBase(existingAcc);
    }
}
