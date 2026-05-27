package main.java.enums.data;

public enum JobPosition {
    MANAGER("Manager"),
    COUNTER_STAFF("Counter staff"),
    TECHNICIAN("Technician");

    private final String jobPosition;

    JobPosition(String jobPosition) {
        this.jobPosition = jobPosition;
    }

    public String getJobPosition() {
        return jobPosition;
    }

    public static JobPosition getJobPosByValue(String value) {
        for (JobPosition jp : JobPosition.values()) {
            if (jp.jobPosition.equalsIgnoreCase(value)) {
                return jp;
            }
        }

        // If not found, throw an exception
        throw new IllegalArgumentException("Invalid job position: " + value);
    }
}
