package main.java.backend.service.serviceactivity;

import main.java.backend.model.ServiceActivity;
import main.java.backend.repository.ServiceActivityRepository;
import main.java.backend.service.RecordReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServiceActivityRetrieval implements RecordReader<ServiceActivity> {

    private final ServiceActivityRepository serviceActivityRepository;

    public ServiceActivityRetrieval(ServiceActivityRepository serviceActivityRepository) {
        this.serviceActivityRepository = serviceActivityRepository;
    }

    /**
     * Read and return all service activities from the repository
     */
    @Override
    public Optional<List<ServiceActivity>> getAllRecords() {

        List<ServiceActivity> existingServiceActs = new ArrayList<>(serviceActivityRepository.getAllRecords());

        if (existingServiceActs.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(existingServiceActs);
    }

    /**
     * Read and return a single service activity based on the given ID
     */
    @Override
    public Optional<ServiceActivity> getRecordById(String serviceActId) {

        ServiceActivity existingServiceAct = serviceActivityRepository.getRecordById(serviceActId);

        if (existingServiceAct == null) {
            return Optional.empty();
        }

        return Optional.of(existingServiceAct);
    }

    /**
     * Read and return multiple service activities based on the given IDs
     */
    @Override
    public Optional<List<ServiceActivity>> getRecordsByIds(List<String> serviceActIds) {

        List<ServiceActivity> existingServiceActs = serviceActivityRepository.getRecordsByIds(serviceActIds);

        if (existingServiceActs.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(existingServiceActs);
    }
}
