package main.java.enums;

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

        // If not found, return null
        return null;
    }
}
