package main.java.backend.dto;

import main.java.enums.AccountStatus;
import main.java.enums.EmploymentStatus;
import main.java.enums.JobPosition;
import main.java.enums.Role;
import main.java.records.PasswordHash;

import java.time.LocalDate;
import java.util.UUID;

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
     * First get a line of comma-separated user details
     * Then merge the user details with the staff details for writing into staff account file
     */
    @Override
    public String toString() {
        String basicDetails = super.toString();

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
