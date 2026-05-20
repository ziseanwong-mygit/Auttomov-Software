package main.java.backend.repository;

import main.java.backend.model.ServiceActivity;
import main.java.backend.repository.general.AbstractRepository;
import main.java.backend.util.DataTypeConverter;
import main.java.enums.system.TextFilePath;
import main.java.records.OperationResult;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class ServiceActivityRepository extends AbstractRepository<ServiceActivity> {

    private static ServiceActivityRepository instance;
    private final String filePath = TextFilePath.SERVICE_ACTIVITIES_FILE_PATH.getTextFilePath();

    private final Map<String, ServiceActivity> serviceActivityById = new HashMap<>();

    // Private constructor to prevent instantiations from outside
    // Pre-populate the records into maps when the instance is created
    private ServiceActivityRepository() {
        retrieveRecords();
    }

    // Global access point by creating instance for once only
    // So all classes can access the same instance for reading and writing files
    public static ServiceActivityRepository getInstance() {
        if (instance == null) {
            instance = new ServiceActivityRepository();
        }
        return instance;
    }

    @Override
    public String getFilePath() {
        return filePath;
    }

    @Override
    public OperationResult createRecord(ServiceActivity serviceActToCreate) {

        try (BufferedWriter bufferWrite = new BufferedWriter(new FileWriter(getFilePath(), true))) {

            // Write the new service activity record into the file
            bufferWrite.write(serviceActToCreate.toTextFile());
            bufferWrite.newLine();

            // Add the new service activity records into the maps to ensure the latest changes are reflected
            addRecordIntoMap(serviceActToCreate);

        } catch (IOException e) {
            System.out.println(e.getMessage()); // Print exception message for debugging
            return OperationResult.failed(
                    "Oops! There was an error writing the new service activity record. " +
                            "Please try again.");
        }

        return OperationResult.success(
                "The service activity record has been created successfully. " +
                        "Please check the record to verify its creation.");
    }

    @Override
    public OperationResult updateRecord(ServiceActivity serviceActToUpdate) {

        String serviceActID = serviceActToUpdate.getServiceActivityID();

        // Map the target service activity object by its ID
        Map<String, Function<String, Optional<String>>> modifier = Map.of(
                serviceActID, _ -> Optional.of(serviceActToUpdate.toTextFile())
        );

        // Rewrite the file to update the target record
        OperationResult result = rewriteFile(modifier);
        if (!result.isValid()) {
            return result;
        }

        // Replace the old record with the updated one to apply the latest changes
        removeRecordIntoMap(serviceActivityById.get(serviceActID));
        addRecordIntoMap(serviceActToUpdate);

        return OperationResult.success(
                "The service activity with ID " + serviceActID + " has been updated successfully. "
                        + "Please check the details to ensure the changes are applied.");
    }

    @Override
    public OperationResult deleteRecord(ServiceActivity serviceActToDelete) {

        String serviceActID = serviceActToDelete.getServiceActivityID();

        // Map the target service activity object by its ID
        Map<String, Function<String, Optional<String>>> modifier = Map.of(
                serviceActID, _ -> Optional.empty()
        );

        // Rewrite the file to delete the target record
        OperationResult result = rewriteFile(modifier);
        if (!result.isValid()) {
            return result;
        }

        // Remove the deleted record from the maps to apply the latest changes
        removeRecordIntoMap(serviceActToDelete);

        return OperationResult.success(
                "The service activity with ID " + serviceActID + " has been deleted successfully. "
                        + "Please check the records to verify its deletion.");
    }

    @Override
    public List<ServiceActivity> getAllRecords() {
        return new ArrayList<>(serviceActivityById.values());
    }

    @Override
    public ServiceActivity getRecordById(String serviceActId) {
        return serviceActivityById.get(serviceActId);
    }

    /**
     * Used for retrieving records from the text file
     * Here the line is split into columns using comma as the delimiter
     * Service Activity CSV format (4 columns): serviceActivityID, serviceActivityName, serviceActivityDescription,
     * serviceActivityPrice
     */
    protected ServiceActivity parseRecord(String line) {
        String[] columns = line.split(",\\s*");

        // If the description field is empty, set it to null, otherwise keep it as-is
        String description = columns[2].isBlank() ? null : columns[2];

        return new ServiceActivity(
                columns[0],
                columns[1],
                description,
                DataTypeConverter.convertStringToDouble(columns[3])
        );
    }

    /**
     * Add a new/updated service activity into all lookup maps
     */
    protected void addRecordIntoMap(ServiceActivity serviceActivity) {

        // End the method early if the service activity is not provided
        if (serviceActivity == null) {
            return;
        }

        serviceActivityById.put(serviceActivity.getServiceActivityID(), serviceActivity);
    }

    /**
     * Remove the old/deleted service activity from all lookup maps
     */
    protected void removeRecordIntoMap(ServiceActivity serviceActivity) {

        // End the method early if the service activity is not provided
        if (serviceActivity == null) {
            return;
        }

        serviceActivityById.remove(serviceActivity.getServiceActivityID());
    }
}
