package main.java.backend.service.account;

import main.java.backend.model.UserAccount;
import main.java.backend.repository.account.AccountRepository;
import main.java.backend.service.GeneralProcess;
import main.java.backend.util.PswdHashing;
import main.java.dtos.UserAccountDTO;
import main.java.enums.data.AccountStatus;
import main.java.records.OperationResult;

public class AccountLogin implements GeneralProcess {

    // Wildcard is used so can accept either customer or staff repository in one variable
    private final AccountRepository<? extends UserAccount> accountRepository;
    private final UserAccountDTO userAccountDTO;

    public AccountLogin(AccountRepository<? extends UserAccount> accountRepository, UserAccountDTO userAccountDTO) {
        this.accountRepository = accountRepository;
        this.userAccountDTO = userAccountDTO;
    }

    @Override
    public OperationResult executeProcess() {

        // Check if the customer/staff account exists with the given email address
        UserAccount existingAcc = accountRepository.getAccountByEmail(userAccountDTO.getEmailAddress());
        if (existingAcc == null) {
            return OperationResult.failed("Sorry! We could not find your account with this " +
                    "email address: " + userAccountDTO.getEmailAddress() + " Please sign up a new account first.");
        }

        // Verify input password
        boolean pwdVerification = PswdHashing.verifyPassword(userAccountDTO.getPassword(), existingAcc);
        if (!pwdVerification) {
            return OperationResult.failed(
                    "Sorry! The password you entered is incorrect. Please try again.");
        }

        // Check the account's status. If the account is in pending, denied or suspended status, return an error message
        if (existingAcc.getAccountStatus() == AccountStatus.PENDING) {
            return OperationResult.failed("Your account is still pending approval. " +
                    "Please wait for the manager's approval.");
        } else if (existingAcc.getAccountStatus() == AccountStatus.DENIED) {
            return OperationResult.failed("Your account has been denied access. " +
                    "Please contact the manager for more information.");
        } else if (existingAcc.getAccountStatus() == AccountStatus.SUSPENDED) {
            return OperationResult.failed("Your account has been suspended. " +
                    "Please contact the manager for more information.");
        }

        return OperationResult.success();
    }
}
