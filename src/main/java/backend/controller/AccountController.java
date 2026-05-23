package main.java.backend.controller;

import main.java.backend.model.UserAccount;
import main.java.backend.repository.impl.account.AccountRepository;
import main.java.backend.repository.impl.account.CustomerAccountRepository;
import main.java.backend.repository.impl.account.StaffAccountRepository;
import main.java.backend.service.RecordReader;
import main.java.backend.service.RecordWriter;
import main.java.backend.service.account.*;
import main.java.backend.util.DataValidator;
import main.java.backend.util.NullValidator;
import main.java.dtos.StaffAccountDTO;
import main.java.dtos.UserAccountDTO;
import main.java.enums.data.AccountStatus;
import main.java.enums.data.JobPosition;
import main.java.enums.data.Role;
import main.java.enums.system.DataFieldName;
import main.java.enums.system.OperationType;
import main.java.records.OperationResult;

import java.util.*;

/**
 * Used for managing both customer and staff accounts
 */
public class AccountController {

    // Singleton instances of the repositories
    private final CustomerAccountRepository customerAccountRepo = CustomerAccountRepository.getInstance();
    private final StaffAccountRepository staffAccountRepo = StaffAccountRepository.getInstance();

    public OperationResult createAccount(UserAccountDTO userAccountDTO) {

        // Check if there are any null or empty values in the list
        OperationResult nullValidation = validateNullValues(userAccountDTO, OperationType.CREATE);
        if (!nullValidation.isValid()) {

        }

        // Check if all values have the correct format/standard
        OperationResult formatValidation = validateInputFormats(userAccountDTO);
        if (!formatValidation.isValid()) {
            return formatValidation;
        }

        // Instantiate the customer/staff account repository for creating records
        // Wildcard is used so can accept either customer or staff repository in one variable
        AccountRepository<? extends UserAccount> accountRepo = resolveAccRepo(userAccountDTO.getUserRole());

        // Begin creating account record procedure in service class
        RecordWriter createAccount = new AccountCreation(accountRepo, userAccountDTO);
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
        OperationResult emailFormatValidation = DataValidator.validateEmailAddress(emailAddress);
        if (!emailFormatValidation.isValid()) {
            return emailFormatValidation;
        }

        // Instantiate the customer/staff account repository for logging in account
        // Wildcard is used so can accept either customer or staff repository in one variable
        AccountRepository<? extends UserAccount> accountRepo = resolveAccRepo(userRole);

        // Begin logging in account procedure in service class
        RecordWriter loginAccount = new AccountLogin(accountRepo, userAccountDTO);
        return loginAccount.execute();
    }

    public OperationResult updateAccount(UserAccountDTO userAccountDTO) {

        // Check if there are any null or empty values in the list
        OperationResult nullValidation = validateNullValues(userAccountDTO, OperationType.UPDATE);
        if (!nullValidation.isValid()) {
            return OperationResult.failed("All required fields must be filled in. Please try again.");
        }

        // Check if the account ID is a valid UUID
        if (!DataValidator.validateRecordId(userAccountDTO.getAccountID()).isValid()) {
            return OperationResult.failed(DataFieldName.RECORD_ID, "The account ID: " +
                    userAccountDTO.getAccountID() + " is not an UUID. Please try again.");
        }

        // Instantiate the customer/staff account repository for updating records
        // Wildcard is used so can accept either customer or staff repository in one variable
        AccountRepository<? extends UserAccount> accountRepo = resolveAccRepo(userAccountDTO.getUserRole());

        // Begin updating account record procedure in service class
        RecordWriter updateAccount = new AccountUpdate(accountRepo, userAccountDTO);
        return updateAccount.execute();
    }

