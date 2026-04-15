package main.java.backend.account;

import main.java.backend.account.service.PswdEncryption;
import main.java.backend.general.service.UserDataValidation;
import main.java.backend.pojo.StaffAccount;
import main.java.backend.repository.UserAccRepository;
import main.java.backend.pojo.UserAccount;
import main.java.enums.EmploymentStatus;
import main.java.enums.JobPosition;
import main.java.enums.Role;
import main.java.enums.TextFilePath;
import main.java.records.PasswordHash;
import main.java.records.OperationResult;

public class AccountSignUp {

    private String userID;
    private Role userRole;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String phoneNumber;
    private PasswordHash passwordHash;

    // For staff only
    private JobPosition jobPosition;
    private EmploymentStatus employmentStatus;
    private String specialization;

    // A flag to prevent account creation if data is not validated
    private boolean isDataValidated = false;

    public OperationResult validateUserData(String userID, Role userRole, String firstName, String lastName,
                                            String emailAddress, String phoneNumber, String password,
                                            String confirmPassword) {

        OperationResult validationResult = UserDataValidation.validateAllDetails(
                userID, emailAddress, phoneNumber, password, confirmPassword);

        if (validationResult.isValid()) {
            this.userID = userID;
            this.userRole = userRole;
            this.firstName = firstName;
            this.lastName = lastName;
            this.emailAddress = emailAddress;
            this.phoneNumber = phoneNumber;
            this.passwordHash = PswdEncryption.hashNewPassword(password); // Hash the user's password first
            this.isDataValidated = true;
        }

        return validationResult;
    }

    public OperationResult validateUserData(String userID, Role userRole, String firstName, String lastName,
                                            String emailAddress, String phoneNumber, String password,
                                            String confirmPassword, JobPosition jobPosition,
                                            EmploymentStatus employmentStatus, String specialization) {

        OperationResult validationResult = UserDataValidation.validateAllDetails(
                userID, emailAddress, phoneNumber, password, confirmPassword);

        if (validationResult.isValid()) {

            this.userID = userID;
            this.userRole = userRole;
            this.firstName = firstName;
            this.lastName = lastName;
            this.emailAddress = emailAddress;
            this.phoneNumber = phoneNumber;
            this.passwordHash = PswdEncryption.hashNewPassword(password); // Hash the user's password first
            this.jobPosition = jobPosition;
            this.employmentStatus = employmentStatus;
            this.specialization = specialization;
            this.isDataValidated = true;
        }

        return validationResult;
    }

    // Create accounts for customer, technician, counter staff and manager
    public OperationResult registerNewAccount() {

        if (!isDataValidated) {
            return OperationResult.failed("User details are not validated yet. " +
                    "Please go through the validation process before account creation.");
        }

        if (userRole == Role.STAFF) {
            StaffAccount staffAcc = new StaffAccount(
                    userID, userRole, firstName, lastName, emailAddress, phoneNumber, passwordHash, jobPosition,
                    employmentStatus, specialization);

            UserAccRepository accRepository = new UserAccRepository(TextFilePath.STAFF_ACCOUNT_FILE_PATH);
            return accRepository.createNewAccount(staffAcc);

        } else {
            UserAccount customerAcc = new UserAccount(
                    userID, userRole, firstName, lastName, emailAddress, phoneNumber, passwordHash);

            UserAccRepository accRepository = new UserAccRepository(TextFilePath.CUSTOMER_ACCOUNT_FILE_PATH);
            return accRepository.createNewAccount(customerAcc);
        }
    }
}
