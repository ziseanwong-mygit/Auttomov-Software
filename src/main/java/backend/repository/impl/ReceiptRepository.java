package main.java.backend.repository.impl;

import main.java.backend.model.Receipt;
import main.java.backend.model.StaffAccount;
import main.java.backend.repository.AbstractRepository;
import main.java.backend.repository.impl.account.StaffAccountRepository;
import main.java.backend.util.CSVFormatter;
import main.java.backend.util.DataTypeConverter;
import main.java.enums.system.TextFilePath;
import main.java.records.OperationResult;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class ReceiptRepository extends AbstractRepository<Receipt> {

    private static ReceiptRepository instance;
    private final String filePath = TextFilePath.RECEIPT_RECORDS_FILE_PATH.getTextFilePath();

    // Map key: field value, map value: Receipt object
    private final Map<String, Receipt> receiptById = new HashMap<>();

    // Private constructor to prevent instantiations from outside
    // Pre-populate the records into maps when the instance is created
    private ReceiptRepository() {
        retrieveRecords();
    }

    // Global access point by creating instance for once only
    // So all classes can access the same instance for reading and writing files
    public static ReceiptRepository getInstance() {
        if (instance == null) {
            instance = new ReceiptRepository();
        }
        return instance;
    }

    @Override
    public String getFilePath() {
        return filePath;
    }

    @Override
    public OperationResult createRecord(Receipt receiptToCreate) {

        try (BufferedWriter bufferWrite = new BufferedWriter(new FileWriter(getFilePath(), true))) {

            // Write the new receipt record into the file
            bufferWrite.write(CSVFormatter.toCsvFormat(receiptToCreate));
            bufferWrite.newLine();

            // Add the new receipt records into the maps to ensure the latest changes are reflected
            addRecordIntoMap(receiptToCreate);

        } catch (IOException e) {
            System.out.println(e.getMessage()); // Print exception message for debugging
            return OperationResult.failed(
                    "Oops! There was an error writing the new receipt record. " +
                            "Please try again.");
        }

        return OperationResult.success(
                "The receipt record has been created successfully. " +
                        "Please check the record to verify its creation.");
    }

    @Override
    public OperationResult updateRecord(Receipt notAllowedToUpdate) {
        return OperationResult.failed("Receipt records cannot be modified to preserve the record integrity.");
    }

    /**
     * Update all the receipt records at once
     * Used when deleting a staff account, which will affect all receipts that use them
     */
    public OperationResult bulkUpdateRecord(List<Receipt> receiptsToUpdate) {

        // Build a map to store the CSV line of newly updated receipt by its ID
        Map<String, Function<String, Optional<String>>> modifier = new HashMap<>();

        // For each receipt to update, create a modifier entry
        for (Receipt rcpt : receiptsToUpdate) {
            String rcptId = rcpt.getReceiptID();
            modifier.put(rcptId, _ -> Optional.of(CSVFormatter.toCsvFormat(rcpt)));
        }

        // Single file rewrite covers all receipts simultaneously
        OperationResult result = rewriteFile(modifier);
        if (!result.isValid()) {
            return result;
        }

        // Replace all the old records with the updated ones to apply the latest changes
        for (Receipt rcpt : receiptsToUpdate) {
            removeRecordFromMap(receiptById.get(rcpt.getReceiptID()));
            addRecordIntoMap(rcpt);
        }

        return OperationResult.success(
                receiptsToUpdate.size() + " receipt(s) are updated successfully.");
    }

    @Override
    public OperationResult deleteRecord(Receipt receiptToDelete) {

        String receiptID = receiptToDelete.getReceiptID();

        // Map Optional.empty() by its ID
        Map<String, Function<String, Optional<String>>> modifier = Map.of(
                receiptID, _ -> Optional.empty()
        );

        // Rewrite the file to delete the target record
        OperationResult result = rewriteFile(modifier);
        if (!result.isValid()) {
            return result;
        }

        // Remove the deleted record from the maps to apply the latest changes
        removeRecordFromMap(receiptToDelete);

        return OperationResult.success(
                "The receipt with ID " + receiptID + " has been deleted successfully. "
                        + "Please check the records to verify its deletion.");
    }

    @Override
    public List<Receipt> getAllRecords() {
        return new ArrayList<>(receiptById.values());
    }

    @Override
    public Receipt getRecordById(String receiptId) {
        return receiptById.get(receiptId);
    }

    @Override
    public List<Receipt> getRecordsByIds(List<String> receiptIds) {
        // Filter the receipt objects by their IDs
        return receiptById.values().stream()
                .filter(receipt -> receiptIds.contains(receipt.getReceiptID()))
                .toList();
    }

    public Receipt getRecordByReceiptDocNo(String receiptDocNo) {
        return receiptById.values().stream()
                .filter(receipt -> receipt.getReceiptDocNo().equals(receiptDocNo))
                .findFirst()
                .orElse(null);
    }

    public Receipt getRecordByInvoiceId(String invoiceId) {
        return receiptById.values().stream()
                .filter(receipt -> receipt.getInvoice().getInvoiceID().equals(invoiceId))
                .findFirst()
                .orElse(null);
    }

    public List<Receipt> getRecordsByReceiptIssuer(StaffAccount receiptIssuer) {
        return receiptById.values().stream()
                .filter(receipt -> receipt.getReceiptIssuer().equals(receiptIssuer))
                .toList();
    }

    public Receipt getRecordsByPaymentRecId(String paymentRecId) {
        return receiptById.values().stream()
                .filter(receipt -> receipt.getPaymentRecord().getPaymentRecordID().equals(paymentRecId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Used for retrieving records from the text file
     * Here the line is split into columns using comma as the delimiter
     * Invoice CSV format (9 columns): paymentRecordID, paymentRecordNo, paymentRecordDate, invoice, payerAccount,
     * paymentAmount, paymentMethod, paymentRecordNote, paymentStatus
     */
    @Override
    protected Receipt parseRecord(String line) {

        // Get the repository instances to fetch the respective objects
        StaffAccountRepository staffAccountRepository = StaffAccountRepository.getInstance();
        InvoiceRepository invoiceRepository = InvoiceRepository.getInstance();
        PaymentRecordRepository paymentRecordRepository = PaymentRecordRepository.getInstance();

        String[] columns = line.split("\\|\\s*", -1);

        return new Receipt(
                columns[0],
                columns[1],
                DataTypeConverter.convertStringToLocalDate(columns[2]),
                staffAccountRepository.getRecordById(columns[3]),
                invoiceRepository.getRecordById(columns[4]),
                paymentRecordRepository.getRecordById(columns[5]),
                columns[6].isBlank() ? null : columns[6].strip() // If receipt note is empty, set it to null
        );
    }

    /**
     * Add a new/updated receipt into all lookup maps
     */
    @Override
    protected void addRecordIntoMap(Receipt receipt) {

        // End the method early if the receipt is not provided
        if (receipt == null) {
            return;
        }

        receiptById.put(receipt.getReceiptID(), receipt);
    }

    /**
     * Remove the old/deleted receipt from all lookup maps
     */
    @Override
    protected void removeRecordFromMap(Receipt receipt) {

        // End the method early if the receipt is not provided
        if (receipt == null) {
            return;
        }

        receiptById.remove(receipt.getReceiptID());
    }
}
