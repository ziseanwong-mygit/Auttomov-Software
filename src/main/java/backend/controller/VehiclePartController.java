package main.java.backend.controller;

import main.java.backend.model.VehiclePart;
import main.java.backend.repository.VehiclePartRepository;
import main.java.backend.service.RecordReader;
import main.java.backend.service.RecordWriter;
import main.java.backend.service.vehiclepart.VehiclePartCreation;
import main.java.backend.service.vehiclepart.VehiclePartDeletion;
import main.java.backend.service.vehiclepart.VehiclePartRetrieval;
import main.java.backend.service.vehiclepart.VehiclePartUpdate;
import main.java.backend.util.DataValidation;
import main.java.backend.util.NullValidator;
import main.java.dtos.VehiclePartDTO;
import main.java.enums.system.OperationType;
import main.java.records.OperationResult;

import java.util.ArrayList;
import java.util.List;

public class VehiclePartController {

    public OperationResult createVehiclePart(VehiclePartDTO vehiclePartDTO) {

        // Check if there are any null or empty values in the list
        OperationResult nullValidation = validateNullValues(vehiclePartDTO, OperationType.CREATE);
        if (!nullValidation.isValid()) {
            return OperationResult.failed("All required fields must be filled in. Please try again.");
        }

        // Validate price logic
        OperationResult priceValidation = DataValidation.validatePrice(vehiclePartDTO.getVehiclePartPrice());
        if (!priceValidation.isValid()) {
            return priceValidation;
        }

        // Validate quantity logic
        OperationResult quantityValidation = DataValidation.validateQuantity(vehiclePartDTO.getVehiclePartQuantity());
        if (!quantityValidation.isValid()) {
            return quantityValidation;
        }

        // Instantiate the repository and service for creating records
        VehiclePartRepository vehiclePartRepo = VehiclePartRepository.getInstance();
        RecordWriter createVehiclePart = new VehiclePartCreation(vehiclePartRepo, vehiclePartDTO);

        // Begin creating vehicle part record procedure in service class
        return createVehiclePart.execute();
    }

    public OperationResult updateVehiclePart(VehiclePartDTO vehiclePartDTO) {

        // Check if there are any null or empty values in the list
        OperationResult nullValidation = validateNullValues(vehiclePartDTO, OperationType.UPDATE);
        if (!nullValidation.isValid()) {
            return OperationResult.failed("All required fields must be filled in. Please try again.");
        }

        // Validate price logic
        OperationResult priceValidation = DataValidation.validatePrice(vehiclePartDTO.getVehiclePartPrice());
        if (!priceValidation.isValid()) {
            return priceValidation;
        }

        // Validate quantity logic
        OperationResult quantityValidation = DataValidation.validateQuantity(vehiclePartDTO.getVehiclePartQuantity());
        if (!quantityValidation.isValid()) {
            return quantityValidation;
        }

        // Instantiate the repository and service for updating records
        VehiclePartRepository vehiclePartRepo = VehiclePartRepository.getInstance();
        RecordWriter updateVehiclePart = new VehiclePartUpdate(vehiclePartRepo, vehiclePartDTO);

        // Begin updating vehicle part record procedure in service class
        return updateVehiclePart.execute();
    }

    public OperationResult deleteVehiclePart(String vehiclePartId) {

        // Check if the inputs are null or empty
        if (vehiclePartId == null || vehiclePartId.isBlank()) {
            return OperationResult.failed("The vehicle part ID must be provided to " +
                    "delete a record. Please try again.");
        }

        // Instantiate the repository and service for deleting records
        VehiclePartRepository vehiclePartRepo = VehiclePartRepository.getInstance();
        RecordWriter deleteVehiclePart = new VehiclePartDeletion(vehiclePartRepo, vehiclePartId);

        return deleteVehiclePart.execute();
    }

    public List<VehiclePart> retrieveAllVehicleParts() {

        // Instantiate the repository and service for retrieving records
        VehiclePartRepository vehiclePartRepo = VehiclePartRepository.getInstance();
        RecordReader retrieveAllVehicleParts = new VehiclePartRetrieval(vehiclePartRepo);

        // Retrieve all the records from the repository
        List<?> unknownList = retrieveAllVehicleParts.getAllRecords();

        // Since the retrieved list is a list of unknown type, cast each object to the desired type explicitly
        return unknownList.stream()
                .map(obj -> (VehiclePart) obj)
                .toList();
    }

    public OperationResult retrieveVehiclePartById(String vehiclePartId) {

        // Check if the inputs are null or empty
        if (vehiclePartId == null || vehiclePartId.isBlank()) {
            return OperationResult.failed("All required fields must be filled in. Please try again.");
        }

        // Instantiate the repository and service for retrieving records
        VehiclePartRepository vehiclePartRepo = VehiclePartRepository.getInstance();
        RecordReader retrieveVehiclePartById = new VehiclePartRetrieval(vehiclePartRepo);

        return retrieveVehiclePartById.getRecordById(vehiclePartId);
    }

    /**
     * Check if there are any null or empty values of required fields in the given DTO
     * Used for creating and updating records
     */
    private OperationResult validateNullValues(VehiclePartDTO vehiclePartDTO, OperationType operationType) {
        List<Object> vehiclePartValues = new ArrayList<>();

        // If the operation is updating record, add the ID to the list for null validation
        if (operationType == OperationType.UPDATE) {
            vehiclePartValues.add(vehiclePartDTO.getVehiclePartID());
        }

        // These fields must be checked for null values
        vehiclePartValues.add(vehiclePartDTO.getVehiclePartName());
        vehiclePartValues.add(vehiclePartDTO.getVehiclePartType());
        vehiclePartValues.add(vehiclePartDTO.getVehiclePartBrand());
        vehiclePartValues.add(vehiclePartDTO.getVehiclePartSpec());
        vehiclePartValues.add(vehiclePartDTO.getVehiclePartPrice());
        vehiclePartValues.add(vehiclePartDTO.getVehiclePartQuantity());

        return NullValidator.isAllValuesNull(vehiclePartValues);
    }
}
