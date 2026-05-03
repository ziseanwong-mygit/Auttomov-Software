package main.java.backend.service.vehicleservice;

import main.java.backend.model.VehicleService;
import main.java.backend.repository.VehicleServiceRepository;
import main.java.backend.service.ServiceOperation;
import main.java.enums.VehicleServiceType;
import main.java.records.OperationResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class VehicleServiceCreation implements ServiceOperation {

    private final VehicleServiceRepository repository;

    private final String serviceName;
    private final String serviceDescription;
    private final VehicleServiceType serviceType;
    private final List<String> serviceItemList;
    private final double basePrice;
    private final int serviceDuration;

    private VehicleService newService;

    public VehicleServiceCreation(String serviceName, String serviceDescription,
                                  VehicleServiceType serviceType,
                                  List<String> serviceItemList,
                                  double basePrice, int serviceDuration) {

        this.serviceName = serviceName;
        this.serviceDescription = serviceDescription;
        this.serviceType = serviceType;
        this.serviceItemList = serviceItemList;
        this.basePrice = basePrice;
        this.serviceDuration = serviceDuration;

        this.repository = VehicleServiceRepository.getInstance();
    }

    @Override
    public OperationResult executeProcess() {

        OperationResult validationResult = validateInputValues();

        if (!validationResult.isValid()) {
            return validationResult;
        }

        populateDetails();

        return repository.create(newService);
    }

    private OperationResult validateInputValues() {

        Map<String, String> errors = new HashMap<>();

        if (serviceName == null || serviceName.isEmpty()) {
            errors.put("serviceName", "Service name cannot be empty");
        }

        if (basePrice <= 0) {
            errors.put("basePrice", "Price must be greater than 0");
        }

        if (serviceDuration <= 0) {
            errors.put("serviceDuration", "Duration must be greater than 0");
        }

        return errors.isEmpty()
                ? OperationResult.success("Validation successful")
                : OperationResult.failed("Validation failed", errors);
    }

    private void populateDetails() {

        String id = UUID.randomUUID().toString();

        newService = new VehicleService(
                id,
                serviceName,
                serviceDescription,
                serviceType,
                serviceItemList,
                basePrice,
                serviceDuration
        );
    }
}