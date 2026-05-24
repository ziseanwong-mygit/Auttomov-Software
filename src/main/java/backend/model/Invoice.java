package main.java.backend.model;

import main.java.enums.data.InvoiceStatus;

import java.time.LocalDate;

public class Invoice {

    private final String invoiceID;
    private final String invoiceDocNo;
    private final LocalDate invoiceCreationDate;
    private StaffAccount invoiceIssuer;
    private Appointment appointment;
    private final double subtotalAmount;
    private final double serviceTaxAmount;
    private double additionalFeeAmount;
    private double totalAmount;
    private String invoiceNote;
    private InvoiceStatus invoiceStatus;

    public Invoice(String invoiceID, String invoiceDocNo, LocalDate invoiceCreationDate, StaffAccount invoiceIssuer,
                   Appointment appointment, double subtotalAmount, double serviceTaxAmount,
                   double additionalFeeAmount, double totalAmount, String invoiceNote, InvoiceStatus invoiceStatus) {
        this.invoiceID = invoiceID;
        this.invoiceDocNo = invoiceDocNo;
        this.invoiceCreationDate = invoiceCreationDate;
        this.invoiceIssuer = invoiceIssuer;
        this.appointment = appointment;
        this.subtotalAmount = subtotalAmount;
        this.serviceTaxAmount = serviceTaxAmount;
        this.additionalFeeAmount = additionalFeeAmount;
        this.totalAmount = totalAmount;
        this.invoiceNote = invoiceNote;
        this.invoiceStatus = invoiceStatus;
    }

    public String getInvoiceID() {
        return invoiceID;
    }

    public String getInvoiceDocNo() {
        return invoiceDocNo;
    }

    public LocalDate getInvoiceCreationDate() {
        return invoiceCreationDate;
    }

    public StaffAccount getInvoiceIssuer() {
        return invoiceIssuer;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public double getSubtotalAmount() {
        return subtotalAmount;
    }

    public double getServiceTaxAmount() {
        return serviceTaxAmount;
    }

    public double getAdditionalFeeAmount() {
        return additionalFeeAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getInvoiceNote() {
        return invoiceNote;
    }

    public InvoiceStatus getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceIssuer(StaffAccount invoiceIssuer) {
        this.invoiceIssuer = invoiceIssuer;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public void setAdditionalFeeAmount(double additionalFeeAmount) {
        this.additionalFeeAmount = additionalFeeAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setInvoiceNote(String invoiceNote) {
        this.invoiceNote = invoiceNote;
    }

    public void setInvoiceStatus(InvoiceStatus invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    /**
     * Return a human-readable string representation of the object.
     * Used for logging, debugging, testing, or printing overall information.
     */
    @Override
    public String toString() {
        return "Invoice ID: " + invoiceID +
               "\nInvoice No: " + invoiceDocNo +
               "\nInvoice Creation Date: " + invoiceCreationDate +
               "\nInvoice Issuer: " +
                (invoiceIssuer != null ? invoiceIssuer.getFirstName() + " " + invoiceIssuer.getLastName()
                        : "(issuer unavailable/deleted)") +
               "\nAppointment: " +
                (appointment != null ? appointment.getAppointmentTitle() : "(appointment unavailable/deleted)") +
               "\nSubtotal Amount: " + subtotalAmount +
               "\nService Tax Amount: " + serviceTaxAmount +
               "\nAdditional Fee Amount: " + additionalFeeAmount +
               "\nTotal Amount: " + totalAmount +
               "\nInvoice Note: " +
                (invoiceNote != null ? invoiceNote : "(no note)") +
                "\n";
    }
}
