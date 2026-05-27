package main.java.backend.service.appointment;

import main.java.backend.model.Appointment;
import main.java.backend.model.StaffAccount;
import main.java.backend.repository.impl.AppointmentRepository;
import main.java.backend.repository.impl.account.StaffAccountRepository;
import main.java.enums.data.JobPosition;
import main.java.records.TechAvailabilityResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AppointmentTechAvailabilityRetrieval {

    private final AppointmentRepository appointmentRepository;
    private final StaffAccountRepository staffAccRepository;
    private final String targetAppointmentId;

    public AppointmentTechAvailabilityRetrieval (AppointmentRepository appointmentRepository,
                                                 StaffAccountRepository staffAccRepository,
                                                 String targetAppointmentId) {
        this.appointmentRepository = appointmentRepository;
        this.staffAccRepository = staffAccRepository;
        this.targetAppointmentId   = targetAppointmentId;
    }

    /**
     * Get all the technicians that are available and unavailable to work on the target appointment
     * by comparing their assigned appointments' date time range with the target appointment's
     */
    public Optional<TechAvailabilityResult> getTechAvailability() {

        // Check if the target appointment exists
        Appointment targetAppointment = appointmentRepository.getRecordById(targetAppointmentId);
        if (targetAppointment == null) {
            return Optional.empty(); // Appointment does not exist; controller handles the display message
        }

        // Get all the technicians from the staff account repository
        List<StaffAccount> allTechnicians = staffAccRepository.getAllRecords()
                .stream()
                .filter(staff -> staff.getJobPosition() == JobPosition.TECHNICIAN)
                .toList();

        // Initialize list for storing available and unavailable technicians
        List<StaffAccount> availableTechs = new ArrayList<>();
        List<TechAvailabilityResult.TechnicianConflict> unavailableTechs = new ArrayList<>();

        // Classify every technician into either available or unavailable category list
        for (StaffAccount technician : allTechnicians) {

            // Get all appointments that the technician is currently assigned to
            List<Appointment> techAppointments = appointmentRepository.getRecordsByTechnician(technician);

            // Find the specific appointment that clashes with the target appointment's time
            Appointment conflictingAppointment = AppointmentScheduleChecker.findConflictingAppointment(
                    targetAppointment, techAppointments);

            // If there is no conflicting appointment, put the technician into the available list
            if (conflictingAppointment == null) {
                availableTechs.add(technician); // Free during the target time slot
            } else {
                unavailableTechs.add(new TechAvailabilityResult.TechnicianConflict(
                        technician, conflictingAppointment));
            }
        }

        // Place the available and unavailable technician lists into a record
        TechAvailabilityResult result = new TechAvailabilityResult(availableTechs, unavailableTechs);

        // Return the result with Optional class
        return Optional.of(result);
    }
}
