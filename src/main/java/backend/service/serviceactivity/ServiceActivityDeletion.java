package main.java.backend.service.serviceactivity;

import main.java.backend.model.ServiceActivity;
import main.java.backend.repository.impl.ServiceActivityRepository;
import main.java.backend.service.RecordWriter;
import main.java.records.OperationResult;

public class ServiceActivityDeletion implements RecordWriter {

    private final ServiceActivityRepository serviceActRepository;
    private final String serviceActID;

    public ServiceActivityDeletion(ServiceActivityRepository serviceActRepository, String serviceActID) {
        this.serviceActRepository = serviceActRepository;
        this.serviceActID = serviceActID;
    }

    @Override
    public OperationResult execute() {

        // Get the service activity record by its ID
        ServiceActivity existingServiceAct = serviceActRepository.getRecordById(serviceActID);

        // If the service activity is not found, return an error message
        if (existingServiceAct == null) {
            return OperationResult.failed("The service activity with ID: " + serviceActID +
                    " does not exist in the system.");
        }

        try {
            return serviceActRepository.deleteRecord(existingServiceAct);
        } catch (Exception e) {
            System.out.println(e.getMessage()); // Print exception message for debugging
            return OperationResult.failed("An unexpected error occurred while deleting the " +
                    "service activity record. Please try again.");
        }
    }
}
