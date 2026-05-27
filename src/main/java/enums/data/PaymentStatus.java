package main.java.enums.data;

public enum PaymentStatus {
    SUCCESSFUL("Successful"),
    UNSUCCESSFUL("Unsuccessful");

    private final String paymentStatus;

    PaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public static PaymentStatus getPaymentStatusByValue(String value) {
        for (PaymentStatus ps : PaymentStatus.values()) {
            if (ps.paymentStatus.equalsIgnoreCase(value)) {
                return ps;
            }
        }

        // If not found, throw an exception
        throw new IllegalArgumentException("Invalid payment status: " + value);
    }
}
