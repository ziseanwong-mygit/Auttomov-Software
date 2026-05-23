package main.java.backend.service.appointmentfeedback;

import main.java.backend.model.AppointmentFeedback;
import main.java.backend.repository.impl.AppointmentFeedbackRepository;
import main.java.backend.service.RecordReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AppointmentFeedbackRetrieval implements RecordReader<AppointmentFeedback> {

    private final AppointmentFeedbackRepository appointmentFeedbackRepository;

    public AppointmentFeedbackRetrieval(AppointmentFeedbackRepository appointmentFeedbackRepository) {
        this.appointmentFeedbackRepository = appointmentFeedbackRepository;
    }

    /**
     * Read and return all appointment feedbacks from the repository
     */
    @Override
    public Optional<List<AppointmentFeedback>> getAllRecords() {

        List<AppointmentFeedback> existingFeedbacks = new ArrayList<>(appointmentFeedbackRepository.getAllRecords());

        if (existingFeedbacks.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(existingFeedbacks);
    }

    /**
     * Read and return single appointment feedback based on the given ID
     */
    @Override
    public Optional<AppointmentFeedback> getRecordById(String appointmentId) {

        AppointmentFeedback existingFeedback = appointmentFeedbackRepository.getRecordById(appointmentId);

        if (existingFeedback == null) {
            return Optional.empty();
        }

        return Optional.of(existingFeedback);
    }

    /**
     * Read and return multiple appointment feedbacks based on the given IDs
     */
    @Override
    public Optional<List<AppointmentFeedback>> getRecordsByIds(List<String> appointmentIds) {

        List<AppointmentFeedback> existingFeedbacks = appointmentFeedbackRepository.getRecordsByIds(appointmentIds);

        if (existingFeedbacks.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(existingFeedbacks);
    }

    /**
     * Read and return all appointment feedback for a specific appointment
     */
    public Optional<List<AppointmentFeedback>> getRecordsByAppointmentId(String appointmentId) {

        List<AppointmentFeedback> existingFeedback = appointmentFeedbackRepository.
                getRecordsByAppointmentId(appointmentId);

        if (existingFeedback.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(existingFeedback);

    }
}
