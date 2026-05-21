package main.java.backend.service.appointment;

import main.java.backend.model.Appointment;
import main.java.backend.repository.impl.AppointmentRepository;
import main.java.backend.service.RecordWriter;
import main.java.records.OperationResult;

public class AppointmentDeletion implements RecordWriter {

    private final AppointmentRepository appointmentRepository;
    private final String appointmentID;

    public AppointmentDeletion(AppointmentRepository appointmentRepository, String appointmentID) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentID = appointmentID;
    }

    @Override
    public OperationResult execute() {

        // Check if the appointment exists with the given ID
        Appointment existingAppointment = appointmentRepository.getRecordById(appointmentID);
        if (existingAppointment == null) {
            return OperationResult.failed("The appointment with ID: " + appointmentID +
                    " does not exist in the system.");
        }

        // Begin deleting appointment record in the repository
        return appointmentRepository.deleteRecord(existingAppointment);
    }
}
