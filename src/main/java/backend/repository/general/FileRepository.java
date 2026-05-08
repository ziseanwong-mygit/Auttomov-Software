package main.java.backend.repository.general;

import main.java.records.OperationResult;

import java.io.*;
import java.util.*;
import java.util.function.Function;

/**
 * Base interface for all repositories to inherit and use
 * Provides file rewrite functionality to update and delete records
 */
public interface FileRepository {

    // Each repository provides its own file path
    String getFilePath();

    // Shared rewriteFile() - inherited by all repositories automatically
    default OperationResult rewriteFile(Map<String, Function<String, Optional<String>>> recordModifier) {

        List<String> updatedLines = new ArrayList<>();
        Set<String> recordsFound  = new HashSet<>();

        try (BufferedReader bufferRead = new BufferedReader(new FileReader(getFilePath()))) {
            String line;

            while ((line = bufferRead.readLine()) != null) {
                String[] column = line.split(",\\s*");
                String targetID = column[0];

                // If the ID matches, add it into a set and start the functional interface
                // Retrieve the current record, and check if the optional value is present or not
                // If present, add the updated line into the list to update, otherwise skip the line to delete
                if (recordModifier.containsKey(targetID)) {
                    recordsFound.add(targetID);
                    recordModifier.get(targetID).apply(line).ifPresent(updatedLines::add);
                } else {
                    updatedLines.add(line);
                }
            }

        } catch (IOException e) {
            System.out.println(e.getMessage()); // Print exception message for debugging
            return OperationResult.failed("Oops! There was an error reading records from the file. " +
                    "Please try again.");
        }

        Set<String> recordsNotFound = new HashSet<>(recordModifier.keySet());
        recordsNotFound.removeAll(recordsFound);
        if (!recordsNotFound.isEmpty()) {
            return OperationResult.failed("Oops! The following records were not found: " +
                    recordsNotFound);
        }

        // Rewrite the entire file with the updated records
        try (BufferedWriter bufferWrite = new BufferedWriter(new FileWriter(getFilePath(), false))) {
            for (String updatedLine : updatedLines) {
                bufferWrite.write(updatedLine);
                bufferWrite.newLine();
            }

        } catch (IOException e) {
            System.out.println(e.getMessage()); // Print exception message for debugging
            return OperationResult.failed("Oops! There was an error writing records into the file. " +
                    "Please try again.");
        }

        return OperationResult.success();
    }
}
