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
     * Extract the square bracket list from a line/record and return the values as a list
     * line: the line/record to extract the list from
     * occurrence: the Nth occurrence of the list in the line/record (starting from 0)
     * -----------------------------------------------------------------------------------
     * Example line: "A0001, Test, Desc, Normal, [B0001, B0002], [C0001, C0002], 20.00, 1"
     * occurrence = 0 → ["B0001", "B0002"]
     * occurrence = 1 → ["C0001", "C0002"]
     */
    public static List<String> extractBracketList(String line, int occurrence) {

        /*
        Initialize variables for searching the list
        found: the number of '[' found in the line; start from -1 to find the first '[' (occurrence = 0)
        searchFrom: the index of the next '[' to be found
         */
        int found = -1;
        int searchFrom = 0;

        /*
        Walk through the line finding each '[' until we reach the Nth one
        Example: occurrence = 1, so get the 2nd occurrence of the list - ["C0001", "C0002"]
        1. 1st loop -> found = -1, searchFrom = 0, line = "([)B0001, B0002]", nextOpen = 20
        2. Loop is not exited because the list found is not the 2nd occurrence, 0 < 1
        3. 2nd loop -> found = 0, searchFrom = 21, line = "([)C0001, C0002]", nextOpen = 40
        4. Loop is exited because the list found is the 2nd occurrence, 1 < 1
        Result: searchFrom = 41
         */
        while (found < occurrence) {
            int nextOpen = line.indexOf("[", searchFrom);

            // If the bracket is not found, return an empty list
            if (nextOpen == -1) {
                return new ArrayList<>();
            }

            found++;
            searchFrom = nextOpen + 1;
        }

        /*
        From previous example:
        searchFrom = 41, listStart = 40, line = "([)C0001, C0002]"
         */
        int listStart = searchFrom - 1; // Position of the '[' we want
        int listEnd = line.indexOf("]", listStart); // Find the closing bracket ']' of the list

        // If the list does not have a closing bracket (e.g. [C0001, C0002), return empty list
        if (listEnd == -1) {
            return new ArrayList<>();
        }

        // Extract the items from the bracket list
        // Example: [C0001, C0002] -> C0001, C0002
        String listItems = line.substring(listStart + 1, listEnd).trim();

        // If the bracket list is empty, return an empty list, otherwise split the items into an array
        return listItems.isBlank()
                ? new ArrayList<>()
                : new ArrayList<>(Arrays.asList(listItems.split(",\\s*")));
    }
}
