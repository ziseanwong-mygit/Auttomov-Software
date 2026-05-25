package main.java.backend.service.invoice;

import main.java.backend.model.Invoice;
import main.java.backend.repository.impl.InvoiceRepository;
import main.java.backend.service.RecordWriter;
import main.java.dtos.InvoiceDTO;
import main.java.enums.data.InvoiceStatus;
import main.java.records.OperationResult;

public class InvoiceStatusUpdate implements RecordWriter {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceDTO invoiceDTO;

    public InvoiceStatusUpdate(InvoiceRepository invoiceRepository, InvoiceDTO invoiceDTO) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceDTO = invoiceDTO;
    }

    @Override
    public OperationResult execute() {

        String invoiceID = invoiceDTO.getInvoiceID();

        // Check if the invoice exists with the given ID
        Invoice existingInvoice = invoiceRepository.getRecordById(invoiceID);
        if (existingInvoice == null) {
            return OperationResult.failed("The invoice with ID: " + invoiceID +
                    " does not exist in the system.");
        }

        // Verify status logic to ensure the update is valid
        if (existingInvoice.getInvoiceStatus().equals(InvoiceStatus.PAID)) {
            return OperationResult.failed("The invoice with ID: " + invoiceID +
                    "cannot be cancelled because it has already been paid by the customer.");
        } else if (existingInvoice.getInvoiceStatus().equals(InvoiceStatus.CANCELLED)) {
            return OperationResult.failed("The invoice with ID: " + invoiceID + " has already been cancelled.");
        }

        // Apply updates only to changed fields
        existingInvoice.setInvoiceStatus(InvoiceStatus.CANCELLED);

        // Begin updating invoice record in the repository
        return invoiceRepository.updateRecord(existingInvoice);
    }
}
