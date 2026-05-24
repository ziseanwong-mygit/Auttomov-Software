package main.java.dtos;

import main.java.enums.data.PaymentMethod;

public class PaymentRecordDTO {

    private final String invoiceID;
    private final String customerAccountID;
    private final double paymentAmount;
    private final PaymentMethod paymentMethod;
    private final String paymentRecordNote;

    /**
     * For creating a new payment record
     */
    public PaymentRecordDTO(String invoiceID, String customerAccountID, double paymentAmount,
                            PaymentMethod paymentMethod, String paymentRecordNote) {
        this.invoiceID = invoiceID;
        this.customerAccountID = customerAccountID;
        this.paymentAmount = paymentAmount;
        this.paymentMethod = paymentMethod;
        this.paymentRecordNote = paymentRecordNote;
    }

    public String getInvoiceID() {
        return invoiceID;
    }

    public String getCustomerAccountID() {
        return customerAccountID;
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
}
