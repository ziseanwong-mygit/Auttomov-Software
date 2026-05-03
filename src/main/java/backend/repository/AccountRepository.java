package main.java.backend.repository;

import main.java.backend.model.UserAccount;
import main.java.enums.Role;
import main.java.records.OperationResult;

import java.util.List;

public interface AccountRepository<T extends UserAccount> {

    // Base implementation to cast the account to a generic type before updating or deleting
    // Safe because each repository only ever receives its own account type
    @SuppressWarnings("unchecked")
    default OperationResult createAccountBase(UserAccount account) {
        return updateAccount((T) account);
    }

    @SuppressWarnings("unchecked")
    default OperationResult updateAccountBase(UserAccount account) {
        return updateAccount((T) account);
    }

    @SuppressWarnings("unchecked")
    default OperationResult deleteAccountBase(UserAccount account) {
        return deleteAccount((T) account);
    }

    OperationResult createAccount(T account);
    OperationResult updateAccount(T account);
    OperationResult deleteAccount(T account);

    List<T> getAllAccounts();
    T getAccountByAccId(String accountId);
    T getAccountByUserId(String userId);
    T getAccountByEmail(String email);

    // Get the instance of account repository for CRUD operations
    // Static method - can be used by other classes without instantiation/implementation
    static AccountRepository<? extends UserAccount> getInstance(Role userRole) {
        return userRole == Role.CUSTOMER
                ? CustomerAccountRepository.getInstance()
                : StaffAccountRepository.getInstance();
    }

    /*
    OperationResult updateAccount(List<T> accounts);
    OperationResult deleteAccount(List<T> accounts);
     */
}
