package main.java.backend.service.vehiclepart;

import main.java.backend.model.ServicePackage;
import main.java.backend.model.VehiclePart;
import main.java.backend.repository.impl.ServicePackageRepository;
import main.java.backend.repository.impl.VehiclePartRepository;
import main.java.backend.service.RecordWriter;
import main.java.records.OperationResult;

import java.util.ArrayList;
import java.util.List;

public class VehiclePartDeletion implements RecordWriter {

    private final VehiclePartRepository vehiclePartRepository;
    private final ServicePackageRepository servicePackageRepository;
    private final String vehiclePartID;

    public VehiclePartDeletion(VehiclePartRepository vehiclePartRepository,
                               ServicePackageRepository servicePackageRepository, String vehiclePartID) {
        this.vehiclePartRepository = vehiclePartRepository;
        this.servicePackageRepository = servicePackageRepository;
        this.vehiclePartID = vehiclePartID;
    }

    @Override
    public OperationResult execute() {

        // Check if the vehicle part exists with the given ID
        VehiclePart existingVehiclePart = vehiclePartRepository.getRecordById(vehiclePartID);
        if (existingVehiclePart == null) {
            return OperationResult.failed("The vehicle part with ID: " + vehiclePartID +
                    " does not exist in the system.");
        }

        // Get all the service packages that have this vehicle part
        List<ServicePackage> involvedSvcPackages = servicePackageRepository.getRecordsByVehiclePart(
                existingVehiclePart);

        // Update the involved packages by removing the vehicle part from their lists
        List<ServicePackage> packagesToUpdate = involvedSvcPackages.stream()
                .peek(pkg -> {
                    List<VehiclePart> updatedParts = new ArrayList<>(pkg.getVehiclePartList());
                    updatedParts.remove(existingVehiclePart);
                    pkg.setVehiclePartList(updatedParts);
                })
                .toList();

        // Update all the service packages in the text file at once
        if (!packagesToUpdate.isEmpty()) {
            OperationResult packageUpdateResult = servicePackageRepository.bulkUpdateRecord(packagesToUpdate);

            if (!packageUpdateResult.isValid()) {
                return OperationResult.failed("Failed to update service packages linked to vehicle part ID "
                        + vehiclePartID + ". Deletion process has stopped.");
            }
        }

        // Finally, delete the vehicle part record in the text file
        return vehiclePartRepository.deleteRecord(existingVehiclePart);
    }
}
