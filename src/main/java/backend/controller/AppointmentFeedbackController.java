package main.java.backend.controller;

import main.java.backend.model.Appointment;
import main.java.backend.model.AppointmentFeedback;
import main.java.backend.model.StaffAccount;
import main.java.backend.repository.impl.AppointmentFeedbackRepository;
import main.java.backend.repository.impl.AppointmentRepository;
import main.java.backend.repository.impl.account.StaffAccountRepository;
import main.java.backend.service.RecordReader;
import main.java.backend.service.RecordWriter;
import main.java.backend.service.appointmentfeedback.AppointmentFeedbackCreation;
import main.java.backend.service.appointmentfeedback.AppointmentFeedbackDeletion;
import main.java.backend.service.appointmentfeedback.AppointmentFeedbackRetrieval;
import main.java.backend.service.appointmentfeedback.AppointmentFeedbackUpdate;
import main.java.backend.util.DataValidator;
import main.java.backend.util.NullValidator;
import main.java.dtos.AppointmentFeedbackDTO;
import main.java.enums.system.DataFieldName;
import main.java.enums.system.OperationType;
import main.java.records.OperationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AppointmentFeedbackController {

    // Singleton instances of the repositories
    private final AppointmentFeedbackRepository appointmentFeedbackRepo = AppointmentFeedbackRepository.getInstance();
    private final AppointmentRepository appointmentRepo = AppointmentRepository.getInstance();
    private final StaffAccountRepository staffAccountRepo = StaffAccountRepository.getInstance();

    public OperationResult createAppointmentFeedback(AppointmentFeedbackDTO appointmentFeedbackDTO) {

        // Check if there are any null or empty values in the list
        OperationResult nullValidation = validateNullValues(appointmentFeedbackDTO, OperationType.CREATE);
        if (!nullValidation.isValid()) {
            return OperationResult.failed("All required fields must be filled in. Please try again.");
        }

        // Validate appointment ID
        String appointmentId = appointmentFeedbackDTO.getAppointmentID();
        OperationResult appointmentIdVerification = validateAuthorId(appointmentId);
        if (!appointmentIdVerification.isValid()) {
            return appointmentIdVerification;
        }

        // Validate author (technician account) ID
        String technicianAccountId = appointmentFeedbackDTO.getFeedbackAuthorID();
        OperationResult technicianIdVerification = validateAuthorId(technicianAccountId);
        if (!technicianIdVerification.isValid()) {
            return technicianIdVerification;
        }

        // Retrieve the appointment and technician account records with the respective IDs from the repositories
        Appointment appointment = appointmentRepo.getRecordById(appointmentId);
        StaffAccount technicianAcc = staffAccountRepo.getRecordById(technicianAccountId);

        // Begin creating appointment feedback record procedure in service class
        RecordWriter createAppointmentFeedback = new AppointmentFeedbackCreation(
                appointmentFeedbackRepo, appointmentFeedbackDTO, appointment, technicianAcc);
        return createAppointmentFeedback.execute();
    }

    public OperationResult updateAppointmentComment(AppointmentFeedbackDTO appointmentFeedbackDTO) {

        // Check if there are any null or empty values in the list
        OperationResult nullValidation = validateNullValues(appointmentFeedbackDTO, OperationType.CREATE);
        if (!nullValidation.isValid()) {
            return OperationResult.failed("All required fields must be filled in. Please try again.");
        }

        // Check if the feedback ID is in UUID format
        if (!DataValidator.validateRecordId(appointmentFeedbackDTO.getFeedbackID()).isValid()) {
            return OperationResult.failed(DataFieldName.RECORD_ID, "The feedback ID: " +
                    appointmentFeedbackDTO.getFeedbackID() + " is not an UUID. Please try again.");
        }

        // Begin updating appointment feedback record procedure in service class
        RecordWriter updateAppointmentFeedback = new AppointmentFeedbackUpdate(
                appointmentFeedbackRepo, appointmentFeedbackDTO);
        return updateAppointmentFeedback.execute();
    }

    public OperationResult deleteAppointmentFeedback(String feedbackId) {

        // Check if the inputs are null or empty
        if (feedbackId == null || feedbackId.isBlank()) {
            return OperationResult.failed("The feedback ID must be provided to " +
                    "delete a record. Please try again.");
        }

        // Check if the input is a valid UUID
        if (!DataValidator.validateRecordId(feedbackId).isValid()) {
            return OperationResult.failed("The feedback ID: " + feedbackId +
                    " is not an UUID. Please try again.");
        }

        // Begin deleting service package record procedure in service class
        RecordWriter deleteAppointmentFeedback = new AppointmentFeedbackDeletion(appointmentFeedbackRepo, feedbackId);
        return deleteAppointmentFeedback.execute();
    }

    /**
     * Optional class is used to determine whether the records are present or not
     * This is for frontend logic in displaying the result message for UI
     */
    public Optional<List<AppointmentFeedback>> retrieveAllApptFeedback() {

        // Begin retrieving all appointment feedback record procedures in service class
        RecordReader<AppointmentFeedback> retrieveAllFeedback =
                new AppointmentFeedbackRetrieval(appointmentFeedbackRepo);
        return retrieveAllFeedback.getAllRecords();
    }

    public Optional<AppointmentFeedback> retrieveApptCommentById(String feedbackId) {

        // Begin retrieving appointment feedback record by ID procedure in service class
        RecordReader<AppointmentFeedback> retrieveCommentById =
                new AppointmentFeedbackRetrieval(appointmentFeedbackRepo);
        return retrieveCommentById.getRecordById(feedbackId);
    }

    public Optional<List<AppointmentFeedback>> retrieveApptFeedbackByIds(List<String> feedbackIds) {

        // Begin retrieving multiple appointment feedback records by IDs procedure in service class
        RecordReader<AppointmentFeedback> retrieveFeedbackByIds =
                new AppointmentFeedbackRetrieval(appointmentFeedbackRepo);
        return retrieveFeedbackByIds.getRecordsByIds(feedbackIds);
    }

    public Optional<List<AppointmentFeedback>> retrieveApptFeedbackByApptId(String appointmentId) {

        // Begin retrieving multiple appointment feedback records by appointment ID procedure in service class
        AppointmentFeedbackRetrieval retrieveFeedbackByApptId =
                new AppointmentFeedbackRetrieval(appointmentFeedbackRepo);
        return retrieveFeedbackByApptId.getRecordsByAppointmentId(appointmentId);
    }

    /**
     * Validate the format and length of feedback ID
     * This validation is done before record retrieval, so the frontend can first display the validation result
     */
    public OperationResult validateApptFeedbackId(String feedbackId) {

        // Check if the feedback ID and its type are null or empty
        if (feedbackId == null || feedbackId.isBlank()) {
            return OperationResult.failed("The feedback ID must be provided to retrieve records. Please try again.");
        }

        if (!DataValidator.validateRecordId(feedbackId).isValid()) {
            return OperationResult.failed("The feedback ID: " + feedbackId + " is not an UUID. Please try again.");
        }

        return OperationResult.success();
    }

    public OperationResult validateApptFeedbackIds(List<String> feedbackIds) {

        // Check if the ID list and its type are null or empty
        if (feedbackIds == null || feedbackIds.isEmpty()) {
            return OperationResult.failed("The feedback IDs must be provided to retrieve records. Please try again.");
        }

        // Check if any of the feedback IDs are null or empty
        for (String feedbackId : feedbackIds) {
            if (feedbackId == null || feedbackId.isBlank() ) {
                return OperationResult.failed("One of the feedback IDs is null or empty. Please try again.");
            }
        }

        // Check if any of the feedback IDs are invalid
        for (String feedbackId : feedbackIds) {
            if (!DataValidator.validateRecordId(feedbackId).isValid()) {
                return OperationResult.failed("The feedback ID: " + feedbackId + " is not an UUID. Please try again.");
            }
        }

        return OperationResult.success();
    }

    /**
     * Check if there are any null or empty values of required fields in the given DTO
     * Used for creating and updating records
     */
    private OperationResult validateNullValues(AppointmentFeedbackDTO appointmentFeedbackDTO,
                                               OperationType operationType) {

        List<Object> feedbackValues = new ArrayList<>();

        // If the operation is updating record, add the ID to the list for null validation
        if (operationType == OperationType.UPDATE) {
            feedbackValues.add(appointmentFeedbackDTO.getFeedbackID());
        }

        // If the operation is creating a new record, add the appointment ID and technician account ID to the list
        if (operationType == OperationType.CREATE) {
            feedbackValues.add(appointmentFeedbackDTO.getAppointmentID());
            feedbackValues.add(appointmentFeedbackDTO.getFeedbackAuthorID());
        }

        feedbackValues.add(appointmentFeedbackDTO.getFeedbackContent());

        return NullValidator.isAllValuesNull(feedbackValues);
    }

    /**
     * Full validation of the appointment ID in checking nullity, format, and linking record
     */
    private OperationResult validateAppointmentId(String appointmentId) {

        // 1. Check if the appointment ID is null or blank
        if (appointmentId == null || appointmentId.isBlank()) {
            return OperationResult.failed(DataFieldName.APPOINTMENT_ID,
                    "The appointment ID must be provided. Please try again.");
        }

        // 2. Check if the appointment ID in UUID format
        if (!DataValidator.validateRecordId(appointmentId).isValid()) {
            return OperationResult.failed(DataFieldName.APPOINTMENT_ID,
                    "The appointment ID: " + appointmentId + " is not an UUID. Please try again.");
        }

        // 3. Check if the appointment ID exists in the repository
        if (appointmentRepo.getRecordById(appointmentId) == null) {
            return OperationResult.failed(DataFieldName.APPOINTMENT_ID,
                    "The appointment with ID " + appointmentId + " does not exist in the records.");
        }

        // If all validations pass, return success
        return OperationResult.success();
    }

    /**
     * Full validation of the author (technician account) ID in checking nullity, format, and linking record
     */
    private OperationResult validateAuthorId(String technicianId) {

        // 1. Check if the technician account ID is null or blank
        if (technicianId == null || technicianId.isBlank()) {
            return OperationResult.failed(DataFieldName.TECHNICIAN_ID,
                    "The technician account ID must be provided. Please try again.");
        }

        // 2. Check if the technician account ID in UUID format
        if (!DataValidator.validateRecordId(technicianId).isValid()) {
            return OperationResult.failed(DataFieldName.TECHNICIAN_ID,
                    "The technician account ID: " + technicianId + " is not an UUID. Please try again.");
        }

        // 3. Check if the technician account ID exists in the repository
        if (staffAccountRepo.getRecordById(technicianId) == null) {
            return OperationResult.failed(DataFieldName.TECHNICIAN_ID,
                    "The technician account with ID " + technicianId + " does not exist in the records.");
        }

        // If all validations pass, return success
        return OperationResult.success();
    }
}