    public OperationResult updateStaffAccountStatus(StaffAccountDTO staffAccountDTO) {

        String accountID = staffAccountDTO.getAccountID();
        AccountStatus accountStatus = staffAccountDTO.getAccountStatus();

        if (accountID == null || accountStatus == null || accountID.isBlank()) {
            return OperationResult.failed("All required fields must be filled in. Please try again.");
        }

        // Check if the staff account ID is a valid UUID
        if (!DataValidator.validateRecordId(accountID).isValid()) {
            return OperationResult.failed(DataFieldName.RECORD_ID, "The account ID: " +
                    accountID + " is not an UUID. Please try again.");
        }

        // Instantiate the repository and service for updating records
        RecordWriter updateStatus = new StaffAccountStatusUpdate(staffAccountRepo, staffAccountDTO);

        // Begin updating account status procedure in service class
        OperationResult updateStatusResult = updateStatus.execute();

        // Customize result message for updating account status
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

        // Check if the account ID is a valid UUID
        if (!DataValidator.validateRecordId(accountID).isValid()) {
            return OperationResult.failed(DataFieldName.RECORD_ID, "The account ID: " +
                    accountID + " is not an UUID. Please try again.");
        }

        // Check the format and confirmation of the password
        OperationResult pwdValidation = DataValidator.validatePassword(password, confirmPassword);
        if (!pwdValidation.isValid()) {
            return pwdValidation;
        }

        // Instantiate the customer/staff account repository for updating records
        // Wildcard is used so can accept either customer or staff repository in one variable
        AccountRepository<? extends UserAccount> accountRepo = resolveAccRepo(userRole);

        // Begin updating account password procedure in service class
        RecordWriter updatePassword = new AccountPasswordUpdate(accountRepo, userAccountDTO);
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

        // Check if the account ID is a valid UUID
        if (!DataValidator.validateRecordId(accountID).isValid()) {
            return OperationResult.failed("The account ID: " + accountID +
                    " is not an UUID. Please try again.");
        }

        // Instantiate the customer/staff account repository for deleting records
        // Wildcard is used so can accept either customer or staff repository in one variable
        AccountRepository<? extends UserAccount> accountRepo = resolveAccRepo(userRole);

        // Begin deleting account record procedure in service class
        RecordWriter deleteAccount = new AccountDeletion(accountRepo, accountID);
        return deleteAccount.execute();
    }

    /**
     * Optional class is used to determine whether the records are present or not
     * This is for frontend logic in displaying the result message for UI
     */
    public Optional<List<UserAccount>> retrieveAllAccounts(Role userRole) {

        // Instantiate the customer/staff account repository for retrieving records
        // Wildcard is used so can accept either customer or staff repository in one variable
        AccountRepository<? extends UserAccount> accountRepo = resolveAccRepo(userRole);

        // Begin retrieving all account record procedures in service class
        RecordReader<UserAccount> retrieveAllAccounts = new AccountRetrieval(accountRepo);
        return retrieveAllAccounts.getAllRecords();
    }

    /**
     * The customer or staff account record can be retrieved by account ID, user ID, or email address
     */
    public Optional<UserAccount> retrieveAccountById(String uniqueId, Role userRole) {

        // Instantiate the repository and service for retrieving records
        // Wildcard is used so can accept either customer or staff repository in one variable
        AccountRepository<? extends UserAccount> accountRepo = resolveAccRepo(userRole);

        // Begin retrieving account record by ID procedure in service class
        RecordReader<UserAccount> retrieveAccountById = new AccountRetrieval(accountRepo);
        return retrieveAccountById.getRecordById(uniqueId);
    }

    /**
     * The customer or staff account records can be retrieved by a list of account IDs, user IDs, or email addresses
     */
    public Optional<List<UserAccount>> retrieveAccountsByIds(List<String> uniqueIds, Role userRole) {

        // Instantiate the customer/staff account repository for retrieving records
        // Wildcard is used so can accept either customer or staff repository in one variable
        AccountRepository<? extends UserAccount> accountRepo = resolveAccRepo(userRole);

        // Begin retrieving multiple account records by IDs procedure in service class
        RecordReader<UserAccount> retrieveAccountsByIds = new AccountRetrieval(accountRepo);
        return retrieveAccountsByIds.getRecordsByIds(uniqueIds);
    }

    public Optional<List<UserAccount>> retrieveAccountsByAccStatus(AccountStatus accountStatus, Role userRole) {

        // Instantiate the customer/staff account repository for retrieving records
        // Wildcard is used so can accept either customer or staff repository in one variable
        AccountRepository<? extends UserAccount> accountRepo = resolveAccRepo(userRole);

        // Begin retrieving multiple account records by account status procedure in service class
        AccountRetrieval retrieveAccountsByAccStatus = new AccountRetrieval(accountRepo);
        return retrieveAccountsByAccStatus.getRecordsByAccStatus(accountStatus);
    }

    /**
     * Validate the format and length of recordID (accountID), userID, or emailAddress
     * Used for retrieving a single record by ID
     * This validation is done before record retrieval, so the frontend can first display the validation result
     */
    public OperationResult validateUniqueId(String uniqueId, DataFieldName fieldName, Role userRole) {

        // Check if the unique ID and its type are null or empty
        if (uniqueId == null || uniqueId.isBlank() || fieldName == null || userRole == null) {
            return OperationResult.failed("The unique ID, field name and user role must be provided " +
                    "to retrieve records. Please try again.");
        }

        // Validate the unique ID based on its type
        if (fieldName == DataFieldName.RECORD_ID) {
            return DataValidator.validateRecordId(uniqueId);

        } else if (fieldName == DataFieldName.USER_ID) {
            return DataValidator.validateUserId(uniqueId);

        } else if (fieldName == DataFieldName.EMAIL_ADDRESS) {
            return DataValidator.validateEmailAddress(uniqueId);
        }

        return OperationResult.failed("The data field name is invalid for this validation. Please try again.");
    }

