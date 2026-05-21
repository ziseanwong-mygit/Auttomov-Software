package main.java.backend.service.appointment;

import main.java.backend.model.Appointment;
import main.java.backend.model.StaffAccount;
import main.java.backend.repository.impl.AppointmentRepository;
import main.java.backend.service.RecordWriter;
import main.java.dtos.AppointmentDTO;
import main.java.records.FieldComparison;
import main.java.records.OperationResult;

public class AppointmentTechAssignment implements RecordWriter {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentDTO appointmentDTO;
    private final StaffAccount existingTechnician;

    public AppointmentTechAssignment(AppointmentRepository appointmentRepository, AppointmentDTO appointmentDTO,
                                     StaffAccount existingTechnician) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentDTO = appointmentDTO;
        this.existingTechnician = existingTechnician;
    }

    @Override
    public OperationResult execute() {

        // Check if the appointment exists with the given ID
        Appointment existingAppointment = appointmentRepository.getRecordById(appointmentDTO.getAppointmentID());
        if (existingAppointment == null) {
            return OperationResult.failed("The appointment with ID: " + appointmentDTO.getAppointmentID() +
                    " does not exist in the system.");
        }

        // Check if the new technician is different from the current technician,
        // even though the appointment has not been assigned with technician yet
        FieldComparison<String> technicianChange = new FieldComparison<>(
                "assignedTechnician", existingAppointment.getAssignedTechnician().getAccountID(),
                existingTechnician.getAccountID(), null);

        if (!technicianChange.isChanged()) {
            return OperationResult.failed("The new technician provided is the same as the " +
                    "current technician. Please assign to a different technician for update.");
        }

        // Set the new technician into the existing appointment record
        existingAppointment.setAssignedTechnician(existingTechnician);

        // Begin updating appointment record with new technician in the repository
        return appointmentRepository.updateRecord(existingAppointment);
    }
}
