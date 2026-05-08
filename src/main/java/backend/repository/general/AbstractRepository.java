package main.java.backend.repository.general;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.format.DateTimeParseException;

public abstract class AbstractRepository<T> implements CrudRepository<T> {

    /**
     * Retrieve all records from the text file and populate them into maps
     * Parsing is done by the subclass
     */
    protected void retrieveRecords() {
        try (BufferedReader reader = new BufferedReader(new FileReader(getFilePath()))) {
            String line;

            while ((line = reader.readLine()) != null) {
                try {
                    String[] columns = line.split(",\\s*");
                    T record = parseRecord(columns);  // Subclass gives implementation on how to parse the records
                    addRecordIntoMap(record); // Add the parsed records into the lookup maps

                } catch (IllegalArgumentException | DateTimeParseException | ArrayIndexOutOfBoundsException e) {
                    System.out.println(
                            "This record: " + line + " is skipped due to the following: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Oops! There was an error occurred in reading the file due to the following: " +
                    e.getMessage());
            System.out.println("Ensure the file is in the correct format and the path is correct, then try again.");
        }
    }

    /** Turn one CSV line (already split into columns) into an object of type T. */
    protected abstract T parseRecord(String[] columns);

    /**
     * Store a new/updated record into all lookup maps
     */
    protected abstract void addRecordIntoMap(T record);

    /**
     * Remove the old/deleted vehicle part from all lookup maps
     */
    protected abstract void removeRecordIntoMap(T record);
}
