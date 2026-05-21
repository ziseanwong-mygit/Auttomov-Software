package main.java.backend.service.servicepackage;

import main.java.backend.model.ServiceActivity;
import main.java.backend.model.ServicePackage;
import main.java.backend.model.VehiclePart;
import main.java.backend.repository.impl.ServicePackageRepository;
import main.java.backend.service.RecordWriter;
import main.java.dtos.ServicePackageDTO;
import main.java.enums.data.ServicePackageType;
import main.java.records.OperationResult;

import java.util.List;
import java.util.UUID;

public class ServicePackageCreation implements RecordWriter {
    
    private final ServicePackageRepository servicePackageRepository;
    private final ServicePackageDTO servicePackageDTO;
    private final List<ServiceActivity> serviceActivityList;
    private final List<VehiclePart> vehiclePartList;

    public ServicePackageCreation(ServicePackageRepository servicePackageRepository,
                                  ServicePackageDTO servicePackageDTO, List<ServiceActivity> serviceActivityList,
                                  List<VehiclePart> vehiclePartList) {
        this.servicePackageRepository = servicePackageRepository;
        this.servicePackageDTO = servicePackageDTO;
        this.serviceActivityList = serviceActivityList;
        this.vehiclePartList = vehiclePartList;
    }
    
    @Override
    public OperationResult execute() {

        // Create a new service package object
        ServicePackage newServicePackage = populateServicePkgDetails();

        // Save the service package into the file
        return servicePackageRepository.createRecord(newServicePackage);
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
        
        return new ServicePackage(servicePkgId, servicePkgName, servicePkgDescription, servicePkgType,
                serviceActivityList, vehiclePartList, packageBasePrice, serviceDuration);
    }
}
