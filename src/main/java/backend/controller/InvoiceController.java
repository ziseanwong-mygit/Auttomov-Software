package main.java.backend.controller;

import main.java.backend.model.Appointment;
import main.java.backend.model.Invoice;
import main.java.backend.model.StaffAccount;
import main.java.backend.repository.impl.AppointmentRepository;
import main.java.backend.repository.impl.InvoiceRepository;
import main.java.backend.repository.impl.PaymentRecordRepository;
import main.java.backend.repository.impl.ReceiptRepository;
import main.java.backend.repository.impl.account.StaffAccountRepository;
import main.java.backend.service.RecordReader;
import main.java.backend.service.RecordWriter;
import main.java.backend.service.invoice.*;
import main.java.backend.util.DataValidator;
import main.java.backend.util.NullValidator;
import main.java.dtos.InvoiceDTO;
import main.java.enums.data.InvoiceStatus;
import main.java.enums.data.JobPosition;
import main.java.enums.system.DataFieldName;
import main.java.enums.system.OperationType;
import main.java.records.OperationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InvoiceController {

    // Singleton instances of the repositories
    private final InvoiceRepository invoiceRepo = InvoiceRepository.getInstance();
    private final StaffAccountRepository staffAccountRepo = StaffAccountRepository.getInstance();
    private final AppointmentRepository appointmentRepo = AppointmentRepository.getInstance();
    private final PaymentRecordRepository paymentRecordRepo = PaymentRecordRepository.getInstance();
    private final ReceiptRepository receiptRepo = ReceiptRepository.getInstance();

    public OperationResult createInvoice(InvoiceDTO invoiceDTO) {

        // Check if there are any null or empty values in the list
        OperationResult nullValidation = validateNullValues(invoiceDTO, OperationType.CREATE);
        if (!nullValidation.isValid()) {
            return OperationResult.failed("All required fields must be filled in. Please try again.");
        }

        // Validate appointment ID
        String appointmentId = invoiceDTO.getAppointmentID();
        OperationResult appointmentIdVerification = validateAppointmentId(appointmentId);
        if (!appointmentIdVerification.isValid()) {
            return appointmentIdVerification;
        }

        // Validate staff account ID
        String staffAccountId = invoiceDTO.getStaffAccountID();
        OperationResult staffIdVerification = validateStaffId(staffAccountId);
        if (!staffIdVerification.isValid()) {
            return staffIdVerification;
        }

        // Validate additional fee price logic
        OperationResult addFeeValidation = DataValidator.validatePrice(invoiceDTO.getAdditionalFeeAmount());
        if (!addFeeValidation.isValid()) {
            return addFeeValidation;
        }

        // Retrieve the staff account and appointment records with the respective IDs from the repository
        StaffAccount staffAcc = staffAccountRepo.getRecordById(staffAccountId);
        Appointment appointment = appointmentRepo.getRecordById(appointmentId);

        // Begin creating invoice record procedure in service class
        RecordWriter createInvoice = new InvoiceCreation(invoiceRepo, invoiceDTO, staffAcc, appointment);
        return createInvoice.execute();
    }

    public OperationResult updateInvoice(InvoiceDTO invoiceDTO) {

        // Check if there are any null or empty values in the list
        OperationResult nullValidation = validateNullValues(invoiceDTO, OperationType.UPDATE);
        if (!nullValidation.isValid()) {
            return OperationResult.failed("All required fields must be filled in. Please try again.");
        }

        // Check if the invoice ID is in UUID format
        if (!DataValidator.validateRecordId(invoiceDTO.getInvoiceID()).isValid()) {
            return OperationResult.failed(DataFieldName.RECORD_ID, "The invoice ID: " +
                    invoiceDTO.getInvoiceID() + " is not an UUID. Please try again.");
        }

        // Validate additional fee price logic
        OperationResult addFeeValidation = DataValidator.validatePrice(invoiceDTO.getAdditionalFeeAmount());
        if (!addFeeValidation.isValid()) {
            return addFeeValidation;
        }

        // Begin updating invoice record procedure in service class
        RecordWriter updateInvoice = new InvoiceUpdate(invoiceRepo, invoiceDTO);
        return updateInvoice.execute();
    }

    public OperationResult cancelInvoice(String invoiceId) {

        // Check if the inputs are null or empty
        if (invoiceId == null || invoiceId.isBlank()) {
            return OperationResult.failed("The invoice ID must be provided to cancel an invoice. Please try again.");
        }

        // Check if the invoice ID is in UUID format
        if (!DataValidator.validateRecordId(invoiceId).isValid()) {
            return OperationResult.failed(DataFieldName.RECORD_ID, "The invoice ID: " +
                    invoiceId + " is not an UUID. Please try again.");
        }

        // Begin cancelling invoice record procedure in service class
        RecordWriter cancelInvoice = new InvoiceCancellation(invoiceRepo, invoiceId);
        return cancelInvoice.execute();
    }

    public OperationResult deleteInvoice(String invoiceId) {

        // Check if the inputs are null or empty
        if (invoiceId == null || invoiceId.isBlank()) {
            return OperationResult.failed("The invoice ID must be provided to delete a record. Please try again.");
        }

        // Check if the invoice ID is in UUID format
        if (!DataValidator.validateRecordId(invoiceId).isValid()) {
            return OperationResult.failed(DataFieldName.RECORD_ID, "The invoice ID: " +
                    invoiceId + " is not an UUID. Please try again.");
        }

        // Begin deleting invoice record procedure in service class
        RecordWriter deleteInvoice = new InvoiceDeletion(invoiceRepo, paymentRecordRepo, receiptRepo, invoiceId);
        return deleteInvoice.execute();
    }

    /**
     * Optional class is used to determine whether the records are present or not
     * This is for frontend logic in displaying the result message for UI
     */
    public Optional<List<Invoice>> retrieveAllInvoices() {

        // Begin retrieving all invoice record procedures in service class
        RecordReader<Invoice> retrieveAllInvoices = new InvoiceRetrieval(invoiceRepo);
        return retrieveAllInvoices.getAllRecords();
    }

    public Optional<Invoice> retrieveInvoiceById(String invoiceId) {

        // Begin retrieving invoice record by ID procedure in service class
        RecordReader<Invoice> retrieveInvoiceById = new InvoiceRetrieval(invoiceRepo);
        return retrieveInvoiceById.getRecordById(invoiceId);
    }

    public Optional<List<Invoice>> retrieveInvoicesByIds(List<String> invoiceIds) {

        // Begin retrieving multiple invoice records by IDs procedure in service class
        RecordReader<Invoice> retrieveInvoicesByIds = new InvoiceRetrieval(invoiceRepo);
        return retrieveInvoicesByIds.getRecordsByIds(invoiceIds);
    }

    public Optional<Invoice> retrieveInvoiceByInvoiceNo(String invoiceNo) {

        // Begin retrieving invoice record by document number procedure in service class
        InvoiceRetrieval retrieveInvoicesByInvoiceNo = new InvoiceRetrieval(invoiceRepo);
        return retrieveInvoicesByInvoiceNo.getRecordByInvoiceDocNo(invoiceNo);
    }

    public Optional<List<Invoice>> retrieveInvoicesByApptId(String appointmentId) {

        // Begin retrieving multiple invoice records by appointment ID procedure in service class
        InvoiceRetrieval retrieveInvoicesByApptId = new InvoiceRetrieval(invoiceRepo);
        return retrieveInvoicesByApptId.getRecordsByAppointmentId(appointmentId);
    }

    public Optional<List<Invoice>> retrieveInvoicesByInvoiceStatus(InvoiceStatus invoiceStatus) {

        // Begin retrieving multiple invoice records by invoice status procedure in service class
        InvoiceRetrieval retrieveInvoicesByInvoiceStatus = new InvoiceRetrieval(invoiceRepo);
        return retrieveInvoicesByInvoiceStatus.getRecordsByInvoiceStatus(invoiceStatus);
    }

    /**
     * Validate the format and length of invoice ID
     * This validation is done before record retrieval, so the frontend can first display the validation result
     */
    public OperationResult validateInvoiceId(String invoiceId) {

        // Check if the invoice ID and its type are null or empty
        if (invoiceId == null || invoiceId.isBlank()) {
            return OperationResult.failed("The invoice ID must be provided to retrieve records. " +
                    "Please try again.");
        }

        if (!DataValidator.validateRecordId(invoiceId).isValid()) {
            return OperationResult.failed("The invoice ID: " + invoiceId +
                    " is not an UUID. Please try again.");
        }

        return OperationResult.success();
    }

    public OperationResult validateInvoiceIds(List<String> invoiceIds) {

        // Check if the ID list and its type are null or empty
        if (invoiceIds == null || invoiceIds.isEmpty()) {
            return OperationResult.failed("The invoice IDs must be provided to retrieve records. " +
                    "Please try again.");
        }

        // Check if any of the invoice IDs are null or empty
        for (String invoiceId : invoiceIds) {
            if (invoiceId == null || invoiceId.isBlank() ) {
                return OperationResult.failed("One of the invoice IDs is null or empty. Please try again.");
            }
        }

        // Check if any of the invoice IDs are invalid
        for (String invoiceId : invoiceIds) {
            if (!DataValidator.validateRecordId(invoiceId).isValid()) {
                return OperationResult.failed("The invoice ID: " + invoiceId +
                        " is not an UUID. Please try again.");
            }
        }

        return OperationResult.success();
    }

    /**
     * Check if there are any null or empty values of required fields in the given DTO,
     * excluding staff account and appointment IDs
     * Used for creating and updating records
     */
    private OperationResult validateNullValues(InvoiceDTO invoiceDTO, OperationType operationType) {
        List<Object> invoiceValues = new ArrayList<>();

        // If the operation is updating record, add the ID to the list for null validation
        if (operationType == OperationType.UPDATE) {
            invoiceValues.add(invoiceDTO.getInvoiceID());
        }

        invoiceValues.add(invoiceDTO.getAdditionalFeeAmount());

        return NullValidator.isAllValuesNull(invoiceValues);
    }

    /**
     * Full validation of the staff account ID in checking nullity, format, and linking record
     */
    private OperationResult validateStaffId(String staffId) {

        // 1. Check if the staff account ID is null or blank
        if (staffId == null || staffId.isBlank()) {
            return OperationResult.failed(DataFieldName.STAFF_ID,
                    "The staff account ID must be provided. Please try again.");
        }

        // 2. Check if the staff account ID in UUID format
        if (!DataValidator.validateRecordId(staffId).isValid()) {
            return OperationResult.failed(DataFieldName.STAFF_ID,
                    "The staff account ID: " + staffId + " is not an UUID. Please try again.");
        }

        // 3. Check if the staff account ID exists in the repository
        StaffAccount staffAcc = staffAccountRepo.getRecordById(staffId);
        if (staffAcc == null) {
            return OperationResult.failed(DataFieldName.STAFF_ID,
                    "The staff account with ID " + staffId + " does not exist in the records.");
        }

        // 4. If the staff account is a technician, return an error message.
        //    Only counter-staffs and managers can create invoices
        if (staffAcc.getJobPosition() == JobPosition.TECHNICIAN) {
            return OperationResult.failed(DataFieldName.STAFF_ID,
                    "Only counter staffs and managers can strictly create invoices.");
        }

        // If all validations pass, return success
        return OperationResult.success();
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
}
