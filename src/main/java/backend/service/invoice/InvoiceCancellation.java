package main.java.backend.service.invoice;

import main.java.backend.model.Invoice;
import main.java.backend.repository.impl.InvoiceRepository;
import main.java.backend.service.RecordWriter;
import main.java.enums.data.InvoiceStatus;
import main.java.records.OperationResult;

public class InvoiceCancellation implements RecordWriter {

    private final InvoiceRepository invoiceRepository;
    private final String invoiceID;

    public InvoiceCancellation(InvoiceRepository invoiceRepository, String invoiceID) {
        this.invoiceRepository = invoiceRepository;
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

        // Apply updates only to changed fields
        existingInvoice.setInvoiceStatus(InvoiceStatus.CANCELLED);

        // Begin updating invoice record in the repository
        return invoiceRepository.updateRecord(existingInvoice);
    }
}
