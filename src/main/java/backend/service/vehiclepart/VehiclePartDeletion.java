package main.java.backend.service.vehiclepart;

import main.java.backend.model.VehiclePart;
import main.java.backend.repository.impl.VehiclePartRepository;
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

        // Check if the vehicle part exists with the given ID
        VehiclePart existingVehiclePart = vehiclePartRepository.getRecordById(vehiclePartID);
        if (existingVehiclePart == null) {
            return OperationResult.failed("The vehicle part with ID: " + vehiclePartID +
                    " does not exist in the system.");
        }

        // Begin deleting vehicle part record in the repository
        return vehiclePartRepository.deleteRecord(existingVehiclePart);
    }
}
