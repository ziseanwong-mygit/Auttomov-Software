package main.java.backend.model;

import main.java.enums.data.PaymentMethod;
import main.java.enums.data.PaymentStatus;

import java.time.LocalDate;

public class PaymentRecord {

    private final String paymentRecordID;
    private final String paymentRecordNo;
    private final LocalDate paymentDate;
    private final Invoice invoice;
    private UserAccount payerAccount;
    private final double paymentAmount;
    private final PaymentMethod paymentMethod;
    private final String paymentRecordNote;
    private final PaymentStatus paymentStatus;

    public PaymentRecord(String paymentRecordID, String paymentRecordNo, LocalDate paymentDate,
                         Invoice invoice, UserAccount payerAccount, double paymentAmount, PaymentMethod paymentMethod,
                         String paymentRecordNote, PaymentStatus paymentStatus) {
        this.paymentRecordID = paymentRecordID;
        this.paymentRecordNo = paymentRecordNo;
        this.paymentDate = paymentDate;
        this.payerAccount = payerAccount;
        this.invoice = invoice;
        this.paymentAmount = paymentAmount;
        this.paymentMethod = paymentMethod;
        this.paymentRecordNote = paymentRecordNote;
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentRecordID() {
        return paymentRecordID;
    }

    public String getPaymentRecordNo() {
        return paymentRecordNo;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public UserAccount getPayerAccount() {
        return payerAccount;
    }

    public double getPaymentAmount() {
        return paymentAmount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentRecordNote() {
        return paymentRecordNote;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPayerAccount(UserAccount payerAccount) {
        this.payerAccount = payerAccount;
    }

    /**
     * Return a human-readable string representation of the object.
     * Used for logging, debugging, testing, or printing overall information.
     */
    @Override
    public String toString() {
        return "Payment Record ID: " + paymentRecordID +
                "\nPayment Record No: " + paymentRecordNo +
                "\nPayment Date: " + paymentDate +
                "\nInvoice No: " + invoice.getInvoiceDocNo() +
                "\nInvoice Amount: RM" + invoice.getTotalAmount() +
                "\nPayer Name: " +
                (payerAccount != null ? payerAccount.getFirstName() + " " + payerAccount.getLastName()
                        : "(payer unavailable/deleted)") +
                "\nPayment Amount: RM" + paymentAmount +
                "\nPayment Method: " + paymentMethod.getPaymentMethod() +
                "\nPayment Record Note: " + paymentRecordNote +
                "\nPayment Status: " + paymentStatus.getPaymentStatus() +
                "\n";
    }
}
