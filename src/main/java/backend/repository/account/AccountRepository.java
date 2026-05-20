package main.java.backend.repository.account;

import main.java.backend.model.UserAccount;
import main.java.backend.repository.general.AbstractRepository;
import main.java.enums.data.Role;
import main.java.records.OperationResult;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

// TODO: Need to adapt to Swing/AWT interface in all catch exception parts

public abstract class AccountRepository<T extends UserAccount> extends AbstractRepository<T> {

    private final Map<String, T> accountByAccId = new HashMap<>();
    private final Map<String, T> accountByUserId = new HashMap<>();
    private final Map<String, T> accountByEmail = new HashMap<>();

    // Base implementation to cast the account to a generic type before CRUD
    // Safe because each repository only ever receives its own account type
    @SuppressWarnings("unchecked")
    public OperationResult createRecordBase(UserAccount account) {
        try {
            return createRecord((T) account);
        } catch (ClassCastException e) {
            System.out.println(e.getMessage()); // Print exception message for debugging
            return OperationResult.failed("Oops! There was an error in handling the account for " +
                    "creation process. Please try again.");
        }
    }

    @SuppressWarnings("unchecked")
    public OperationResult updateRecordBase(UserAccount account) {
        try {
            return updateRecord((T) account);
        } catch (ClassCastException e) {
            System.out.println(e.getMessage()); // Print exception message for debugging
            return OperationResult.failed("Oops! There was an error in handling the account for " +
                    "update process. Please try again.");
        }
    }

    @SuppressWarnings("unchecked")
    public OperationResult deleteRecordBase(UserAccount account) {
        try {
            return deleteRecord((T) account);
        } catch (ClassCastException e) {
            System.out.println(e.getMessage()); // Print exception message for debugging
            return OperationResult.failed("Oops! There was an error in handling the account for " +
                    "deletion process. Please try again.");
        }
    }

    // Get the instance of account repository for CRUD operations
    // Static method - can be used by other classes without instantiation/implementation
    public static AccountRepository<? extends UserAccount> getInstance(Role userRole) {
        if (userRole == Role.CUSTOMER) {
            return CustomerAccountRepository.getInstance();
        } else if (userRole == Role.STAFF) {
            return StaffAccountRepository.getInstance();
        } else {
            throw new IllegalArgumentException("Invalid user role:" +  userRole);
        }
    }

    @Override
    public OperationResult createRecord(T accToCreate) {

        try (BufferedWriter bufferWrite = new BufferedWriter(new FileWriter(getFilePath(), true))) {

            // Write the new account record into the file
            bufferWrite.write(accToCreate.toTextFile());
            bufferWrite.newLine();

            // Add the new account records into the maps to ensure the latest changes are reflected
            addRecordIntoMap(accToCreate);

        } catch (IOException e) {
            System.out.println(e.getMessage()); // Print exception message for debugging
            return OperationResult.failed(
                    "Oops! There was an error writing the new account record. Please try again.");
        }

        return OperationResult.success(
                "The account record has been created successfully. " +
                        "Please check the record to verify its creation.");
    }

    @Override
    public OperationResult updateRecord(T accToUpdate) {

        String accountId = accToUpdate.getAccountID();

        // Map the target account object by its accountID
        Map<String, Function<String, Optional<String>>> modifier = Map.of(
                accountId, _ -> Optional.of(accToUpdate.toTextFile())
        );

        // Rewrite the file to update the target record
        OperationResult result = rewriteFile(modifier);
        if (!result.isValid()) {
            return result;
        }

        // Replace the old record with the updated one to apply the latest changes
        removeRecordIntoMap(accountByAccId.get(accountId));
        addRecordIntoMap(accToUpdate);

        return OperationResult.success(
                "The account with ID " + accountId + " has been updated successfully. "
                        + "Please check the details to ensure the changes are applied.");
    }

    @Override
    public OperationResult deleteRecord(T accToDelete) {

        String accountId = accToDelete.getAccountID();

        // Map the target account object by its accountID
        Map<String, Function<String, Optional<String>>> modifier = Map.of(
                accountId, _ -> Optional.empty()
        );

        // Rewrite the file to delete the target record
        OperationResult result = rewriteFile(modifier);
        if (!result.isValid()) {
            return result;
        }

        // Remove the deleted record from the maps to apply the latest changes
        removeRecordIntoMap(accToDelete);

        return OperationResult.success(
                "The account with ID " + accountId + " has been deleted successfully. "
                        + "Please check the records to verify its deletion.");
    }

    @Override
    public List<T> getAllRecords() {
        return new ArrayList<>(accountByAccId.values());
    }

    @Override
    public T getRecordById(String accId) {
        return accountByAccId.get(accId);
    }

    public T getAccountByUserId(String userId) {
        return accountByUserId.get(userId);
    }

    public T getAccountByEmail(String email) {
        return accountByEmail.get(email);
    }

    /**
     * Parse a single CSV line into a specific account object
     * The subclass provides implementation to parse the record into its concrete product
     */
    @Override
    protected abstract T parseRecord(String line);

    /**
     * Add a new/updated account into all lookup maps
     */
    @Override
    protected void addRecordIntoMap(T account) {

        // End the method early if the account is not provided
        if (account == null) {
            return;
        }

        accountByAccId.put(account.getAccountID(), account);
        accountByUserId.put(account.getUserID(), account);
        accountByEmail.put(account.getEmailAddress(), account);
    }

    /**
     * Remove the old/deleted account from all lookup maps
     */
    @Override
    protected void removeRecordIntoMap(T account) {

        // End the method early if the account is not provided
        if (account == null) {
            return;
        }

        accountByAccId.remove(account.getAccountID());
        accountByUserId.remove(account.getUserID());
        accountByEmail.remove(account.getEmailAddress());
    }
}
