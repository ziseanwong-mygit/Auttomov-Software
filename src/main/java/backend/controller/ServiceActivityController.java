package main.java.backend.controller;

import main.java.backend.model.ServiceActivity;
import main.java.backend.repository.ServiceActivityRepository;
import main.java.backend.service.RecordReader;
import main.java.backend.service.RecordWriter;
import main.java.backend.service.serviceactivity.ServiceActivityCreation;
import main.java.backend.service.serviceactivity.ServiceActivityDeletion;
import main.java.backend.service.serviceactivity.ServiceActivityRetrieval;
import main.java.backend.service.serviceactivity.ServiceActivityUpdate;
import main.java.backend.util.DataValidation;
import main.java.backend.util.NullValidator;
import main.java.dtos.ServiceActivityDTO;
import main.java.enums.system.OperationType;
import main.java.records.OperationResult;

import java.util.ArrayList;
import java.util.List;

public class ServiceActivityController {

    public OperationResult createServiceActivity(ServiceActivityDTO serviceActivityDTO) {

        // Check if there are any null or empty values in the list
        OperationResult nullValidation = validateNullValues(serviceActivityDTO, OperationType.CREATE);
        if (!nullValidation.isValid()) {
            return OperationResult.failed("All required fields must be filled in. Please try again.");
        }

        // Validate price logic
        OperationResult priceValidation = DataValidation.validatePrice(
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
        OperationResult formatValidation = DataValidation.validatePrice(
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

    public List<ServiceActivity> retrieveAllServiceActivities() {

        // Instantiate the repository and service for retrieving records
        ServiceActivityRepository serviceActivityRepo = ServiceActivityRepository.getInstance();
        RecordReader retrieveAllServiceActs = new ServiceActivityRetrieval(serviceActivityRepo);

        // Retrieve all the records from the repository
        List<?> unknownList = retrieveAllServiceActs.getAllRecords();

        // Since the retrieved list is a list of unknown type, cast each object to the desired type explicitly
        return unknownList.stream()
                .map(obj -> (ServiceActivity) obj)
                .toList();
    }

    public OperationResult retrieveServiceActivityById(String serviceActId) {

        // Check if the inputs are null or empty
        if (serviceActId == null || serviceActId.isBlank()) {
            return OperationResult.failed("All required fields must be filled in. Please try again.");
        }

        // Instantiate the repository and service for retrieving records
        ServiceActivityRepository serviceActivityRepo = ServiceActivityRepository.getInstance();
        RecordReader retrieveServiceActById = new ServiceActivityRetrieval(serviceActivityRepo);

        return retrieveServiceActById.getRecordById(serviceActId);
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
