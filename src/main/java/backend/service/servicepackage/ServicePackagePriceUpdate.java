package main.java.backend.service.servicepackage;

import main.java.backend.model.ServicePackage;
import main.java.backend.repository.impl.ServicePackageRepository;
import main.java.backend.service.RecordWriter;
import main.java.dtos.ServicePackageDTO;
import main.java.records.FieldComparison;
import main.java.records.OperationResult;

public class ServicePackagePriceUpdate implements RecordWriter {

    private final ServicePackageRepository servicePackageRepository;
    private final ServicePackageDTO servicePackageDTO;

    public ServicePackagePriceUpdate(ServicePackageRepository servicePackageRepository,
                                     ServicePackageDTO servicePackageDTO) {
        this.servicePackageRepository = servicePackageRepository;
        this.servicePackageDTO = servicePackageDTO;
    }

    @Override
    public OperationResult execute(){

        // Check if the service package exists with the given ID
        ServicePackage existingServicePackage = servicePackageRepository.getRecordById(
                servicePackageDTO.getServicePackageID());
        if (existingServicePackage == null) {
            return OperationResult.failed("The service package with ID: " + servicePackageDTO.getServicePackageID() +
                    " does not exist in the system.");
        }

        // Check if the input price is different from the current price
        FieldComparison<Double> priceChange = new FieldComparison<>("packageBasePrice",
                existingServicePackage.getPackageBasePrice(), servicePackageDTO.getPackageBasePrice(), null);

        if (!priceChange.isChanged()) {
            return OperationResult.failed("The input price provided is the same as the " +
                    "current price. Please provide a different price for update.");
        }

        // Set the new price into the existing service package record
        existingServicePackage.setPackageBasePrice(servicePackageDTO.getPackageBasePrice());

        // Begin updating service package record with new price in the repository
        return servicePackageRepository.updateRecord(existingServicePackage);
    }
}
