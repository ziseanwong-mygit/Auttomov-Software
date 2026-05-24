package main.java.backend.repository.impl;

import main.java.backend.model.Invoice;
import main.java.backend.model.StaffAccount;
import main.java.backend.repository.AbstractRepository;
import main.java.backend.repository.impl.account.StaffAccountRepository;
import main.java.backend.util.CSVFormatter;
import main.java.backend.util.DataTypeConverter;
import main.java.enums.data.InvoiceStatus;
import main.java.enums.system.TextFilePath;
import main.java.records.OperationResult;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class InvoiceRepository extends AbstractRepository<Invoice> {

    private static InvoiceRepository instance;
    private final String filePath = TextFilePath.INVOICE_RECORDS_FILE_PATH.getTextFilePath();

    // Map key: field value, map value: Invoice object
    private final Map<String, Invoice> invoiceById = new HashMap<>();

    // Private constructor to prevent instantiations from outside
    // Pre-populate the records into maps when the instance is created
    private InvoiceRepository() {
        retrieveRecords();
    }

    // Global access point by creating instance for once only
    // So all classes can access the same instance for reading and writing files
    public static InvoiceRepository getInstance() {
        if (instance == null) {
            instance = new InvoiceRepository();
        }
        return instance;
    }

    @Override
    public String getFilePath() {
        return filePath;
    }

    @Override
    public OperationResult createRecord(Invoice invoiceToCreate) {

        try (BufferedWriter bufferWrite = new BufferedWriter(new FileWriter(getFilePath(), true))) {

            // Write the new invoice record into the file
            bufferWrite.write(CSVFormatter.toCsvFormat(invoiceToCreate));
            bufferWrite.newLine();

            // Add the new invoice records into the maps to ensure the latest changes are reflected
            addRecordIntoMap(invoiceToCreate);

        } catch (IOException e) {
            System.out.println(e.getMessage()); // Print exception message for debugging
            return OperationResult.failed(
                    "Oops! There was an error writing the new invoice record. " +
                            "Please try again.");
        }

        return OperationResult.success(
                "The invoice record has been created successfully. " +
                        "Please check the record to verify its creation.");
    }

    @Override
    public OperationResult updateRecord(Invoice invoiceToUpdate) {

        String invoiceID = invoiceToUpdate.getInvoiceID();

        // Map the target invoice object by its ID
        Map<String, Function<String, Optional<String>>> modifier = Map.of(
                invoiceID, _ -> Optional.of(CSVFormatter.toCsvFormat(invoiceToUpdate))
        );

        // Rewrite the file to update the target record
        OperationResult result = rewriteFile(modifier);
        if (!result.isValid()) {
            return result;
        }

        // Replace the old record with the updated one to apply the latest changes
        removeRecordFromMap(invoiceById.get(invoiceID));
        addRecordIntoMap(invoiceToUpdate);

        return OperationResult.success(
                "The invoice with ID " + invoiceID + " has been updated successfully. "
                        + "Please check the details to ensure the changes are applied.");
    }

    /**
     * Update all the invoice records at once
     * Used when deleting an appointment or staff account, which will affect all invoices that use them
     */
    public OperationResult bulkUpdateRecord(List<Invoice> invoicesToUpdate) {

        // Build a map to store the CSV line of newly updated invoice by its ID
        Map<String, Function<String, Optional<String>>> modifier = new HashMap<>();

        // For each invoice to update, create a modifier entry
        for (Invoice inv : invoicesToUpdate) {
            String invId = inv.getInvoiceID();
            modifier.put(invId, _ -> Optional.of(CSVFormatter.toCsvFormat(inv)));
        }

        // Single file rewrite covers all invoices simultaneously
        OperationResult result = rewriteFile(modifier);
        if (!result.isValid()) {
            return result;
        }

        // Replace all the old records with the updated ones to apply the latest changes
        for (Invoice inv : invoicesToUpdate) {
            removeRecordFromMap(invoiceById.get(inv.getInvoiceID()));
            addRecordIntoMap(inv);
        }

        return OperationResult.success(
                invoicesToUpdate.size() + " invoice(s) are updated successfully.");
    }

    @Override
    public OperationResult deleteRecord(Invoice invoiceToDelete) {

        String invoiceID = invoiceToDelete.getInvoiceID();

        // Map Optional.empty() by its ID
        Map<String, Function<String, Optional<String>>> modifier = Map.of(
                invoiceID, _ -> Optional.empty()
        );

        // Rewrite the file to delete the target record
        OperationResult result = rewriteFile(modifier);
        if (!result.isValid()) {
            return result;
        }

        // Remove the deleted record from the maps to apply the latest changes
        removeRecordFromMap(invoiceToDelete);

        return OperationResult.success(
                "The invoice with ID " + invoiceID + " has been deleted successfully. "
                        + "Please check the records to verify its deletion.");
    }

    @Override
    public List<Invoice> getAllRecords() {
        return new ArrayList<>(invoiceById.values());
    }

    @Override
    public Invoice getRecordById(String invoiceId) {
        return invoiceById.get(invoiceId);
    }

    @Override
    public List<Invoice> getRecordsByIds(List<String> invoiceIds) {
        // Filter the invoice objects by their IDs
        return invoiceById.values().stream()
                .filter(invoice -> invoiceIds.contains(invoice.getInvoiceID()))
                .toList();
    }

    public Invoice getRecordByInvoiceDocNo(String invoiceDocNo) {
        return invoiceById.values().stream()
                .filter(invoice -> invoice.getInvoiceDocNo().equals(invoiceDocNo))
                .findFirst()
                .orElse(null);
    }

    public List<Invoice> getRecordsByInvoiceIssuer(StaffAccount invoiceIssuer) {
        return invoiceById.values().stream()
                .filter(invoice -> invoice.getInvoiceIssuer().equals(invoiceIssuer))
                .toList();
    }

    public List<Invoice> getRecordsByAppointmentId(String appointmentId) {
        return invoiceById.values().stream()
                .filter(invoice -> invoice.getAppointment().getAppointmentID().equals(appointmentId))
                .toList();
    }

    /**
     * Get all the invoices that are paid for the given appointment IDs
     * Used for generating reports
     */
    public List<Invoice> getPaidRecordsByAppointmentIds(List<String> appointmentIds) {
        return invoiceById.values().stream()
                .filter(invoice -> appointmentIds.contains(invoice.getAppointment().getAppointmentID()) &&
                        invoice.getInvoiceStatus().equals(InvoiceStatus.PAID))
                .toList();
    }

    public List<Invoice> getRecordsByInvoiceStatus(InvoiceStatus invoiceStatus) {
        return invoiceById.values().stream()
                .filter(invoice -> invoice.getInvoiceStatus().equals(invoiceStatus))
                .toList();
    }

    /**
     * Used for retrieving records from the text file
     * Here the line is split into columns using comma as the delimiter
     * Invoice CSV format (11 columns): invoiceID, invoiceDocNo, invoiceCreationDate, invoiceIssuer, appointment,
     * subtotalAmount, serviceTaxAmount, additionalFeeAmount, totalAmount, invoiceNote, invoiceStatus
     */
    @Override
    protected Invoice parseRecord(String line) {

        // Get the repository instances to fetch the respective objects
        StaffAccountRepository staffAccountRepository = StaffAccountRepository.getInstance();
        AppointmentRepository appointmentRepository = AppointmentRepository.getInstance();

        String[] columns = line.split(",\\s*");

        return new Invoice(
                columns[0],
                columns[1],
                DataTypeConverter.convertStringToLocalDate(columns[2]),
                staffAccountRepository.getRecordById(columns[3]),
                appointmentRepository.getRecordById(columns[4]),
                DataTypeConverter.convertStringToDouble(columns[5]),
                DataTypeConverter.convertStringToDouble(columns[6]),
                DataTypeConverter.convertStringToDouble(columns[7]),
                DataTypeConverter.convertStringToDouble(columns[8]),
                columns[9].isBlank() ? null : columns[9], // If invoice note is empty, set it to null
                DataTypeConverter.convertStringToInvoiceStatus(columns[10])
        );
    }

    /**
     * Add a new/updated invoice into all lookup maps
     */
    @Override
    protected void addRecordIntoMap(Invoice invoice) {

        // End the method early if the invoice is not provided
        if (invoice == null) {
            return;
        }

        invoiceById.put(invoice.getInvoiceID(), invoice);
    }

    /**
     * Remove the old/deleted invoice from all lookup maps
     */
    @Override
    protected void removeRecordFromMap(Invoice invoice) {

        // End the method early if the invoice is not provided
        if (invoice == null) {
            return;
        }

        invoiceById.remove(invoice.getInvoiceID());
    }
}
