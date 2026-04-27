package main.java.records;

import java.util.List;
import java.util.function.Function;

/**
 * Used to store the original values and input values for each field
 * Then compare the two values to determine if the field has been changed
 * Functional interface is used to validate the input value such as formatting, length, etc.
 */
public record FieldComparison(String fieldName, String currentValue, String newValue,
                              List<Function<String, OperationResult>> validators) {

    public boolean isChanged() {
        return !newValue.equals(currentValue);
    }

    // Run each validation function one by one
    // If any fails, stop validation and return the error message
    public OperationResult validate() {

        // If there are no any validations, end the process
        if (validators == null || validators.isEmpty())
            return OperationResult.success();

        for (Function<String, OperationResult> validator : validators) {
            OperationResult result = validator.apply(newValue);
            if (!result.isValid())
                return result;
        }

        return OperationResult.success();
    }
}
