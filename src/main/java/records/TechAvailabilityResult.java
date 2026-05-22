package main.java.records;

import main.java.backend.model.Appointment;
import main.java.backend.model.StaffAccount;

import java.util.List;

/**
 * Holds the list of available and unavailable technicians for a given appointment
 * For frontend to use and display the respective lists during the technician assignment process
 */
public record TechAvailabilityResult(List<StaffAccount> availableTechnicians,
                                     List<TechnicianConflict> unavailableTechnicians) {
    /**
     * Nested record that represents the unavailable technician and its assigned appointment
     * For the frontend to display the exact clash details
     */
    public record TechnicianConflict(StaffAccount technician, Appointment conflictingAppointment) {}
}
