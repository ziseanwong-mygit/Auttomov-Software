package main.java.backend.service.vehiclepart;

import main.java.backend.model.VehiclePart;
import main.java.backend.repository.VehiclePartRepository;
import main.java.backend.service.RecordWriter;
import main.java.records.OperationResult;

public class VehiclePartDeletion implements RecordWriter {

    private final VehiclePartRepository vehiclePartRepository;
    private final String vehiclePartID;

    public VehiclePartDeletion(VehiclePartRepository vehiclePartRepository, String vehiclePartID) {
        this.vehiclePartRepository = vehiclePartRepository;
        this.vehiclePartID = vehiclePartID;
    }

    @Override
    public OperationResult execute() {

        // Get the vehicle part record by its ID
        VehiclePart existingVehiclePart = vehiclePartRepository.getRecordById(vehiclePartID);

        // If the vehicle part is not found, return an error message
        if (existingVehiclePart == null) {
            return OperationResult.failed("The vehicle part with ID: " + vehiclePartID +
                    " does not exist in the system.");
        }

        try {
            return vehiclePartRepository.deleteRecord(existingVehiclePart);
        } catch (Exception e) {
            System.out.println(e.getMessage()); // Print exception message for debugging
            return OperationResult.failed("An unexpected error occurred while deleting the " +
                    "vehicle part record. Please try again.");
        }
    }
}
