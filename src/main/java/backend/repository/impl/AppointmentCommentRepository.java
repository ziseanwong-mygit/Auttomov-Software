package main.java.backend.repository.impl;

import main.java.backend.model.AppointmentComment;
import main.java.backend.model.UserAccount;
import main.java.backend.repository.AbstractRepository;
import main.java.backend.repository.impl.account.CustomerAccountRepository;
import main.java.backend.util.CSVFormatter;
import main.java.backend.util.DataTypeConverter;
import main.java.enums.system.TextFilePath;
import main.java.records.OperationResult;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class AppointmentCommentRepository extends AbstractRepository<AppointmentComment> {

    private static AppointmentCommentRepository instance;
    private final String filePath = TextFilePath.APPOINTMENT_COMMENTS_FILE_PATH.getTextFilePath();

    private final Map<String, AppointmentComment> appointmentCommentById = new HashMap<>();

    // Private constructor to prevent instantiations from outside
    // Pre-populate the records into maps when the instance is created
    private AppointmentCommentRepository() {
        retrieveRecords();
    }

    // Global access point by creating instance for once only
    // So all classes can access the same instance for reading and writing files
    public static AppointmentCommentRepository getInstance() {
        if (instance == null) {
            instance = new AppointmentCommentRepository();
        }
        return instance;
    }

    @Override
    public String getFilePath() {
        return filePath;
    }

    @Override
    public OperationResult createRecord(AppointmentComment commentToCreate) {

        try (BufferedWriter bufferWrite = new BufferedWriter(new FileWriter(getFilePath(), true))) {

            // Write the new comment record into the file
            bufferWrite.write(CSVFormatter.toCsvFormat(commentToCreate));
            bufferWrite.newLine();

            // Add the new comment records into the maps to ensure the latest changes are reflected
            addRecordIntoMap(commentToCreate);

        } catch (IOException e) {
            System.out.println(e.getMessage()); // Print exception message for debugging
            return OperationResult.failed(
                    "Oops! There was an error writing the new comment record. Please try again.");
        }

        return OperationResult.success(
                "The comment record has been created successfully. Please check the record to verify its creation.");
    }

    @Override
    public OperationResult updateRecord(AppointmentComment commentToUpdate) {

        String commentID = commentToUpdate.getCommentID();

        // Map the target comment object by its ID
        Map<String, Function<String, Optional<String>>> modifier = Map.of(
                commentID, _ -> Optional.of(CSVFormatter.toCsvFormat(commentToUpdate))
        );

        // Rewrite the file to update the target record
        OperationResult result = rewriteFile(modifier);
        if (!result.isValid()) {
            return result;
        }

        // Replace the old record with the updated one to apply the latest changes
        removeRecordFromMap(appointmentCommentById.get(commentID));
        addRecordIntoMap(commentToUpdate);

        return OperationResult.success(
                "The comment with ID " + commentID + " has been updated successfully. "
                        + "Please check the details to ensure the changes are applied.");
    }

    /**
     * Update all the appointment comment records at once
     * Used when deleting a customer account, which will affect all comments that use them
     */
    public OperationResult bulkUpdateRecord(List<AppointmentComment> commentsToUpdate) {

        // Build a map to store the CSV line of newly updated comment by its ID
        Map<String, Function<String, Optional<String>>> modifier = new HashMap<>();

        // For each comment to update, create a modifier entry
        for (AppointmentComment comment : commentsToUpdate) {
            String commentId = comment.getCommentID();
            modifier.put(commentId, _ -> Optional.of(CSVFormatter.toCsvFormat(comment)));
        }

        // Single file rewrite covers all comments simultaneously
        OperationResult result = rewriteFile(modifier);
        if (!result.isValid()) {
            return result;
        }

        // Replace all the old records with the updated ones to apply the latest changes
        for (AppointmentComment comment : commentsToUpdate) {
            removeRecordFromMap(appointmentCommentById.get(comment.getCommentID()));
            addRecordIntoMap(comment);
        }

        return OperationResult.success(
                commentsToUpdate.size() + " comment(s) are updated successfully.");
    }

    @Override
    public OperationResult deleteRecord(AppointmentComment commentToDelete) {

        String commentID = commentToDelete.getCommentID();

        // Map Optional.empty() by its ID
        Map<String, Function<String, Optional<String>>> modifier = Map.of(
                commentID, _ -> Optional.empty()
        );

        // Rewrite the file to delete the target record
        OperationResult result = rewriteFile(modifier);
        if (!result.isValid()) {
            return result;
        }

        // Remove the deleted record from the maps to apply the latest changes
        removeRecordFromMap(commentToDelete);

        return OperationResult.success(
                "The comment with ID " + commentID + " has been deleted successfully. "
                        + "Please check the records to verify its deletion.");
    }

    public OperationResult bulkDeleteRecord(List<AppointmentComment> commentsToDelete) {

        // Build a map to store Optional.empty() mapped by each comment ID
        // Optional.empty() indicates that the record should be deleted in the logic
        Map<String, Function<String, Optional<String>>> modifier = new HashMap<>();

        // Map Optional.empty() by its ID
        for (AppointmentComment comment : commentsToDelete) {
            modifier.put(comment.getCommentID(), _ -> Optional.empty());
        }

        // Single file rewrite removes all comments simultaneously
        OperationResult result = rewriteFile(modifier);
        if (!result.isValid()) {
            return result;
        }

        // Remove all the deleted record from the maps to apply the latest changes
        for (AppointmentComment comment : commentsToDelete) {
            removeRecordFromMap(comment);
        }

        return OperationResult.success(
                commentsToDelete.size() + " comment(s) have been deleted successfully.");
    }

    @Override
    public List<AppointmentComment> getAllRecords() {
        return new ArrayList<>(appointmentCommentById.values());
    }

    @Override
    public AppointmentComment getRecordById(String serviceActId) {
        return appointmentCommentById.get(serviceActId);
    }

    @Override
    public List<AppointmentComment> getRecordsByIds(List<String> commentIds) {
        // Filter the appointment comment objects by their IDs
        return appointmentCommentById.values().stream()
                .filter(comment -> commentIds.contains(comment.getCommentID()))
                .toList();
    }

    public List<AppointmentComment> getRecordsByCommentAuthor(UserAccount commentAuthor) {
        return appointmentCommentById.values().stream()
                .filter(comment -> comment.getCommentAuthor().equals(commentAuthor))
                .toList();
    }

    public List<AppointmentComment> getRecordsByAppointmentId(String appointmentId) {
        return appointmentCommentById.values().stream()
                .filter(comment -> comment.getAppointment().getAppointmentID().equals(appointmentId))
                .toList();
    }

    /**
     * Used for retrieving records from the text file
     * Here the line is split into columns using comma as the delimiter
     * Appointment Comment CSV format (6 columns): commentID, commentCreationTime, commentUpdateTime, appointment,
     * commentAuthor, commentContent
     */
    @Override
    protected AppointmentComment parseRecord(String line) {

        // Get the appointment and customer account repository instance to fetch the respective object
        AppointmentRepository appointmentRepository = AppointmentRepository.getInstance();
        CustomerAccountRepository customerAccountRepository = CustomerAccountRepository.getInstance();

        String[] columns = line.split("\\|\\s*");

        return new AppointmentComment(
                columns[0],
                DataTypeConverter.convertStringToLocalDateTime(columns[1]),
                DataTypeConverter.convertStringToLocalDateTime(columns[2]),
                appointmentRepository.getRecordById(columns[3]),
                customerAccountRepository.getRecordById(columns[4]),
                columns[5]
        );
    }

    /**
     * Add a new/updated service activity into all lookup maps
     */
    @Override
    protected void addRecordIntoMap(AppointmentComment comment) {

        // End the method early if the service activity is not provided
        if (comment == null) {
            return;
        }

        appointmentCommentById.put(comment.getCommentID(), comment);
    }

    /**
     * Remove the old/deleted service activity from all lookup maps
     */
    @Override
    protected void removeRecordFromMap(AppointmentComment comment) {

        // End the method early if the service activity is not provided
        if (comment == null) {
            return;
        }

        appointmentCommentById.remove(comment.getCommentID());
    }
}
