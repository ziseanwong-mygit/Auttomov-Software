package main.java.backend.service.appointment;

import main.java.backend.model.Appointment;
import main.java.backend.repository.impl.AppointmentRepository;
import main.java.backend.service.RecordWriter;
import main.java.dtos.AppointmentDTO;
import main.java.enums.data.AppointmentStatus;
import main.java.records.FieldComparison;
import main.java.records.OperationResult;

/**
 * Used for marking appointments as complete/incomplete (technician) and cancelling appointments (counter staff)
 */
public class AppointmentStatusUpdate implements RecordWriter {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentDTO appointmentDTO;

    public AppointmentStatusUpdate(AppointmentRepository appointmentRepository, AppointmentDTO appointmentDTO) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentDTO = appointmentDTO;
    }

    @Override
    public OperationResult execute() {

        String appointmentID = appointmentDTO.getAppointmentID();
        AppointmentStatus inputStatus = appointmentDTO.getAppointmentStatus();

        // Check if the appointment exists with the given ID
        Appointment existingAppointment = appointmentRepository.getRecordById(appointmentID);
        if (existingAppointment == null) {
            return OperationResult.failed("The appointment with ID: " + appointmentID +
                    " does not exist in the system.");
        }

        AppointmentStatus currentStatus = existingAppointment.getAppointmentStatus();

        // If the appointment has been canceled, return an error message
        if (currentStatus.equals(AppointmentStatus.CANCELLED)) {
            return OperationResult.failed("The appointment with ID: " + appointmentID +
                    "cannot be updated with new status because it has already been cancelled.");
        }

        // Appointments with completed/incomplete status cannot be marked as PENDING
        if (inputStatus.equals(AppointmentStatus.PENDING)) {
            if (currentStatus.equals(AppointmentStatus.COMPLETED) ||
                    currentStatus.equals(AppointmentStatus.INCOMPLETE)) {
                return OperationResult.failed("The appointment with ID: " + appointmentID +
                        "cannot be updated with pending status because it has already " +
                        "been marked as complete/incomplete.");
            }
        }

        // If the appointment is in progress, it cannot be canceled
        if (inputStatus.equals(AppointmentStatus.CANCELLED)) {
            if (currentStatus.equals(AppointmentStatus.IN_PROGRESS)) {
                return OperationResult.failed("The appointment with ID: " + appointmentID +
                        "cannot be cancelled because it is currently working in progress.");
            }
        }

        // Check if the input status is different from the current status
        FieldComparison<AppointmentStatus> statusChange = new FieldComparison<>(
                "appointmentStatus", existingAppointment.getAppointmentStatus(),
                appointmentDTO.getAppointmentStatus(), null);

        if (!statusChange.isChanged()) {
            return OperationResult.failed("The input status provided is the same as the " +
                    "current status. Please set to a different status for update.");
        }

        // Set the new status into the existing appointment record
        existingAppointment.setAppointmentStatus(appointmentDTO.getAppointmentStatus());

        // Begin updating appointment record with new status in the repository
        return appointmentRepository.updateRecord(existingAppointment);
    }
}
