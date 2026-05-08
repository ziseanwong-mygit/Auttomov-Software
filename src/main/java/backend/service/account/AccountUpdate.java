package main.java.backend.service.account;

import main.java.backend.model.StaffAccount;
import main.java.backend.model.UserAccount;
import main.java.backend.repository.account.AccountRepository;
import main.java.backend.service.GeneralProcess;
import main.java.backend.util.DataValidation;
import main.java.dtos.StaffAccountDTO;
import main.java.dtos.UserAccountDTO;
import main.java.enums.data.JobPosition;
import main.java.enums.data.Role;
import main.java.records.FieldComparison;
import main.java.records.OperationResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountUpdate implements GeneralProcess {

    // Wildcard is used so can accept either customer or staff repository in one variable
    private final AccountRepository<? extends UserAccount> accountRepository;
    private final UserAccountDTO userAccountDTO;
    private final UserAccount existingAcc;

    public AccountUpdate(AccountRepository<? extends UserAccount> accountRepository, UserAccountDTO userAccountDTO) {
        this.accountRepository = accountRepository;
        this.userAccountDTO = userAccountDTO;
        this.existingAcc = accountRepository.getRecordById(userAccountDTO.getAccountID());
    }

    @Override
    public OperationResult executeProcess() {

        String accountID = userAccountDTO.getAccountID();

        // If the account is not found, return an error message
        if (existingAcc == null) {
            return OperationResult.failed("The account with ID: " + accountID +
                    " does not exist in the system.");
        }

        // Get the list of fields that are changed
        List<FieldComparison<?>> changedFields = compareChanges();

        // If no fields are changed, return an error message
        if (changedFields.isEmpty()) {
            return OperationResult.failed("No fields have been changed. " +
                    "Please update one or more fields.");
        }

        // Create a shared map to collect all error messages
        Map<String, String> allErrors = new HashMap<>();

        // For each changed field, validate the new value such as formatting, length, etc.
        // If the validation is unsuccessful, add the error message into the shared map
        for (FieldComparison<?> field : changedFields) {
            OperationResult result = field.validate();
            if (!result.isValid())
                allErrors.putAll(result.fieldErrors());
        }

        if (!allErrors.isEmpty()) {
            return OperationResult.failed("Field validation is unsuccessful. " +
                    "Please try again.", allErrors);
        }

        // Apply updates only to changed fields
        setChangedValues(changedFields);

        try {
            // Cast the existingAcc to specific account type, then only update the record; base implementation
            return accountRepository.updateRecordBase(existingAcc);
        } catch (Exception e) {
            System.out.println(e.getMessage()); // Print exception message for debugging
            return OperationResult.failed("An unexpected error occurred while updating the account. " +
                    "Please try again.");
        }
    }

    /**
     * Compare the values between the existing account and incoming inputs (DTO) to get the changed fields
     */
    private List<FieldComparison<?>> compareChanges() {

        // Declare a list to store all the fields for comparison later
        List<FieldComparison<?>> accountFields = new ArrayList<>();
        Role userRole = existingAcc.getUserRole();

        // Add all the fields to the list
        accountFields.add(new FieldComparison<>("userID", existingAcc.getUserID(),
                userAccountDTO.getUserID(), List.of(DataValidation::validateUserId,
                id -> AccountDataVerification.verifyUniqueUserId(userRole, id, existingAcc.getUserID())
        )));
        accountFields.add(new FieldComparison<>("firstName", existingAcc.getFirstName(),
                userAccountDTO.getFirstName(), null));
        accountFields.add(new FieldComparison<>("lastName", existingAcc.getLastName(),
                userAccountDTO.getLastName(), null));
        accountFields.add(new FieldComparison<>("emailAddress", existingAcc.getEmailAddress(),
                userAccountDTO.getEmailAddress(), List.of(DataValidation::validateEmailAddress,
                email -> AccountDataVerification.verifyUniqueEmail(userRole, email, existingAcc.getEmailAddress())
        )));
        accountFields.add(new FieldComparison<>("phoneNumber", existingAcc.getPhoneNumber(),
                userAccountDTO.getPhoneNumber(), List.of(DataValidation::validatePhoneNumber)));

        // For staff account only
        if (userAccountDTO instanceof StaffAccountDTO staffAccountDTO &&
                existingAcc instanceof StaffAccount staffAcc)  {
            accountFields.add(new FieldComparison<>("jobPosition", staffAcc.getJobPosition(),
                    staffAccountDTO.getJobPosition(), null));
            accountFields.add(new FieldComparison<>("employmentDate", staffAcc.getEmploymentDate().toString(),
                    staffAccountDTO.getEmploymentDate().toString(), null));
            accountFields.add(new FieldComparison<>("employmentStatus",
                    staffAcc.getEmploymentStatus(), staffAccountDTO.getEmploymentStatus(), null));

            // If the user is a technician, add the specialization field
            if (staffAccountDTO.getJobPosition() == JobPosition.TECHNICIAN) {
                accountFields.add(new FieldComparison<>("specialization", staffAcc.getSpecialization(),
                        staffAccountDTO.getSpecialization(), null));
            }
        }

        // Filter to get the changed fields
        accountFields = accountFields.stream()
                .filter(FieldComparison::isChanged)
                .toList();
        return accountFields;
    }

    /**
     * Set the new values of the changed fields in the existing account object
     * The object will be used to update the record in the file
     */
    private void setChangedValues(List<FieldComparison<?>> changedFields) {
        for (FieldComparison<?> field : changedFields) {
            switch (field.fieldName()) {
                case "userID"           -> existingAcc.setUserID(userAccountDTO.getUserID());
                case "firstName"        -> existingAcc.setFirstName(userAccountDTO.getFirstName());
                case "lastName"         -> existingAcc.setLastName(userAccountDTO.getLastName());
                case "emailAddress"     -> existingAcc.setEmailAddress(userAccountDTO.getEmailAddress());
                case "phoneNumber"      -> existingAcc.setPhoneNumber(userAccountDTO.getPhoneNumber());
            }
            if (userAccountDTO instanceof StaffAccountDTO staffAccountDTO &&
                    existingAcc instanceof StaffAccount staffAcc) {
                switch (field.fieldName()) {
                    case "jobPosition"      -> staffAcc.setJobPosition(staffAccountDTO.getJobPosition());
                    case "employmentStatus" -> staffAcc.setEmploymentStatus(staffAccountDTO.getEmploymentStatus());
                    case "employmentDate"   -> staffAcc.setEmploymentDate(staffAccountDTO.getEmploymentDate());
                    case "specialization"   -> staffAcc.setSpecialization(staffAccountDTO.getSpecialization());
                }
            }
        }
    }
}
