package main.java.backend.service.appointmentfeedback;

import main.java.backend.model.AppointmentFeedback;
import main.java.backend.repository.impl.AppointmentFeedbackRepository;
import main.java.backend.service.RecordWriter;
import main.java.dtos.AppointmentFeedbackDTO;
import main.java.records.FieldComparison;
import main.java.records.OperationResult;

public class AppointmentFeedbackUpdate implements RecordWriter {

    private final AppointmentFeedbackRepository appointmentFeedbackRepository;
    private final AppointmentFeedbackDTO appointmentFeedbackDTO;

    public AppointmentFeedbackUpdate(AppointmentFeedbackRepository appointmentFeedbackRepository,
                                     AppointmentFeedbackDTO appointmentFeedbackDTO) {
        this.appointmentFeedbackRepository = appointmentFeedbackRepository;
        this.appointmentFeedbackDTO = appointmentFeedbackDTO;
    }

    @Override
    public OperationResult execute() {

        // Check if the feedback exists with the given ID
        AppointmentFeedback existingFeedback = appointmentFeedbackRepository.getRecordById(
                appointmentFeedbackDTO.getFeedbackID());
        if (existingFeedback == null) {
            return OperationResult.failed("The feedback with ID: " +
                    appointmentFeedbackDTO.getFeedbackID() + " does not exist in the system.");
        }

        // Check if the input feedback content is different from the current content
        FieldComparison<String> contentChange = new FieldComparison<>(
                "feedbackContent", existingFeedback.getFeedbackContent(),
                appointmentFeedbackDTO.getFeedbackContent(), null);

        if (!contentChange.isChanged()) {
            return OperationResult.failed("The input feedback content provided is the same as the " +
                    "current content. Please provide a different feedback content for update.");
        }

        // Set the new content into the existing feedback record
        existingFeedback.setFeedbackContent(appointmentFeedbackDTO.getFeedbackContent());

        return appointmentFeedbackRepository.updateRecord(existingFeedback);
    }
}
