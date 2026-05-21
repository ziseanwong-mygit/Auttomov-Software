package main.java.backend.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Generate a random document/order number for appointments, invoices, payment records and receipts
 * This number is unique and shorter than UUID; used to quickly retrieve the record when searching
 * Class is used when creating a new record
 */
public class RecordNoGenerator {

    // Private constructor to prevent instantiation
    private RecordNoGenerator() {
        throw new UnsupportedOperationException("This class is a utility class, which cannot be instantiated.");
    }

    public static String generateRecordNo() {

        // Get the current date in the format of yyyyMMdd (e.g., 20260501)
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String dateTodayStr = currentDate.format(dateFormatter);

        // Generate 6 random characters for the record number (e.g., "A1ZK83")
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) { // For each character, generate a random index and append it to the StringBuilder
            sb.append(chars[rnd.nextInt(chars.length)]);
        }

        // Concatenate the formatted date and random string together and return it (e.g., "20260501-A1ZK83")
        return dateTodayStr + "-" + sb;
    }

}
