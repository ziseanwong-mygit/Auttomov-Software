package main.java.backend.repository.impl;

import main.java.backend.model.VehiclePart;
import main.java.backend.repository.AbstractRepository;
import main.java.backend.util.CSVFormatter;
import main.java.backend.util.DataTypeConverter;
import main.java.enums.system.TextFilePath;
import main.java.records.OperationResult;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class VehiclePartRepository extends AbstractRepository<VehiclePart> {

    private static VehiclePartRepository instance;
    private final String filePath = TextFilePath.VEHICLE_PARTS_FILE_PATH.getTextFilePath();

    private final Map<String, VehiclePart> vehiclePartById = new HashMap<>();
    
    // Private constructor to prevent instantiations from outside
    // Pre-populate the records into maps when the instance is created
    private VehiclePartRepository() {
        retrieveRecords();
    }

    // Global access point by creating instance for once only
    // So all classes can access the same instance for reading and writing files
    public static VehiclePartRepository getInstance() {
        if (instance == null) {
            instance = new VehiclePartRepository();
        }
        return instance;
    }

    @Override
    public String getFilePath() {
        return filePath;
    }

    @Override
    public OperationResult createRecord(VehiclePart vehPartToCreate) {

        try (BufferedWriter bufferWrite = new BufferedWriter(new FileWriter(getFilePath(), true))) {

            // Write the new vehicle part record into the file
            bufferWrite.write(CSVFormatter.toCsvFormat(vehPartToCreate));
            bufferWrite.newLine();

            // Add the new vehicle part records into the maps to ensure the latest changes are reflected
            addRecordIntoMap(vehPartToCreate);

        } catch (IOException e) {
            System.out.println(e.getMessage()); // Print exception message for debugging
            return OperationResult.failed(
                    "Oops! There was an error writing the new vehicle part record. " +
                            "Please try again.");
        }

        return OperationResult.success(
                "The vehicle part record has been created successfully. " +
                        "Please check the record to verify its creation.");
    }

    @Override
    public OperationResult updateRecord(VehiclePart vehPartToUpdate) {

        String vehiclePartID = vehPartToUpdate.getVehiclePartID();

        // Map the target vehicle part object by its ID
        Map<String, Function<String, Optional<String>>> modifier = Map.of(
                vehiclePartID, _ -> Optional.of(CSVFormatter.toCsvFormat(vehPartToUpdate))
        );

        // Rewrite the file to update the target record
        OperationResult result = rewriteFile(modifier);
        if (!result.isValid()) {
            return result;
        }

        // Replace the old record with the updated one to apply the latest changes
        removeRecordIntoMap(vehiclePartById.get(vehiclePartID));
        addRecordIntoMap(vehPartToUpdate);

        return OperationResult.success(
                "The vehicle part with ID " + vehiclePartID + " has been updated successfully. "
                        + "Please check the details to ensure the changes are applied.");
    }

    @Override
    public OperationResult deleteRecord(VehiclePart vehPartToDelete) {

        String vehiclePartID = vehPartToDelete.getVehiclePartID();

        // Map the target vehicle part object by its ID
        Map<String, Function<String, Optional<String>>> modifier = Map.of(
                vehiclePartID, _ -> Optional.empty()
        );

        // Rewrite the file to delete the target record
        OperationResult result = rewriteFile(modifier);
        if (!result.isValid()) {
            return result;
        }

        // Remove the deleted record from the maps to apply the latest changes
        removeRecordIntoMap(vehPartToDelete);

        return OperationResult.success(
                "The vehicle part with ID " + vehiclePartID + " has been deleted successfully. "
                        + "Please check the records to verify its deletion.");
    }

    @Override
    public List<VehiclePart> getAllRecords() {
        return new ArrayList<>(vehiclePartById.values());
    }

    @Override
    public VehiclePart getRecordById(String vehiclePartID) {
        return vehiclePartById.get(vehiclePartID);
    }

    @Override
    public List<VehiclePart> getRecordsByIds(List<String> vehiclePartIds) {
        // Filter the vehicle part objects by their IDs
        return vehiclePartById.values().stream()
                .filter(serviceAct -> vehiclePartIds.contains(serviceAct.getVehiclePartID()))
                .toList();
    }
    
    /**
     * Used for retrieving records from the text file
     * Here the line is split into columns using comma as the delimiter
     * Vehicle Part CSV format (8 columns): vehiclePartID, vehiclePartName, vehiclePartDescription, vehiclePartType,
     * vehiclePartBrand, vehiclePartSpec, vehiclePartPrice, vehiclePartQuantity
     */
    @Override
    protected VehiclePart parseRecord(String line) {
        String[] columns = line.split(",\\s*");

        return new VehiclePart(
                columns[0],
                columns[1],
                columns[2].isBlank() ? null : columns[2], // If description is empty, set it to null
                columns[3],
                columns[4],
                columns[5],
                DataTypeConverter.convertStringToDouble(columns[6]),
                DataTypeConverter.convertStringToInt(columns[7])
        );
    }

    /**
     * Add a new/updated vehicle part into all lookup maps
     */
    @Override
    protected void addRecordIntoMap(VehiclePart vehiclePart) {

        // End the method early if the vehicle part is not provided
        if (vehiclePart == null) {
            return;
        }

        vehiclePartById.put(vehiclePart.getVehiclePartID(), vehiclePart);
    }

    /**
     * Remove the old/deleted vehicle part from all lookup maps
     */
    @Override
    protected void removeRecordIntoMap(VehiclePart vehiclePart) {

        // End the method early if the vehicle part is not provided
        if (vehiclePart == null) {
            return;
        }

        vehiclePartById.remove(vehiclePart.getVehiclePartID());
    }
}
