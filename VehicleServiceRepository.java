package main.java.backend.repository;

import main.java.backend.model.VehicleService;
import main.java.records.OperationResult;

import java.util.ArrayList;
import java.util.List;

public class VehicleServiceRepository {

    private static VehicleServiceRepository instance;
    private final List<VehicleService> services = new ArrayList<>();

    private VehicleServiceRepository() {}

    public static VehicleServiceRepository getInstance() {
        if (instance == null) {
            instance = new VehicleServiceRepository();
        }
        return instance;
    }

    // C
    public OperationResult create(VehicleService service) {
        services.add(service);
        return OperationResult.success("Vehicle service created");
    }

    // R
    public VehicleService findById(String serviceID) {
        return services.stream()
                .filter(s -> s.getServiceID().equals(serviceID)) 
                .findFirst()
                .orElse(null);
    }

    // U
    public OperationResult update(VehicleService service) {
        return OperationResult.success("Vehicle service updated");
    }

    // D
    public boolean delete(String serviceID) {
        return services.removeIf(s -> s.getServiceID().equals(serviceID)); 
    }

    public List<VehicleService> getAll() {
        return services;
    }
}