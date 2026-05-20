package main.java.enums.system;

/**
 * Used for identifying the type of data field to display a certain error message during data validation
 * Or to determine which type of data to validate its format or length
 */
public enum DataFieldName {
    RECORD_ID,
    USER_ID,
    EMAIL_ADDRESS,
    PHONE_NUMBER,
    PASSWORD,
    PRICE,
    QUANTITY
}
