package main.java.backend.service.serviceactivity;

import main.java.backend.model.ServiceActivity;
import main.java.backend.model.ServicePackage;
import main.java.backend.repository.impl.ServiceActivityRepository;
import main.java.backend.repository.impl.ServicePackageRepository;
import main.java.backend.service.RecordWriter;
import main.java.records.OperationResult;

import java.util.ArrayList;
import java.util.List;

public class ServiceActivityDeletion implements RecordWriter {

    private final ServiceActivityRepository serviceActRepository;
    private final ServicePackageRepository servicePackageRepository;
    private final String serviceActID;

    public ServiceActivityDeletion(ServiceActivityRepository serviceActRepository,
                                   ServicePackageRepository servicePackageRepository, String serviceActID) {
        this.serviceActRepository = serviceActRepository;
        this.servicePackageRepository = servicePackageRepository;
        this.serviceActID = serviceActID;
    }

    @Override
    public OperationResult execute() {

        // Check if the service activity exists with the given ID
        ServiceActivity existingServiceAct = serviceActRepository.getRecordById(serviceActID);
        if (existingServiceAct == null) {
            return OperationResult.failed("The service activity with ID: " + serviceActID +
                    " does not exist in the system.");
        }

        // Get all the service packages that have this service activity
        List<ServicePackage> involvedSvcPackages = servicePackageRepository.
                getRecordsByServiceActivity(existingServiceAct);

        // Check if any of the involved service packages would be left with no service activities after deletion
        List<String> packagesHaveNoServiceActs = involvedSvcPackages.stream()
                .filter(pkg -> pkg.getServiceActivityList().size() == 1
                        && pkg.getServiceActivityList().contains(existingServiceAct))
                .map(ServicePackage::getServicePackageID)
                .toList();

        if (!packagesHaveNoServiceActs.isEmpty()) {
            return OperationResult.failed("The service activity with ID: " + serviceActID + "cannot be deleted " +
                    "because the following service package(s) would be left with no service activities: " +
                    String.join(", ", packagesHaveNoServiceActs) + ". " +
                    "Each service package must contain at least one service activity. " +
                    "Please reassign or remove these packages first.");
        }

        // Update the involved packages by removing the service activity from their lists
        List<ServicePackage> packagesToUpdate = involvedSvcPackages.stream()
                .peek(pkg -> {
                    List<ServiceActivity> updatedActivities = new ArrayList<>(pkg.getServiceActivityList());
                    updatedActivities.remove(existingServiceAct);
                    pkg.setServiceActivityList(updatedActivities);
                })
                .toList();

        // Update all the service packages in the text file at once
        if (!packagesToUpdate.isEmpty()) {
            OperationResult packageUpdateResult = servicePackageRepository.bulkUpdateRecord(packagesToUpdate);

            if (!packageUpdateResult.isValid()) {
                return OperationResult.failed("Failed to update service packages linked to service activity ID "
                                + serviceActID + ". Deletion process has stopped here.");
            }
        }

        // Finally, delete the service activity record in the text file
        return serviceActRepository.deleteRecord(existingServiceAct);
    }
}
