package main.java.enums.data;

public enum EmploymentStatus {
    FULL_TIME("Full time"),
    PART_TIME("Part time"),
    CONTRACT("Contract"),
    TEMPORARY("Temporary"),
    SUSPENDED("Suspended");

    private final String empStatus;

    EmploymentStatus(String empStatus) {
        this.empStatus = empStatus;
    }

    public String getEmpStatus() {
        return empStatus;
    }

    public static EmploymentStatus getEmpStatusByValue(String value) {
        for (EmploymentStatus es : EmploymentStatus.values()) {
            if (es.empStatus.equalsIgnoreCase(value)) {
                return es;
            }
        }

        // If not found, return null
        throw new IllegalArgumentException("Invalid employment status: " + value);
    }
}
