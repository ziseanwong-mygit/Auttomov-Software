package main.java.backend.service.appointmentcomment;

import main.java.backend.model.AppointmentComment;
import main.java.backend.repository.impl.AppointmentCommentRepository;
import main.java.backend.service.RecordReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AppointmentCommentRetrieval implements RecordReader<AppointmentComment> {

    public final AppointmentCommentRepository appointmentCommentRepository;

    public AppointmentCommentRetrieval(AppointmentCommentRepository appointmentCommentRepository) {
        this.appointmentCommentRepository = appointmentCommentRepository;
    }

    /**
     * Read and return all appointment comments from the repository
     */
    @Override
    public Optional<List<AppointmentComment>> getAllRecords() {

        List<AppointmentComment> existingComments = new ArrayList<>(appointmentCommentRepository.getAllRecords());

        if (existingComments.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(existingComments);
    }

    /**
     * Read and return a single appointment comment based on the given ID
     */
    @Override
    public Optional<AppointmentComment> getRecordById(String appointmentId) {

        AppointmentComment existingComment = appointmentCommentRepository.getRecordById(appointmentId);

        if (existingComment == null) {
            return Optional.empty();
        }

        return Optional.of(existingComment);
    }

    /**
     * Read and return multiple appointment comments based on the given IDs
     */
    @Override
    public Optional<List<AppointmentComment>> getRecordsByIds(List<String> appointmentIds) {

        List<AppointmentComment> existingComments = appointmentCommentRepository.getRecordsByIds(appointmentIds);

        if (existingComments.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(existingComments);
    }

    /**
     * Read and return all appointment comments for a specific appointment
     */
    public Optional<List<AppointmentComment>> getRecordsByAppointmentId(String appointmentId) {

        List<AppointmentComment> existingComments = appointmentCommentRepository.
                getRecordsByAppointmentId(appointmentId);

        if (existingComments.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(existingComments);

    }
}
