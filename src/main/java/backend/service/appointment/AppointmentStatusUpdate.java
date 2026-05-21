package main.java.backend.service.appointment;

import main.java.backend.model.Appointment;
import main.java.backend.repository.impl.AppointmentRepository;
import main.java.backend.service.RecordWriter;
import main.java.dtos.AppointmentDTO;
import main.java.enums.data.AppointmentStatus;
import main.java.records.FieldComparison;
import main.java.records.OperationResult;

/**
 * Used for updating the status of an appointment record only for the staff's convenience
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

        // Check if the appointment exists with the given ID
        Appointment existingAppointment = appointmentRepository.getRecordById(appointmentDTO.getAppointmentID());
        if (existingAppointment == null) {
            return OperationResult.failed("The appointment with ID: " + appointmentDTO.getAppointmentID() +
                    " does not exist in the system.");
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
