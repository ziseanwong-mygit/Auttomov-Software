package main.java.backend.model;

import main.java.enums.data.AccountStatus;
import main.java.enums.data.EmploymentStatus;
import main.java.enums.data.JobPosition;
import main.java.enums.data.Role;
import main.java.records.PasswordHash;

import java.time.LocalDate;

public class StaffAccount extends UserAccount {

    private JobPosition jobPosition;
    private LocalDate employmentDate;
    private EmploymentStatus employmentStatus;
    private String specialization;

    public StaffAccount(String accountID, LocalDate accountCreationDate, String userID, Role userRole,
                        String firstName, String lastName, String emailAddress, String phoneNumber,
                        PasswordHash passwordHash, AccountStatus accountStatus, JobPosition jobPosition,
                        LocalDate employmentDate, EmploymentStatus employmentStatus, String specialization) {

        super(accountID, accountCreationDate, userID, userRole, firstName, lastName,
                emailAddress, phoneNumber, passwordHash, accountStatus);

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

    public void setJobPosition(JobPosition jobPosition) {
        this.jobPosition = jobPosition;
    }

    public void setEmploymentDate(LocalDate employmentDate) {
        this.employmentDate = employmentDate;
    }

    public void setEmploymentStatus(EmploymentStatus employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    /**
     * Return a human-readable string representation of the object.
     * Used for logging, debugging, testing, or printing overall information.
     */
    @Override
    public String toString() {
        String basicDetails = super.toString();

        return basicDetails +
                "\nJob Position: " + jobPosition.getJobPosition() +
                "\nEmployment Date: " + employmentDate +
                "\nEmployment Status: " + employmentStatus.getEmpStatus() +
                "\nSpecialization: " + specialization;
    }

    /**
     * Return a CSV format string representation of the object.
     * Used for saving the object to a text file.
     */
    public String toTextFile() {
        String basicDetails = super.toTextFile();

        // If the staff is a technician, the specialization field will be added into the record
        if (jobPosition == JobPosition.TECHNICIAN) {
            return String.join(", ",
                    basicDetails,
                    jobPosition.getJobPosition(),
                    employmentDate.toString(),
                    employmentStatus.getEmpStatus(),
                    specialization
            );
        } else {
            return String.join(", ",
                    basicDetails,
                    jobPosition.getJobPosition(),
                    employmentDate.toString(),
                    employmentStatus.getEmpStatus()
            );
        }
    }
}
