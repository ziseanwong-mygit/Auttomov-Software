package main.java.backend.service.account;

import main.java.backend.model.StaffAccount;
import main.java.backend.model.UserAccount;
import main.java.backend.repository.AccountRepository;
import main.java.backend.util.DataValidation;
import main.java.enums.EmploymentStatus;
import main.java.enums.JobPosition;
import main.java.enums.Role;
import main.java.records.OperationResult;
import main.java.records.FieldComparison;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountUpdate<T extends UserAccount> {

    // Generics is used so can accept either UserAccount or StaffAccount in this variable
    // The repository instance is stored in AccountRepository interface, to eliminate the need of checking type
    private final AccountRepository<T> accountRepo;

    // Existing customer, technician, counter-staff, or manager account
    private final T existingAcc;

    private final String accountID;
    private final String userID;
    private final Role userRole;
    private final String firstName;
    private final String lastName;
    private final String emailAddress;
    private final String phoneNumber;

    // For staff only
    private JobPosition jobPosition;
    private LocalDate employmentDate;
    private EmploymentStatus employmentStatus;
    private String specialization;

    public AccountUpdate(String accountID, String userID, Role userRole, String firstName,
                         String lastName, String emailAddress, String phoneNumber, AccountRepository<T> accountRepo) {
        this.accountID = accountID;
        this.userID = userID;
        this.userRole = userRole;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.accountRepo = accountRepo;
        this.existingAcc = accountRepo.getAccountByAccId(accountID);
    }

    public AccountUpdate(String accountID, String userID, Role userRole, String firstName,
                         String lastName, String emailAddress, String phoneNumber, JobPosition jobPosition,
                         LocalDate employmentDate, EmploymentStatus employmentStatus, String specialization,
                         AccountRepository<T> accountRepo) {
        this(accountID, userID, userRole, firstName, lastName, emailAddress, phoneNumber, accountRepo);
        this.jobPosition = jobPosition;
        this.employmentDate = employmentDate;
        this.employmentStatus = employmentStatus;
        this.specialization = specialization;
    }

    public OperationResult executeProcess() {

        if (existingAcc == null) {
            return OperationResult.failed("The account with ID: " + accountID +
                    " does not exist in the system.");
        }

        // Gather all the original and new values of each field for comparison
        List<FieldComparison> accountFields = new ArrayList<>();

        accountFields.add(new FieldComparison("userID", existingAcc.getUserID(),
                userID, List.of(id -> DataValidation.validateUserId(userRole, id))));
        accountFields.add(new FieldComparison("firstName", existingAcc.getFirstName(),
                firstName, null));
        accountFields.add(new FieldComparison("lastName", existingAcc.getLastName(),
                lastName, null));
        accountFields.add(new FieldComparison("emailAddress", existingAcc.getEmailAddress(),
                emailAddress, List.of(DataValidation::validateEmailAddress,
                email -> AccountDataVerification.verifyUniqueEmail(userRole, email)
        )));
        accountFields.add(new FieldComparison("phoneNumber", existingAcc.getPhoneNumber(),
                phoneNumber, List.of(DataValidation::validatePhoneNumber)));

        if (existingAcc instanceof StaffAccount staffAcc) {
            accountFields.add(new FieldComparison("jobPosition", staffAcc.getJobPosition().toString(),
                    jobPosition.toString(), null));
            accountFields.add(new FieldComparison("employmentDate", staffAcc.getEmploymentDate().toString(),
                    employmentDate.toString(), null));
            accountFields.add(new FieldComparison("employmentStatus",
                    staffAcc.getEmploymentStatus().getEmpStatus(), employmentStatus.getEmpStatus(), null));
            // If the user is a technician, add the specialization field
            if (JobPosition.TECHNICIAN.equals(staffAcc.getJobPosition())) {
                accountFields.add(new FieldComparison("specialization", staffAcc.getSpecialization(),
                        specialization, null));
            }
        }

        // Filter to get the changed fields
        List<FieldComparison> changedFields = accountFields.stream()
                .filter(FieldComparison::isChanged)
                .toList();

        // If no fields are changed, return an error message
        if (changedFields.isEmpty()) {
            return OperationResult.failed(
                    "No fields have been changed. Please update one or more fields.");
        }

        // Create a shared map to collect all error messages
        Map<String, String> allErrors = new HashMap<>();

        // For each changed field, validate the new value such as formatting, length, etc.
        // If the validation is unsuccessful, add the error message into the shared map
        for (FieldComparison field : changedFields) {
            OperationResult result = field.validate();
            if (!result.isValid())
                allErrors.putAll(result.fieldErrors());
        }

        if (!allErrors.isEmpty()) {
            return OperationResult.failed(
                    "Field validation is unsuccessful. Please try again.", allErrors);
        }

        // Apply updates only to changed fields
        for (FieldComparison field : changedFields) {
            switch (field.fieldName()) {
                case "userID"           -> existingAcc.setUserID(field.newValue());
                case "firstName"        -> existingAcc.setFirstName(field.newValue());
                case "lastName"         -> existingAcc.setLastName(field.newValue());
                case "emailAddress"     -> existingAcc.setEmailAddress(field.newValue());
                case "phoneNumber"      -> existingAcc.setPhoneNumber(field.newValue());
            }
            if (existingAcc instanceof StaffAccount staffAcc) {
                switch (field.fieldName()) {
                    case "jobPosition"      -> staffAcc.setJobPosition(jobPosition);
                    case "employmentStatus" -> staffAcc.setEmploymentStatus(employmentStatus);
                    case "employmentDate"   -> staffAcc.setEmploymentDate(employmentDate);
                    case "specialization"   -> staffAcc.setSpecialization(field.newValue());
                }
            }
        }

        return accountRepo.updateAccount(existingAcc);
    }
}
