package main.java.backend.util;

import main.java.records.OperationResult;

import java.util.List;

public class NullValidator {

    // Private constructor to prevent instantiation
    private NullValidator() {
        throw new UnsupportedOperationException("This class is a utility class, which cannot be instantiated.");
    }

    /**
     * Accept and check any type of value in the list for null or empty values
     */
    public static OperationResult isAllValuesNull(List<?> values) {
        for (Object value : values) {
            if (value == null) {
                return OperationResult.failed();
            }

            if (value instanceof String str && str.isBlank()) {
                return OperationResult.failed();
            }
        }
        return OperationResult.success();
    }

}
