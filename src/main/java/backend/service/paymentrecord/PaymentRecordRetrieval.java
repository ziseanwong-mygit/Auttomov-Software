package main.java.backend.service.paymentrecord;

import main.java.backend.model.PaymentRecord;
import main.java.backend.repository.impl.PaymentRecordRepository;
import main.java.backend.service.RecordReader;
import main.java.enums.data.PaymentStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaymentRecordRetrieval implements RecordReader<PaymentRecord> {

    private final PaymentRecordRepository paymentRecordRepository;

    public PaymentRecordRetrieval(PaymentRecordRepository paymentRecordRepository) {
        this.paymentRecordRepository = paymentRecordRepository;
    }

    /**
     * Read and return all invoices from the repository
     */
    @Override
    public Optional<List<PaymentRecord>> getAllRecords() {

        List<PaymentRecord> existingPaymentRecords = new ArrayList<>(paymentRecordRepository.getAllRecords());

        if (existingPaymentRecords.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(existingPaymentRecords);
    }

    /**
     * Read and return a single payment record based on the given ID
     */
    @Override
    public Optional<PaymentRecord> getRecordById(String paymentRecordId) {

        PaymentRecord existingPaymentRecord = paymentRecordRepository.getRecordById(paymentRecordId);

        if (existingPaymentRecord == null) {
            return Optional.empty();
        }

        return Optional.of(existingPaymentRecord);
    }

    /**
     * Read and return multiple payment records based on the given IDs
     */
    @Override
    public Optional<List<PaymentRecord>> getRecordsByIds(List<String> paymentRecordIds) {

        List<PaymentRecord> existingPaymentRecords = paymentRecordRepository.getRecordsByIds(paymentRecordIds);

        if (existingPaymentRecords.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(existingPaymentRecords);
    }

    /**
     * Read and return a single payment record based on the given record number
     */
    public Optional<PaymentRecord> getRecordByPaymentRecordNo(String paymentRecordNo) {

        PaymentRecord existingPaymentRecord = paymentRecordRepository.getRecordByPaymentRecordNo(paymentRecordNo);

        if (existingPaymentRecord == null) {
            return Optional.empty();
        }

        return Optional.of(existingPaymentRecord);
    }

    /**
     * Read and return a single payment record based on the given invoice ID
     */
    public Optional<PaymentRecord> getRecordByInvoiceId(String invoiceId) {

        PaymentRecord existingPaymentRecord = paymentRecordRepository.getRecordByInvoiceId(invoiceId);

        if (existingPaymentRecord == null) {
            return Optional.empty();
        }

        return Optional.of(existingPaymentRecord);
    }

    /**
     * Read and return multiple invoices based on the given payment status
     */
    public Optional<List<PaymentRecord>> getRecordsByPaymentRecordStatus(PaymentStatus status) {

        List<PaymentRecord> existingPaymentRecords = paymentRecordRepository.getRecordsByPaymentRecordStatus(status);

        if (existingPaymentRecords.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(existingPaymentRecords);
    }
}
