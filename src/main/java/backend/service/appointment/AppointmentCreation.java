package main.java.backend.service.appointment;

import main.java.backend.model.Appointment;
import main.java.backend.model.ServicePackage;
import main.java.backend.model.UserAccount;
import main.java.backend.repository.impl.AppointmentRepository;
import main.java.backend.service.RecordWriter;
import main.java.backend.util.RecordNoGenerator;
import main.java.dtos.AppointmentDTO;
import main.java.enums.data.AppointmentStatus;
import main.java.records.OperationResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public class AppointmentCreation implements RecordWriter {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentDTO appointmentDTO;
    private final UserAccount existingCustomer;
    private final ServicePackage existingPackage;

    public AppointmentCreation(AppointmentRepository appointmentRepository, AppointmentDTO appointmentDTO,
                               UserAccount existingCustomer, ServicePackage existingPackage) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentDTO = appointmentDTO;
        this.existingCustomer = existingCustomer;
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
        LocalTime serviceStartTime = appointmentDTO.getServiceStartTime();

        // Calculate the end time of the service based on the service duration from the package
        int serviceDuration = existingPackage.getServiceDuration();
        LocalTime serviceEndTime = serviceStartTime.plusHours(serviceDuration);

        // When creating a new appointment, the technician is not assigned yet,
        // so it is set to an empty list to prevent NullPointerException
        // Also set the appointment status to PENDING by default
        return new Appointment(appointmentId, appointmentOrderNo, currentDateTime, currentDateTime,
                appointmentTitle, appointmentDescription, existingCustomer, existingPackage, serviceDate,
                serviceStartTime, serviceEndTime, List.of(), AppointmentStatus.PENDING);
    }
}
