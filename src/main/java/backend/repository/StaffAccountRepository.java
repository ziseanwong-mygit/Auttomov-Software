package main.java.backend.repository;

import main.java.backend.model.StaffAccount;
import main.java.enums.*;
import main.java.records.OperationResult;
import main.java.records.PasswordHash;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

/**
 * This repository class follows the singleton pattern to declare a single instance when the application starts.
 * Instance is usually created in the Main class.
 */
public class StaffAccountRepository implements AccountRepository<StaffAccount> {

    private static StaffAccountRepository instance;
    private final String filePath = TextFilePath.STAFF_ACCOUNT_FILE_PATH.getTextFilePath();

    private final Map<String, StaffAccount> accountByAccId = new HashMap<>();
    private final Map<String, StaffAccount> accountByUserId = new HashMap<>();
    private final Map<String, StaffAccount> accountByEmail = new HashMap<>();

    // Private constructor to prevent instantiations from outside
    // Pre-populate the account records into maps when the instance is created
    private StaffAccountRepository() {
        retrieveAccounts();
    }

    // Global access point by creating instance for once only
    // So all classes can access the same instance for reading and writing files
    public static StaffAccountRepository getInstance() {
        if (instance == null) {
            instance = new StaffAccountRepository();
        }
        return instance;
    }

