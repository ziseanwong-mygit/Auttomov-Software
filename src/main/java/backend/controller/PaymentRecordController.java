package main.java.backend.controller;

import main.java.backend.model.Invoice;
import main.java.backend.model.PaymentRecord;
import main.java.backend.model.UserAccount;
import main.java.backend.repository.impl.InvoiceRepository;
import main.java.backend.repository.impl.PaymentRecordRepository;
import main.java.backend.repository.impl.account.CustomerAccountRepository;
import main.java.backend.service.RecordReader;
import main.java.backend.service.RecordWriter;
import main.java.backend.service.paymentrecord.PaymentRecordCreation;
import main.java.backend.service.paymentrecord.PaymentRecordRetrieval;
import main.java.backend.util.DataValidator;
import main.java.backend.util.NullValidator;
import main.java.dtos.PaymentRecordDTO;
import main.java.enums.data.PaymentStatus;
import main.java.enums.system.DataFieldName;
import main.java.records.OperationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaymentRecordController {

    // Singleton instances of the repositories
    private final PaymentRecordRepository paymentRecordRepo = PaymentRecordRepository.getInstance();
    private final InvoiceRepository invoiceRepo = InvoiceRepository.getInstance();
    private final CustomerAccountRepository customerAccountRepo = CustomerAccountRepository.getInstance();

    public OperationResult createPaymentRecord(PaymentRecordDTO paymentRecordDTO) {

        // Check if there are any null or empty values in the list
        OperationResult nullValidation = validateNullValues(paymentRecordDTO);
        if (!nullValidation.isValid()) {
            return OperationResult.failed("All required fields must be filled in. Please try again.");
        }

        // Validate invoice ID
        String invoiceId = paymentRecordDTO.getInvoiceID();
        OperationResult invoiceIdVerification = validateInvoiceId(invoiceId);
        if (!invoiceIdVerification.isValid()) {
            return invoiceIdVerification;
        }

        // Validate customer account ID
        String customerAccountId = paymentRecordDTO.getCustomerAccountID();
        OperationResult customerIdVerification = validateCustomerId(customerAccountId);
        if (!customerIdVerification.isValid()) {
            return customerIdVerification;
        }

        // Validate payment amount logic
        OperationResult paymentAmountValidation = DataValidator.validatePrice(paymentRecordDTO.getPaymentAmount());
        if (!paymentAmountValidation.isValid()) {
            return paymentAmountValidation;
        }

        // Retrieve the customer account and invoice records with the respective IDs from the repository
        UserAccount customerAcc = customerAccountRepo.getRecordById(customerAccountId);
        Invoice invoice = invoiceRepo.getRecordById(invoiceId);

        // Begin creating payment record procedure in service class
        RecordWriter createPaymentRecord = new PaymentRecordCreation(
                paymentRecordRepo, paymentRecordDTO, invoice, customerAcc);
        return createPaymentRecord.execute();
    }

    /**
     * Optional class is used to determine whether the records are present or not
     * This is for frontend logic in displaying the result message for UI
     */
    public Optional<List<PaymentRecord>> retrieveAllPaymentRecords() {

        // Begin retrieving all payment record procedures in service class
        RecordReader<PaymentRecord> retrieveAllPaymentRecords = new PaymentRecordRetrieval(paymentRecordRepo);
        return retrieveAllPaymentRecords.getAllRecords();
    }

    public Optional<PaymentRecord> retrievePaymentRecordById(String servicePkgId) {

        // Begin retrieving payment record by ID procedure in service class
        RecordReader<PaymentRecord> retrievePaymentRecordById = new PaymentRecordRetrieval(paymentRecordRepo);
        return retrievePaymentRecordById.getRecordById(servicePkgId);
    }

    public Optional<List<PaymentRecord>> retrievePaymentRecordsByIds(List<String> invoiceIds) {

        // Begin retrieving multiple payment records by IDs procedure in service class
        RecordReader<PaymentRecord> retrievePaymentRecordsByIds = new PaymentRecordRetrieval(paymentRecordRepo);
        return retrievePaymentRecordsByIds.getRecordsByIds(invoiceIds);
    }

    public Optional<PaymentRecord> retrievePaymentRecordByPayRecNo(String paymentRecordNo) {

        // Begin retrieving payment record by document number procedure in service class
        PaymentRecordRetrieval retrievePaymentRecordByPayRecNo = new PaymentRecordRetrieval(paymentRecordRepo);
        return retrievePaymentRecordByPayRecNo.getRecordByPaymentRecordNo(paymentRecordNo);
    }

    public Optional<PaymentRecord> retrievePaymentRecordByInvoiceId(String invoiceId) {

        // Begin retrieving payment record by invoice ID procedure in service class
        PaymentRecordRetrieval retrievePaymentRecordByInvoiceId = new PaymentRecordRetrieval(paymentRecordRepo);
        return retrievePaymentRecordByInvoiceId.getRecordByInvoiceId(invoiceId);
    }

    public Optional<List<PaymentRecord>> retrievePaymentRecordsByPaymentStatus(PaymentStatus paymentStatus) {

        // Begin retrieving multiple payment records by payment status procedure in service class
        PaymentRecordRetrieval retrievePaymentRecordsByStatus = new PaymentRecordRetrieval(paymentRecordRepo);
        return retrievePaymentRecordsByStatus.getRecordsByPaymentRecordStatus(paymentStatus);
    }

    /**
     * Validate the format and length of payment record ID
     * This validation is done before record retrieval, so the frontend can first display the validation result
     */
    public OperationResult validatePaymentRecordId(String paymentRecordId) {

        // Check if the payment record ID and its type are null or empty
        if (paymentRecordId == null || paymentRecordId.isBlank()) {
            return OperationResult.failed("The payment record ID must be provided to retrieve records. " +
                    "Please try again.");
        }

        if (!DataValidator.validateRecordId(paymentRecordId).isValid()) {
            return OperationResult.failed("The payment record ID: " + paymentRecordId +
                    " is not an UUID. Please try again.");
        }

        return OperationResult.success();
    }

    public OperationResult validatePaymentRecordIds(List<String> paymentRecordIds) {

        // Check if the ID list and its type are null or empty
        if (paymentRecordIds == null || paymentRecordIds.isEmpty()) {
            return OperationResult.failed("The payment record IDs must be provided to retrieve records. " +
                    "Please try again.");
        }

        // Check if any of the payment record IDs are null or empty
        for (String paymentRecId : paymentRecordIds) {
            if (paymentRecId == null || paymentRecId.isBlank() ) {
                return OperationResult.failed("One of the payment record IDs is null or empty. Please try again.");
            }
        }

        // Check if any of the payment record IDs are invalid
        for (String paymentRecId : paymentRecordIds) {
            if (!DataValidator.validateRecordId(paymentRecId).isValid()) {
                return OperationResult.failed("The payment record ID: " + paymentRecId +
                        " is not an UUID. Please try again.");
            }
        }

        return OperationResult.success();
    }

    /**
     * Check if there are any null or empty values of required fields in the given DTO,
     * excluding invoice and customer account IDs
     * Used for creating records only
     */
    private OperationResult validateNullValues(PaymentRecordDTO paymentRecordDTO) {
        List<Object> paymentRecordValues = new ArrayList<>();

        paymentRecordValues.add(paymentRecordDTO.getPaymentAmount());
        paymentRecordValues.add(paymentRecordDTO.getPaymentMethod());

        return NullValidator.isAllValuesNull(paymentRecordValues);
    }

    /**
     * Full validation of the customer account ID in checking nullity, format, and linking record
     */
    private OperationResult validateCustomerId(String customerId) {

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
}
