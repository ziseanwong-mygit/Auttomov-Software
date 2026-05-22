package main.java.backend.service.appointmentcomment;

import main.java.backend.model.AppointmentComment;
import main.java.backend.repository.impl.AppointmentCommentRepository;
import main.java.backend.service.RecordWriter;
import main.java.records.OperationResult;

public class AppointmentCommentDeletion implements RecordWriter {

    private final AppointmentCommentRepository appointmentCommentRepository;
    private final String commentID;

    public AppointmentCommentDeletion(AppointmentCommentRepository appointmentCommentRepository, String commentID) {
        this.appointmentCommentRepository = appointmentCommentRepository;
        this.commentID = commentID;
    }

    @Override
    public OperationResult execute() {

        // Check if the comment exists with the given ID
        AppointmentComment existingComment = appointmentCommentRepository.getRecordById(commentID);
        if (existingComment == null) {
            return OperationResult.failed("The comment with ID: " + commentID + " does not exist in the system.");
        }

        // Begin deleting comment record in the repository
        return appointmentCommentRepository.deleteRecord(existingComment);
    }
}
