package main.java.backend.model;

import java.time.LocalDate;

public class Receipt {

    private final String receiptID;
    private final String receiptDocNo;
    private final LocalDate receiptCreationDate;
    private StaffAccount receiptIssuer;
    private final Invoice invoice;
    private final PaymentRecord paymentRecord;
    private String receiptNote;

    public Receipt(String receiptID, String receiptDocNo, LocalDate receiptCreationDate, StaffAccount receiptIssuer,
                   Invoice invoice, PaymentRecord paymentRecord, String receiptNote) {
        this.receiptID = receiptID;
        this.receiptDocNo = receiptDocNo;
        this.receiptCreationDate = receiptCreationDate;
        this.receiptIssuer = receiptIssuer;
        this.invoice = invoice;
        this.paymentRecord = paymentRecord;
        this.receiptNote = receiptNote;
    }

    public String getReceiptID() {
        return receiptID;
    }

    public String getReceiptDocNo() {
        return receiptDocNo;
    }

    public LocalDate getReceiptCreationDate() {
        return receiptCreationDate;
    }

    public StaffAccount getReceiptIssuer() {
        return receiptIssuer;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public PaymentRecord getPaymentRecord() {
        return paymentRecord;
    }

    public String getReceiptNote() {
        return receiptNote;
    }

    public void setReceiptIssuer(StaffAccount receiptIssuer) {
        this.receiptIssuer = receiptIssuer;
    }

    public void setReceiptNote(String receiptNote) {
        this.receiptNote = receiptNote;
    }

    /**
     * Return a human-readable string representation of the object.
     * Used for logging, debugging, testing, or printing overall information.
     */
    @Override
    public String toString() {
        return "Receipt ID: " + receiptID +
                "\nReceipt No: " + receiptDocNo +
                "\nReceipt Creation Date: " + receiptCreationDate +
                "\nReceipt Issuer: " +
                (receiptIssuer != null ? receiptIssuer.getFirstName() + " " + receiptIssuer.getLastName()
                        : "(issuer unavailable/deleted)") +
                "\nInvoice No: " + invoice.getInvoiceDocNo() +
                "\nSubtotal Amount: RM" + invoice.getSubtotalAmount() +
                "\nService Tax Amount: RM" + invoice.getServiceTaxAmount() +
                "\nAdditional Fee Amount: RM" + invoice.getAdditionalFeeAmount() +
                "\nTotal Amount: RM" + invoice.getTotalAmount() +
                "\nPaid Amount: RM" + paymentRecord.getPaymentAmount() +
                "\nPayment Date: " + paymentRecord.getPaymentDate() +
                "\nPayer Name: " +
                (paymentRecord.getPayerAccount() != null ? paymentRecord.getPayerAccount().getFirstName() + " " +
                        paymentRecord.getPayerAccount().getLastName() : "(payer unavailable/deleted)") +
                "\nPayment Method: " + paymentRecord.getPaymentMethod().getPaymentMethod() +
                "\nPayment Status: " + paymentRecord.getPaymentStatus().getPaymentStatus() +
                "\nReceipt Note: " +
                (receiptNote != null ? receiptNote : "(no note)");
    }
}
