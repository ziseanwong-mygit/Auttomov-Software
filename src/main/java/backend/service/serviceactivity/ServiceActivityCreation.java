package main.java.backend.service.serviceactivity;

import main.java.backend.model.ServiceActivity;
import main.java.backend.repository.impl.ServiceActivityRepository;
import main.java.backend.service.RecordWriter;
import main.java.dtos.ServiceActivityDTO;
import main.java.records.OperationResult;

import java.util.UUID;

public class ServiceActivityCreation implements RecordWriter {

    private final ServiceActivityRepository serviceActRepository;
    private final ServiceActivityDTO serviceActivityDTO;

    public ServiceActivityCreation(ServiceActivityRepository serviceActRepository,
                                   ServiceActivityDTO serviceActivityDTO) {
        this.serviceActRepository = serviceActRepository;
        this.serviceActivityDTO = serviceActivityDTO;
    }

    @Override
    public OperationResult execute() {

        // Create a new service activity object
        ServiceActivity newServiceAct = populateServiceActDetails();

        // Save the service activity into the file
        return serviceActRepository.createRecord(newServiceAct);
    }

    /**
     * Create a new ServiceActivity object for record creation
     */
    private ServiceActivity populateServiceActDetails() {

        // Generate new service activity ID
        String serviceActId = UUID.randomUUID().toString();

        // Extract the service activity details from the DTO
        String serviceActName = serviceActivityDTO.getServiceActivityName();
        String serviceActDescription = serviceActivityDTO.getServiceActivityDescription();
        double serviceActPrice = serviceActivityDTO.getServiceActivityPrice();

        // Return the new object here
        return new ServiceActivity(serviceActId, serviceActName, serviceActDescription, serviceActPrice);
    }
}
