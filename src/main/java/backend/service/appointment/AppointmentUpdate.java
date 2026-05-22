package main.java.backend.service.appointment;

import main.java.backend.model.Appointment;
import main.java.backend.model.ServicePackage;
import main.java.backend.repository.impl.AppointmentRepository;
import main.java.backend.service.RecordWriter;
import main.java.dtos.AppointmentDTO;
import main.java.records.FieldComparison;
import main.java.records.OperationResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentUpdate implements RecordWriter {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentDTO appointmentDTO;
    private final ServicePackage existingServicePkg;

    public AppointmentUpdate(AppointmentRepository appointmentRepository, AppointmentDTO appointmentDTO,
                             ServicePackage existingServicePkg) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentDTO = appointmentDTO;
        this.existingServicePkg = existingServicePkg;
    }

    @Override
    public OperationResult execute() {

        // Check if the appointment exists with the given ID
        Appointment existingAppointment = appointmentRepository.getRecordById(appointmentDTO.getAppointmentID());
        if (existingAppointment == null) {
            return OperationResult.failed("The appointment with ID: " + appointmentDTO.getAppointmentID() +
                    " does not exist in the system.");
        }

        // Get the list of fields that are changed
        List<FieldComparison<?>> changedFields = compareChanges(existingAppointment);

        // If nothing changed at all across both kinds
        if (changedFields.isEmpty()) {
            return OperationResult.failed(
                    "No fields have been changed. Please update one or more fields.");
        }

        // Apply updates only to changed fields
        setChangedValues(changedFields, existingAppointment);

        // Begin updating appointment record in the repository
        return appointmentRepository.updateRecord(existingAppointment);
    }

    /**
     * Compare the values between the existing appointment and incoming inputs (DTO) to get the changed fields
     */
    private List<FieldComparison<?>> compareChanges(Appointment existingAppointment) {

        // Declare a list to store all the fields for comparison later
        List<FieldComparison<?>> appointmentFields = new ArrayList<>();

        appointmentFields.add(new FieldComparison<>("title", 
                existingAppointment.getAppointmentTitle(), appointmentDTO.getAppointmentTitle(), null));
        appointmentFields.add(new FieldComparison<>("description", 
                existingAppointment.getAppointmentDescription(), appointmentDTO.getAppointmentDescription(),
                null));
        appointmentFields.add(new FieldComparison<>("servicePackage", 
                existingAppointment.getServicePackage().getServicePackageID(), existingServicePkg.getServicePackageID(),
                null));
        appointmentFields.add(new FieldComparison<>("serviceDate", 
                existingAppointment.getServiceDate(), appointmentDTO.getServiceDate(), null));
        appointmentFields.add(new FieldComparison<>("serviceTime",
                existingAppointment.getServiceStartTime(), appointmentDTO.getServiceStartTime(), null));
        appointmentFields.add(new FieldComparison<>("appointmentStatus",
                existingAppointment.getAppointmentStatus(), appointmentDTO.getAppointmentStatus(), null));

        // Filter to get the changed fields
        return appointmentFields.stream()
                .filter(FieldComparison::isChanged)
                .toList();
    }

    /**
     * Set the new values of the changed fields in the existing appointment object
     * The object will be used to update the record in the file
     */
    private void setChangedValues(List<FieldComparison<?>> changedFields, Appointment existingAppointment) {

        // Set the appointment record update time with the current date and time
        existingAppointment.setAppointmentUpdateTime(LocalDateTime.now());

        for (FieldComparison<?> field : changedFields) {
            switch (field.fieldName()) {
                case "title" -> existingAppointment.setAppointmentTitle(appointmentDTO.getAppointmentTitle());
                case "description" -> existingAppointment.setAppointmentDescription(
                        appointmentDTO.getAppointmentDescription());
                case "servicePackage" -> existingAppointment.setServicePackage(existingServicePkg);
                case "serviceDate" -> existingAppointment.setServiceDate(appointmentDTO.getServiceDate());
                case "serviceTime" -> existingAppointment.setServiceStartTime(appointmentDTO.getServiceStartTime());
                case "appointmentStatus" -> existingAppointment.setAppointmentStatus(
                        appointmentDTO.getAppointmentStatus());
            }
        }
    }
}
