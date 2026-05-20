package main.java.backend.service.vehiclepart;

import main.java.backend.model.VehiclePart;
import main.java.backend.repository.VehiclePartRepository;
import main.java.backend.service.RecordReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VehiclePartRetrieval implements RecordReader<VehiclePart> {

    private final VehiclePartRepository vehiclePartRepository;

    public VehiclePartRetrieval(VehiclePartRepository vehiclePartRepository) {
        this.vehiclePartRepository = vehiclePartRepository;
    }

    /**
     * Read and return all vehicle parts from the repository
     */
    @Override
    public Optional<List<VehiclePart>> getAllRecords() {

        List<VehiclePart> existingVehicleParts = new ArrayList<>(vehiclePartRepository.getAllRecords());

        if (existingVehicleParts.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(existingVehicleParts);
    }

    /**
     * Read and return a single vehicle part based on the given ID
     */
    @Override
    public Optional<VehiclePart> getRecordById(String vehiclePartId) {

        VehiclePart existingVehiclePart = vehiclePartRepository.getRecordById(vehiclePartId);

        if (existingVehiclePart == null) {
            return Optional.empty();
        }

        return Optional.of(existingVehiclePart);
    }

    /**
     * Read and return multiple vehicle parts based on the given IDs
     */
    @Override
    public Optional<List<VehiclePart>> getRecordsByIds(List<String> vehiclePartIds) {

        List<VehiclePart> existingVehicleParts = vehiclePartRepository.getRecordsByIds(vehiclePartIds);

        if (existingVehicleParts.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(existingVehicleParts);
    }
}
