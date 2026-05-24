package main.java.backend.service.receipt;

import main.java.backend.model.Invoice;
import main.java.backend.model.PaymentRecord;
import main.java.backend.model.Receipt;
import main.java.backend.model.StaffAccount;
import main.java.backend.repository.impl.ReceiptRepository;
import main.java.backend.service.RecordWriter;
import main.java.backend.util.RecordNoGenerator;
import main.java.dtos.ReceiptDTO;
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

        // Create a new receipt object
        Receipt newReceipt = populateReceiptDetails();

        // Save the receipt into the file
        return receiptRepository.createRecord(newReceipt);
    }

    private Receipt populateReceiptDetails() {

        // Generate new receipt ID and document number for the new record
        String receiptId = UUID.randomUUID().toString();
        String receiptDocNo = "RPT" + RecordNoGenerator.generateRecordNo();

        // Extract the receipt details from the DTO
        String receiptNote = receiptDTO.getReceiptNote();

        return new Receipt(receiptId, receiptDocNo, LocalDate.now(), existingStaff, existingInvoice,
                existingPaymentRecord, receiptNote);
    }
}
