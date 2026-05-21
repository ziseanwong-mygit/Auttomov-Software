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

        // Check if the service activity exists with the given ID
        ServiceActivity existingServiceAct = serviceActRepository.getRecordById(serviceActID);
        if (existingServiceAct == null) {
            return OperationResult.failed("The service activity with ID: " + serviceActID +
                    " does not exist in the system.");
        }

        // Begin deleting service activity record in the repository
        return serviceActRepository.deleteRecord(existingServiceAct);
    }
}
