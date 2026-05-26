package main.java.backend.service.paymentrecord;

import main.java.backend.model.Invoice;
import main.java.backend.model.PaymentRecord;
import main.java.backend.model.UserAccount;
import main.java.backend.repository.impl.PaymentRecordRepository;
import main.java.backend.service.RecordWriter;
import main.java.backend.util.RecordNoGenerator;
import main.java.dtos.PaymentRecordDTO;
import main.java.enums.data.InvoiceStatus;
import main.java.enums.data.PaymentMethod;
import main.java.enums.data.PaymentStatus;
import main.java.records.OperationResult;

import java.time.LocalDate;
import java.util.UUID;

public class PaymentRecordCreation implements RecordWriter {

    private final PaymentRecordRepository paymentRecordRepository;
    private final PaymentRecordDTO paymentRecordDTO;
    private final Invoice existingInvoice;
    private final UserAccount existingCustomer;

    public PaymentRecordCreation(PaymentRecordRepository paymentRecordRepository, PaymentRecordDTO paymentRecordDTO,
                                Invoice existingInvoice, UserAccount existingCustomer) {
        this.paymentRecordRepository = paymentRecordRepository;
        this.paymentRecordDTO = paymentRecordDTO;
        this.existingInvoice = existingInvoice;
        this.existingCustomer = existingCustomer;
    }

    @Override
    public OperationResult execute() {

        // If the invoice is canceled, the payment record cannot be made, thus returning an error message
        if (existingInvoice.getInvoiceStatus().equals(InvoiceStatus.CANCELLED)) {
            return OperationResult.failed("You cannot make payment for the invoice with ID: " +
                    existingInvoice.getInvoiceID() + " because it has already been cancelled.");
        }

        // Create a new payment record object
        PaymentRecord newPaymentRecord = populatePaymentRecordDetails();

        // Save the payment record into the file
        return paymentRecordRepository.createRecord(newPaymentRecord);
    }

    private PaymentRecord populatePaymentRecordDetails() {

        // Generate new payment record ID and document number for the new record
        String paymentRecordId = UUID.randomUUID().toString();
        String paymentDocNo = "PYT" + RecordNoGenerator.generateRecordNo();

        // Extract the payment record details from the DTO
        double paymentAmount = paymentRecordDTO.getPaymentAmount();
        PaymentMethod paymentMethod = paymentRecordDTO.getPaymentMethod();
        String paymentRecordNote = paymentRecordDTO.getPaymentRecordNote();

        // Set the payment status to SUCCESSFUL by default
        return new PaymentRecord(paymentRecordId, paymentDocNo, LocalDate.now(), existingInvoice, existingCustomer,
                paymentAmount, paymentMethod, paymentRecordNote, PaymentStatus.SUCCESSFUL);
    }
}
