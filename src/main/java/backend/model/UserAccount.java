package main.java.backend.model;

import main.java.enums.data.Role;
import main.java.enums.data.AccountStatus;
import main.java.records.PasswordHash;

import java.time.LocalDate;

public class UserAccount {

    private final String accountID;
    private final LocalDate accountCreationDate;
    private String userID;
    private final Role userRole;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String phoneNumber;
    private PasswordHash passwordHash;
    private AccountStatus accountStatus;

    public UserAccount(String accountID, LocalDate accountCreationDate, String userID, Role userRole, String firstName,
                       String lastName, String emailAddress, String phoneNumber, PasswordHash passwordHash,
                       AccountStatus accountStatus) {

        this.accountID = accountID;
        this.accountCreationDate = accountCreationDate;
        this.userID = userID;
        this.userRole = userRole;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.passwordHash = passwordHash;
        this.accountStatus = accountStatus;
    }

    public String getAccountID() {
        return accountID;
    }

    public LocalDate getAccountCreationDate() {
        return accountCreationDate;
    }

    public String getUserID() {
        return userID;
    }

    public Role getUserRole() {
        return userRole;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public PasswordHash getPasswordHash() {
        return passwordHash;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPasswordHash(PasswordHash passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    /**
     * Return a human-readable string representation of the object.
     * Used for logging, debugging, testing, or printing overall information.
     */
    @Override
    public String toString() {
        return "Account ID: " + accountID +
                "\nAccount Creation Date: " + accountCreationDate +
                "\nUser ID: " + userID +
                "\nUser Role: " + userRole.getUserRole() +
                "\nFirst Name: " + firstName +
                "\nLast Name: " + lastName +
                "\nEmail Address: " + emailAddress +
                "\nPhone Number: " + phoneNumber +
                "\nPassword: (not shown)" +
                "\nAccount Status: " + accountStatus.getAccStatus() +
                "\n";
    }

    /**
     * Return a CSV format string representation of the object.
     * Used for saving the object to a text file.
     */
    public String toTextFile() {
        return String.join(", ",
                accountID,
                accountCreationDate.toString(),
                userID,
                userRole.getUserRole(),
                firstName,
                lastName,
                emailAddress,
                phoneNumber,
                passwordHash.toString(),
                accountStatus.getAccStatus()
        );
    }
}
