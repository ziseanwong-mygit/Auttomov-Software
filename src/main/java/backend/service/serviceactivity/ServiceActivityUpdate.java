package main.java.backend.service.serviceactivity;

import main.java.backend.model.ServiceActivity;
import main.java.backend.repository.ServiceActivityRepository;
import main.java.backend.service.RecordWriter;
import main.java.dtos.ServiceActivityDTO;
import main.java.records.FieldComparison;
import main.java.records.OperationResult;

import java.util.ArrayList;
import java.util.List;

public class ServiceActivityUpdate implements RecordWriter {

    private final ServiceActivityRepository serviceActRepository;
    private final ServiceActivityDTO serviceActivityDTO;
    private final ServiceActivity existingServiceAct;

    public ServiceActivityUpdate(ServiceActivityRepository serviceActRepository,
                                   ServiceActivityDTO serviceActivityDTO) {
        this.serviceActRepository = serviceActRepository;
        this.serviceActivityDTO = serviceActivityDTO;
        this.existingServiceAct = serviceActRepository.getRecordById(serviceActivityDTO.getServiceActivityID());
    }

    @Override
    public OperationResult execute() {

        String serviceActivityID = serviceActivityDTO.getServiceActivityID();

        if (existingServiceAct == null) {
            return OperationResult.failed("The service activity with ID: " + serviceActivityID +
                    " does not exist in the system.");
        }

        // Get the list of fields that are changed
        List<FieldComparison<?>> changedFields = compareChanges();

        // If no fields are changed, return an error message
        if (changedFields.isEmpty()) {
            return OperationResult.failed("No fields have been changed. " +
                    "Please update one or more fields.");
        }

        // Apply updates only to changed fields
        setChangedValues(changedFields);

        // Begin updating service activity record in the repository
        try {
            return serviceActRepository.updateRecord(existingServiceAct);
        } catch (Exception e) {
            System.out.println(e.getMessage()); // Print exception message for debugging
            return OperationResult.failed("An unexpected error occurred while updating " +
                    "the service activity record. Please try again.");
        }
    }

    /**
     * Compare the values between the existing service activity and incoming inputs (DTO) to get the changed fields
     */
    private List<FieldComparison<?>> compareChanges() {

        // Declare a list to store all the fields for comparison later
        List<FieldComparison<?>> serviceActFields = new ArrayList<>();

        serviceActFields.add(new FieldComparison<>("name", existingServiceAct.getServiceActivityName(),
                serviceActivityDTO.getServiceActivityName(), null));
        serviceActFields.add(new FieldComparison<>("description",
                existingServiceAct.getServiceActivityDescription(), serviceActivityDTO.getServiceActivityDescription(),
                null));
        serviceActFields.add(new FieldComparison<>("price", existingServiceAct.getServiceActivityPrice(),
                serviceActivityDTO.getServiceActivityPrice(), null));

        // Filter to get the changed fields
        return serviceActFields.stream()
                .filter(FieldComparison::isChanged)
                .toList();
    }

    /**
     * Set the new values of the changed fields in the existing service activity object
     * The object will be used to update the record in the file
     */
    private void setChangedValues(List<FieldComparison<?>> changedFields) {
        for (FieldComparison<?> field : changedFields) {
            switch (field.fieldName()) {
                case "name" -> existingServiceAct.setServiceActivityName(
                        serviceActivityDTO.getServiceActivityName());
                case "description" -> existingServiceAct.setServiceActivityDescription(
                        serviceActivityDTO.getServiceActivityDescription());
                case "price" -> existingServiceAct.setServiceActivityPrice(
                        serviceActivityDTO.getServiceActivityPrice());
            }
        }
    }
}