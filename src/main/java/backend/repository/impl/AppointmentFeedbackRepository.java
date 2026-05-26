package main.java.backend.repository.impl;

import main.java.backend.model.AppointmentFeedback;
import main.java.backend.model.StaffAccount;
import main.java.backend.repository.AbstractRepository;
import main.java.backend.repository.impl.account.StaffAccountRepository;
import main.java.backend.util.CSVFormatter;
import main.java.backend.util.DataTypeConverter;
import main.java.enums.system.TextFilePath;
import main.java.records.OperationResult;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class AppointmentFeedbackRepository extends AbstractRepository<AppointmentFeedback> {

    private static AppointmentFeedbackRepository instance;
    private final String filePath = TextFilePath.APPOINTMENT_FEEDBACK_FILE_PATH.getTextFilePath();

    private final Map<String, AppointmentFeedback> appointmentFeedbackById = new HashMap<>();

    // Private constructor to prevent instantiations from outside
    // Pre-populate the records into maps when the instance is created
    private AppointmentFeedbackRepository() {
        retrieveRecords();
    }

    // Global access point by creating instance for once only
    // So all classes can access the same instance for reading and writing files
    public static AppointmentFeedbackRepository getInstance() {
        if (instance == null) {
            instance = new AppointmentFeedbackRepository();
        }
        return instance;
    }

    @Override
    public String getFilePath() {
        return filePath;
    }

    @Override
    public OperationResult createRecord(AppointmentFeedback feedbackToCreate) {

        try (BufferedWriter bufferWrite = new BufferedWriter(new FileWriter(getFilePath(), true))) {

            // Write the new feedback record into the file
            bufferWrite.write(CSVFormatter.toCsvFormat(feedbackToCreate));
            bufferWrite.newLine();

            // Add the new feedback records into the maps to ensure the latest changes are reflected
            addRecordIntoMap(feedbackToCreate);

        } catch (IOException e) {
            System.out.println(e.getMessage()); // Print exception message for debugging
            return OperationResult.failed(
                    "Oops! There was an error writing the new feedback record. Please try again.");
        }

        return OperationResult.success(
                "The feedback record has been created successfully. Please check the record to verify its creation.");
    }

    @Override
    public OperationResult updateRecord(AppointmentFeedback feedbackToUpdate) {

        String feedbackID = feedbackToUpdate.getFeedbackID();

        // Map the target feedback object by its ID
        Map<String, Function<String, Optional<String>>> modifier = Map.of(
                feedbackID, _ -> Optional.of(CSVFormatter.toCsvFormat(feedbackToUpdate))
        );

        // Rewrite the file to update the target record
        OperationResult result = rewriteFile(modifier);
        if (!result.isValid()) {
            return result;
        }

        // Replace the old record with the updated one to apply the latest changes
        removeRecordFromMap(appointmentFeedbackById.get(feedbackID));
        addRecordIntoMap(feedbackToUpdate);

        return OperationResult.success(
                "The feedback with ID " + feedbackID + " has been updated successfully. "
                        + "Please check the details to ensure the changes are applied.");
    }

    /**
     * Update all the appointment feedback records at once
     * Used when deleting a staff account, which will affect all feedbacks that use them
     */
    public OperationResult bulkUpdateRecord(List<AppointmentFeedback> feedbacksToUpdate) {

        // Build a map to store the CSV line of newly updated feedback by its ID
        Map<String, Function<String, Optional<String>>> modifier = new HashMap<>();

        // For each feedback to update, create a modifier entry
        for (AppointmentFeedback feedback : feedbacksToUpdate) {
            String feedbackId = feedback.getFeedbackID();
            modifier.put(feedbackId, _ -> Optional.of(CSVFormatter.toCsvFormat(feedback)));
        }

        // Single file rewrite covers all feedbacks simultaneously
        OperationResult result = rewriteFile(modifier);
        if (!result.isValid()) {
            return result;
        }

        // Replace all the old records with the updated ones to apply the latest changes
        for (AppointmentFeedback feedback : feedbacksToUpdate) {
            removeRecordFromMap(appointmentFeedbackById.get(feedback.getFeedbackID()));
            addRecordIntoMap(feedback);
        }

        return OperationResult.success(
                feedbacksToUpdate.size() + " feedback(s) are updated successfully.");
    }

    @Override
    public OperationResult deleteRecord(AppointmentFeedback feedbackToDelete) {

        String feedbackID = feedbackToDelete.getFeedbackID();

        // Map the target feedback object by its ID
        Map<String, Function<String, Optional<String>>> modifier = Map.of(
                feedbackID, _ -> Optional.empty()
        );

        // Rewrite the file to delete the target record
        OperationResult result = rewriteFile(modifier);
        if (!result.isValid()) {
            return result;
        }

        // Remove the deleted record from the maps to apply the latest changes
        removeRecordFromMap(feedbackToDelete);

        return OperationResult.success(
                "The feedback with ID " + feedbackID + " has been deleted successfully. "
                        + "Please check the records to verify its deletion.");
    }

    public OperationResult bulkDeleteRecord(List<AppointmentFeedback> feedbacksToDelete) {

        // Build a map to store Optional.empty() mapped by each feedback ID
        // Optional.empty() indicates that the record should be deleted in the logic
        Map<String, Function<String, Optional<String>>> modifier = new HashMap<>();

        // Map Optional.empty() by its ID
        for (AppointmentFeedback feedback : feedbacksToDelete) {
            modifier.put(feedback.getFeedbackID(), _ -> Optional.empty());
        }

        // Single file rewrite removes all feedbacks simultaneously
        OperationResult result = rewriteFile(modifier);
        if (!result.isValid()) {
            return result;
        }

        // Remove all the deleted record from the maps to apply the latest changes
        for (AppointmentFeedback feedback : feedbacksToDelete) {
            removeRecordFromMap(feedback);
        }

        return OperationResult.success(
                feedbacksToDelete.size() + " feedback(s) have been deleted successfully.");
    }

    @Override
    public List<AppointmentFeedback> getAllRecords() {
        return new ArrayList<>(appointmentFeedbackById.values());
    }

    @Override
    public AppointmentFeedback getRecordById(String serviceActId) {
        return appointmentFeedbackById.get(serviceActId);
    }

    @Override
    public List<AppointmentFeedback> getRecordsByIds(List<String> feedbackIds) {
        // Filter the appointment feedback objects by their IDs
        return appointmentFeedbackById.values().stream()
                .filter(feedback -> feedbackIds.contains(feedback.getFeedbackID()))
                .toList();
    }

    public List<AppointmentFeedback> getRecordsFromFeedbackAuthor(StaffAccount feedbackAuthor) {
        return appointmentFeedbackById.values().stream()
                .filter(feedback -> feedback.getFeedbackAuthor().equals(feedbackAuthor))
                .toList();
    }

    public List<AppointmentFeedback> getRecordsByAppointmentId(String appointmentId) {
        return appointmentFeedbackById.values().stream()
                .filter(feedback -> feedback.getAppointment().getAppointmentID().equals(appointmentId))
                .toList();
    }

    /**
     * Used for retrieving records from the text file
     * Here the line is split into columns using comma as the delimiter
     * Appointment Feedback CSV format (6 columns): feedbackID, feedbackCreationTime, feedbackUpdateTime, appointment,
     * feedbackAuthor, feedbackContent
     */
    @Override
    protected AppointmentFeedback parseRecord(String line) {

        // Get the appointment and customer account repository instance to fetch the respective object
        AppointmentRepository appointmentRepository = AppointmentRepository.getInstance();
        StaffAccountRepository staffAccountRepository = StaffAccountRepository.getInstance();

        String[] columns = line.split(",\\s*");

        return new AppointmentFeedback(
                columns[0],
                DataTypeConverter.convertStringToLocalDateTime(columns[1]),
                DataTypeConverter.convertStringToLocalDateTime(columns[2]),
                appointmentRepository.getRecordById(columns[3]),
                staffAccountRepository.getRecordById(columns[4]),
                columns[5]
        );
    }

    /**
     * Add a new/updated service activity into all lookup maps
     */
    @Override
    protected void addRecordIntoMap(AppointmentFeedback feedback) {

        // End the method early if the service activity is not provided
        if (feedback == null) {
            return;
        }

        appointmentFeedbackById.put(feedback.getFeedbackID(), feedback);
    }

    /**
     * Remove the old/deleted service activity from all lookup maps
     */
    @Override
    protected void removeRecordFromMap(AppointmentFeedback feedback) {

        // End the method early if the service activity is not provided
        if (feedback == null) {
            return;
        }

        appointmentFeedbackById.remove(feedback.getFeedbackID());
    }
}
