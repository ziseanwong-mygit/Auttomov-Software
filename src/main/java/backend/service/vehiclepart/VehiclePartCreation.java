package main.java.backend.service.vehiclepart;

import main.java.backend.model.VehiclePart;
import main.java.backend.repository.impl.VehiclePartRepository;
import main.java.backend.service.RecordWriter;
import main.java.dtos.VehiclePartDTO;
import main.java.records.OperationResult;

import java.util.UUID;

public class VehiclePartCreation implements RecordWriter {

    private final VehiclePartRepository vehiclePartRepository;
    private final VehiclePartDTO vehiclePartDTO;

    public VehiclePartCreation(VehiclePartRepository vehiclePartRepository, VehiclePartDTO vehiclePartDTO) {
        this.vehiclePartRepository = vehiclePartRepository;
        this.vehiclePartDTO = vehiclePartDTO;
    }

    @Override
    public OperationResult execute() {

        // Create a new vehicle part object
        VehiclePart newVehiclePart = populateVehiclePartDetails();

        // Save the vehicle part into the file
        return vehiclePartRepository.createRecord(newVehiclePart);
    }

    /**
     * Create a new VehiclePart object for record creation
     */
    private VehiclePart populateVehiclePartDetails() {

        // Generate new vehicle part ID
        String vehiclePartId = UUID.randomUUID().toString();

        // Extract the vehicle part details from the DTO
        String vehiclePartName = vehiclePartDTO.getVehiclePartName();
        String vehiclePartDescription = vehiclePartDTO.getVehiclePartDescription();
        String vehiclePartType = vehiclePartDTO.getVehiclePartType();
        String vehiclePartBrand = vehiclePartDTO.getVehiclePartBrand();
        String vehiclePartSpec = vehiclePartDTO.getVehiclePartSpec();
        double vehiclePartPrice = vehiclePartDTO.getVehiclePartPrice();
        int vehiclePartQuantity = vehiclePartDTO.getVehiclePartQuantity();

        // Return the new object here
        return new VehiclePart(vehiclePartId, vehiclePartName, vehiclePartDescription, vehiclePartType,
                vehiclePartBrand, vehiclePartSpec, vehiclePartPrice, vehiclePartQuantity);
    }
}
