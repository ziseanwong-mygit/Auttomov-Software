package main.java.backend.service.invoice;

import main.java.backend.model.Invoice;
import main.java.backend.repository.impl.InvoiceRepository;
import main.java.backend.service.RecordReader;
import main.java.enums.data.InvoiceStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InvoiceRetrieval implements RecordReader<Invoice> {

    private final InvoiceRepository invoiceRepository;

    public InvoiceRetrieval(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    /**
     * Read and return all invoices from the repository
     */
    @Override
    public Optional<List<Invoice>> getAllRecords() {

        List<Invoice> existingInvoices = new ArrayList<>(invoiceRepository.getAllRecords());

        if (existingInvoices.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(existingInvoices);
    }

    /**
     * Read and return a single invoice based on the given ID
     */
    @Override
    public Optional<Invoice> getRecordById(String invoiceId) {

        Invoice existingInvoice = invoiceRepository.getRecordById(invoiceId);

        if (existingInvoice == null) {
            return Optional.empty();
        }

        return Optional.of(existingInvoice);
    }

    /**
     * Read and return multiple invoices based on the given IDs
     */
    @Override
    public Optional<List<Invoice>> getRecordsByIds(List<String> invoiceIds) {

        List<Invoice> existingInvoices = invoiceRepository.getRecordsByIds(invoiceIds);

        if (existingInvoices.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(existingInvoices);
    }

    /**
     * Read and return a single invoice based on the given document number
     */
    public Optional<Invoice> getRecordByInvoiceDocNo(String invoiceDocNo) {

        Invoice existingInvoice = invoiceRepository.getRecordByInvoiceDocNo(invoiceDocNo);

        if (existingInvoice == null) {
            return Optional.empty();
        }

        return Optional.of(existingInvoice);
    }

    /**
     * Read and return multiple invoices based on the given appointment ID
     */
    public Optional<List<Invoice>> getRecordsByAppointmentId(String appointmentId) {

        List<Invoice> existingInvoices = invoiceRepository.getRecordsByAppointmentId(appointmentId);

        if (existingInvoices.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(existingInvoices);
    }

    /**
     * Read and return multiple invoices based on the given invoice status
     */
    public Optional<List<Invoice>> getRecordsByInvoiceStatus(InvoiceStatus status) {

        List<Invoice> existingInvoices = invoiceRepository.getRecordsByInvoiceStatus(status);

        if (existingInvoices.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(existingInvoices);
    }
}
