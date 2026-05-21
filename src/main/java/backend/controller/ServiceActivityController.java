package main.java.backend.controller;

import main.java.backend.model.ServiceActivity;
import main.java.backend.repository.impl.ServiceActivityRepository;
import main.java.backend.service.RecordReader;
import main.java.backend.service.RecordWriter;
import main.java.backend.service.serviceactivity.ServiceActivityCreation;
import main.java.backend.service.serviceactivity.ServiceActivityDeletion;
import main.java.backend.service.serviceactivity.ServiceActivityRetrieval;
import main.java.backend.service.serviceactivity.ServiceActivityUpdate;
import main.java.backend.util.DataValidator;
import main.java.backend.util.NullValidator;
import main.java.dtos.ServiceActivityDTO;
import main.java.enums.system.OperationType;
import main.java.records.OperationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServiceActivityController {

    public OperationResult createServiceActivity(ServiceActivityDTO serviceActivityDTO) {

        // Check if there are any null or empty values in the list
        OperationResult nullValidation = validateNullValues(serviceActivityDTO, OperationType.CREATE);
        if (!nullValidation.isValid()) {
            return OperationResult.failed("All required fields must be filled in. Please try again.");
        }

        // Validate price logic
        OperationResult priceValidation = DataValidator.validatePrice(
                serviceActivityDTO.getServiceActivityPrice());
        if (!priceValidation.isValid()) {
            return priceValidation;
        }

        // Instantiate the repository and service for creating records
        ServiceActivityRepository serviceActivityRepo = ServiceActivityRepository.getInstance();
        RecordWriter createServiceActivity = new ServiceActivityCreation(serviceActivityRepo, serviceActivityDTO);

        // Begin creating service activity record procedure in service class
        return createServiceActivity.execute();
    }

    public OperationResult updateServiceActivity(ServiceActivityDTO serviceActivityDTO) {

        // Check if there are any null or empty values in the list
        OperationResult nullValidation = validateNullValues(serviceActivityDTO, OperationType.UPDATE);
        if (!nullValidation.isValid()) {
            return OperationResult.failed("All required fields must be filled in. Please try again.");
        }

        // Validate price format
        OperationResult formatValidation = DataValidator.validatePrice(
                serviceActivityDTO.getServiceActivityPrice());
        if (!formatValidation.isValid()) {
            return formatValidation;
        }

        // Instantiate the repository and service for updating records
        ServiceActivityRepository serviceActivityRepo = ServiceActivityRepository.getInstance();
        RecordWriter updateServiceActivity = new ServiceActivityUpdate(serviceActivityRepo, serviceActivityDTO);

        // Begin updating service activity record procedure in service class
        return updateServiceActivity.execute();
    }

    public OperationResult deleteServiceActivity(String serviceActId) {

        // Check if the inputs are null or empty
        if (serviceActId == null || serviceActId.isBlank()) {
            return OperationResult.failed("The service activity ID must be provided to " +
                    "delete a record. Please try again.");
        }

        // Instantiate the repository and service for deleting records
        ServiceActivityRepository serviceActivityRepo = ServiceActivityRepository.getInstance();
        RecordWriter deleteServiceActivity = new ServiceActivityDeletion(serviceActivityRepo, serviceActId);

        return deleteServiceActivity.execute();
    }

    /**
     * Optional class is used to determine whether the records are present or not
     * This is for frontend logic in displaying the result message for UI
     */
    public Optional<List<ServiceActivity>> retrieveAllServiceActivities() {

        // Instantiate the repository and service for retrieving records
        ServiceActivityRepository serviceActivityRepo = ServiceActivityRepository.getInstance();
        RecordReader<ServiceActivity> retrieveAllServiceActs = new ServiceActivityRetrieval(serviceActivityRepo);

        // Retrieve all the records from the repository
        return retrieveAllServiceActs.getAllRecords();
    }

    public Optional<ServiceActivity> retrieveServiceActivityById(String serviceActId) {

        // Instantiate the repository and service for retrieving records
        ServiceActivityRepository serviceActivityRepo = ServiceActivityRepository.getInstance();
        RecordReader<ServiceActivity> retrieveServiceActById = new ServiceActivityRetrieval(serviceActivityRepo);

        // Retrieve the record from the repository based on the ID
        return retrieveServiceActById.getRecordById(serviceActId);
    }

    public Optional<List<ServiceActivity>> retrieveServiceActivitiesByIds(List<String> serviceActIds) {

        // Instantiate the repository and service for retrieving records
        ServiceActivityRepository serviceActivityRepo = ServiceActivityRepository.getInstance();
        RecordReader<ServiceActivity> retrieveServiceActsByIds = new ServiceActivityRetrieval(serviceActivityRepo);

        // Retrieve the records from the repository based on the ID list
        return retrieveServiceActsByIds.getRecordsByIds(serviceActIds);
    }

    /**
     * Validate the format and length of service activity ID
     * This validation is done before record retrieval, so the frontend can first display the validation result
     */
    public OperationResult validateServiceActId(String serviceActId) {

        // Check if the service activity ID and its type are null or empty
        if (serviceActId == null || serviceActId.isBlank()) {
            return OperationResult.failed("The service activity ID must be provided to validate. " +
                    "Please try again.");
        }

        if (!DataValidator.validateRecordId(serviceActId).isValid()) {
            return OperationResult.failed("The service activity ID: " + serviceActId +
                    " is not an UUID. Please try again.");
        }

        return OperationResult.success();
    }

    public OperationResult validateServiceActIds(List<String> serviceActIds) {

        // Check if the ID list and its type are null or empty
        if (serviceActIds == null || serviceActIds.isEmpty()) {
            return OperationResult.failed("The service activity IDs must be provided to validate. " +
                    "Please try again.");
        }

        // Check if any of the service activity IDs are null or empty
        for (String serviceActId : serviceActIds) {
            if (serviceActId == null || serviceActId.isBlank() ) {
                return OperationResult.failed("One of the service activity IDs is null or empty. Please try again.");
            }
        }

        // Check if any of the service activity IDs are invalid
        for (String serviceActId : serviceActIds) {
            if (!DataValidator.validateRecordId(serviceActId).isValid()) {
                return OperationResult.failed("The service activity ID: " + serviceActId +
                        " is not an UUID. Please try again.");
            }
        }

        return OperationResult.success();
    }

    /**
     * Check if there are any null or empty values of required fields in the given DTO
     * Used for creating and updating records
     */
    private OperationResult validateNullValues(ServiceActivityDTO serviceActivityDTO, OperationType operationType) {
        List<Object> serviceActValues = new ArrayList<>();

        // If the operation is updating record, add the ID to the list for null validation
        if (operationType == OperationType.UPDATE) {
            serviceActValues.add(serviceActivityDTO.getServiceActivityID());
        }

        serviceActValues.add(serviceActivityDTO.getServiceActivityName());
        serviceActValues.add(serviceActivityDTO.getServiceActivityPrice());

        return NullValidator.isAllValuesNull(serviceActValues);
    }
}
