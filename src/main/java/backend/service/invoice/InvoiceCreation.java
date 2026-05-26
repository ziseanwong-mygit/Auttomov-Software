package main.java.backend.service.invoice;

import main.java.backend.model.Appointment;
import main.java.backend.model.Invoice;
import main.java.backend.model.StaffAccount;
import main.java.backend.repository.impl.InvoiceRepository;
import main.java.backend.service.RecordWriter;
import main.java.backend.util.RecordNoGenerator;
import main.java.dtos.InvoiceDTO;
import main.java.enums.data.AppointmentStatus;
import main.java.enums.data.InvoiceStatus;
import main.java.records.OperationResult;

import java.time.LocalDate;
import java.util.UUID;

public class InvoiceCreation implements RecordWriter {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceDTO invoiceDTO;
    private final StaffAccount existingStaff;
    private final Appointment existingAppointment;

    public InvoiceCreation(InvoiceRepository invoiceRepository, InvoiceDTO invoiceDTO, StaffAccount existingStaff,
                           Appointment existingAppointment) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceDTO = invoiceDTO;
        this.existingStaff = existingStaff;
        this.existingAppointment = existingAppointment;
    }

    @Override
    public OperationResult execute() {

        // If the appointment is canceled, invoice cannot be created, thus returning an error message
        if (existingAppointment.getAppointmentStatus().equals(AppointmentStatus.CANCELLED)) {
            return OperationResult.failed("You cannot create an invoice for the appointment: " +
                    existingAppointment.getAppointmentID() + " because it already has been cancelled.");
        }

        // Create a new invoice object
        Invoice newInvoice = populateInvoiceDetails();

        // Save the invoice into the file
        return invoiceRepository.createRecord(newInvoice);
    }

    private Invoice populateInvoiceDetails() {

        // Generate new invoice ID and document number for the new record
        String invoiceId = UUID.randomUUID().toString();
        String invoiceDocNo = "INV" + RecordNoGenerator.generateRecordNo();

        // Get the subtotal amount from the service package in the appointment
        double subtotalAmount = existingAppointment.getServicePackage().getPackageBasePrice();

        // Calculate the service tax amount based on the subtotal amount
        double serviceTaxAmount = subtotalAmount * 0.1;

        // Extract the invoice details from the DTO
        double additionalFeeAmount = invoiceDTO.getAdditionalFeeAmount();
        String invoiceNote = invoiceDTO.getInvoiceNote();

        // Calculate the total amount
        double totalAmount = subtotalAmount + serviceTaxAmount + additionalFeeAmount;

        // Set the invoice status to UNPAID by default
        return new Invoice(invoiceId, invoiceDocNo, LocalDate.now(), existingStaff, existingAppointment,
                subtotalAmount, serviceTaxAmount, additionalFeeAmount, totalAmount, invoiceNote, InvoiceStatus.UNPAID);
    }
}
