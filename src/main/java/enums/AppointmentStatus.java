package main.java.enums;

public enum AppointmentStatus {
    COMPLETED("Completed"),
    IN_PROGRESS("In progress"),
    PENDING("Pending"),
    INCOMPLETE("Incomplete"),
    CANCELLED("Cancelled");

    private final String appointmentStatus;

    AppointmentStatus(String appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }

    public String getAppointmentStatus() {
        return appointmentStatus;
    }

    public static AppointmentStatus getApptStatusByValue(String value) {
        for (AppointmentStatus as : AppointmentStatus.values()) {
            if (as.appointmentStatus.equalsIgnoreCase(value)) {
                return as;
            }
        }

        // If not found, throw an exception
        throw new IllegalArgumentException("Invalid appointment status: " + value);
    }
}
