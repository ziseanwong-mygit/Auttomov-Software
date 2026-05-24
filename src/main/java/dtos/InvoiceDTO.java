package main.java.dtos;

public class InvoiceDTO {

    private String invoiceID;
    private String staffAccountID;
    private String appointmentID;
    private final double additionalFeeAmount;
    private final String invoiceNote;

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
}
