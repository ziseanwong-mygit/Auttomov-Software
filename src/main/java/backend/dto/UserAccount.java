package main.java.backend.dto;

import main.java.enums.Role;
import main.java.enums.AccountStatus;
import main.java.records.PasswordHash;

import java.time.LocalDate;
import java.util.UUID;

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

    public String getUserID() {
        return userID;
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
     * Gather all data into one single line, with each value separated by commas
     * To ease the process of writing new record into the file
     */
    @Override
    public String toString() {
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
