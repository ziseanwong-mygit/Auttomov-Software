package main.java.backend.service.appointmentcomment;

import main.java.backend.model.AppointmentComment;
import main.java.backend.repository.impl.AppointmentCommentRepository;
import main.java.backend.service.RecordWriter;
import main.java.dtos.AppointmentCommentDTO;
import main.java.records.FieldComparison;
import main.java.records.OperationResult;

import java.time.LocalDateTime;

public class AppointmentCommentUpdate implements RecordWriter {

    private final AppointmentCommentRepository appointmentCommentRepository;
    private final AppointmentCommentDTO appointmentCommentDTO;

    public AppointmentCommentUpdate(AppointmentCommentRepository appointmentCommentRepository,
                                    AppointmentCommentDTO appointmentCommentDTO) {
        this.appointmentCommentRepository = appointmentCommentRepository;
        this.appointmentCommentDTO = appointmentCommentDTO;
    }

    @Override
    public OperationResult execute() {

        // Check if the comment exists with the given ID
        AppointmentComment existingComment = appointmentCommentRepository.getRecordById(
                appointmentCommentDTO.getCommentID());
        if (existingComment == null) {
            return OperationResult.failed("The comment with ID: " +
                    appointmentCommentDTO.getCommentID() + " does not exist in the system.");
        }

        // Check if the input comment content is different from the current content
        FieldComparison<String> contentChange = new FieldComparison<>(
                "commentContent", existingComment.getCommentContent(),
                appointmentCommentDTO.getCommentContent(), null);

        if (!contentChange.isChanged()) {
            return OperationResult.failed("The input comment content provided is the same as the " +
                    "current content. Please provide a different comment content for update.");
        }

        // Set the new content into the existing comment record
        existingComment.setCommentUpdateTime(LocalDateTime.now());
        existingComment.setCommentContent(appointmentCommentDTO.getCommentContent());

        return appointmentCommentRepository.updateRecord(existingComment);
    }
}
