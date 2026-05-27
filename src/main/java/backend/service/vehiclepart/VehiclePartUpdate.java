package main.java.backend.service.vehiclepart;

import main.java.backend.model.ServicePackage;
import main.java.backend.model.VehiclePart;
import main.java.backend.repository.impl.ServicePackageRepository;
import main.java.backend.repository.impl.VehiclePartRepository;
import main.java.backend.service.RecordWriter;
import main.java.dtos.VehiclePartDTO;
import main.java.records.FieldComparison;
import main.java.records.OperationResult;

import java.util.ArrayList;
import java.util.List;

public class VehiclePartUpdate implements RecordWriter {
    
    private final VehiclePartRepository vehiclePartRepository;
    private final ServicePackageRepository servicePackageRepository;
    private final VehiclePartDTO vehiclePartDTO;
    
    public VehiclePartUpdate(VehiclePartRepository vehiclePartRepository,
                             ServicePackageRepository servicePackageRepository,
                             VehiclePartDTO vehiclePartDTO) {
        this.vehiclePartRepository = vehiclePartRepository;
        this.servicePackageRepository = servicePackageRepository;
        this.vehiclePartDTO = vehiclePartDTO;
    }
    
    @Override
    public OperationResult execute() {

        // Check if the vehicle part exists with the given ID
        VehiclePart existingVehiclePart = vehiclePartRepository.getRecordById(vehiclePartDTO.getVehiclePartID());
        if (existingVehiclePart == null) {
            return OperationResult.failed("The vehicle part with ID: " + vehiclePartDTO.getVehiclePartID() +
                    " does not exist in the system.");
        }

        // Get the list of fields that are changed
        List<FieldComparison<?>> changedFields = compareChanges(existingVehiclePart);

        // If no fields are changed, return an error message
        if (changedFields.isEmpty()) {
            return OperationResult.failed("No fields have been changed. " +
                    "Please update one or more fields.");
        }

        // Get the old price of the vehicle part before setting the new price
        double oldPrice = existingVehiclePart.getVehiclePartPrice();

        // Apply updates only to changed fields
        setChangedValues(changedFields, existingVehiclePart);

        // Begin updating vehicle part record in the repository
        OperationResult updateResult = vehiclePartRepository.updateRecord(existingVehiclePart);
        if (!updateResult.isValid()) { // If the update fails, return the error message
            return updateResult;
        }

        // Check if the vehicle part price has been updated
        boolean isPriceUpdated = false;
        for (FieldComparison<?> field : changedFields) {
            if (field.fieldName().equals("price")) {
                isPriceUpdated = true;
            }
        }

        // If the price has been updated, update the service packages that involve this vehicle part
        if (isPriceUpdated) {

            // Accumulate all the service packages that are updated
            List<ServicePackage> packagesToUpdate = new ArrayList<>();

            // Get all the service packages that involve this vehicle part
            List<ServicePackage> involvedPackages = servicePackageRepository
                    .getRecordsByVehiclePart(existingVehiclePart);

            // Price is updated here
            for (ServicePackage pkg : involvedPackages) {
                double newBasePrice = (pkg.getPackageBasePrice() - oldPrice) + vehiclePartDTO.getVehiclePartPrice();
                pkg.setPackageBasePrice(newBasePrice);
                packagesToUpdate.add(pkg);
            }

            // Update all the service packages in the text file at once
            return servicePackageRepository.bulkUpdateRecord(packagesToUpdate);
        }

        // If no price update, return success
        return OperationResult.success();
    }

    /**
     * Compare the values between the existing vehicle part and incoming inputs (DTO) to get the changed fields
     */
    private List<FieldComparison<?>> compareChanges(VehiclePart existingVehiclePart) {

        // Declare a list to store all the fields for comparison later
        List<FieldComparison<?>> vehiclePartFields = new ArrayList<>();
        
        vehiclePartFields.add(new FieldComparison<>("name", existingVehiclePart.getVehiclePartName(), 
                vehiclePartDTO.getVehiclePartName(), null));
        vehiclePartFields.add(new FieldComparison<>("description", 
                existingVehiclePart.getVehiclePartDescription(), vehiclePartDTO.getVehiclePartDescription(), 
                null));
        vehiclePartFields.add(new FieldComparison<>("type", existingVehiclePart.getVehiclePartType(), 
                vehiclePartDTO.getVehiclePartType(), null));
        vehiclePartFields.add(new FieldComparison<>("brand", existingVehiclePart.getVehiclePartBrand(), 
                vehiclePartDTO.getVehiclePartBrand(), null));
        vehiclePartFields.add(new FieldComparison<>("spec", existingVehiclePart.getVehiclePartSpec(), 
                vehiclePartDTO.getVehiclePartSpec(), null));
        vehiclePartFields.add(new FieldComparison<>("price", existingVehiclePart.getVehiclePartPrice(), 
                vehiclePartDTO.getVehiclePartPrice(), null));
        vehiclePartFields.add(new FieldComparison<>("quantity", existingVehiclePart.getVehiclePartQuantity(), 
                vehiclePartDTO.getVehiclePartQuantity(), null));

        // Filter to get the changed fields
        return vehiclePartFields.stream()
                .filter(FieldComparison::isChanged)
                .toList();
    }

    /**
     * Set the new values of the changed fields in the existing vehicle part object
     * The object will be used to update the record in the file
     */
    private void setChangedValues(List<FieldComparison<?>> changedFields, VehiclePart existingVehiclePart) {
        for (FieldComparison<?> field : changedFields) {
            switch (field.fieldName()) {
                case "name" -> existingVehiclePart.setVehiclePartName(vehiclePartDTO.getVehiclePartName());
                case "description" -> existingVehiclePart.setVehiclePartDescription(
                        vehiclePartDTO.getVehiclePartDescription());
                case "type" -> existingVehiclePart.setVehiclePartType(vehiclePartDTO.getVehiclePartType());
                case "brand" -> existingVehiclePart.setVehiclePartBrand(vehiclePartDTO.getVehiclePartBrand());
                case "spec" -> existingVehiclePart.setVehiclePartSpec(vehiclePartDTO.getVehiclePartSpec());
                case "price" -> existingVehiclePart.setVehiclePartPrice(vehiclePartDTO.getVehiclePartPrice());
                case "quantity" -> existingVehiclePart.setVehiclePartQuantity(
                        vehiclePartDTO.getVehiclePartQuantity());
            }
        }
    }
}
