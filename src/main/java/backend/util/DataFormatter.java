package main.java.backend.util;

public class DataFormatter {

    // Private constructor to prevent instantiation
    private DataFormatter() {
        throw new UnsupportedOperationException("This class is a utility class, which cannot be instantiated.");
    }

    public static String roundOffDecimal(double value) {
        return String.format("%.2f", value);
    }
}
