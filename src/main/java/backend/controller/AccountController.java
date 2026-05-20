package main.java.backend.controller;

import main.java.backend.model.UserAccount;
import main.java.backend.repository.account.AccountRepository;
import main.java.backend.repository.account.StaffAccountRepository;
import main.java.backend.service.RecordReader;
import main.java.backend.service.RecordWriter;
import main.java.backend.service.account.*;
import main.java.backend.util.DataValidation;
import main.java.backend.util.NullValidator;
import main.java.dtos.StaffAccountDTO;
import main.java.dtos.UserAccountDTO;
import main.java.enums.data.AccountStatus;
import main.java.enums.data.JobPosition;
import main.java.enums.data.Role;
import main.java.enums.system.OperationType;
import main.java.records.OperationResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used for managing both customer and staff accounts
 */
public class AccountController {

    public OperationResult createAccount(UserAccountDTO userAccountDTO) {

        // Check if there are any null or empty values in the list
        OperationResult nullValidation = validateNullValues(userAccountDTO, OperationType.CREATE);
        if (!nullValidation.isValid()) {
            return OperationResult.failed("All required fields must be filled in. Please try again.");
        }

        // Check if all values have the correct format/standard
        OperationResult formatValidation = validateInputFormats(userAccountDTO);
        if (!formatValidation.isValid()) {
            return formatValidation;
        }

        // Instantiate the repository and service for creating records
        // Wildcard is used so can accept either customer or staff repository in one variable
        AccountRepository<? extends UserAccount> accountRepo = AccountRepository.getInstance(
                userAccountDTO.getUserRole());
        RecordWriter createAccount = new AccountCreation(accountRepo, userAccountDTO);

        // Begin creating account record procedure in service class
        return createAccount.execute();
    }

    public OperationResult loginAccount(UserAccountDTO userAccountDTO) {

        Role userRole = userAccountDTO.getUserRole();
        String emailAddress = userAccountDTO.getEmailAddress();
        String password = userAccountDTO.getPassword();

        // Check if there are any null or empty values in the list
        if (userRole == null || emailAddress == null || password == null ||
                emailAddress.isBlank() || password.isBlank()) {
            return OperationResult.failed("All required fields must be filled in. Please try again.");
        }

        // Check email address format
        // If email address format is invalid, return error message
        OperationResult emailFormatValidation = DataValidation.validateEmailAddress(emailAddress);
        if (!emailFormatValidation.isValid()) {
            return emailFormatValidation;
        }

        // Instantiate the repository and service for creating records
        // Wildcard is used so can accept either customer or staff repository in one variable
        AccountRepository<? extends UserAccount> accountRepo = AccountRepository.getInstance(
                userAccountDTO.getUserRole());
        RecordWriter loginAccount = new AccountLogin(accountRepo, userAccountDTO);

        // Begin logging in account procedure in service class
        return loginAccount.execute();
    }

    public OperationResult updateAccount(UserAccountDTO userAccountDTO) {

        // Check if there are any null or empty values in the list
        OperationResult nullValidation = validateNullValues(userAccountDTO, OperationType.UPDATE);
        if (!nullValidation.isValid()) {
            return OperationResult.failed("All required fields must be filled in. Please try again.");
        }

        // Instantiate the repository and service for updating records
        // Wildcard is used so can accept either customer or staff repository in one variable
        AccountRepository<? extends UserAccount> accountRepo = AccountRepository.getInstance(
                userAccountDTO.getUserRole());
        RecordWriter updateAccount = new AccountUpdate(accountRepo, userAccountDTO);

        // Begin updating account record procedure in service class
        return updateAccount.execute();
    }

    public OperationResult updateStaffAccountStatus(StaffAccountDTO staffAccountDTO) {

        String accountID = staffAccountDTO.getAccountID();
        AccountStatus accountStatus = staffAccountDTO.getAccountStatus();

        if (accountID == null || accountStatus == null || accountID.isBlank()) {
            return OperationResult.failed("All required fields must be filled in. Please try again.");
        }

        // Instantiate the repository and service for updating records
        StaffAccountRepository staffAccountRepo = StaffAccountRepository.getInstance();
        RecordWriter updateStatus = new StaffAccountStatusUpdate(staffAccountRepo, staffAccountDTO);

        // Begin updating account status procedure in service class
        OperationResult updateStatusResult = updateStatus.execute();

        // Customize result message for updating account password
        if (updateStatusResult.isValid()) {
            return OperationResult.success("The account status for ID: " + accountID +
                    " has been updated successfully. Please check the new status to ensure it has been changed.");
        } else {
            return OperationResult.failed("Oops! The account status for ID: " + accountID +
            " has failed to be updated. Please try again.");
        }
    }

