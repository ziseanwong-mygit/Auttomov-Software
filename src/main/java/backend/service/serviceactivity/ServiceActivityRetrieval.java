package main.java.backend.service.serviceactivity;

import main.java.backend.model.ServiceActivity;
import main.java.backend.repository.ServiceActivityRepository;
import main.java.backend.service.RecordReader;
import main.java.records.OperationResult;

import java.util.List;

public class ServiceActivityRetrieval implements RecordReader {

    private final ServiceActivityRepository serviceActivityRepository;


    public ServiceActivityRetrieval(ServiceActivityRepository serviceActivityRepository) {
        this.serviceActivityRepository = serviceActivityRepository;
    }

    /**
     * Read and return all service activities from the repository
     */
    @Override
    public List<ServiceActivity> getAllRecords() {
        return serviceActivityRepository.getAllRecords();
    }

    /**
     * Read and return a single service activity based on the given keyword like serviceActivityID
     */
    @Override
    public OperationResult getRecordById(String serviceActId) {

        // Find the service activity by its ID
        ServiceActivity existingServiceAct = serviceActivityRepository.getRecordById(serviceActId);

        if (existingServiceAct == null) {
            return OperationResult.failed("The service activity with ID: " + serviceActId +
                    " does not exist in the system.");
        }

        return OperationResult.success("The service activity with ID: " + serviceActId +
                "has been found.", existingServiceAct);
    }
}
