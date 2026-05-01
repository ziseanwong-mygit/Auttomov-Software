package main.java.backend.repository;

import main.java.backend.model.UserAccount;
import main.java.records.OperationResult;

import java.util.List;

public interface AccountRepository<T extends UserAccount> {

    OperationResult createAccount(T account);
    OperationResult updateAccount(T account);
    OperationResult deleteAccount(T account);
    OperationResult updateAccount(List<T> accounts);
    OperationResult deleteAccount(List<T> accounts);

    T getAccountByAccId(String accountId);
    T getAccountByUserId(String userId);
    T getAccountByEmail(String email);
}
