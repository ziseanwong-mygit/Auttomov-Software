package main.java.dtos;

import main.java.enums.data.InvoiceStatus;

public class InvoiceDTO {

    private String invoiceID;
    private String staffAccountID;
    private String appointmentID;
    private double additionalFeeAmount;
    private String invoiceNote;
    private InvoiceStatus invoiceStatus;

    /**
     * For creating a new invoice
     */
    public InvoiceDTO(String staffAccountID, String appointmentID, double additionalFeeAmount, String invoiceNote) {
        this.staffAccountID = staffAccountID;
        this.appointmentID = appointmentID;
        this.additionalFeeAmount = additionalFeeAmount;
        this.invoiceNote = invoiceNote;
    }

    /**
     * For updating an existing invoice's details
     */
    public InvoiceDTO(String invoiceID, double additionalFeeAmount, String invoiceNote) {
        this.invoiceID = invoiceID;
        this.additionalFeeAmount = additionalFeeAmount;
        this.invoiceNote = invoiceNote;
    }

    /**
     * For cancelling invoices or marking invoice as complete when payment is made
     */
    public InvoiceDTO(String invoiceID, InvoiceStatus invoiceStatus) {
        this.invoiceID = invoiceID;
        this.invoiceStatus = invoiceStatus;
    }

    public String getInvoiceID() {
        return invoiceID;
    }

    public String getStaffAccountID() {
        return staffAccountID;
    }

    public String getAppointmentID() {
        return appointmentID;
    }

    public double getAdditionalFeeAmount() {
        return additionalFeeAmount;
    }

    public String getInvoiceNote() {
        return invoiceNote;
    }

    public InvoiceStatus getInvoiceStatus() {
        return invoiceStatus;
    }
}
