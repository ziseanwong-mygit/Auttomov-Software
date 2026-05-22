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
    SERVICE_ACTIVITY("Service Activity"),
    VEHICLE_PART( "Vehicle Part"),
    SERVICE_PACKAGE("Service Package"),
    TECHNICIAN_ID("Technician ID"),;

    private final String fieldName;

    DataFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getDataFieldName() {
        return fieldName;
    }
}
