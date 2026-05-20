package main.java.backend.controller;

import main.java.backend.model.ServicePackage;
import main.java.backend.repository.ServiceActivityRepository;
import main.java.backend.repository.ServicePackageRepository;
import main.java.backend.repository.VehiclePartRepository;
import main.java.backend.service.RecordReader;
import main.java.backend.service.RecordWriter;
import main.java.backend.service.servicepackage.ServicePackageCreation;
import main.java.backend.service.servicepackage.ServicePackageDeletion;
import main.java.backend.service.servicepackage.ServicePackageRetrieval;
import main.java.backend.service.servicepackage.ServicePackageUpdate;
import main.java.backend.util.DataValidation;
import main.java.backend.util.NullValidator;
import main.java.dtos.ServicePackageDTO;
import main.java.enums.system.OperationType;
import main.java.records.OperationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServicePackageController {

    public OperationResult createServicePackage(ServicePackageDTO servicePackageDTO) {

        // Check if there are any null or empty values in the list
        OperationResult nullValidation = validateNullValues(servicePackageDTO, OperationType.CREATE);
        if (!nullValidation.isValid()) {
            return nullValidation;
        }

        // Validate price logic
        OperationResult priceValidation = DataValidation.validatePrice(servicePackageDTO.getPackageBasePrice());
        if (!priceValidation.isValid()) {
            return priceValidation;
        }

        /*
        Instantiate the repository and service for creating records
        Service activity and vehicle part repositories are used to
        retrieve their respective objects for creating record
         */
        ServicePackageRepository servicePackageRepo = ServicePackageRepository.getInstance();
        ServiceActivityRepository serviceActivityRepo = ServiceActivityRepository.getInstance();
        VehiclePartRepository vehiclePartRepo = VehiclePartRepository.getInstance();
        RecordWriter createServicePackage = new ServicePackageCreation(servicePackageRepo,
                serviceActivityRepo, vehiclePartRepo, servicePackageDTO);

        // Begin creating service package record procedure in service class
        return createServicePackage.execute();
    }

    public OperationResult updateServicePackage(ServicePackageDTO servicePackageDTO) {

        // Check if there are any null or empty values in the list
        OperationResult nullValidation = validateNullValues(servicePackageDTO, OperationType.UPDATE);
        if (!nullValidation.isValid()) {
            return nullValidation;
        }

        // Validate price logic
        OperationResult priceValidation = DataValidation.validatePrice(servicePackageDTO.getPackageBasePrice());
        if (!priceValidation.isValid()) {
            return priceValidation;
        }

        /*
        Instantiate the repository and service for creating records
        Service activity and vehicle part repositories are used to
        retrieve their respective objects for updating record
         */
        ServicePackageRepository servicePackageRepo = ServicePackageRepository.getInstance();
        ServiceActivityRepository serviceActivityRepo = ServiceActivityRepository.getInstance();
        VehiclePartRepository vehiclePartRepo = VehiclePartRepository.getInstance();
        RecordWriter updateServicePackage = new ServicePackageUpdate(servicePackageRepo,
                serviceActivityRepo, vehiclePartRepo, servicePackageDTO);

        // Begin updating service package record procedure in service class
        return updateServicePackage.execute();
    }

    public OperationResult deleteServicePackage(String servicePkgId) {

        // Check if the inputs are null or empty
        if (servicePkgId == null || servicePkgId.isBlank()) {
            return OperationResult.failed("The service package ID must be provided to " +
                    "delete a record. Please try again.");
        }

        // Instantiate the repository and service for deleting records
        ServicePackageRepository servicePackageRepo = ServicePackageRepository.getInstance();
        RecordWriter deleteServicePackage = new ServicePackageDeletion(servicePackageRepo, servicePkgId);

        return deleteServicePackage.execute();
    }

    /**
     * Optional class is used to determine whether the records are present or not
     * This is for frontend logic in displaying the result message for UI
     */
    public Optional<List<ServicePackage>> retrieveAllServicePackages() {

        // Instantiate the repository and service for deleting records
        ServicePackageRepository servicePackageRepo = ServicePackageRepository.getInstance();
        RecordReader<ServicePackage> retrieveAllServicePackages = new ServicePackageRetrieval(servicePackageRepo);

        // Retrieve all the records from the repository
        return retrieveAllServicePackages.getAllRecords();
    }

    public Optional<ServicePackage> retrieveServicePackageById(String servicePkgId) {

        // Instantiate the repository and service for retrieving records
        ServicePackageRepository servicePackageRepo = ServicePackageRepository.getInstance();
        RecordReader<ServicePackage> retrieveServicePackageById = new ServicePackageRetrieval(servicePackageRepo);

        // Retrieve the record from the repository based on the ID
        return retrieveServicePackageById.getRecordById(servicePkgId);
    }

    public Optional<List<ServicePackage>> retrieveServicePackagesByIds(List<String> servicePkgIds) {

        // Instantiate the repository and service for retrieving records
        ServicePackageRepository vehiclePartRepo = ServicePackageRepository.getInstance();
        RecordReader<ServicePackage> retrieveServiceActsByIds = new ServicePackageRetrieval(vehiclePartRepo);

        // Retrieve the records from the repository based on the ID list
        return retrieveServiceActsByIds.getRecordsByIds(servicePkgIds);
    }

    /**
     * Validate the format and length of service package ID
     * This validation is done before record retrieval, so the frontend can first display the validation result
     */
    public OperationResult validateServicePackageId(String servicePkgId) {

        // Check if the service package ID and its type are null or empty
        if (servicePkgId == null || servicePkgId.isBlank()) {
            return OperationResult.failed("The service package ID must be provided to validate. " +
                    "Please try again.");
        }

        if (!DataValidation.validateRecordId(servicePkgId).isValid()) {
            return OperationResult.failed("The service package ID: " + servicePkgId +
                    " is not an UUID. Please try again.");
        }

        return OperationResult.success();
    }

    public OperationResult validateServicePackageIds(List<String> servicePkgIds) {

        // Check if the ID list and its type are null or empty
        if (servicePkgIds == null || servicePkgIds.isEmpty()) {
            return OperationResult.failed("The service package IDs must be provided to validate. " +
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
            if (!DataValidation.validateRecordId(servicePkgId).isValid()) {
                return OperationResult.failed("The service package ID: " + servicePkgId +
                        " is not an UUID. Please try again.");
            }
        }

        return OperationResult.success();
    }

    /**
     * Check if there are any null or empty values of required fields in the given DTO
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

        // Both the service activity and vehicle part lists cannot be empty
        // Either one of the lists must have values
        if (servicePackageDTO.getServiceActivityList() == null &&
                servicePackageDTO.getVehiclePartList() == null ) {
            return new OperationResult(false,
                    "Both service activity and vehicle part list cannot be empty");
        }

        // Check if all values are null and get the boolean result
        boolean validationResult =  NullValidator.isAllValuesNull(servicePkgValues).isValid();

        // This conditional statement is used to customize the error message
        // if there is a null value in the servicePkgValues
        if (!validationResult) {
            return new OperationResult(false,
                    "All required fields must be filled in. Please try again.");
        } else {
            return OperationResult.success();
        }
    }
}
