package main.java.records;

import java.util.Map;

/**
 * To record the overall result of the operation or function, with a result message.
 * Optionally, if the operation fails, it can also record the error messages for each field during validation.
 */
public record OperationResult(boolean isValid, String generalMessage, Map<String, String> fieldErrors) {

    public static OperationResult success() {
        return new OperationResult(true, null, Map.of());
    }

    public static OperationResult success(String successMessage) {
        return new OperationResult(true, successMessage, Map.of());
    }

    public static OperationResult failed(String generalMessage) {
        return new OperationResult(false, generalMessage, Map.of());
    }

    public static OperationResult failed(String fieldName, String errorMessage) {
        return new OperationResult(false, null, Map.of(fieldName, errorMessage));
    }

    public static OperationResult failed(String failedMessage, Map<String, String> fieldErrors) {
        return new OperationResult(false, failedMessage, Map.copyOf(fieldErrors));
    }
}
