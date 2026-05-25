package main.java.backend.service.invoice;

import main.java.backend.model.Invoice;
import main.java.backend.repository.impl.InvoiceRepository;
import main.java.backend.service.RecordWriter;
import main.java.dtos.InvoiceDTO;
import main.java.enums.data.InvoiceStatus;
import main.java.records.FieldComparison;
import main.java.records.OperationResult;

import java.util.ArrayList;
import java.util.List;

public class InvoiceUpdate implements RecordWriter {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceDTO invoiceDTO;

    public InvoiceUpdate(InvoiceRepository invoiceRepository, InvoiceDTO invoiceDTO) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceDTO = invoiceDTO;
    }

    @Override
    public OperationResult execute() {

        String invoiceID = invoiceDTO.getInvoiceID();

        // Check if the invoice exists with the given ID
        Invoice existingInvoice = invoiceRepository.getRecordById(invoiceID);
        if (existingInvoice == null) {
            return OperationResult.failed("The invoice with ID: " + invoiceID + " does not exist in the system.");
        }

        // Verify status logic to ensure the update is valid
        if (existingInvoice.getInvoiceStatus().equals(InvoiceStatus.PAID)) {
            return OperationResult.failed("The invoice with ID: " + invoiceID +
                    "cannot be updated because it has already been paid by the customer.");
        } else if (existingInvoice.getInvoiceStatus().equals(InvoiceStatus.CANCELLED)) {
            return OperationResult.failed("The invoice with ID: " + invoiceID +
                    "cannot be updated because it has already been cancelled.");
        }

        // Get the list of fields that are changed
        List<FieldComparison<?>> changedFields = compareChanges(existingInvoice);

        // If nothing changed at all across both kinds
        if (changedFields.isEmpty()) {
            return OperationResult.failed("No fields have been changed. Please update one or more fields.");
        }

        // Apply updates only to changed fields
        setChangedValues(changedFields, existingInvoice);

        // Begin updating invoice record in the repository
        return invoiceRepository.updateRecord(existingInvoice);
    }

    /**
     * Compare the values between the existing invoice and incoming inputs (DTO) to get the changed fields
     */
    private List<FieldComparison<?>> compareChanges(Invoice existingInvoice) {

        // Declare a list to store all the fields for comparison later
        List<FieldComparison<?>> invoiceFields = new ArrayList<>();

        invoiceFields.add(new FieldComparison<>("additionalFeeAmount",
                existingInvoice.getAdditionalFeeAmount(), invoiceDTO.getAdditionalFeeAmount(), null));
        invoiceFields.add(new FieldComparison<>("invoiceNote", existingInvoice.getInvoiceNote(),
                invoiceDTO.getInvoiceNote(), null));

        // Filter to get the changed fields
        return invoiceFields.stream()
                .filter(FieldComparison::isChanged)
                .toList();
    }

    /**
     * Set the new values of the changed fields in the existing invoice object
     * The object will be used to update the record in the file
     */
    private void setChangedValues(List<FieldComparison<?>> changedFields, Invoice existingInvoice) {

        for (FieldComparison<?> field : changedFields) {
            switch (field.fieldName()) {
                case "additionalFeeAmount" -> {
                    // Calculate the new total fee amount if the additional fees are changed
                    double oldAddFee = existingInvoice.getAdditionalFeeAmount();
                    double oldTotalFee = existingInvoice.getTotalAmount();
                    double newAddFee = invoiceDTO.getAdditionalFeeAmount();
                    double newTotalFee = (oldTotalFee - oldAddFee) + newAddFee;

                    // Set the additional fee amount and total fee amount with new values
                    existingInvoice.setAdditionalFeeAmount(newAddFee);
                    existingInvoice.setTotalAmount(newTotalFee);
                }
                case "invoiceNote" -> existingInvoice.setInvoiceNote(invoiceDTO.getInvoiceNote());
            }
        }
    }
}
