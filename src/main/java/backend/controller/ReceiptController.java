package main.java.backend.controller;

import main.java.backend.model.Invoice;
import main.java.backend.model.PaymentRecord;
import main.java.backend.model.Receipt;
import main.java.backend.model.StaffAccount;
import main.java.backend.repository.impl.InvoiceRepository;
import main.java.backend.repository.impl.PaymentRecordRepository;
import main.java.backend.repository.impl.ReceiptRepository;
import main.java.backend.repository.impl.account.StaffAccountRepository;
import main.java.backend.service.RecordReader;
import main.java.backend.service.RecordWriter;
import main.java.backend.service.receipt.ReceiptCreation;
import main.java.backend.service.receipt.ReceiptNoteUpdate;
import main.java.backend.service.receipt.ReceiptRetrieval;
import main.java.backend.util.DataValidator;
import main.java.backend.util.NullValidator;
import main.java.dtos.ReceiptDTO;
import main.java.enums.data.JobPosition;
import main.java.enums.system.DataFieldName;
import main.java.enums.system.OperationType;
import main.java.records.OperationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReceiptController {

    // Singleton instances of the repositories
    private final ReceiptRepository receiptRepo = ReceiptRepository.getInstance();
    private final StaffAccountRepository staffAccountRepo = StaffAccountRepository.getInstance();
    private final InvoiceRepository invoiceRepo = InvoiceRepository.getInstance();
    private final PaymentRecordRepository paymentRecordRepo = PaymentRecordRepository.getInstance();

    public OperationResult createReceipt(ReceiptDTO receiptDTO) {

        // Check if there are any null or empty values in the list
        OperationResult nullValidation = validateNullValues(receiptDTO, OperationType.CREATE);
        if (!nullValidation.isValid()) {
            return OperationResult.failed("All required fields must be filled in. Please try again.");
        }

        // Validate staff account ID
        String staffAccountId = receiptDTO.getStaffAccountID();
        OperationResult staffIdVerification = validateStaffId(staffAccountId);
        if (!staffIdVerification.isValid()) {
            return staffIdVerification;
        }

        // Validate invoice ID
        String invoiceId = receiptDTO.getInvoiceID();
        OperationResult invoiceIdVerification = validateInvoiceId(invoiceId);
        if (!invoiceIdVerification.isValid()) {
            return invoiceIdVerification;
        }

        // Validate payment record ID
        String paymentRecordId = receiptDTO.getPaymentRecordID();
        OperationResult paymentRecIdVerification = validatePaymentRecordId(paymentRecordId);
        if (!paymentRecIdVerification.isValid()) {
            return paymentRecIdVerification;
        }

        // Retrieve the staff account, invoice and payment records with the respective IDs from the repository
        StaffAccount staffAcc = staffAccountRepo.getRecordById(staffAccountId);
        Invoice invoice = invoiceRepo.getRecordById(invoiceId);
        PaymentRecord paymentRecord = paymentRecordRepo.getRecordById(paymentRecordId);

        // Begin creating receipt record procedure in service class
        RecordWriter createReceipt = new ReceiptCreation(receiptRepo, receiptDTO, staffAcc, invoice, paymentRecord);
        return createReceipt.execute();
    }

    public OperationResult updateReceiptNote(ReceiptDTO receiptDTO) {

        // Check if there are any null or empty values in the list
        OperationResult nullValidation = validateNullValues(receiptDTO, OperationType.UPDATE);
        if (!nullValidation.isValid()) {
            return OperationResult.failed("All required fields must be filled in. Please try again.");
        }

        // Check if the receipt ID is in UUID format
        if (!DataValidator.validateRecordId(receiptDTO.getInvoiceID()).isValid()) {
            return OperationResult.failed(DataFieldName.RECORD_ID, "The receipt ID: " +
                    receiptDTO.getInvoiceID() + " is not an UUID. Please try again.");
        }

        // Begin updating receipt note procedure in service class
        RecordWriter updateReceiptNote = new ReceiptNoteUpdate(receiptRepo, receiptDTO);
        return updateReceiptNote.execute();
    }

    /**
     * Optional class is used to determine whether the records are present or not
     * This is for frontend logic in displaying the result message for UI
     */
    public Optional<List<Receipt>> retrieveAllReceipts() {

        // Begin retrieving all receipt record procedures in service class
        RecordReader<Receipt> retrieveAllReceipts = new ReceiptRetrieval(receiptRepo);
        return retrieveAllReceipts.getAllRecords();
    }

    public Optional<Receipt> retrieveReceiptById(String servicePkgId) {

        // Begin retrieving receipt record by ID procedure in service class
        RecordReader<Receipt> retrieveReceiptById = new ReceiptRetrieval(receiptRepo);
        return retrieveReceiptById.getRecordById(servicePkgId);
    }

    public Optional<List<Receipt>> retrieveReceiptsByIds(List<String> receiptIds) {

        // Begin retrieving multiple receipt records by IDs procedure in service class
        RecordReader<Receipt> retrieveReceiptsByIds = new ReceiptRetrieval(receiptRepo);
        return retrieveReceiptsByIds.getRecordsByIds(receiptIds);
    }

    public Optional<Receipt> retrieveReceiptByReceiptDocNo(String receiptDocNo) {

        // Begin retrieving receipt record by document number procedure in service class
        ReceiptRetrieval retrieveReceiptByReceiptDocNo = new ReceiptRetrieval(receiptRepo);
        return retrieveReceiptByReceiptDocNo.getRecordByReceiptDocNo(receiptDocNo);
    }

    public Optional<Receipt> retrieveReceiptByInvoiceId(String invoiceId) {

        // Begin retrieving receipt record by invoice ID procedure in service class
        ReceiptRetrieval retrieveReceiptByInvoiceId = new ReceiptRetrieval(receiptRepo);
        return retrieveReceiptByInvoiceId.getRecordByInvoiceId(invoiceId);
    }

    public Optional<Receipt> retrieveReceiptByPaymentRecordId(String paymentRecordId) {

        // Begin retrieving receipt record by payment record ID procedure in service class
        ReceiptRetrieval retrieveReceiptByPayRecId = new ReceiptRetrieval(receiptRepo);
        return retrieveReceiptByPayRecId.getRecordByPaymentRecId(paymentRecordId);
    }

    /**
     * Validate the format and length of receipt ID
     * This validation is done before record retrieval, so the frontend can first display the validation result
     */
    public OperationResult validateReceiptId(String receiptId) {

        // Check if the receipt ID and its type are null or empty
        if (receiptId == null || receiptId.isBlank()) {
            return OperationResult.failed("The receipt ID must be provided to retrieve records. " +
                    "Please try again.");
        }

        if (!DataValidator.validateRecordId(receiptId).isValid()) {
            return OperationResult.failed("The receipt ID: " + receiptId +
                    " is not an UUID. Please try again.");
        }

        return OperationResult.success();
    }

    public OperationResult validateReceiptIds(List<String> receiptIds) {

        // Check if the ID list and its type are null or empty
        if (receiptIds == null || receiptIds.isEmpty()) {
            return OperationResult.failed("The receipt IDs must be provided to retrieve records. " +
                    "Please try again.");
        }

        // Check if any of the receipt IDs are null or empty
        for (String receiptId : receiptIds) {
            if (receiptId == null || receiptId.isBlank() ) {
                return OperationResult.failed("One of the receipt IDs is null or empty. Please try again.");
            }
        }

        // Check if any of the receipt IDs are invalid
        for (String receiptId : receiptIds) {
            if (!DataValidator.validateRecordId(receiptId).isValid()) {
                return OperationResult.failed("The receipt ID: " + receiptId +
                        " is not an UUID. Please try again.");
            }
        }

        return OperationResult.success();
    }

    /**
     * Check if there are any null or empty values of required fields in the given DTO,
     * excluding staff account, invoice, and payment record IDs
     * Used for creating and updating records
     */
    private OperationResult validateNullValues(ReceiptDTO receiptDTO, OperationType operationType) {
        List<Object> receiptValues = new ArrayList<>();

        // If the operation is updating record, add the ID to the list for null validation
        if (operationType == OperationType.UPDATE) {
            receiptValues.add(receiptDTO.getReceiptID());
        }

        receiptValues.add(receiptDTO.getReceiptNote());

        return NullValidator.isAllValuesNull(receiptValues);
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
     * Full validation of the invoice ID in checking nullity, format, and linking record
     */
    public OperationResult validateInvoiceId(String invoiceId) {

        // 1. Check if the invoice ID is null or blank
        if (invoiceId == null || invoiceId.isBlank()) {
            return OperationResult.failed(DataFieldName.INVOICE_ID,
                    "The invoice ID must be provided. Please try again.");
        }

        // 2. Check if the invoice ID in UUID format
        if (!DataValidator.validateRecordId(invoiceId).isValid()) {
            return OperationResult.failed(DataFieldName.INVOICE_ID,
                    "The invoice ID: " + invoiceId + " is not an UUID. Please try again.");
        }

        // 3. Check if the invoice ID exists in the repository
        if (invoiceRepo.getRecordById(invoiceId) == null) {
            return OperationResult.failed(DataFieldName.INVOICE_ID,
                    "The invoice with ID " + invoiceId + " does not exist in the records.");
        }

        // If all validations pass, return success
        return OperationResult.success();
    }

    /**
     * Full validation of the payment record ID in checking nullity, format, and linking record
     */
    public OperationResult validatePaymentRecordId(String paymentRecordId) {

        // 1. Check if the payment record ID is null or blank
        if (paymentRecordId == null || paymentRecordId.isBlank()) {
            return OperationResult.failed(DataFieldName.PAYMENT_RECORD_ID,
                    "The payment record ID must be provided. Please try again.");
        }

        // 2. Check if the payment record ID in UUID format
        if (!DataValidator.validateRecordId(paymentRecordId).isValid()) {
            return OperationResult.failed(DataFieldName.PAYMENT_RECORD_ID,
                    "The payment record ID: " + paymentRecordId + " is not an UUID. Please try again.");
        }

        // 3. Check if the payment record ID exists in the repository
        if (paymentRecordRepo.getRecordById(paymentRecordId) == null) {
            return OperationResult.failed(DataFieldName.PAYMENT_RECORD_ID,
                    "The payment record with ID " + paymentRecordId + " does not exist in the records.");
        }

        // If all validations pass, return success
        return OperationResult.success();
    }
}
