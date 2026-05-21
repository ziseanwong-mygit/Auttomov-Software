package main.java.backend.service.servicepackage;

import main.java.backend.model.ServicePackage;
import main.java.backend.repository.impl.ServicePackageRepository;
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

        // Check if the service package exists with the given ID
        ServicePackage existingServicePkg = servicePackageRepository.getRecordById(servicePkgID);
        if (existingServicePkg == null) {
            return OperationResult.failed("The service package with ID: " + servicePkgID +
                    " does not exist in the system.");
        }

        // Begin deleting service package record in the repository
        return servicePackageRepository.deleteRecord(existingServicePkg);
    }
}
