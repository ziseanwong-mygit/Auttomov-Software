package main.java.backend.pojo;

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

    public UserAccount(String userID, Role userRole, String firstName, String lastName, String emailAddress,
                       String phoneNumber, PasswordHash passwordHash) {

        // Chained to the constructor above
        this(
                UUID.randomUUID().toString(),
                LocalDate.now(),
                userID,
                userRole,
                firstName,
                lastName,
                emailAddress,
                phoneNumber,
                passwordHash,
                AccountStatus.ACTIVE
        );
    }

    public String getUserID() {
        return userID;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public PasswordHash getPasswordHash() {
        return passwordHash;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
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
