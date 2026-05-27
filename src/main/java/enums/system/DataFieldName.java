package main.java.enums.system;

/**
 * Used for identifying the type of data field to display a certain error message during data validation
 * Or to determine which type of data to validate its format or length
 */
public enum DataFieldName {
    RECORD_ID("Record ID"),
    USER_ID("User ID"),
    EMAIL_ADDRESS("Email Address"),
    PHONE_NUMBER("Phone Number"),
    PASSWORD("Password"),
    PRICE("Price"),
    QUANTITY("Quantity"),
    SERVICE_ACTIVITY_ID("Service Activity"),
    VEHICLE_PART_ID( "Vehicle Part"),
    SERVICE_PACKAGE_ID("Service Package"),
    TECHNICIAN_ID("Technician ID"),
    CUSTOMER_ID("Customer ID"),
    APPOINTMENT_ID("Appointment ID"),
    STAFF_ID("Staff ID"),
    INVOICE_ID("Invoice ID"),
    PAYMENT_RECORD_ID("Payment Record ID");

    private final String fieldName;

    DataFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getDataFieldName() {
        return fieldName;
    }
}
