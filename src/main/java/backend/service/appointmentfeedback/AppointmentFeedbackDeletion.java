package main.java.backend.service.appointmentfeedback;

import main.java.backend.model.AppointmentFeedback;
import main.java.backend.repository.impl.AppointmentFeedbackRepository;
import main.java.backend.service.RecordWriter;
import main.java.records.OperationResult;

public class AppointmentFeedbackDeletion implements RecordWriter {

    private final AppointmentFeedbackRepository appointmentFeedbackRepository;
    private final String feedbackID;

    public AppointmentFeedbackDeletion(AppointmentFeedbackRepository appointmentFeedbackRepository,
                                       String feedbackID) {
        this.appointmentFeedbackRepository = appointmentFeedbackRepository;
        this.feedbackID = feedbackID;
    }

    @Override
    public OperationResult execute() {

        // Check if the feedback exists with the given ID
        AppointmentFeedback existingFeedback = appointmentFeedbackRepository.getRecordById(feedbackID);
        if (existingFeedback == null) {
            return OperationResult.failed("The feedback with ID: " + feedbackID + " does not exist in the system.");
        }

        // Begin deleting feedback record in the repository
        return appointmentFeedbackRepository.deleteRecord(existingFeedback);
    }
}
