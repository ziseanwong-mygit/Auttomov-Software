package main.java.backend.service.servicepackage;

import main.java.backend.model.ServicePackage;
import main.java.backend.repository.impl.ServicePackageRepository;
import main.java.backend.service.RecordReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServicePackageRetrieval implements RecordReader<ServicePackage> {
    
    private final ServicePackageRepository servicePackageRepository;
    
    public ServicePackageRetrieval(ServicePackageRepository servicePackageRepository) {
        this.servicePackageRepository = servicePackageRepository;
    }

    /**
     * Read and return all service packages from the repository
     */
    @Override
    public Optional<List<ServicePackage>> getAllRecords() {
        
        List<ServicePackage> existingServicePkgs = new ArrayList<>(servicePackageRepository.getAllRecords());

        if (existingServicePkgs.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(existingServicePkgs);
    }

    /**
     * Read and return a single service package based on the given ID
     */
    @Override
    public Optional<ServicePackage> getRecordById(String servicePackageId) {

        ServicePackage existingServicePkg = servicePackageRepository.getRecordById(servicePackageId);

        if (existingServicePkg == null) {
            return Optional.empty();
        }

        return Optional.of(existingServicePkg);
    }

    /**
     * Read and return multiple service packages based on the given IDs
     */
    @Override
    public Optional<List<ServicePackage>> getRecordsByIds(List<String> servicePackageIds) {

        List<ServicePackage> existingServicePkgs = servicePackageRepository.getRecordsByIds(servicePackageIds);

        if (existingServicePkgs.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(existingServicePkgs);
    }
}
