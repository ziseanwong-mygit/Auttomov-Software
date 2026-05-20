package main.java.backend.service.vehiclepart;

import main.java.backend.model.VehiclePart;
import main.java.backend.repository.VehiclePartRepository;
import main.java.backend.service.RecordReader;
import main.java.records.OperationResult;

import java.util.List;

public class VehiclePartRetrieval implements RecordReader {

    private final VehiclePartRepository vehiclePartRepository;

    public VehiclePartRetrieval(VehiclePartRepository vehiclePartRepository) {
        this.vehiclePartRepository = vehiclePartRepository;
    }

    /**
     * Read and return all vehicle parts from the repository
     */
    @Override
    public List<VehiclePart> getAllRecords() {
        return vehiclePartRepository.getAllRecords();
    }

    /**
     * Read and return a single vehicle part based on the given keyword like vehiclePartID
     */
    @Override
    public OperationResult getRecordById(String vehiclePartId) {

        // Find the vehicle part by its ID
        VehiclePart existingVehiclePart = vehiclePartRepository.getRecordById(vehiclePartId);

        if (existingVehiclePart == null) {
            return OperationResult.failed("The vehicle part with ID: " + vehiclePartId +
                    " does not exist in the system.");
        }

        return OperationResult.success("The vehicle part with ID: " + vehiclePartId +
                "has been found.", existingVehiclePart);
    }
}
