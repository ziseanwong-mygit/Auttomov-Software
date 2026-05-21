package main.java.backend.controller;

import main.java.backend.model.ServiceActivity;
import main.java.backend.model.ServicePackage;
import main.java.backend.model.VehiclePart;
import main.java.backend.repository.impl.ServiceActivityRepository;
import main.java.backend.repository.impl.ServicePackageRepository;
import main.java.backend.repository.impl.VehiclePartRepository;
import main.java.backend.service.RecordReader;
import main.java.backend.service.RecordWriter;
import main.java.backend.service.servicepackage.ServicePackageCreation;
import main.java.backend.service.servicepackage.ServicePackageDeletion;
import main.java.backend.service.servicepackage.ServicePackageRetrieval;
import main.java.backend.service.servicepackage.ServicePackageUpdate;
import main.java.backend.util.DataValidator;
import main.java.backend.util.NullValidator;
import main.java.dtos.ServicePackageDTO;
import main.java.enums.system.DataFieldName;
import main.java.enums.system.OperationType;
import main.java.records.OperationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServicePackageController {

    // Singleton instances of the repositories
    private final ServicePackageRepository servicePackageRepo = ServicePackageRepository.getInstance();
    private final ServiceActivityRepository serviceActivityRepo = ServiceActivityRepository.getInstance();
    private final VehiclePartRepository vehiclePartRepo = VehiclePartRepository.getInstance();

    public OperationResult createServicePackage(ServicePackageDTO servicePackageDTO) {

        // Check if there are any null or empty values in the list
        OperationResult nullValidation = validateNullValues(servicePackageDTO, OperationType.CREATE);
        if (!nullValidation.isValid()) {
            return nullValidation;
        }

        // Validate price logic
        OperationResult priceValidation = DataValidator.validatePrice(servicePackageDTO.getPackageBasePrice());
        if (!priceValidation.isValid()) {
            return priceValidation;
        }

        // Validate service activity and vehicle part IDs
        OperationResult idVerification = validateReferencedIds(servicePackageDTO);
        if (!idVerification.isValid()) {
            return idVerification;
        }

        // Fetch all the service activities and vehicle parts after validation process
        List<ServiceActivity> serviceActivityList =
                serviceActivityRepo.getRecordsByIds(servicePackageDTO.getServiceActivityIDs());
        List<VehiclePart> vehiclePartList =
                vehiclePartRepo.getRecordsByIds(servicePackageDTO.getVehiclePartIDs());

        // Begin creating service package record procedure in service class
        RecordWriter createServicePackage = new ServicePackageCreation(servicePackageRepo, servicePackageDTO,
                serviceActivityList, vehiclePartList);
        return createServicePackage.execute();
    }

    public OperationResult updateServicePackage(ServicePackageDTO servicePackageDTO) {

        // Check if there are any null or empty values in the list
        OperationResult nullValidation = validateNullValues(servicePackageDTO, OperationType.UPDATE);
        if (!nullValidation.isValid()) {
            return nullValidation;
        }

        // Check if the service package ID is in UUID format
        if (!DataValidator.validateRecordId(servicePackageDTO.getServicePackageID()).isValid()) {
            return OperationResult.failed(DataFieldName.RECORD_ID, "The service package ID: " +
                    servicePackageDTO.getServicePackageID() + " is not an UUID. Please try again.");
        }

        // Validate price logic
        OperationResult priceValidation = DataValidator.validatePrice(servicePackageDTO.getPackageBasePrice());
        if (!priceValidation.isValid()) {
            return priceValidation;
        }

        // Validate service activity and vehicle part IDs
        OperationResult referenceIdValidation = validateReferencedIds(servicePackageDTO);
        if (!referenceIdValidation.isValid()) {
            return referenceIdValidation;
        }

        // Fetch all the service activities and vehicle parts after ID verification
        List<ServiceActivity> serviceActivityList =
                serviceActivityRepo.getRecordsByIds(servicePackageDTO.getServiceActivityIDs());
        List<VehiclePart> vehiclePartList =
                vehiclePartRepo.getRecordsByIds(servicePackageDTO.getVehiclePartIDs());

        // Begin updating service package record procedure in service class
        RecordWriter updateServicePackage = new ServicePackageUpdate(servicePackageRepo, servicePackageDTO,
                serviceActivityList, vehiclePartList);
        return updateServicePackage.execute();
    }

    public OperationResult deleteServicePackage(String servicePkgId) {

        // Check if the inputs are null or empty
        if (servicePkgId == null || servicePkgId.isBlank()) {
            return OperationResult.failed("The service package ID must be provided to " +
                    "delete a record. Please try again.");
        }

        // Check if the input is a valid UUID
        if (!DataValidator.validateRecordId(servicePkgId).isValid()) {
            return OperationResult.failed("The service package ID: " + servicePkgId +
                    " is not an UUID. Please try again.");
        }

        // Begin deleting service package record procedure in service class
        RecordWriter deleteServicePackage = new ServicePackageDeletion(servicePackageRepo, servicePkgId);
        return deleteServicePackage.execute();
    }

    /**
     * Optional class is used to determine whether the records are present or not
     * This is for frontend logic in displaying the result message for UI
     */
    public Optional<List<ServicePackage>> retrieveAllServicePackages() {

        // Begin retrieving all service package record procedures in service class
        RecordReader<ServicePackage> retrieveAllServicePackages = new ServicePackageRetrieval(servicePackageRepo);
        return retrieveAllServicePackages.getAllRecords();
    }

    public Optional<ServicePackage> retrieveServicePackageById(String servicePkgId) {

        // Begin retrieving service package record by ID procedure in service class
        RecordReader<ServicePackage> retrieveServicePackageById = new ServicePackageRetrieval(servicePackageRepo);
        return retrieveServicePackageById.getRecordById(servicePkgId);
    }

    public Optional<List<ServicePackage>> retrieveServicePackagesByIds(List<String> servicePkgIds) {

        // Begin retrieving multiple service package records by IDs procedure in service class
        RecordReader<ServicePackage> retrieveServiceActsByIds = new ServicePackageRetrieval(servicePackageRepo);
        return retrieveServiceActsByIds.getRecordsByIds(servicePkgIds);
    }

    /**
     * Validate the format and length of service package ID
     * This validation is done before record retrieval, so the frontend can first display the validation result
     */
    public OperationResult validateServicePackageId(String servicePkgId) {

        // Check if the service package ID and its type are null or empty
        if (servicePkgId == null || servicePkgId.isBlank()) {
            return OperationResult.failed("The service package ID must be provided to retrieve records. " +
                    "Please try again.");
        }

        if (!DataValidator.validateRecordId(servicePkgId).isValid()) {
            return OperationResult.failed("The service package ID: " + servicePkgId +
                    " is not an UUID. Please try again.");
        }

        return OperationResult.success();
    }

    public OperationResult validateServicePackageIds(List<String> servicePkgIds) {

        // Check if the ID list and its type are null or empty
        if (servicePkgIds == null || servicePkgIds.isEmpty()) {
            return OperationResult.failed("The service package IDs must be provided to retrieve records. " +
                    "Please try again.");
        }

        // Check if any of the service package IDs are null or empty
        for (String servicePkgId : servicePkgIds) {
            if (servicePkgId == null || servicePkgId.isBlank() ) {
                return OperationResult.failed("One of the service package IDs is null or empty. Please try again.");
            }
        }

        // Check if any of the service package IDs are invalid
        for (String servicePkgId : servicePkgIds) {
            if (!DataValidator.validateRecordId(servicePkgId).isValid()) {
                return OperationResult.failed("The service package ID: " + servicePkgId +
                        " is not an UUID. Please try again.");
            }
        }

        return OperationResult.success();
    }

    /**
     * Check if there are any null or empty values of required fields in the given DTO,
     * excluding service activity and vehicle part IDs.
     * Used for creating and updating records
     */
    private OperationResult validateNullValues(ServicePackageDTO servicePackageDTO, OperationType operationType) {
        List<Object> servicePkgValues = new ArrayList<>();

        if (operationType == OperationType.UPDATE) {
            servicePkgValues.add(servicePackageDTO.getServicePackageID());
        }

        servicePkgValues.add(servicePackageDTO.getServicePackageName());
        servicePkgValues.add(servicePackageDTO.getServicePackageType());
        servicePkgValues.add(servicePackageDTO.getPackageBasePrice());

        // Check if all values are null and get the boolean result
        boolean validationResult =  NullValidator.isAllValuesNull(servicePkgValues).isValid();

        // This conditional statement is used to customize the error message
        // if there is a null value in the servicePkgValues
        if (!validationResult) {
            return OperationResult.failed("All required fields must be filled in. Please try again.");
        }

        // If all values are not null, return success
        return OperationResult.success();
    }

    /**
     * Full validation for service activity and vehicle part IDs in checking nullity, format, and linking records
     */
    private OperationResult validateReferencedIds(ServicePackageDTO servicePackageDTO) {

        // 1. Check if both service activity and vehicle part lists are empty; if so, return an error message
        if (servicePackageDTO.getServiceActivityIDs() == null && servicePackageDTO.getVehiclePartIDs() == null ) {
            return OperationResult.failed("Both service activity and vehicle part list cannot be empty. " +
                    "Please fill in at least one of them.");
        }

        /*
            For each loop:
            2. Check if one of the IDs is null or empty; if so, return an error message
            3. Then check if the ID is a valid UUID; if not, return an error message
            4. Finally, check if the record exists with the ID
         */
        for (String activityId : servicePackageDTO.getServiceActivityIDs()) {

            if (activityId == null || activityId.isBlank() ) {
                return OperationResult.failed(DataFieldName.SERVICE_ACTIVITY,
                        "One of the service activity IDs is null or empty. Please try again.");

            }

            if (!DataValidator.validateRecordId(activityId).isValid()) {
                return OperationResult.failed(DataFieldName.SERVICE_ACTIVITY,
                        "The service activity ID: " + activityId + " is not an UUID. Please try again.");
            }

            if (serviceActivityRepo.getRecordById(activityId) == null) {
                return OperationResult.failed(DataFieldName.SERVICE_ACTIVITY,
                        "The service activity with ID " + activityId + " does not exist in the records.");
            }

        }
        for (String partId : servicePackageDTO.getVehiclePartIDs()) {

            if (partId == null || partId.isBlank() ) {
                return OperationResult.failed("One of the vehicle part IDs is null or empty. Please try again.");

            }

            if (!DataValidator.validateRecordId(partId).isValid()) {
                return OperationResult.failed(DataFieldName.VEHICLE_PART,
                        "The vehicle part ID: " + partId + " is not an UUID. Please try again.");
            }

            if (vehiclePartRepo.getRecordById(partId) == null) {
                return OperationResult.failed(DataFieldName.VEHICLE_PART,
                        "The vehicle part with ID " + partId + " does not exist in the records.");
            }
        }

        // If all validations pass, return success
        return OperationResult.success();
    }
}
