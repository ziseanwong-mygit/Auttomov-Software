package main.java.backend.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeStringFormatter {

    // Private constructor to prevent instantiation
    private DateTimeStringFormatter() {
        throw new UnsupportedOperationException("This class is a utility class, which cannot be instantiated.");
    }

    /**
     * Transform the LocalDateTime object into a string in the format "yyyy-MM-dd HH:mm:ss"
     * Used in CSV formatting
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }
}
