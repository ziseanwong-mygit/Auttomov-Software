package main.java.records;

import java.util.Map;

/**
 * To record the overall result of the operation or function, with true/false value as the base value
 * Optionally, can include a general message, map of error messages by field names, and the data object retrieved
 * from reading records
 */
public record OperationResult(boolean isValid, String generalMessage, Map<String, String> fieldErrors, Object data) {

    // Canonical constructor to store list of error messages if data validation happens
    // Otherwise, create an empty immutable map to prevent modifications
    public OperationResult {
        fieldErrors = (fieldErrors != null)
                ? Map.copyOf(fieldErrors)
                : Map.of();
    }

    // Overloaded constructors with necessary values for different use cases
    // Act as blueprint for factory methods below to use

    // Mostly for simple validation/operations by returning true/false value without custom message
    public OperationResult(boolean isValid) {
        this(isValid, null, null, null);
    }

    // With true/false value and general message
    // Mostly for CRUD operations and other methods that may need to return a message for display
    public OperationResult(boolean isValid, String message) {
        this(isValid, message, null, null);
    }

    // With true/false value and list of error messages mapped by field names
    // Mostly for data validation and verification, especially for account records to return all errors in one go
    public OperationResult(boolean isValid, Map<String, String> fieldErrors) {
        this(isValid, null, fieldErrors, null);
    }

    // With true/false value, general message and data object
    // Mostly for retrieving records from text files, where the data object is the record itself
    public OperationResult(boolean isValid, String message, Object data) {
        this(isValid, message, null, data);
    }

    // Factory methods used by calling methods in external classes to fetch specific messages and record

    public static OperationResult success() {
        return new OperationResult(true);
    }

    public static OperationResult success(String message) {
        return new OperationResult(true, message);
    }

    public static OperationResult success(String message, Object data) {
        return new OperationResult(true, message, data);
    }

    public static OperationResult failed() {
        return new OperationResult(false);
    }

    public static OperationResult failed(String message) {
        return new OperationResult(false, message);
    }

    public static OperationResult failed(String fieldName, String errorMessage) {
        return new OperationResult(false, Map.of(fieldName, errorMessage));
    }

    public static OperationResult failed(Map<String, String> fieldErrors) {
        return new OperationResult(false, fieldErrors);
    }

    public static OperationResult failed(String message, Map<String, String> fieldErrors) {
        return new OperationResult(false, message, fieldErrors, null);
    }
}