    /**
     * Validate the format and length of multiple account unique IDs; used for retreiving record by multiple IDs
     * This validation is done before retrieving multiple account records by their unique IDs
     */
    public OperationResult validateUniqueIds(List<String> uniqueIds, DataFieldName fieldName, Role userRole) {

        // Check if the ID list and its type are null or empty
        if (uniqueIds == null || fieldName == null || userRole == null) {
            return OperationResult.failed("The unique ID, field name and user role must be provided " +
                    "to retrieve records. Please try again.");
        }

        // Check if any of the unique IDs are null or empty
        for (String uniqueId : uniqueIds) {
            if (uniqueId == null || uniqueId.isBlank() ) {
                return OperationResult.failed("One of the unique IDs is null or empty. Please try again.");
            }
        }

        // If one of the unique IDs is invalid, return the error message immediately
        // Otherwise if all unique IDs are valid, return the success status
        if (fieldName == DataFieldName.RECORD_ID) {

            for (String uniqueId : uniqueIds) {
                OperationResult validationResult = DataValidator.validateRecordId(uniqueId);
                if (!validationResult.isValid()) {
                    return OperationResult.failed("The account ID: " + uniqueId +
                            " is not an UUID. Please try again.");
                }
            }
            return OperationResult.success();

        } else if (fieldName == DataFieldName.USER_ID) {

            for (String uniqueId : uniqueIds) {

                OperationResult validationResult = DataValidator.validateUserId(uniqueId);
                if (!validationResult.isValid()) {
                    return OperationResult.failed("The user ID: " + uniqueId +
                            " is not an IC number or passport number. Please try again.");
                }
            }
            return OperationResult.success();

        } else if (fieldName == DataFieldName.EMAIL_ADDRESS) {

            for (String uniqueId : uniqueIds) {

                OperationResult validationResult = DataValidator.validateEmailAddress(uniqueId);
                if (!validationResult.isValid()) {
                    return OperationResult.failed("The email address: " + uniqueId + " is invalid. Please try again.");
                }
            }
            return OperationResult.success();

        }

        // If the field name is not recognized, return an error message
        return OperationResult.failed("The data field name is invalid for this validation. Please try again.");
    }

    /**
     * Check if the user role is null before retrieving records; used for retrieving all records
     * So the frontend can display the validation result before beginning the retrieval
     */
    public OperationResult validateUserRole(Role userRole) {

        if (userRole == null) {
            return OperationResult.failed("The user role must be provided to retrieve records. " +
                    "Please try again.");
        }

        return OperationResult.success();
    }


    /**
     * Check if the account status is null before retrieving records; used for retrieving records by status
     * So the frontend can display the validation result before beginning the retrieval
     */
    public OperationResult validateAccountStatus(AccountStatus accountStatus, Role userRole) {

        if (accountStatus == null || userRole == null) {
            return OperationResult.failed("The account status and user role must be provided to retrieve records. " +
                    "Please try again.");
        }

        return OperationResult.success();
    }

    /**
     * Resolve the customer or staff account repository based on the user role
     */
    private AccountRepository<? extends UserAccount> resolveAccRepo(Role userRole) {
        return userRole == Role.CUSTOMER ? customerAccountRepo : staffAccountRepo;
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
                accountValues.add(userAccountDTO.getAccountStatus());

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
        Map<DataFieldName, String> allErrors = new HashMap<>();

        // Validate the user detail first, then if there are errors, collect them into the shared list
        collectErrors(allErrors, DataValidator.validateUserId(userAccountDTO.getUserID()));
        collectErrors(allErrors, DataValidator.validateEmailAddress(userAccountDTO.getEmailAddress()));
        collectErrors(allErrors, DataValidator.validatePhoneNumber(userAccountDTO.getPhoneNumber()));
        collectErrors(allErrors, DataValidator.validatePassword(userAccountDTO.getPassword(),
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
    private void collectErrors(Map<DataFieldName, String> allErrors, OperationResult result) {
        if (!result.isValid()) {
            allErrors.putAll(result.fieldErrors());
        }
    }
}
