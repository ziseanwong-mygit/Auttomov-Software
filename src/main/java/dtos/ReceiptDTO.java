package main.java.dtos;

public class ReceiptDTO {

    private String receiptID;
    private String staffAccountID;
    private String invoiceID;
    private String paymentRecordID;
    private String receiptNote;

    /**
     * For creating a new receipt
     */
    public ReceiptDTO(String staffAccountID, String invoiceID, String paymentRecordID, String receiptNote) {
        this.staffAccountID = staffAccountID;
        this.invoiceID = invoiceID;
        this.paymentRecordID = paymentRecordID;
        this.receiptNote = receiptNote;
    }

    /**
     * For updating the note of an existing receipt
     */
    public ReceiptDTO(String receiptID, String receiptNote) {
        this.receiptID = receiptID;
        this.receiptNote = receiptNote;
    }

    public String getReceiptID() {
        return receiptID;
    }

    public String getStaffAccountID() {
        return staffAccountID;
    }

    public String getInvoiceID() {
        return invoiceID;
    }

    public String getPaymentRecordID() {
        return paymentRecordID;
    }

    public String getReceiptNote() {
        return receiptNote;
    }
}
