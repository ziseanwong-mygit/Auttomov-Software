package main.java.records;

import java.util.List;

/**
 * A record to represent the result of overall data validation, and a list of field-specific errors.
 * Example: ValidationResult(false, List.of(FieldError("email", "Email address is invalid")))
 * Used in UserDataValidationFunc.java
 */
public record ValidationResult(boolean isValid, List<FieldError> fieldErrorsList) {

    // Nested record for field-specific error messages
    public record FieldError(String fieldName, String errorMessage) {}

    public static ValidationResult success() {
        return new ValidationResult(true, List.of());
    }

    public static ValidationResult failed(String dataField, String errorMessage) {
        return new ValidationResult(false, List.of(new FieldError(dataField, errorMessage)));
    }
}
