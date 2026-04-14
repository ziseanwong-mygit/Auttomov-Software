package main.java.enums;

public enum EmploymentStatus {
    FULL_TIME("Full time"),
    PART_TIME("Part time"),
    CONTRACT("Contract"),
    TEMPORARY("Temporary");

    private final String empStatus;

    EmploymentStatus(String empStatus) {
        this.empStatus = empStatus;
    }

    public String getEmpStatus() {
        return empStatus;
    }
}
