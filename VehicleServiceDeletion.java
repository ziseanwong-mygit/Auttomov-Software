package main.java.backend.service.vehicleservice;

import main.java.backend.repository.VehicleServiceRepository;
import main.java.backend.service.ServiceOperation;
import main.java.records.OperationResult;

public class VehicleServiceDeletion implements ServiceOperation {

    private final VehicleServiceRepository repository;
    private final String serviceID;

    public VehicleServiceDeletion(String serviceID) {
        this.serviceID = serviceID;
        this.repository = VehicleServiceRepository.getInstance();
    }

    @Override
    public OperationResult executeProcess() {

        boolean deleted = repository.delete(serviceID);

        if (!deleted) {
            return OperationResult.failed("Service not found");
        }

        return OperationResult.success("Service deleted successfully");
    }
}