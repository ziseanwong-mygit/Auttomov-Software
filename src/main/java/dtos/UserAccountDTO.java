package main.java.dtos;

import main.java.enums.data.AccountStatus;

public class UserAccountDTO {

    private String accountID;
    private String userID;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String phoneNumber;
    private String password;
    private String confirmPassword;
    private AccountStatus accountStatus;

    /**
     * For creating a new account
     */
    public UserAccountDTO(String userID, String firstName, String lastName, String emailAddress,
                          String phoneNumber, String password, String confirmPassword) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    /**
     * For logging in an existing account
     */
    public UserAccountDTO(String emailAddress, String password) {
        this.emailAddress = emailAddress;
        this.password = password;
    }

    /**
     * For updating an existing account's details
     */
    public UserAccountDTO(String accountID, String userID, String firstName, String lastName,
                          String emailAddress, String phoneNumber, AccountStatus accountStatus) {
        this.accountID = accountID;
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.accountStatus = accountStatus;
    }

    /**
     * For updating the password of an existing account
     */
    public UserAccountDTO(String accountID, String password, String confirmPassword) {
        this.accountID = accountID;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    /**
     * For updating the status of a pending staff account
     */
    public UserAccountDTO(String accountID, AccountStatus accountStatus) {
        this.accountID = accountID;
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

    public String getPassword() {
        return password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }
}
