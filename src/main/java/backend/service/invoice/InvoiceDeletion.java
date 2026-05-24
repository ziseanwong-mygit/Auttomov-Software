package main.java.backend.service.invoice;

import main.java.backend.model.Invoice;
import main.java.backend.model.PaymentRecord;
import main.java.backend.model.Receipt;
import main.java.backend.repository.impl.InvoiceRepository;
import main.java.backend.repository.impl.PaymentRecordRepository;
import main.java.backend.repository.impl.ReceiptRepository;
import main.java.backend.service.RecordWriter;
import main.java.records.OperationResult;

public class InvoiceDeletion implements RecordWriter {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRecordRepository paymentRecordRepository;
    private final ReceiptRepository receiptRepository;
    private final String invoiceID;

    public InvoiceDeletion(InvoiceRepository invoiceRepository, PaymentRecordRepository paymentRecordRepository,
                           ReceiptRepository receiptRepository, String invoiceID) {
        this.invoiceRepository = invoiceRepository;
        this.paymentRecordRepository = paymentRecordRepository;
        this.receiptRepository = receiptRepository;
        this.invoiceID = invoiceID;
    }

    @Override
    public OperationResult execute() {

        // Check if the invoice exists with the given ID
        Invoice existingInvoice = invoiceRepository.getRecordById(invoiceID);
        if (existingInvoice == null) {
            return OperationResult.failed("The invoice with ID: " + invoiceID +
                    " does not exist in the system.");
        }

        // Get the payment record and receipt associated with this invoice
        PaymentRecord payRecord = paymentRecordRepository.getRecordByInvoiceId(invoiceID);
        Receipt receipt = receiptRepository.getRecordByInvoiceId(invoiceID);

        // Delete the payment record and receipt
        if (payRecord != null) {
            OperationResult paymentRecordDeletion = paymentRecordRepository.deleteRecord(payRecord);
            if (!paymentRecordDeletion.isValid()) {
                return OperationResult.failed("Failed to delete payment record for the invoice " + invoiceID
                        + ". Deletion process has stopped here.");
            }
        }
        if (receipt != null) {
            OperationResult receiptDeletion = receiptRepository.deleteRecord(receipt);
            if (!receiptDeletion.isValid()) {
                return OperationResult.failed("Failed to delete receipt for the invoice " + invoiceID
                        + ". Deletion process has stopped here.");
            }
        }

        // Finally, delete the invoice record in the text file
        return invoiceRepository.deleteRecord(existingInvoice);
    }
}
