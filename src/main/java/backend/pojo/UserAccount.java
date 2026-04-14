package main.java.backend.pojo;

import main.java.enums.Role;
import main.java.enums.AccountStatus;

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
    private String passwordHash;
    private AccountStatus accountStatus;

    public UserAccount(String userID, Role userRole, String firstName, String lastName, String emailAddress,
                       String phoneNumber, String passwordHash) {
        this.accountID = UUID.randomUUID().toString();
        this.accountCreationDate = LocalDate.now();
        this.userID = userID;
        this.userRole = userRole;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.passwordHash = passwordHash;
        this.accountStatus = AccountStatus.ACTIVE;
    }

    /**
     * Use this specifically for your file writing logic.
     * It's better than an array because the format is explicit.
     */
    public String toTextFileFormat() {
        return String.join(",",
                accountID,
                accountCreationDate.toString(),
                userID,
                userRole.toString(),
                firstName,
                lastName,
                emailAddress,
                phoneNumber,
                passwordHash,
                accountStatus.toString()
        );
    }
}
