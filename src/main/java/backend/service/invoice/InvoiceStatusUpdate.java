package main.java.backend.service.invoice;

import main.java.backend.model.Invoice;
import main.java.backend.repository.impl.InvoiceRepository;
import main.java.backend.service.RecordWriter;
import main.java.dtos.InvoiceDTO;
import main.java.enums.data.InvoiceStatus;
import main.java.records.FieldComparison;
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

        String inputInvId = invoiceDTO.getInvoiceID();
        InvoiceStatus inputInvStatus = invoiceDTO.getInvoiceStatus();

        // Check if the invoice exists with the given ID
        Invoice existingInvoice = invoiceRepository.getRecordById(inputInvId);
        if (existingInvoice == null) {
            return OperationResult.failed("The invoice with ID: " + inputInvId +
                    " does not exist in the system.");
        }

        // Verify status logic to ensure the update is valid
        if (inputInvStatus.equals(InvoiceStatus.PAID)) {
            if (existingInvoice.getInvoiceStatus().equals(InvoiceStatus.CANCELLED)) {
                return OperationResult.failed("The invoice with ID: " + inputInvId +
                        " cannot be updated to paid status because it has already been cancelled.");
            }
        } else if (inputInvStatus.equals(InvoiceStatus.CANCELLED)) {
            if (existingInvoice.getInvoiceStatus().equals(InvoiceStatus.PAID)) {
                return OperationResult.failed("The invoice with ID: " + inputInvId +
                        " cannot be updated to cancelled status because it has already been paid.");
            }
        }

        // Check if the input status is different from the current status
        FieldComparison<InvoiceStatus> statusChange = new FieldComparison<>(
                "invoiceStatus", existingInvoice.getInvoiceStatus(), inputInvStatus, null);

        if (!statusChange.isChanged()) {
            return OperationResult.failed("The input status provided is the same as the " +
                    "current status. Please set to a different status for update.");
        }

        // Apply updates only to changed fields
        existingInvoice.setInvoiceStatus(inputInvStatus);

        // Begin updating invoice record in the repository
        return invoiceRepository.updateRecord(existingInvoice);
    }
}
