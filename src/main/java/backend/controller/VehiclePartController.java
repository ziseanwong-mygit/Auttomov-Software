package main.java.backend.controller;

import main.java.backend.model.VehiclePart;
import main.java.backend.repository.impl.ServicePackageRepository;
import main.java.backend.repository.impl.VehiclePartRepository;
import main.java.backend.service.RecordReader;
import main.java.backend.service.RecordWriter;
import main.java.backend.service.vehiclepart.VehiclePartCreation;
import main.java.backend.service.vehiclepart.VehiclePartDeletion;
import main.java.backend.service.vehiclepart.VehiclePartRetrieval;
import main.java.backend.service.vehiclepart.VehiclePartUpdate;
import main.java.backend.util.DataValidator;
import main.java.backend.util.NullValidator;
import main.java.dtos.VehiclePartDTO;
import main.java.enums.system.DataFieldName;
import main.java.enums.system.OperationType;
import main.java.records.OperationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VehiclePartController {

    // Singleton instances of the repositories
    private final VehiclePartRepository vehiclePartRepo = VehiclePartRepository.getInstance();
    private final ServicePackageRepository servicePackageRepo = ServicePackageRepository.getInstance();

    public OperationResult createVehiclePart(VehiclePartDTO vehiclePartDTO) {

        // Check if there are any null or empty values in the list
        OperationResult nullValidation = validateNullValues(vehiclePartDTO, OperationType.CREATE);
        if (!nullValidation.isValid()) {
            return OperationResult.failed("All required fields must be filled in. Please try again.");
        }

        // Validate price logic
        OperationResult priceValidation = DataValidator.validatePrice(vehiclePartDTO.getVehiclePartPrice());
        if (!priceValidation.isValid()) {
            return priceValidation;
        }

        // Validate quantity logic
        OperationResult quantityValidation = DataValidator.validateQuantity(vehiclePartDTO.getVehiclePartQuantity());
        if (!quantityValidation.isValid()) {
            return quantityValidation;
        }

        // Begin creating vehicle part record procedure in service class
        RecordWriter createVehiclePart = new VehiclePartCreation(vehiclePartRepo, vehiclePartDTO);
        return createVehiclePart.execute();
    }

    public OperationResult updateVehiclePart(VehiclePartDTO vehiclePartDTO) {

        // Check if there are any null or empty values in the list
        OperationResult nullValidation = validateNullValues(vehiclePartDTO, OperationType.UPDATE);
        if (!nullValidation.isValid()) {
            return OperationResult.failed("All required fields must be filled in. Please try again.");
        }

        // Check if the vehicle part ID is in UUID format
        if (!DataValidator.validateRecordId(vehiclePartDTO.getVehiclePartID()).isValid()) {
            return OperationResult.failed(DataFieldName.RECORD_ID, "The vehicle part ID: " +
                    vehiclePartDTO.getVehiclePartID() + " is not an UUID. Please try again.");
        }

        // Validate price logic
        OperationResult priceValidation = DataValidator.validatePrice(vehiclePartDTO.getVehiclePartPrice());
        if (!priceValidation.isValid()) {
            return priceValidation;
        }

        // Validate quantity logic
        OperationResult quantityValidation = DataValidator.validateQuantity(vehiclePartDTO.getVehiclePartQuantity());
        if (!quantityValidation.isValid()) {
            return quantityValidation;
        }

        // Begin updating vehicle part record procedure in service class
        RecordWriter updateVehiclePart = new VehiclePartUpdate(vehiclePartRepo, servicePackageRepo, vehiclePartDTO);
        return updateVehiclePart.execute();
    }

    public OperationResult deleteVehiclePart(String vehiclePartId) {

        // Check if the inputs are null or empty
        if (vehiclePartId == null || vehiclePartId.isBlank()) {
            return OperationResult.failed("The vehicle part ID must be provided to " +
                    "delete a record. Please try again.");
        }

        // Check if the input is a valid UUID
        if (!DataValidator.validateRecordId(vehiclePartId).isValid()) {
            return OperationResult.failed("The vehicle part ID: " + vehiclePartId +
                    " is not an UUID. Please try again.");
        }

        // Begin deleting vehicle part record procedure in service class
        RecordWriter deleteVehiclePart = new VehiclePartDeletion(vehiclePartRepo, servicePackageRepo, vehiclePartId);
        return deleteVehiclePart.execute();
    }

    /**
     * Optional class is used to determine whether the records are present or not
     * This is for frontend logic in displaying the result message for UI
     */
    public Optional<List<VehiclePart>> retrieveAllVehicleParts() {

        // Begin retrieving all vehicle part record procedures in service class
        RecordReader<VehiclePart> retrieveAllVehicleParts = new VehiclePartRetrieval(vehiclePartRepo);
        return retrieveAllVehicleParts.getAllRecords();
    }

    public Optional<VehiclePart> retrieveVehiclePartById(String vehiclePartId) {

        // Begin retrieving vehicle part record by ID procedure in service class
        RecordReader<VehiclePart> retrieveVehiclePartById = new VehiclePartRetrieval(vehiclePartRepo);
        return retrieveVehiclePartById.getRecordById(vehiclePartId);
    }

    public Optional<List<VehiclePart>> retrieveVehiclePartsByIds(List<String> vehiclePartIds) {

        // Begin retrieving multiple vehicle part records by IDs procedure in service class
        RecordReader<VehiclePart> retrieveServiceActsByIds = new VehiclePartRetrieval(vehiclePartRepo);
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

        if (!DataValidator.validateRecordId(vehiclePartId).isValid()) {
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
            if (!DataValidator.validateRecordId(vehiclePartId).isValid()) {
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
