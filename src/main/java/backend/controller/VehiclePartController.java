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
import java.util.Optional;

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

    /**
     * Optional class is used to determine whether the records are present or not
     * This is for frontend logic in displaying the result message for UI
     */
    public Optional<List<VehiclePart>> retrieveAllVehicleParts() {

        // Instantiate the repository and service for retrieving records
        VehiclePartRepository vehiclePartRepo = VehiclePartRepository.getInstance();
        RecordReader<VehiclePart> retrieveAllVehicleParts = new VehiclePartRetrieval(vehiclePartRepo);

        // Retrieve all the records from the repository
        return retrieveAllVehicleParts.getAllRecords();
    }

    public Optional<VehiclePart> retrieveVehiclePartById(String vehiclePartId) {

        // Instantiate the repository and service for retrieving records
        VehiclePartRepository vehiclePartRepo = VehiclePartRepository.getInstance();
        RecordReader<VehiclePart> retrieveVehiclePartById = new VehiclePartRetrieval(vehiclePartRepo);

        // Retrieve the record from the repository based on the ID
        return retrieveVehiclePartById.getRecordById(vehiclePartId);
    }

    public Optional<List<VehiclePart>> retrieveServiceActivitiesByIds(List<String> vehiclePartIds) {

        // Instantiate the repository and service for retrieving records
        VehiclePartRepository vehiclePartRepo = VehiclePartRepository.getInstance();
        RecordReader<VehiclePart> retrieveServiceActsByIds = new VehiclePartRetrieval(vehiclePartRepo);

        // Retrieve the records from the repository based on the ID list
        return retrieveServiceActsByIds.getRecordsByIds(vehiclePartIds);
    }

    /**
     * Validate the format and length of vehicle part ID
     * This validation is done before record retrieval, so the frontend can first display the validation result
     */
    public OperationResult validateVehiclePartId(String vehiclePartId) {

        // Check if the vehicle part ID and its type are null or empty
        if (vehiclePartId == null || vehiclePartId.isBlank()) {
            return OperationResult.failed("The vehicle part ID must be provided to validate. " +
                    "Please try again.");
        }

        if (!DataValidation.validateRecordId(vehiclePartId).isValid()) {
            return OperationResult.failed("The vehicle part ID: " + vehiclePartId +
                    " is not an UUID. Please try again.");
        }

        return OperationResult.success();
    }

    public OperationResult validateVehiclePartIds(List<String> vehiclePartIds) {

        // Check if the ID list and its type are null or empty
        if (vehiclePartIds == null || vehiclePartIds.isEmpty()) {
            return OperationResult.failed("The vehicle part IDs must be provided to validate. " +
                    "Please try again.");
        }

        // Check if any of the vehicle part IDs are null or empty
        for (String vehiclePartId : vehiclePartIds) {
            if (vehiclePartId == null || vehiclePartId.isBlank() ) {
                return OperationResult.failed("One of the vehicle part IDs is null or empty. Please try again.");
            }
        }

        // Check if any of the vehicle part IDs are invalid
        for (String vehiclePartId : vehiclePartIds) {
            if (!DataValidation.validateRecordId(vehiclePartId).isValid()) {
                return OperationResult.failed("The vehicle part ID: " + vehiclePartId +
                        " is not an UUID. Please try again.");
            }
        }

        return OperationResult.success();
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
