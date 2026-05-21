package main.java.backend.controller;

import main.java.backend.model.Appointment;
import main.java.backend.model.ServicePackage;
import main.java.backend.model.StaffAccount;
import main.java.backend.repository.impl.AppointmentRepository;
import main.java.backend.repository.impl.ServicePackageRepository;
import main.java.backend.repository.impl.account.StaffAccountRepository;
import main.java.backend.service.RecordReader;
import main.java.backend.service.RecordWriter;
import main.java.backend.service.appointment.*;
import main.java.backend.util.DataValidator;
import main.java.backend.util.NullValidator;
import main.java.dtos.AppointmentDTO;
import main.java.enums.data.AppointmentStatus;
import main.java.enums.system.DataFieldName;
import main.java.enums.system.OperationType;
import main.java.records.OperationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AppointmentController {

    // Singleton instances of the repositories
    private final AppointmentRepository appointmentRepo = AppointmentRepository.getInstance();
    private final ServicePackageRepository servicePackageRepo = ServicePackageRepository.getInstance();
    private final StaffAccountRepository staffAccountRepo = StaffAccountRepository.getInstance();

    public OperationResult createAppointment(AppointmentDTO appointmentDTO) {

        // Check if there are any null or empty values in the list
        OperationResult nullValidation = validateNullValues(appointmentDTO, OperationType.CREATE);
        if (!nullValidation.isValid()) {
            return nullValidation;
        }

        // Validate service package ID
        OperationResult pkgIdVerification = validateServicePackageId(appointmentDTO.getServicePackageID());
        if (!pkgIdVerification.isValid()) {
            return pkgIdVerification;
        }

        // Retrieve the service package record with the input ID from the repository
        ServicePackage servicePackage = servicePackageRepo.getRecordById(appointmentDTO.getServicePackageID());

        // Begin creating appointment record procedure in service class
        RecordWriter createAppointment = new AppointmentCreation(appointmentRepo, appointmentDTO, servicePackage);
        return createAppointment.execute();
    }

    public OperationResult updateAppointment(AppointmentDTO appointmentDTO) {

        // Check if there are any null or empty values in the list
        OperationResult nullValidation = validateNullValues(appointmentDTO, OperationType.UPDATE);
        if (!nullValidation.isValid()) {
            return nullValidation;
        }

        // Check if the appointment ID is a valid UUID
        if (!DataValidator.validateRecordId(appointmentDTO.getAppointmentID()).isValid()) {
            return OperationResult.failed(DataFieldName.RECORD_ID, " The appointment ID: " +
                    appointmentDTO.getAppointmentID() + " is not an UUID. Please try again.");
        }

        // Validate if the service package ID is a valid UUID
        if (!DataValidator.validateRecordId(appointmentDTO.getServicePackageID()).isValid()) {
            return OperationResult.failed(DataFieldName.RECORD_ID, "The service package ID: " +
                    appointmentDTO.getServicePackageID() + " is not an UUID. Please try again.");
        }

        // Retrieve the service package record with the input ID from the repository
        ServicePackage servicePackage = servicePackageRepo.getRecordById(appointmentDTO.getServicePackageID());

        // Begin updating appointment record procedure in service class
        RecordWriter updateAppointment = new AppointmentUpdate(appointmentRepo, appointmentDTO, servicePackage);
        return updateAppointment.execute();
    }

    public OperationResult assignTechnicianToAppointment(AppointmentDTO appointmentDTO) {

        String appointmentID = appointmentDTO.getAppointmentID();
        String technicianID = appointmentDTO.getTechnicianID();

        if (appointmentID == null || appointmentID.isBlank() || technicianID == null || technicianID.isBlank()) {
            return OperationResult.failed("All required fields must be filled in. Please try again.");
        }

        // Check if the appointment ID is a valid UUID
        if (!DataValidator.validateRecordId(appointmentDTO.getAppointmentID()).isValid()) {
            return OperationResult.failed(DataFieldName.RECORD_ID, " The appointment ID: " +
                    appointmentDTO.getAppointmentID() + " is not an UUID. Please try again.");
        }

        // Check if the technician ID is a valid UUID
        if (!DataValidator.validateRecordId(technicianID).isValid()) {
            return OperationResult.failed(DataFieldName.RECORD_ID, "The technician ID: " +
                    technicianID + " is not an UUID. Please try again.");
        }

        // Retrieve the technician account record with the input ID from the repository
        StaffAccount technicianAcc = staffAccountRepo.getRecordById(technicianID);

        // Begin assigning technician to appointment procedure in service class
        RecordWriter assignTechnician = new AppointmentTechAssignment(appointmentRepo, appointmentDTO, technicianAcc);
        return assignTechnician.execute();
    }

    /**
     * Also used for marking appointment as completed for technicians
     */
    public OperationResult updateAppointmentStatus(AppointmentDTO appointmentDTO) {

        String appointmentID = appointmentDTO.getAppointmentID();
        AppointmentStatus appointmentStatus = appointmentDTO.getAppointmentStatus();

        if (appointmentID == null || appointmentID.isBlank() || appointmentStatus == null) {
            return OperationResult.failed("All required fields must be filled in. Please try again.");
        }

        // Check if the appointment ID is a valid UUID
        if (!DataValidator.validateRecordId(appointmentID).isValid()) {
            return OperationResult.failed(DataFieldName.RECORD_ID, "The appointment ID: " +
                    appointmentID + " is not an UUID. Please try again.");
        }

        // Begin updating appointment status procedure in service class
        RecordWriter updateStatus = new AppointmentStatusUpdate(appointmentRepo, appointmentDTO);
        OperationResult updateStatusResult = updateStatus.execute();

        // Customize result message for updating appointment status
        if (updateStatusResult.isValid()) {
            return OperationResult.success("The appointment status for ID: " + appointmentID +
                    " has been updated successfully. Please check the new status to ensure it has been changed.");
        } else {
            return OperationResult.failed("Oops! The appointment status for ID: " + appointmentID +
                    " has failed to be updated. Please try again.");
        }
    }

    public OperationResult deleteAppointment(String appointmentId) {

        // Check if the inputs are null or empty
        if (appointmentId == null || appointmentId.isBlank()) {
            return OperationResult.failed("The appointment ID must be provided to " +
                    "delete a record. Please try again.");
        }

        // Check if the input is a valid UUID
        if (!DataValidator.validateRecordId(appointmentId).isValid()) {
            return OperationResult.failed("The appointment ID: " + appointmentId +
                    " is not an UUID. Please try again.");
        }

        // Begin deleting service package record procedure in service class
        RecordWriter deleteAppointment = new AppointmentDeletion(appointmentRepo, appointmentId);
        return deleteAppointment.execute();
    }

    /**
     * Optional class is used to determine whether the records are present or not
     * This is for frontend logic in displaying the result message for UI
     */
    public Optional<List<Appointment>> retrieveAllAppointments() {

        // Begin retrieving all appointment record procedures in service class
        RecordReader<Appointment> retrieveAllAppointments = new AppointmentRetrieval(appointmentRepo);
        return retrieveAllAppointments.getAllRecords();
    }

    public Optional<Appointment> retrieveAppointmentById(String servicePkgId) {

        // Begin retrieving appointment record by ID procedure in service class
        RecordReader<Appointment> retrieveAppointmentById = new AppointmentRetrieval(appointmentRepo);
        return retrieveAppointmentById.getRecordById(servicePkgId);
    }

    public Optional<List<Appointment>> retrieveAppointmentsByIds(List<String> appointmentIds) {

        // Begin retrieving multiple appointment records by IDs procedure in service class
        RecordReader<Appointment> retrieveAppointmentsByIds = new AppointmentRetrieval(appointmentRepo);
        return retrieveAppointmentsByIds.getRecordsByIds(appointmentIds);
    }

    public Optional<List<Appointment>> retrieveAppointmentsByApptStatus(AppointmentStatus appointmentStatus) {

        // Begin retrieving multiple appointment records by status procedure in service class
        AppointmentRetrieval retrieveAppointmentsByApptStatus = new AppointmentRetrieval(appointmentRepo);
        return retrieveAppointmentsByApptStatus.getRecordsByApptStatus(appointmentStatus);
    }

    /**
     * Validate the format and length of appointment ID
     * This validation is done before record retrieval, so the frontend can first display the validation result
     */
    public OperationResult validateAppointmentId(String appointmentId) {

        // Check if the appointment ID and its type are null or empty
        if (appointmentId == null || appointmentId.isBlank()) {
            return OperationResult.failed("The appointment ID must be provided to retrieve records. " +
                    "Please try again.");
        }

        if (!DataValidator.validateRecordId(appointmentId).isValid()) {
            return OperationResult.failed("The appointment ID: " + appointmentId +
                    " is not an UUID. Please try again.");
        }

        return OperationResult.success();
    }

    public OperationResult validateAppointmentIds(List<String> appointmentIds) {

        // Check if the ID list and its type are null or empty
        if (appointmentIds == null || appointmentIds.isEmpty()) {
            return OperationResult.failed("The appointment IDs must be provided to retrieve records. " +
                    "Please try again.");
        }

        // Check if any of the appointment IDs are null or empty
        for (String servicePkgId : appointmentIds) {
            if (servicePkgId == null || servicePkgId.isBlank() ) {
                return OperationResult.failed("One of the appointment IDs is null or empty. Please try again.");
            }
        }

        // Check if any of the appointment IDs are invalid
        for (String servicePkgId : appointmentIds) {
            if (!DataValidator.validateRecordId(servicePkgId).isValid()) {
                return OperationResult.failed("The appointment ID: " + servicePkgId +
                        " is not an UUID. Please try again.");
            }
        }

        return OperationResult.success();
    }

    /**
     * Check if the appointment status is null before retrieving records
     * So the frontend can display the validation result before beginning the retrieval
     */
    public OperationResult validateAppointmentStatus(AppointmentStatus appointmentStatus) {

        if (appointmentStatus == null) {
            return OperationResult.failed("The appointment status must be provided to retrieve records. " +
                    "Please try again.");
        }

        return OperationResult.success();
    }

    /**
     * Check if there are any null or empty values of required fields in the given DTO, excluding service package ID
     * Used for creating and updating records
     */
    private OperationResult validateNullValues(AppointmentDTO appointmentDTO, OperationType operationType) {
        List<Object> appointmentValues = new ArrayList<>();

        // If the operation is updating record, add the ID to the list for null validation
        if (operationType == OperationType.UPDATE) {
            appointmentValues.add(appointmentDTO.getAppointmentID());
        }

        appointmentValues.add(appointmentDTO.getAppointmentTitle());
        appointmentValues.add(appointmentDTO.getAppointmentDescription());
        appointmentValues.add(appointmentDTO.getServiceDate());
        appointmentValues.add(appointmentDTO.getServiceTime());
        appointmentValues.add(appointmentDTO.getAppointmentStatus());

        return NullValidator.isAllValuesNull(appointmentValues);
    }

    /**
     * Full validation of the service package ID in checking nullity, format, and linking record
     */
    private OperationResult validateServicePackageId(String servicePackageId) {

        // 1. Check if the service package ID is null or blank
        if (servicePackageId == null || servicePackageId.isBlank()) {
            return OperationResult.failed(DataFieldName.SERVICE_PACKAGE,
                    "The service package ID must be provided. Please try again.");
        }

        // 2. Check if the service package ID in UUID format
        if (!DataValidator.validateRecordId(servicePackageId).isValid()) {
            return OperationResult.failed(DataFieldName.SERVICE_PACKAGE,
                    "The service package ID: " + servicePackageId + " is not an UUID. Please try again.");
        }

        // 3. Check if the service package ID exists in the repository
        if (servicePackageRepo.getRecordById(servicePackageId) == null) {
            return OperationResult.failed(DataFieldName.SERVICE_PACKAGE,
                    "The service package with ID " + servicePackageId + " does not exist in the records.");
        }

        // If all validations pass, return success
        return OperationResult.success();
    }
}
