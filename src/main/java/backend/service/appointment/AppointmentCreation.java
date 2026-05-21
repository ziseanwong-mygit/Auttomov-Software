package main.java.backend.service.appointment;

import main.java.backend.model.Appointment;
import main.java.backend.model.ServicePackage;
import main.java.backend.repository.impl.AppointmentRepository;
import main.java.backend.service.RecordWriter;
import main.java.backend.util.RecordNoGenerator;
import main.java.dtos.AppointmentDTO;
import main.java.enums.data.AppointmentStatus;
import main.java.records.OperationResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public class AppointmentCreation implements RecordWriter {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentDTO appointmentDTO;
    private final ServicePackage existingPackage;

    public AppointmentCreation(AppointmentRepository appointmentRepository, AppointmentDTO appointmentDTO,
                               ServicePackage existingPackage) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentDTO = appointmentDTO;
        this.existingPackage = existingPackage;
    }

    @Override
    public OperationResult execute() {

        // Create a new appointment object
        Appointment newAppointment = populateAppointmentDetails();

        // Save the appointment into the file
        return appointmentRepository.createRecord(newAppointment);
    }

    private Appointment populateAppointmentDetails() {

        // Retrieve today's date for appointment creation and update date
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Generate new appointment ID and order number for the new record
        String appointmentId = UUID.randomUUID().toString();
        String appointmentOrderNo = "APPT" + RecordNoGenerator.generateRecordNo();

        // Extract the appointment details from the DTO
        String appointmentTitle = appointmentDTO.getAppointmentTitle();
        String appointmentDescription = appointmentDTO.getAppointmentDescription();
        LocalDate serviceDate = appointmentDTO.getServiceDate();
        LocalTime serviceTime = appointmentDTO.getServiceTime();
        AppointmentStatus appointmentStatus = AppointmentStatus.PENDING;

        // When creating a new appointment, the technician is not assigned yet, so it is set to null
        return new Appointment(appointmentId, appointmentOrderNo, currentDateTime, currentDateTime,
                appointmentTitle, appointmentDescription, existingPackage, serviceDate, serviceTime,
                null, appointmentStatus);
    }
}