    public OperationResult updateAccountPassword(UserAccountDTO userAccountDTO) {

        String accountID = userAccountDTO.getAccountID();
        Role userRole = userAccountDTO.getUserRole();
        String password = userAccountDTO.getPassword();
        String confirmPassword = userAccountDTO.getConfirmPassword();

        if (accountID == null || userRole == null || password == null || confirmPassword == null ||
                accountID.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            return OperationResult.failed("All required fields must be filled in. Please try again.");
        }

        OperationResult pwdValidation = DataValidation.validatePassword(password, confirmPassword);
        if (!pwdValidation.isValid()) {
            return pwdValidation;
        }

        // Instantiate the repository and service for updating records
        // Wildcard is used so can accept either customer or staff repository in one variable
        AccountRepository<? extends UserAccount> accountRepo = AccountRepository.getInstance(
                userAccountDTO.getUserRole());
        RecordWriter updatePassword = new AccountPasswordUpdate(accountRepo, userAccountDTO);

        // Begin updating account password procedure in service class
        OperationResult updatePwdResult = updatePassword.execute();

        // Customize result message for updating account password
        if (updatePwdResult.isValid()) {
            return OperationResult.success("Your account password has been updated successfully. " +
                    "Please check your new password to ensure it has been changed.");
        } else {
            return OperationResult.failed("Oops! Your account password has failed to be updated. " +
                    "Please try again.");
        }
    }

    public OperationResult deleteAccount(String accountID, Role userRole) {

        // Check if the inputs are null or empty
        if (accountID == null || userRole == null || accountID.isBlank()) {
            return OperationResult.failed("The account ID and user role must be provided to " +
                    "delete a record. Please try again.");
        }

        // Instantiate the repository and service for deleting records
        // Wildcard is used so can accept either customer or staff repository in one variable
        AccountRepository<? extends UserAccount> accountRepo = AccountRepository.getInstance(userRole);
        RecordWriter deleteAccount = new AccountDeletion(accountRepo, accountID);

        // Begin logging in account procedure in service class
        return deleteAccount.execute();
    }

    public List<? extends UserAccount> retrieveAllAccounts(Role userRole) {

        // Instantiate the repository and service for retrieving records
        // Wildcard is used so can accept either customer or staff repository in one variable
        AccountRepository<? extends UserAccount> accountRepo = AccountRepository.getInstance(userRole);
        RecordReader retrieveAllAccounts = new AccountRetrieval(accountRepo);

        // Retrieve all the records from the repository
        List<?> unknownList = retrieveAllAccounts.getAllRecords();

        return unknownList.stream()
                .map(obj -> (UserAccount) obj)
                .toList();
    }

    public OperationResult retrieveAccountByAccID(String accountID, Role userRole) {

        // Check if the inputs are null or empty
        if (accountID == null || userRole == null || accountID.isBlank()) {
            return OperationResult.failed("All required fields must be filled in. Please try again.");
        }

        // Instantiate the repository and service for retrieving records
        // Wildcard is used so can accept either customer or staff repository in one variable
        AccountRepository<? extends UserAccount> accountRepo = AccountRepository.getInstance(userRole);
        RecordReader retrieveAccountByAccID = new AccountRetrieval(accountRepo);

        return retrieveAccountByAccID.getRecordById(accountID);
    }

    public OperationResult retrieveAccountByUserID(String userID, Role userRole) {

        // Check if the inputs are null or empty
        if (userID == null || userRole == null || userID.isBlank()) {
            return OperationResult.failed("All required fields must be filled in. Please try again.");
        }

        // Instantiate the repository and service for retrieving records
        // Wildcard is used so can accept either customer or staff repository in one variable
        AccountRepository<? extends UserAccount> accountRepo = AccountRepository.getInstance(userRole);
        AccountRetrieval retrieveAccountByUserID = new AccountRetrieval(accountRepo);

        return retrieveAccountByUserID.getAccountByUserID(userID);
    }

