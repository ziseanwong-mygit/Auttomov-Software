package main.java.backend.service.receipt;

import main.java.backend.model.Invoice;
import main.java.backend.model.PaymentRecord;
import main.java.backend.model.Receipt;
import main.java.backend.model.StaffAccount;
import main.java.backend.repository.impl.ReceiptRepository;
import main.java.backend.service.RecordWriter;
import main.java.backend.util.RecordNoGenerator;
import main.java.dtos.ReceiptDTO;
import main.java.enums.data.InvoiceStatus;
import main.java.enums.data.PaymentStatus;
import main.java.records.OperationResult;

import java.time.LocalDate;
import java.util.UUID;

public class ReceiptCreation implements RecordWriter {

    private final ReceiptRepository receiptRepository;
    private final ReceiptDTO receiptDTO;
    private final StaffAccount existingStaff;
    private final Invoice existingInvoice;
    private final PaymentRecord existingPaymentRecord;

    public ReceiptCreation(ReceiptRepository receiptRepository, ReceiptDTO receiptDTO, StaffAccount existingStaff,
                           Invoice existingInvoice, PaymentRecord existingPaymentRecord) {
        this.receiptRepository = receiptRepository;
        this.receiptDTO = receiptDTO;
        this.existingStaff = existingStaff;
        this.existingInvoice = existingInvoice;
        this.existingPaymentRecord = existingPaymentRecord;
    }

    @Override
    public OperationResult execute() {

        // If the invoice is canceled, the payment record cannot be made, thus returning an error message
        if (existingInvoice.getInvoiceStatus().equals(InvoiceStatus.CANCELLED)) {
            return OperationResult.failed("You cannot make payment for the invoice with ID: " +
                    existingInvoice.getInvoiceID() + " because it has already been cancelled.");
        }

        // If the payment is unsuccessful, the receipt cannot be created, thus returning an error message
        if (existingPaymentRecord.getPaymentStatus().equals(PaymentStatus.UNSUCCESSFUL)) {
            return OperationResult.failed("You cannot generate receipt for the payment with ID: " +
                    existingPaymentRecord.getPaymentRecordID() + " because it is unsuccessful.");
        }

        // Create a new receipt object
        Receipt newReceipt = populateReceiptDetails();

        // Save the receipt into the file
        return receiptRepository.createRecord(newReceipt);
    }

    private Receipt populateReceiptDetails() {

        // Generate new receipt ID and document number for the new record
        String receiptId = UUID.randomUUID().toString();
        String receiptDocNo = "RCT" + RecordNoGenerator.generateRecordNo();

        // Extract the receipt details from the DTO
        String receiptNote = receiptDTO.getReceiptNote();

        return new Receipt(receiptId, receiptDocNo, LocalDate.now(), existingStaff, existingInvoice,
                existingPaymentRecord, receiptNote);
    }
}
