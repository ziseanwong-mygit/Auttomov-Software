package main.java.backend.service.appointment;

import main.java.backend.model.Appointment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AppointmentScheduleChecker {

    // Private constructor to prevent instantiation
    private AppointmentScheduleChecker() {
        throw new UnsupportedOperationException("This class is a utility class, which cannot be instantiated.");
    }

    /**
     * Check if the given technician has any appointment that conflicts with the target appointment's
     * date and time range.
     * Back-to-back appointments (one ends exactly when the next starts) are not considered a conflict.
     * targetAppointment: The appointment to be assigned with the technician
     * techAppointments: The list of appointments assigned to the technician
     * Returns true if there is a conflict, false otherwise.
     */
    public static boolean hasConflict(Appointment targetAppointment, List<Appointment> techAppointments) {

        // Combine the service date and start/end time to LocalDateTime format for easier comparison
        LocalDate targetDate = targetAppointment.getServiceDate();
        LocalDateTime targetStartTime = LocalDateTime.of(targetDate, targetAppointment.getServiceStartTime());
        LocalDateTime targetEndTime = LocalDateTime.of(targetDate, targetAppointment.getServiceEndTime());

        // Check for each assigned appointment of the technician
        for (Appointment assignedAppt : techAppointments) {

            // Skip the target appointment itself
            // Relevant when the technician is already assigned and checking for reassignment
            if (assignedAppt.getAppointmentID()
                    .equals(targetAppointment.getAppointmentID())) {
                continue;
            }

            // Only compare appointments that are on the same date
            if (!assignedAppt.getServiceDate().equals(targetDate)) {
                continue;
            }

            // Combine the service date and start/end time to LocalDateTime format for easier comparison
            LocalDateTime occupiedStartTime = LocalDateTime.of(assignedAppt.getServiceDate(),
                    assignedAppt.getServiceStartTime());
            LocalDateTime occupiedEndTime = LocalDateTime.of(assignedAppt.getServiceDate(),
                    assignedAppt.getServiceEndTime());

            /*
            Logic for checking schedule conflicts:
            1. If the target start time is before the occupied end time
               AND
               the target end time is after the occupied start time: conflict exists, returns true.
            2. Example: Target -> 7:00am - 10:00am, Occupied -> 8:00am - 11:00am
            3. (7:00am.isBefore(11:00am) [true] && (10:00am.isAfter(8:00am) [true] -> true; conflict happens
            */
            boolean hasSchedulingConflict = targetStartTime.isBefore(occupiedEndTime) &&
                    targetEndTime.isAfter(occupiedStartTime);

            if (hasSchedulingConflict) {
                return true;
            }
        }

        return false;
    }

    /**
     * If a conflict exists, returns the specific appointment causing it.
     * Used when the UI or error message needs to show which appointment clashed.
     * Returns null if no conflict exists.
     */
    public static Appointment findConflictingAppointment(Appointment targetAppointment,
                                                         List<Appointment> techAppointments) {

        // Combine the service date and start/end time to LocalDateTime format for easier comparison
        LocalDate targetDate = targetAppointment.getServiceDate();
        LocalDateTime targetStart = LocalDateTime.of(targetDate, targetAppointment.getServiceStartTime());
        LocalDateTime targetEnd = LocalDateTime.of(targetDate, targetAppointment.getServiceEndTime());

        // Check for each assigned appointment of the technician
        for (Appointment assignedAppt : techAppointments) {

            // Skip the target appointment itself
            // Relevant when the technician is already assigned and checking for reassignment
            if (assignedAppt.getAppointmentID()
                    .equals(targetAppointment.getAppointmentID())) {
                continue;
            }

            // Only compare appointments that are on the same date
            if (!assignedAppt.getServiceDate().equals(targetDate)) {
                continue;
            }

            // Combine the service date and start/end time to LocalDateTime format for easier comparison
            LocalDateTime occupiedStartTime = LocalDateTime.of(assignedAppt.getServiceDate(),
                    assignedAppt.getServiceStartTime());
            LocalDateTime occupiedEndTime = LocalDateTime.of(assignedAppt.getServiceDate(),
                    assignedAppt.getServiceEndTime());

            /*
            Logic for checking schedule conflicts:
            1. If the target start time is before the occupied end time
               AND
               the target end time is after the occupied start time: conflict exists, returns true.
            2. Example: Target -> 7:00am - 10:00am, Occupied -> 8:00am - 11:00am
            3. (7:00am.isBefore(11:00am) [true] && (10:00am.isAfter(8:00am) [true] -> true; conflict happens
            */
            if (targetStart.isBefore(occupiedEndTime) && targetEnd.isAfter(occupiedStartTime)) {
                return assignedAppt;
            }
        }

        return null;
    }
}
