package main.java.backend.service.servicepackage;

import main.java.backend.model.Appointment;
import main.java.backend.model.ServicePackage;
import main.java.backend.repository.impl.AppointmentRepository;
import main.java.backend.repository.impl.ServicePackageRepository;
import main.java.backend.service.RecordWriter;
import main.java.enums.data.AppointmentStatus;
import main.java.records.OperationResult;

import java.util.List;

public class ServicePackageDeletion implements RecordWriter {

    private final ServicePackageRepository servicePackageRepository;
    private final AppointmentRepository appointmentRepository;
    private final String servicePkgID;

    public ServicePackageDeletion(ServicePackageRepository servicePackageRepository,
                                  AppointmentRepository appointmentRepository, String servicePkgID) {
        this.servicePackageRepository = servicePackageRepository;
        this.appointmentRepository = appointmentRepository;
        this.servicePkgID = servicePkgID;
    }

    @Override
    public OperationResult execute() {

        // Check if the service package exists with the given ID
        ServicePackage existingServicePkg = servicePackageRepository.getRecordById(servicePkgID);
        if (existingServicePkg == null) {
            return OperationResult.failed("The service package with ID: " + servicePkgID +
                    " does not exist in the system.");
        }

        // Get all the appointments that have this service package
        List<Appointment> involvedAppointments = appointmentRepository.
                getRecordsByServicePackage(existingServicePkg);

        // Check if one of the involved appointments is still pending or work in progress
        // If there is, return an error message
        for (Appointment appointment : involvedAppointments) {
            if (appointment.getAppointmentStatus() == AppointmentStatus.PENDING ||
            appointment.getAppointmentStatus() == AppointmentStatus.IN_PROGRESS) {
                return OperationResult.failed("The service package cannot be deleted because the involved " +
                        "appointment with ID: " + appointment.getAppointmentID() +
                        " is still pending or work in progress. Deletion process has stopped here.");
            }
        }

        // Set the appointment of all the involved appointments to null
        List<Appointment> appointmentsToUpdate = involvedAppointments.stream()
                .peek(appointment -> appointment.setServicePackage(null))
                .toList();

        // Update all the appointments in the text file at once
        if (!appointmentsToUpdate.isEmpty()) {
            OperationResult packageUpdateResult = appointmentRepository.bulkUpdateRecord(appointmentsToUpdate);

            if (!packageUpdateResult.isValid()) {
                return OperationResult.failed("Failed to update appointments linked to service package ID "
                        + servicePkgID + ". Deletion process has stopped here.");
            }
        }

        // Finally, delete the service package record in the text file
        return servicePackageRepository.deleteRecord(existingServicePkg);
    }
}
