package main.java.backend.service.receipt;

import main.java.backend.model.Receipt;
import main.java.backend.repository.impl.ReceiptRepository;
import main.java.backend.service.RecordReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReceiptRetrieval implements RecordReader<Receipt> {

    private final ReceiptRepository receiptRepository;

    public ReceiptRetrieval(ReceiptRepository receiptRepository) {
        this.receiptRepository = receiptRepository;
    }

    /**
     * Read and return all receipts from the repository
     */
    @Override
    public Optional<List<Receipt>> getAllRecords() {

        List<Receipt> existingReceipts = new ArrayList<>(receiptRepository.getAllRecords());

        if (existingReceipts.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(existingReceipts);
    }

    /**
     * Read and return a single receipt based on the given ID
     */
    @Override
    public Optional<Receipt> getRecordById(String receiptId) {

        Receipt existingReceipt = receiptRepository.getRecordById(receiptId);

        if (existingReceipt == null) {
            return Optional.empty();
        }

        return Optional.of(existingReceipt);
    }

    /**
     * Read and return multiple receipts based on the given IDs
     */
    @Override
    public Optional<List<Receipt>> getRecordsByIds(List<String> receiptIds) {

        List<Receipt> existingReceipts = receiptRepository.getRecordsByIds(receiptIds);

        if (existingReceipts.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(existingReceipts);
    }

    /**
     * Read and return a single receipt based on the given document number
     */
    public Optional<Receipt> getRecordByReceiptDocNo(String invoiceDocNo) {

        Receipt existingReceipt = receiptRepository.getRecordByReceiptDocNo(invoiceDocNo);

        if (existingReceipt == null) {
            return Optional.empty();
        }

        return Optional.of(existingReceipt);
    }

    /**
     * Read and return a single receipt based on the given invoice ID
     */
    public Optional<Receipt> getRecordByInvoiceId(String invoiceId) {

        Receipt existingReceipt = receiptRepository.getRecordByInvoiceId(invoiceId);

        if (existingReceipt == null) {
            return Optional.empty();
        }

        return Optional.of(existingReceipt);
    }

    /**
     * Read and return a single receipt based on the given invoice ID
     */
    public Optional<Receipt> getRecordByPaymentRecId(String paymentRecordId) {

        Receipt existingReceipt = receiptRepository.getRecordsByPaymentRecId(paymentRecordId);

        if (existingReceipt == null) {
            return Optional.empty();
        }

        return Optional.of(existingReceipt);
    }
}
