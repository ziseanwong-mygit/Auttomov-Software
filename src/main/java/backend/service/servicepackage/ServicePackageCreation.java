package main.java.backend.service.servicepackage;

import main.java.backend.model.ServiceActivity;
import main.java.backend.model.ServicePackage;
import main.java.backend.model.VehiclePart;
import main.java.backend.repository.impl.ServiceActivityRepository;
import main.java.backend.repository.impl.ServicePackageRepository;
import main.java.backend.repository.impl.VehiclePartRepository;
import main.java.backend.service.RecordWriter;
import main.java.dtos.ServicePackageDTO;
import main.java.enums.data.ServicePackageType;
import main.java.records.OperationResult;

import java.util.List;
import java.util.UUID;

public class ServicePackageCreation implements RecordWriter {
    
    private final ServicePackageRepository servicePackageRepository;
    private final ServiceActivityRepository serviceActivityRepository;
    private final VehiclePartRepository vehiclePartRepository;
    private final ServicePackageDTO servicePackageDTO;

    public ServicePackageCreation(ServicePackageRepository servicePackageRepository,
                                  ServiceActivityRepository serviceActivityRepository,
                                  VehiclePartRepository vehiclePartRepository,
                                  ServicePackageDTO servicePackageDTO) {
        this.servicePackageRepository = servicePackageRepository;
        this.serviceActivityRepository = serviceActivityRepository;
        this.vehiclePartRepository = vehiclePartRepository;
        this.servicePackageDTO = servicePackageDTO;
    }
    
    @Override
    public OperationResult execute() {

        try {
            // Create a new service package object
            ServicePackage newServicePackage = populateServicePkgDetails();
            // Save the service package into the file
            return servicePackageRepository.createRecord(newServicePackage);

        } catch (Exception e) {
            System.out.println(e.getMessage()); // Print exception message for debugging
            return OperationResult.failed("An unexpected error occurred while creating a new " +
                    "service package record. Please try again.");
        }
    }

    /**
     * Create a new ServicePackage object for record creation
     */
    private ServicePackage populateServicePkgDetails() {
        
        // Generate new service package ID
        String servicePkgId = UUID.randomUUID().toString();
        
        // Extract the service package details from the DTO
        String servicePkgName = servicePackageDTO.getServicePackageName();
        String servicePkgDescription = servicePackageDTO.getServicePackageDescription();
        ServicePackageType servicePkgType = servicePackageDTO.getServicePackageType();
        double packageBasePrice = servicePackageDTO.getPackageBasePrice();
        int serviceDuration = servicePackageDTO.getServicePackageType().getServiceDuration();

        // Get the service activity and vehicle part objects from the repository
        List<ServiceActivity> serviceActivityList = serviceActivityRepository.getRecordsByIds(
                servicePackageDTO.getServiceActivityIDs());
        List<VehiclePart> vehiclePartList = vehiclePartRepository.getRecordsByIds(
                servicePackageDTO.getVehiclePartIDs());
        
        return new ServicePackage(servicePkgId, servicePkgName, servicePkgDescription, servicePkgType, 
                serviceActivityList, vehiclePartList, packageBasePrice, serviceDuration);
    }
}
