package main.java.backend.service.servicepackage;

import main.java.backend.model.ServicePackage;
import main.java.backend.repository.ServicePackageRepository;
import main.java.backend.service.RecordWriter;
import main.java.records.OperationResult;

public class ServicePackageDeletion implements RecordWriter {

    private final ServicePackageRepository servicePackageRepository;
    private final String servicePkgID;

    public ServicePackageDeletion(ServicePackageRepository servicePackageRepository, String servicePkgID) {
        this.servicePackageRepository = servicePackageRepository;
        this.servicePkgID = servicePkgID;
    }

    @Override
    public OperationResult execute() {

        // Get the service package record by its ID
        ServicePackage existingServicePkg = servicePackageRepository.getRecordById(servicePkgID);

        // If the service package is not found, return an error message
        if (existingServicePkg == null) {
            return OperationResult.failed("The service package with ID: " + servicePkgID +
                    " does not exist in the system.");
        }

        try {
            return servicePackageRepository.deleteRecord(existingServicePkg);
        } catch (Exception e) {
            System.out.println(e.getMessage()); // Print exception message for debugging
            return OperationResult.failed("An unexpected error occurred while deleting the " +
                    "service package record. Please try again.");
        }
    }
}
