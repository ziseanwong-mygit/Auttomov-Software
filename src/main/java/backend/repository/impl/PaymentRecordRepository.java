package main.java.backend.repository.impl;

import main.java.backend.model.PaymentRecord;
import main.java.backend.model.UserAccount;
import main.java.backend.repository.AbstractRepository;
import main.java.backend.repository.impl.account.CustomerAccountRepository;
import main.java.backend.util.CSVFormatter;
import main.java.backend.util.DataTypeConverter;
import main.java.enums.data.PaymentStatus;
import main.java.enums.system.TextFilePath;
import main.java.records.OperationResult;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class PaymentRecordRepository extends AbstractRepository<PaymentRecord> {

    private static PaymentRecordRepository instance;
    private final String filePath = TextFilePath.PAYMENT_RECORDS_FILE_PATH.getTextFilePath();

    // Map key: field value, map value: PaymentRecord object
    private final Map<String, PaymentRecord> paymentRecordById = new HashMap<>();

    // Private constructor to prevent instantiations from outside
    // Pre-populate the records into maps when the instance is created
    private PaymentRecordRepository() {
        retrieveRecords();
    }

    // Global access point by creating instance for once only
    // So all classes can access the same instance for reading and writing files
    public static PaymentRecordRepository getInstance() {
        if (instance == null) {
            instance = new PaymentRecordRepository();
        }
        return instance;
    }

    @Override
    public String getFilePath() {
        return filePath;
    }

    @Override
    public OperationResult createRecord(PaymentRecord paymentRecordToCreate) {

        try (BufferedWriter bufferWrite = new BufferedWriter(new FileWriter(getFilePath(), true))) {

            // Write the new payment record into the file
            bufferWrite.write(CSVFormatter.toCsvFormat(paymentRecordToCreate));
            bufferWrite.newLine();

            // Add the new payment records into the maps to ensure the latest changes are reflected
            addRecordIntoMap(paymentRecordToCreate);

        } catch (IOException e) {
            System.out.println(e.getMessage()); // Print exception message for debugging
            return OperationResult.failed(
                    "Oops! There was an error writing the new payment record. " +
                            "Please try again.");
        }

        return OperationResult.success(
                "The payment record has been created successfully. " +
                        "Please check the record to verify its creation.");
    }

    @Override
    public OperationResult updateRecord(PaymentRecord notAllowedToUpdate) {
        return OperationResult.failed("Payment records cannot be modified to preserve the record integrity.");
    }

    /**
     * Update all the payment records at once
     * Used when deleting a customer account, which will affect all payment records that use them
     */
    public OperationResult bulkUpdateRecord(List<PaymentRecord> paymentToUpdate) {

        // Build a map to store the CSV line of newly updated payment record by its ID
        Map<String, Function<String, Optional<String>>> modifier = new HashMap<>();

        // For each payment record to update, create a modifier entry
        for (PaymentRecord pyt : paymentToUpdate) {
            String pytId = pyt.getPaymentRecordID();
            modifier.put(pytId, _ -> Optional.of(CSVFormatter.toCsvFormat(pyt)));
        }

        // Single file rewrite covers all payment records simultaneously
        OperationResult result = rewriteFile(modifier);
        if (!result.isValid()) {
            return result;
        }

        // Replace all the old records with the updated ones to apply the latest changes
        for (PaymentRecord pyt : paymentToUpdate) {
            removeRecordFromMap(paymentRecordById.get(pyt.getPaymentRecordID()));
            addRecordIntoMap(pyt);
        }

        return OperationResult.success(
                paymentToUpdate.size() + " payment records(s) are updated successfully.");
    }

    @Override
    public OperationResult deleteRecord(PaymentRecord paymentRecordToDelete) {

        String paymentRecordID = paymentRecordToDelete.getPaymentRecordID();

        // Map Optional.empty() by its ID
        Map<String, Function<String, Optional<String>>> modifier = Map.of(
                paymentRecordID, _ -> Optional.empty()
        );

        // Rewrite the file to delete the target record
        OperationResult result = rewriteFile(modifier);
        if (!result.isValid()) {
            return result;
        }

        // Remove the deleted record from the maps to apply the latest changes
        removeRecordFromMap(paymentRecordToDelete);

        return OperationResult.success(
                "The payment record with ID " + paymentRecordID + " has been deleted successfully. "
                        + "Please check the records to verify its deletion.");
    }

    @Override
    public List<PaymentRecord> getAllRecords() {
        return new ArrayList<>(paymentRecordById.values());
    }

    @Override
    public PaymentRecord getRecordById(String paymentRecordId) {
        return paymentRecordById.get(paymentRecordId);
    }

    @Override
    public List<PaymentRecord> getRecordsByIds(List<String> paymentRecordIds) {
        // Filter the payment record objects by their IDs
        return paymentRecordById.values().stream()
                .filter(payment -> paymentRecordIds.contains(payment.getPaymentRecordID()))
                .toList();
    }

    public PaymentRecord getRecordByPaymentRecordNo(String paymentRecordNo) {
        return paymentRecordById.values().stream()
                .filter(payment -> payment.getPaymentRecordNo().equals(paymentRecordNo))
                .findFirst()
                .orElse(null);
    }

    public PaymentRecord getRecordByInvoiceId(String invoiceId) {
        return paymentRecordById.values().stream()
                .filter(payment -> payment.getInvoice().getInvoiceID().equals(invoiceId))
                .findFirst()
                .orElse(null);
    }

    public List<PaymentRecord> getRecordsByPayerAccount(UserAccount payerAccount) {
        return paymentRecordById.values().stream()
                .filter(payment -> payment.getPayerAccount().equals(payerAccount))
                .toList();
    }

    public List<PaymentRecord> getRecordsByPaymentRecordStatus(PaymentStatus paymentStatus) {
        return paymentRecordById.values().stream()
                .filter(payment -> payment.getPaymentStatus().equals(paymentStatus))
                .toList();
    }

    /**
     * Used for retrieving records from the text file
     * Here the line is split into columns using comma as the delimiter
     * Invoice CSV format (9 columns): paymentRecordID, paymentRecordNo, paymentRecordDate, invoice, payerAccount,
     * paymentAmount, paymentMethod, paymentRecordNote, paymentStatus
     */
    @Override
    protected PaymentRecord parseRecord(String line) {

        // Get the repository instances to fetch the respective objects
        CustomerAccountRepository customerAccountRepository = CustomerAccountRepository.getInstance();
        InvoiceRepository invoiceRepository = InvoiceRepository.getInstance();

        String[] columns = line.split(",\\s*");

        return new PaymentRecord(
                columns[0],
                columns[1],
                DataTypeConverter.convertStringToLocalDate(columns[2]),
                invoiceRepository.getRecordById(columns[3]),
                customerAccountRepository.getRecordById(columns[4]),
                DataTypeConverter.convertStringToDouble(columns[5]),
                DataTypeConverter.convertStringToPaymentMethod(columns[6]),
                columns[7].isBlank() ? null : columns[7], // If payment record note is empty, set it to null
                DataTypeConverter.convertStringToPaymentStatus(columns[8])
        );
    }

    /**
     * Add a new/updated payment record into all lookup maps
     */
    @Override
    protected void addRecordIntoMap(PaymentRecord paymentRecord) {

        // End the method early if the payment record is not provided
        if (paymentRecord == null) {
            return;
        }

        paymentRecordById.put(paymentRecord.getPaymentRecordID(), paymentRecord);
    }

    /**
     * Remove the old/deleted payment record from all lookup maps
     */
    @Override
    protected void removeRecordFromMap(PaymentRecord paymentRecord) {

        // End the method early if the payment record is not provided
        if (paymentRecord == null) {
            return;
        }

        paymentRecordById.remove(paymentRecord.getPaymentRecordID());
    }
}
