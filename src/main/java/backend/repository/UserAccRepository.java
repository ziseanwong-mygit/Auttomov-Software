package main.java.backend.repository;

import main.java.backend.pojo.StaffAccount;
import main.java.backend.pojo.UserAccount;
import main.java.enums.Role;
import main.java.enums.AccountStatus;
import main.java.enums.TextFilePath;
import main.java.records.OperationResult;
import main.java.records.PasswordHash;

import java.io.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class UserAccRepository {

    // Map key: field value, map value: UserAccount object
    private final Map<String, UserAccount> accountByUserId = new HashMap<>();
    private final Map<String, UserAccount> accountByEmail = new HashMap<>();
    private final Map<String, UserAccount> accountByPasswordHash = new HashMap<>();
    private final TextFilePath filePath;

    // Constructor to get the file path and start reading the file
    public UserAccRepository(TextFilePath filePath) {
        this.filePath = filePath;
        readAndStoreAccountByField();
    }

    public OperationResult createNewAccount(UserAccount customerAcc) {
        try (BufferedWriter bufferWrite =
                     new BufferedWriter(new FileWriter(filePath.getTextFilePath(), true));) {

            bufferWrite.write(customerAcc.toString());

            accountByUserId.put(customerAcc.getUserID(), customerAcc);
            accountByEmail.put(customerAcc.getEmailAddress(), customerAcc);

        } catch (IOException e) {
            // TODO: Need to adapt to Swing/AWT interface
            return OperationResult.failed(
                    "There was an error in creating a new account. Please try again.");
        }

        return OperationResult.success(
                "Your account has been created successfully. You may now log in your user account.");
    }

    public OperationResult createNewAccount(StaffAccount staffAcc) {
        try (BufferedWriter bufferWrite =
                     new BufferedWriter(new FileWriter(filePath.getTextFilePath(), true));) {

            bufferWrite.write(staffAcc.toString());

            accountByUserId.put(staffAcc.getUserID(), staffAcc);
            accountByEmail.put(staffAcc.getEmailAddress(), staffAcc);

        } catch (IOException e) {
            // TODO: Need to adapt to Swing/AWT interface
            return OperationResult.failed(
                    "There was an error in creating a new account. Please try again.");
        }

        return OperationResult.success(
                "Your account has been created successfully. Please wait for the manager's approval " +
                        "to access the system with your account.");
    }

    /**
     * Read all records from the text file and populates all records into a map.
     */
    private void readAndStoreAccountByField() {
        try (BufferedReader bufferRead = new BufferedReader(new FileReader(filePath.getTextFilePath()))) {
            String line;

            while ((line = bufferRead.readLine()) != null) {

                String[] column = line.split(",\\s*");

                // Convert array to a new UserAccount object
                UserAccount userAcc = new UserAccount(
                        column[0],
                        LocalDate.parse(column[1]),
                        column[2],
                        Role.getRoleByValue(column[3]),
                        column[4],
                        column[5],
                        column[6],
                        column[7],
                        PasswordHash.toRecord(column[8]),
                        AccountStatus.getAccStatusByValue(column[9])
                );

                // Add UserAccount object into the map by its userID and email
                // Used for instant lookup on matching data
                accountByUserId.put(userAcc.getUserID(), userAcc);
                accountByEmail.put(userAcc.getEmailAddress(), userAcc);
                accountByPasswordHash.put(userAcc.getPasswordHash().pswdHash(), userAcc);
            }

        } catch (IOException e) {
            // TODO: Need to adapt to Swing/AWT interface
            System.out.println("Oops! There was an error reading data from the specified file. Please try again!");
        }
    }

    /**
     * Get the UserAccount object by the input data.
     * Return null if the account is not found.
     */
    public UserAccount getUserAccByUserID(String userID) {
        return accountByUserId.get(userID);
    }

    public UserAccount getUserAccByEmail(String email) {
        return accountByEmail.get(email);
    }
}
