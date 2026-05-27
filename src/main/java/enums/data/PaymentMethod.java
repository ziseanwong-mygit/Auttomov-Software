package main.java.enums.data;

public enum PaymentMethod {
    ONLINE_BANKING("Online banking"),
    CARD_PAYMENT("Credit/Debit card"),
    E_WALLET("E-wallet payment");

    private final String paymentMethod;

    PaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public static PaymentMethod getPaymentMethodByValue(String value) {
        for (PaymentMethod pm : PaymentMethod.values()) {
            if (pm.paymentMethod.equalsIgnoreCase(value)) {
                return pm;
            }
        }

        // If not found, throw an exception
        throw new IllegalArgumentException("Invalid payment method: " + value);
    }
}