    public OperationResult retrieveAccountByEmail(String emailAddress, Role userRole) {

        // Check if the inputs are null or empty
        if (emailAddress == null || userRole == null || emailAddress.isBlank()) {
            return OperationResult.failed("All required fields must be filled in. Please try again.");
        }

        // Instantiate the repository and service for retrieving records
        // Wildcard is used so can accept either customer or staff repository in one variable
        AccountRepository<? extends UserAccount> accountRepo = AccountRepository.getInstance(userRole);
        AccountRetrieval retrieveAccountByEmail = new AccountRetrieval(accountRepo);

        return retrieveAccountByEmail.getAccountByEmail(emailAddress);
    }

    /**
     * Check if there are any null or empty values of required fields in the given DTO
     * Used for creating and updating records
     */
    private OperationResult validateNullValues(UserAccountDTO userAccountDTO, OperationType operationType) {
        List<Object> accountValues = new ArrayList<>();

        switch (operationType) {
            case CREATE -> {
                accountValues.add(userAccountDTO.getUserID());
                accountValues.add(userAccountDTO.getUserRole());
                accountValues.add(userAccountDTO.getFirstName());
                accountValues.add(userAccountDTO.getLastName());
                accountValues.add(userAccountDTO.getEmailAddress());
                accountValues.add(userAccountDTO.getPhoneNumber());
                accountValues.add(userAccountDTO.getPassword());
                accountValues.add(userAccountDTO.getConfirmPassword());

                if (userAccountDTO instanceof StaffAccountDTO staffAccountDTO) {
                    accountValues.add(staffAccountDTO.getJobPosition());
                    accountValues.add(staffAccountDTO.getEmploymentStatus());
                    if (staffAccountDTO.getJobPosition() == JobPosition.TECHNICIAN) {
                        accountValues.add(staffAccountDTO.getSpecialization());
                    }
                }
            }

            case UPDATE -> {
                accountValues.add(userAccountDTO.getAccountID());
                accountValues.add(userAccountDTO.getUserID());
                accountValues.add(userAccountDTO.getUserRole());
                accountValues.add(userAccountDTO.getFirstName());
                accountValues.add(userAccountDTO.getLastName());
                accountValues.add(userAccountDTO.getEmailAddress());
                accountValues.add(userAccountDTO.getPhoneNumber());

                if (userAccountDTO instanceof StaffAccountDTO staffAccountDTO) {
                    accountValues.add(staffAccountDTO.getJobPosition());
                    accountValues.add(staffAccountDTO.getEmploymentDate());
                    accountValues.add(staffAccountDTO);
                    if (staffAccountDTO.getJobPosition() == JobPosition.TECHNICIAN) {
                        accountValues.add(staffAccountDTO.getSpecialization());
                    }
                }
            }
        }

        return NullValidator.isAllValuesNull(accountValues);
    }

    /**
     * Check all the formats/lengths of the values
     */
    private OperationResult validateInputFormats(UserAccountDTO userAccountDTO) {

        // Create a shared map to collect all error messages
        Map<String, String> allErrors = new HashMap<>();

        // Validate the user detail first, then if there are errors, collect them into the shared list
        collectErrors(allErrors, DataValidation.validateUserId(userAccountDTO.getUserID()));
        collectErrors(allErrors, DataValidation.validateEmailAddress(userAccountDTO.getEmailAddress()));
        collectErrors(allErrors, DataValidation.validatePhoneNumber(userAccountDTO.getPhoneNumber()));
        collectErrors(allErrors, DataValidation.validatePassword(userAccountDTO.getPassword(),
                userAccountDTO.getConfirmPassword()));

        // If all validations are successful, return the success message
        // Otherwise, return the map of error messages
        return allErrors.isEmpty()
                ? OperationResult.success(
                "Field validation is successful. Proceed to account creation.")
                : OperationResult.failed(
                "Field validation is unsuccessful. Please try again.", allErrors);
    }

    /**
     * For each validation, if there are errors, collect the field name and error message into the shared map
     */
    private void collectErrors(Map<String, String> allErrors, OperationResult result) {
        if (!result.isValid()) {
            allErrors.putAll(result.fieldErrors());
        }
    }
}
