package main.java.dtos;

import main.java.enums.data.EmploymentStatus;
import main.java.enums.data.JobPosition;
import main.java.enums.data.Role;

import java.time.LocalDate;

public class StaffAccountDTO extends UserAccountDTO {

    private final JobPosition jobPosition;
    private LocalDate employmentDate;
    private final EmploymentStatus employmentStatus;
    private final String specialization;

    /**
     * For creating a new account
     */
    public StaffAccountDTO(String userID, Role userRole, String firstName, String lastName, String emailAddress,
                           String phoneNumber, String password, String confirmPassword, JobPosition jobPosition,
                           EmploymentStatus employmentStatus, String specialization) {
        super(userID, userRole, firstName, lastName, emailAddress, phoneNumber, password, confirmPassword);
        this.jobPosition = jobPosition;
        this.employmentStatus = employmentStatus;
        this.specialization = specialization;
    }

    /**
     * For updating an existing account
     */
    public StaffAccountDTO(String accountID, String userID, Role userRole, String firstName, String lastName,
                           String emailAddress, String phoneNumber, JobPosition jobPosition, LocalDate employmentDate,
                           EmploymentStatus employmentStatus, String specialization) {
        super(accountID, userID, userRole, firstName, lastName, emailAddress, phoneNumber);
        this.jobPosition = jobPosition;
        this.employmentDate = employmentDate;
        this.employmentStatus = employmentStatus;
        this.specialization = specialization;
    }

    public JobPosition getJobPosition() {
        return jobPosition;
    }

    public LocalDate getEmploymentDate() {
        return employmentDate;
    }

    public EmploymentStatus getEmploymentStatus() {
        return employmentStatus;
    }

    public String getSpecialization() {
        return specialization;
    }
}
