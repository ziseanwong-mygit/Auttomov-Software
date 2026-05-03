package main.java.backend.service.vehicleservice;

import main.java.backend.model.VehicleService;
import main.java.backend.repository.VehicleServiceRepository;
import main.java.backend.service.ServiceOperation;
import main.java.enums.VehicleServiceType;
import main.java.records.OperationResult;

import java.util.List;

public class VehicleServiceUpdate implements ServiceOperation {

    private final VehicleServiceRepository repository;

    private final String serviceID;
    private final String newName;
    private final String newDescription;
    private final VehicleServiceType newType;
    private final List<String> newItemList;
    private final double newPrice;
    private final int newDuration;

    public VehicleServiceUpdate(String serviceID, String newName, String newDescription,
                                VehicleServiceType newType,
                                List<String> newItemList,
                                double newPrice, int newDuration) {

        this.serviceID = serviceID;
        this.newName = newName;
        this.newDescription = newDescription;
        this.newType = newType;
        this.newItemList = newItemList;
        this.newPrice = newPrice;
        this.newDuration = newDuration;

        this.repository = VehicleServiceRepository.getInstance();
    }

    @Override
    public OperationResult executeProcess() {

        VehicleService service = repository.findById(serviceID);

        if (service == null) {
            return OperationResult.failed("Service not found");
        }

        // Update fields
        service.setServiceName(newName);
        service.setServiceDescription(newDescription);
        service.setServiceType(newType);
        service.setServiceItemList(newItemList);
        service.setBasePrice(newPrice);
        service.setServiceDuration(newDuration);

        return repository.update(service);
    }
}