    private void retrieveAccounts() {
        
        try (BufferedReader bufferRead = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = bufferRead.readLine()) != null) {
                String[] column = line.split(",\\s*");
                String specialization = null;

                // If the staff account record is a technician, the specialization field is included
                if (column.length == 14) {
                    specialization = column[13];
                }

                StaffAccount staffAcc = new StaffAccount(
                        column[0], LocalDate.parse(column[1]),
                        column[2], Role.getRoleByValue(column[3]),
                        column[4], column[5], column[6], column[7],
                        PasswordHash.toRecord(column[8]),
                        AccountStatus.getAccStatusByValue(column[9]),
                        JobPosition.getJobPosByValue(column[10]),
                        LocalDate.parse(column[11]),
                        EmploymentStatus.getEmpStatusByValue(column[12]),
                        specialization
                );

                accountByAccId.put(staffAcc.getAccountID(), staffAcc);
                accountByUserId.put(staffAcc.getUserID(), staffAcc);
                accountByEmail.put(staffAcc.getEmailAddress(), staffAcc);
            }

        } catch (IOException e) {
            System.out.println("Oops! There was an error reading staff account records in the file. Please try again!");
        }
    }

    @Override
    public OperationResult createAccount(StaffAccount accToCreate) {

        try (BufferedWriter bufferWrite = new BufferedWriter(new FileWriter(filePath, true))) {

            bufferWrite.write(accToCreate.toString());
            bufferWrite.newLine();

            // Add the new account records into the maps to ensure the latest changes are reflected
            accountByAccId.put(accToCreate.getAccountID(), accToCreate);
            accountByUserId.put(accToCreate.getUserID(), accToCreate);
            accountByEmail.put(accToCreate.getEmailAddress(), accToCreate);

        } catch (IOException e) {
            // TODO: Need to adapt to Swing/AWT interface
            return OperationResult.failed("Oops! There was an error writing new staff account " +
                    "record in the file. Please try again.");
        }

        return OperationResult.success(
                "Your account has been created successfully. Please wait for the manager's approval " +
                        "to access the system with your account.");
    }

    /**
     * Rewrite the entire file to update or delete the target record.
     * Use key-value pairs to identify (accountID) and modify the target record (Function<>) in one or more accounts
     * Optional.of(newLine) - to replace the matched line (update)
     * Optional.empty() - to skip the matched line (delete)
     */
    private OperationResult rewriteFile(Map<String, Function<String, Optional<String>>> recordModifier) {

        List<String> updatedLines = new ArrayList<>();
        Set<String> accountsFound = new HashSet<>();

        try (BufferedReader bufferRead = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = bufferRead.readLine()) != null) {
                String[] column = line.split(",\\s*");
                String targetID = column[0];

                // If accountID matches, add the ID into a set and start the functional interface
                // Retrieve the current record, and check if the optional value is present or not
                // If present, add the updated line into the list to update, otherwise skip the line to delete
                if (recordModifier.containsKey(targetID)) {
                    accountsFound.add(targetID);
                    recordModifier.get(targetID).apply(line).ifPresent(updatedLines::add);
                } else {
                    updatedLines.add(line); // keep unaffected lines
                }
            }

        } catch (IOException e) {
            return OperationResult.failed("Oops! There was an error reading staff account records" +
                    "in the file. Please try again!");
        }

        // Determine if all target accounts are found or not
        Set<String> accountsNotFound = new HashSet<>(recordModifier.keySet());
        accountsNotFound.removeAll(accountsFound);
        if (!accountsNotFound.isEmpty()) {
            return OperationResult.failed("Oops! The following accounts are not found in the system: " +
                    accountsNotFound);
        }

        // Write all the remaining/updated lines back to the file
        try (BufferedWriter bufferWrite = new BufferedWriter(new FileWriter(filePath, false))) {
            for (int i = 0; i < updatedLines.size(); i++) {
                bufferWrite.write(updatedLines.get(i));
                bufferWrite.newLine();
            }

        } catch (IOException e) {
            return OperationResult.failed("Oops! There was an error writing records into the file. " +
                    "Please try again.");
        }

        return OperationResult.success();
    }

    /**
     * For updating one single account only
     */
    @Override
    public OperationResult updateAccount(StaffAccount accToUpdate) {

        String accountID = accToUpdate.getAccountID();

        // Map the target account object by its accountID
        Map<String, Function<String, Optional<String>>> recordModifier = Map.of(
                accountID, line -> Optional.of(accToUpdate.toString())
        );

        // Rewrite the file to update the target record
        OperationResult result = rewriteFile(recordModifier);
        if (!result.isValid()) {
            return result;
        }

        // Update the maps with the updated record to apply the latest changes
        accountByAccId.put(accountID, accToUpdate);
        accountByUserId.put(accToUpdate.getUserID(), accToUpdate);
        accountByEmail.put(accToUpdate.getEmailAddress(), accToUpdate);

        return OperationResult.success("The staff account with ID: " + accountID + " is updated " +
                "successfully. Please check the details to ensure the changes are applied.");
    }

    /**
     * For deleting one single account
     */
    @Override
    public OperationResult deleteAccount(StaffAccount accToDelete) {

        String accountID = accToDelete.getAccountID();

        // Map the target account object by its accountID
        Map<String, Function<String, Optional<String>>> recordModifier = Map.of(
                accToDelete.getAccountID(), line -> Optional.empty()
        );

        // Rewrite the file to delete the target record
        OperationResult result = rewriteFile(recordModifier);
        if (!result.isValid()) {
            return result;
        }

        // Remove the deleted record from the maps to apply the latest changes
        accountByAccId.remove(accountID);
        accountByUserId.remove(accToDelete.getUserID());
        accountByEmail.remove(accToDelete.getEmailAddress());

        return OperationResult.success("The staff account with ID " + accountID +
                " is deleted successfully. Please check the account records to confirm the deletion.");
    }

    @Override
    public List<StaffAccount> getAllAccounts() {
        return new ArrayList<>(accountByAccId.values());
    }

    @Override
    public StaffAccount getAccountByAccId(String accId) {
        return accountByAccId.get(accId);
    }

    @Override
    public StaffAccount getAccountByUserId(String userId) {
        return accountByUserId.get(userId);
    }

    @Override
    public StaffAccount getAccountByEmail(String email) {
        return accountByEmail.get(email);
    }

    /*
    /**
     * For updating multiple accounts
    @Override
    public OperationResult updateAccount(List<StaffAccount> accToUpdateList) {

        // For each account, map the target account object by its accountID
        Map<String, Function<String, Optional<String>>> recordModifier = new HashMap<>();
        for (StaffAccount acc : accToUpdateList) {
            recordModifier.put(acc.getAccountID(), line -> Optional.of(acc.toString()));
        }

        // Rewrite the file to update the target records
        OperationResult result = rewriteFile(recordModifier);
        if (!result.isValid()) {
            return result;
        }

        // Update all maps with the updated records to apply the latest changes
        for (StaffAccount acc : accToUpdateList) {
            accountByAccId.put(acc.getAccountID(), acc);
            accountByUserId.put(acc.getUserID(), acc);
            accountByEmail.put(acc.getEmailAddress(), acc);
        }

        return OperationResult.success(accToUpdateList.size() + " staff accounts are updated " +
                "successfully. Please check all the accounts' details to ensure the changes are applied.");
    }

    /**
     * For deleting multiple accounts
    @Override
    public OperationResult deleteAccount(List<StaffAccount> accToDeleteList) {

        // For each account, map the target account object by its accountID
        Map<String, Function<String, Optional<String>>> recordModifier = new HashMap<>();
        for (StaffAccount acc : accToDeleteList) {
            recordModifier.put(acc.getAccountID(), line -> Optional.empty());
        }

        // Rewrite the file to delete the target record
        OperationResult result = rewriteFile(recordModifier);
        if (!result.isValid()) {
            return result;
        }

        // Remove all deleted records from the maps to apply the latest changes
        for (StaffAccount acc : accToDeleteList) {
            accountByAccId.remove(acc.getAccountID());
            accountByUserId.remove(acc.getUserID());
            accountByEmail.remove(acc.getEmailAddress());
        }

        return OperationResult.success(accToDeleteList.size() + " staff accounts are deleted " +
                "successfully. Please check the account records to confirm the deletion.");
    }
    */
}
