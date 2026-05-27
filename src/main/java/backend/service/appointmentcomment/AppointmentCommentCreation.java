package main.java.backend.service.appointmentcomment;

import main.java.backend.model.Appointment;
import main.java.backend.model.AppointmentComment;
import main.java.backend.model.UserAccount;
import main.java.backend.repository.impl.AppointmentCommentRepository;
import main.java.backend.service.RecordWriter;
import main.java.dtos.AppointmentCommentDTO;
import main.java.records.OperationResult;

import java.time.LocalDateTime;
import java.util.UUID;

public class AppointmentCommentCreation implements RecordWriter {

    private final AppointmentCommentRepository appointmentCommentRepository;
    private final AppointmentCommentDTO appointmentCommentDTO;
    private final Appointment existingAppointment;
    private final UserAccount existingCustomer;

    public AppointmentCommentCreation(AppointmentCommentRepository appointmentCommentRepository,
                                      AppointmentCommentDTO appointmentCommentDTO, Appointment existingAppointment,
                                      UserAccount existingCustomer) {
        this.appointmentCommentRepository = appointmentCommentRepository;
        this.appointmentCommentDTO = appointmentCommentDTO;
        this.existingAppointment = existingAppointment;
        this.existingCustomer = existingCustomer;
    }

    @Override
    public OperationResult execute() {
        // Create a new comment object
        AppointmentComment newComment = populateApptCommentDetails();

        // Save the comment into the file
        return appointmentCommentRepository.createRecord(newComment);
    }

    /**
     * Create a new AppointmentComment object for record creation
     */
    private AppointmentComment populateApptCommentDetails() {

        // Generate new comment ID
        String commentId = UUID.randomUUID().toString();

        // Retrieve today's date for comment creation and update date
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Return the new object here
        return new AppointmentComment(commentId, currentDateTime, currentDateTime, existingAppointment,
                existingCustomer, appointmentCommentDTO.getCommentContent());
    }

}
