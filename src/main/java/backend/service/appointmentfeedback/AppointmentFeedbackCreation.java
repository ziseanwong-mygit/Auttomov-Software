package main.java.backend.service.appointmentfeedback;

import main.java.backend.model.Appointment;
import main.java.backend.model.AppointmentFeedback;
import main.java.backend.model.StaffAccount;
import main.java.backend.repository.impl.AppointmentFeedbackRepository;
import main.java.backend.service.RecordWriter;
import main.java.dtos.AppointmentFeedbackDTO;
import main.java.records.OperationResult;

import java.time.LocalDateTime;
import java.util.UUID;

public class AppointmentFeedbackCreation implements RecordWriter {

    private final AppointmentFeedbackRepository appointmentFeedbackRepository;
    private final AppointmentFeedbackDTO appointmentFeedbackDTO;
    private final Appointment existingAppointment;
    private final StaffAccount existingTechnician;

    public AppointmentFeedbackCreation(AppointmentFeedbackRepository appointmentFeedbackRepository,
                                       AppointmentFeedbackDTO appointmentFeedbackDTO, Appointment existingAppointment,
                                       StaffAccount existingTechnician) {
        this.appointmentFeedbackRepository = appointmentFeedbackRepository;
        this.appointmentFeedbackDTO = appointmentFeedbackDTO;
        this.existingAppointment = existingAppointment;
        this.existingTechnician = existingTechnician;
    }

    @Override
    public OperationResult execute() {
        // Create a new feedback object
        AppointmentFeedback newFeedback = populateApptFeedbackDetails();

        // Save the feedback into the file
        return appointmentFeedbackRepository.createRecord(newFeedback);
    }

    /**
     * Create a new AppointmentFeedback object for record creation
     */
    private AppointmentFeedback populateApptFeedbackDetails() {

        // Generate new feedback ID
        String feedbackId = UUID.randomUUID().toString();

        // Retrieve today's date for feedback creation and update date
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Return the new object here
        return new AppointmentFeedback(feedbackId, currentDateTime, currentDateTime, existingAppointment,
                existingTechnician, appointmentFeedbackDTO.getFeedbackContent());
    }
}
