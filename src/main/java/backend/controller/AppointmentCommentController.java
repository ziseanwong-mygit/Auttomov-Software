package main.java.backend.controller;

import main.java.backend.model.Appointment;
import main.java.backend.model.AppointmentComment;
import main.java.backend.model.UserAccount;
import main.java.backend.repository.impl.AppointmentCommentRepository;
import main.java.backend.repository.impl.AppointmentRepository;
import main.java.backend.repository.impl.account.CustomerAccountRepository;
import main.java.backend.service.RecordReader;
import main.java.backend.service.RecordWriter;
import main.java.backend.service.appointmentcomment.AppointmentCommentCreation;
import main.java.backend.service.appointmentcomment.AppointmentCommentDeletion;
import main.java.backend.service.appointmentcomment.AppointmentCommentRetrieval;
import main.java.backend.service.appointmentcomment.AppointmentCommentUpdate;
import main.java.backend.util.DataValidator;
import main.java.backend.util.NullValidator;
import main.java.dtos.AppointmentCommentDTO;
import main.java.enums.system.DataFieldName;
import main.java.enums.system.OperationType;
import main.java.records.OperationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AppointmentCommentController {

    // Singleton instances of the repositories
    private final AppointmentCommentRepository appointmentCommentRepo = AppointmentCommentRepository.getInstance();
    private final AppointmentRepository appointmentRepo = AppointmentRepository.getInstance();
    private final CustomerAccountRepository customerAccountRepo = CustomerAccountRepository.getInstance();

    public OperationResult createAppointmentComment(AppointmentCommentDTO appointmentCommentDTO) {

        // Check if there are any null or empty values in the list
        OperationResult nullValidation = validateNullValues(appointmentCommentDTO, OperationType.CREATE);
        if (!nullValidation.isValid()) {
            return OperationResult.failed("All required fields must be filled in. Please try again.");
        }

        // Validate appointment ID
        String appointmentId = appointmentCommentDTO.getAppointmentID();
        OperationResult appointmentIdVerification = validateAppointmentId(appointmentId);
        if (!appointmentIdVerification.isValid()) {
            return appointmentIdVerification;
        }

        // Validate author (customer account) ID
        String customerAccountId = appointmentCommentDTO.getCommentAuthorID();
        OperationResult customerIdVerification = validateAuthorId(customerAccountId);
        if (!customerIdVerification.isValid()) {
            return customerIdVerification;
        }

        // Retrieve the appointment and customer account records with the respective IDs from the repositories
        Appointment appointment = appointmentRepo.getRecordById(appointmentId);
        UserAccount customerAcc = customerAccountRepo.getRecordById(customerAccountId);

        // Begin creating appointment comment record procedure in service class
        RecordWriter createAppointmentComment = new AppointmentCommentCreation(
                appointmentCommentRepo, appointmentCommentDTO, appointment, customerAcc);
        return createAppointmentComment.execute();
    }

    public OperationResult updateAppointmentComment(AppointmentCommentDTO appointmentCommentDTO) {

        // Check if there are any null or empty values in the list
        OperationResult nullValidation = validateNullValues(appointmentCommentDTO, OperationType.UPDATE);
        if (!nullValidation.isValid()) {
            return OperationResult.failed("All required fields must be filled in. Please try again.");
        }

        // Check if the comment ID is in UUID format
        if (!DataValidator.validateRecordId(appointmentCommentDTO.getCommentID()).isValid()) {
            return OperationResult.failed(DataFieldName.RECORD_ID, "The comment ID: " +
                    appointmentCommentDTO.getCommentID() + " is not an UUID. Please try again.");
        }

        // Begin updating appointment comment record procedure in service class
        RecordWriter updateAppointmentComment = new AppointmentCommentUpdate(
                appointmentCommentRepo, appointmentCommentDTO);
        return updateAppointmentComment.execute();
    }

    public OperationResult deleteAppointmentComment(String commentId) {

        // Check if the inputs are null or empty
        if (commentId == null || commentId.isBlank()) {
            return OperationResult.failed("The comment ID must be provided to " +
                    "delete a record. Please try again.");
        }

        // Check if the input is a valid UUID
        if (!DataValidator.validateRecordId(commentId).isValid()) {
            return OperationResult.failed("The comment ID: " + commentId +
                    " is not an UUID. Please try again.");
        }

        // Begin deleting service package record procedure in service class
        RecordWriter deleteAppointmentComment = new AppointmentCommentDeletion(appointmentCommentRepo, commentId);
        return deleteAppointmentComment.execute();
    }

    /**
     * Optional class is used to determine whether the records are present or not
     * This is for frontend logic in displaying the result message for UI
     */
    public Optional<List<AppointmentComment>> retrieveAllApptComments() {

        // Begin retrieving all appointment comment record procedures in service class
        RecordReader<AppointmentComment> retrieveAllComments =
                new AppointmentCommentRetrieval(appointmentCommentRepo);
        return retrieveAllComments.getAllRecords();
    }

    public Optional<AppointmentComment> retrieveApptCommentById(String commentId) {

        // Begin retrieving appointment comment record by ID procedure in service class
        RecordReader<AppointmentComment> retrieveCommentById =
                new AppointmentCommentRetrieval(appointmentCommentRepo);
        return retrieveCommentById.getRecordById(commentId);
    }

    public Optional<List<AppointmentComment>> retrieveApptCommentsByIds(List<String> commentIds) {

        // Begin retrieving multiple appointment comment records by IDs procedure in service class
        RecordReader<AppointmentComment> retrieveCommentsByIds =
                new AppointmentCommentRetrieval(appointmentCommentRepo);
        return retrieveCommentsByIds.getRecordsByIds(commentIds);
    }

    public Optional<List<AppointmentComment>> retrieveApptCommentsByApptId(String appointmentId) {

        // Begin retrieving multiple appointment comment records by appointment ID procedure in service class
        AppointmentCommentRetrieval retrieveCommentsByApptId =
                new AppointmentCommentRetrieval(appointmentCommentRepo);
        return retrieveCommentsByApptId.getRecordsByAppointmentId(appointmentId);
    }

    /**
     * Validate the format and length of comment ID
     * This validation is done before record retrieval, so the frontend can first display the validation result
     */
    public OperationResult validateApptCommentId(String commentId) {

        // Check if the comment ID and its type are null or empty
        if (commentId == null || commentId.isBlank()) {
            return OperationResult.failed("The comment ID must be provided to retrieve records. Please try again.");
        }

        if (!DataValidator.validateRecordId(commentId).isValid()) {
            return OperationResult.failed("The comment ID: " + commentId + " is not an UUID. Please try again.");
        }

        return OperationResult.success();
    }

    public OperationResult validateApptCommentIds(List<String> commentIds) {

        // Check if the ID list and its type are null or empty
        if (commentIds == null || commentIds.isEmpty()) {
            return OperationResult.failed("The comment IDs must be provided to retrieve records. Please try again.");
        }

        // Check if any of the comment IDs are null or empty
        for (String commentId : commentIds) {
            if (commentId == null || commentId.isBlank() ) {
                return OperationResult.failed("One of the comment IDs is null or empty. Please try again.");
            }
        }

        // Check if any of the comment IDs are invalid
        for (String commentId : commentIds) {
            if (!DataValidator.validateRecordId(commentId).isValid()) {
                return OperationResult.failed("The comment ID: " + commentId + " is not an UUID. Please try again.");
            }
        }

        return OperationResult.success();
    }

    /**
     * Check if there are any null or empty values of required fields in the given DTO,
     * excluding appointment and author IDs
     * Used for creating and updating records
     */
    private OperationResult validateNullValues(AppointmentCommentDTO appointmentCommentDTO,
                                               OperationType operationType) {

        List<Object> commentValues = new ArrayList<>();

        // If the operation is updating record, add the ID to the list for null validation
        if (operationType == OperationType.UPDATE) {
            commentValues.add(appointmentCommentDTO.getCommentID());
        }

        commentValues.add(appointmentCommentDTO.getCommentContent());

        return NullValidator.isAllValuesNull(commentValues);
    }

    /**
     * Full validation of the appointment ID in checking nullity, format, and linking record
     */
    public OperationResult validateAppointmentId(String appointmentId) {

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
     * Full validation of the author (customer account) ID in checking nullity, format, and linking record
     */
    private OperationResult validateAuthorId(String customerId) {

        // 1. Check if the customer account ID is null or blank
        if (customerId == null || customerId.isBlank()) {
            return OperationResult.failed(DataFieldName.CUSTOMER_ID,
                    "The customer account ID must be provided. Please try again.");
        }

        // 2. Check if the customer account ID in UUID format
        if (!DataValidator.validateRecordId(customerId).isValid()) {
            return OperationResult.failed(DataFieldName.CUSTOMER_ID,
                    "The customer account ID: " + customerId + " is not an UUID. Please try again.");
        }

        // 3. Check if the customer account ID exists in the repository
        if (customerAccountRepo.getRecordById(customerId) == null) {
            return OperationResult.failed(DataFieldName.CUSTOMER_ID,
                    "The customer account with ID " + customerId + " does not exist in the records.");
        }

        // If all validations pass, return success
        return OperationResult.success();
    }
}
