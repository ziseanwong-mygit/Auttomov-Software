package main.java.backend.account;

import main.java.backend.dto.StaffAccount;
import main.java.backend.dto.UserAccount;
import main.java.backend.repository.StaffAccountRepository;
import main.java.backend.repository.UserAccountRepository;
import main.java.enums.Role;
import main.java.records.OperationResult;

public class AccountDelete {

    // Existing customer, technician, counter-staff, or manager account
    private final UserAccount existingAcc;
    private final String accountID;

    public AccountDelete(String accountID, Role userRole) {
        this.accountID = accountID;
        this.existingAcc = userRole == Role.CUSTOMER
                ? UserAccountRepository.getInstance().getCustomerAccByAccId(accountID)
                : StaffAccountRepository.getInstance().getStaffAccByAccId(accountID);
    }

    public OperationResult executeProcess() {

        if (existingAcc == null) {
            return OperationResult.failed("The account with ID: " + accountID +
                    " does not exist in the system.");
        }

        if (existingAcc instanceof StaffAccount staffAcc) {
            StaffAccountRepository staffRepo = StaffAccountRepository.getInstance();
            return staffRepo.deleteAccount(staffAcc);
        } else {
            UserAccountRepository customerRepo = UserAccountRepository.getInstance();
            return customerRepo.deleteAccount(existingAcc);
        }
    }
}
