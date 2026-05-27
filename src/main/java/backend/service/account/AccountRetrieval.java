package main.java.backend.service.account;

import main.java.backend.model.UserAccount;
import main.java.backend.repository.impl.account.AccountRepository;
import main.java.backend.service.RecordReader;
import main.java.enums.data.AccountStatus;
import main.java.enums.system.RegexPattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountRetrieval implements RecordReader<UserAccount> {

    // Wildcard is used so can accept either customer or staff repository in one variable
    private final AccountRepository<? extends UserAccount> accountRepository;

    // Customer or staff account repository
    public AccountRetrieval(AccountRepository<? extends UserAccount> accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Read and return all customer or staff accounts from the repository
     */
    @Override
    public Optional<List<UserAccount>> getAllRecords() {

        List<UserAccount> existingAccounts = new ArrayList<>(accountRepository.getAllRecords());

        if (existingAccounts.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(existingAccounts);
    }

    /**
     * Read and return a single customer or staff account based on the given unique ID like accID, userID, or email
     */
    @Override
    public Optional<UserAccount> getRecordById(String uniqueId) {

        UserAccount existingAccount = null;

        /*
        If the unique ID is an account ID, find the account by the account ID
        Otherwise if the unique ID is an IC number or passport number, find the account by the user ID
        Otherwise if the unique ID is an email address, find the account by the email address
         */
        if (RegexPattern.UUID_PATTERN.matches(uniqueId)) {
            existingAccount = accountRepository.getRecordById(uniqueId);

        } else if (RegexPattern.IC_PATTERN.matches(uniqueId) || RegexPattern.PASSPORT_PATTERN.matches(uniqueId)) {
            existingAccount = accountRepository.getRecordByUserId(uniqueId);

        } else if (RegexPattern.EMAIL_PATTERN.matches(uniqueId)) {
            existingAccount = accountRepository.getRecordByEmail(uniqueId);
        }

        if (existingAccount == null) {
            return Optional.empty();
        }

        return Optional.of(existingAccount);
    }

    /**
     * Read and return multiple customer or staff accounts based on the given
     * list of unique IDs like accID, userID, or email
     */
    @Override
    public Optional<List<UserAccount>> getRecordsByIds(List<String> uniqueIds) {

        List<? extends UserAccount> existingAccounts = new ArrayList<>();

        if (RegexPattern.UUID_PATTERN.matches(uniqueIds.getFirst())) {
            existingAccounts = accountRepository.getRecordsByIds(uniqueIds);

        } else if (RegexPattern.IC_PATTERN.matches(uniqueIds.getFirst()) ||
                RegexPattern.PASSPORT_PATTERN.matches(uniqueIds.getFirst())) {
            existingAccounts = accountRepository.getRecordsByUserIds(uniqueIds);

        } else if (RegexPattern.EMAIL_PATTERN.matches(uniqueIds.getFirst())) {
            existingAccounts = accountRepository.getRecordsByEmails(uniqueIds);
        }

        if (existingAccounts.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new ArrayList<>(existingAccounts));
    }

    public Optional<List<UserAccount>> getRecordsByAccStatus(AccountStatus status) {

        List<? extends UserAccount> existingAccounts = accountRepository.getRecordsByAccStatus(status);

        if (existingAccounts.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new ArrayList<>(existingAccounts));
    }
}
