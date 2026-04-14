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
}
