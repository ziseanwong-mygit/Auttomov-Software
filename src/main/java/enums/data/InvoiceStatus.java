package main.java.enums.data;

public enum InvoiceStatus {
    PAID("Paid"),
    UNPAID("Unpaid"),
    CANCELLED("Cancelled");

    private final String invoiceStatus;

    InvoiceStatus(String invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public String getInvoiceStatus() {
        return invoiceStatus;
    }

    public static InvoiceStatus getInvoiceStatusByValue(String value) {
        for (InvoiceStatus is : InvoiceStatus.values()) {
            if (is.invoiceStatus.equalsIgnoreCase(value)) {
                return is;
            }
        }
        return null;
    }
}
