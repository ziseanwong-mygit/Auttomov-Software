package main.java.backend.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataStructureOrganizer {

    // Private constructor to prevent instantiation
    private DataStructureOrganizer() {
        throw new UnsupportedOperationException("This class is a utility class, which cannot be instantiated.");
    }

    /**
     * Add all the values from a line/record into a list
     */
    public static List<String> addItemsIntoList(String line) {

        // Find the start and end indices of the list
        int listStart = line.indexOf("[");
        int listEnd = line.indexOf("]");

        // Extract the list items from the line
        String listItems = line.substring(listStart + 1, listEnd).trim();

        // If there are no items, leave the list empty
        // Otherwise, split the string into a list of items
        return listItems.isBlank()
                ? new ArrayList<>()
                : new ArrayList<>(Arrays.asList(listItems.split(",\\s*")));
    }

    /**
     * Add all the values from a line/record into an array
     * Used when there is a list in the record and need to extract the common values during file reading
     */
    public static String[] addValuesIntoArray(String line) {

        // Find the start and end indices of the list
        int listStart = line.indexOf("[");
        int listEnd = line.indexOf("]");

        // Extract the ordinary values from the record, apart from the list itself
        String vehServiceValues = (line.substring(0, listStart - 2).trim()) +
                (line.substring(listEnd + 1).trim());

        // Split the string into an array of values
        return vehServiceValues.split(",\\s*");
    }
}
