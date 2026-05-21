package main.java.backend.service.appointment;

import main.java.backend.model.Appointment;
import main.java.backend.repository.impl.AppointmentRepository;
import main.java.backend.service.RecordReader;
import main.java.enums.data.AppointmentStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AppointmentRetrieval implements RecordReader<Appointment> {

    private final AppointmentRepository appointmentRepository;

    public AppointmentRetrieval(AppointmentRepository appointmentRepository) {
            this.appointmentRepository = appointmentRepository;
    }

    /**
     * Read and return all appointments from the repository
     */
    @Override
    public Optional<List<Appointment>> getAllRecords() {

        List<Appointment> existingAppointments = new ArrayList<>(appointmentRepository.getAllRecords());

        if (existingAppointments.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(existingAppointments);
    }

    /**
     * Read and return a single appointment based on the given ID
     */
    @Override
    public Optional<Appointment> getRecordById(String appointmentId) {

        Appointment existingAppointment = appointmentRepository.getRecordById(appointmentId);

        if (existingAppointment == null) {
            return Optional.empty();
        }

        return Optional.of(existingAppointment);
    }

    /**
     * Read and return multiple appointments based on the given IDs
     */
    @Override
    public Optional<List<Appointment>> getRecordsByIds(List<String> appointmentIds) {

        List<Appointment> existingAppointments = appointmentRepository.getRecordsByIds(appointmentIds);

        if (existingAppointments.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(existingAppointments);
    }

    /**
     * Read and return multiple appointments based on the given appointment status
     * Used for filtering appointments by status
     */
    public Optional<List<Appointment>> getRecordsByApptStatus(AppointmentStatus status) {

        List<Appointment> existingAppointments = appointmentRepository.getRecordsByApptStatus(status);

        if (existingAppointments.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(existingAppointments);
    }
}
