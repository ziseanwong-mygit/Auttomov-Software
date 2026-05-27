package main.java.backend.service.serviceactivity;

import main.java.backend.model.ServiceActivity;
import main.java.backend.model.ServicePackage;
import main.java.backend.repository.impl.ServiceActivityRepository;
import main.java.backend.repository.impl.ServicePackageRepository;
import main.java.backend.service.RecordWriter;
import main.java.dtos.ServiceActivityDTO;
import main.java.records.FieldComparison;
import main.java.records.OperationResult;

import java.util.ArrayList;
import java.util.List;

public class ServiceActivityUpdate implements RecordWriter {

    private final ServiceActivityRepository serviceActRepository;
    private final ServicePackageRepository servicePackageRepository;
    private final ServiceActivityDTO serviceActivityDTO;

    public ServiceActivityUpdate(ServiceActivityRepository serviceActRepository,
                                 ServicePackageRepository servicePackageRepository,
                                 ServiceActivityDTO serviceActivityDTO) {
        this.serviceActRepository = serviceActRepository;
        this.servicePackageRepository = servicePackageRepository;
        this.serviceActivityDTO = serviceActivityDTO;
    }

    @Override
    public OperationResult execute() {

        // Check if the service activity exists with the given ID
        ServiceActivity existingServiceAct = serviceActRepository.getRecordById(
                serviceActivityDTO.getServiceActivityID());
        if (existingServiceAct == null) {
            return OperationResult.failed("The service activity with ID: " +
                    serviceActivityDTO.getServiceActivityID() + " does not exist in the system.");
        }

        // Get the list of fields that are changed
        List<FieldComparison<?>> changedFields = compareChanges(existingServiceAct);

        // If no fields are changed, return an error message
        if (changedFields.isEmpty()) {
            return OperationResult.failed("No fields have been changed. " +
                    "Please update one or more fields.");
        }

        // Get the old price of the service activity before setting the new price
        double oldPrice = existingServiceAct.getServiceActivityPrice();

        // Apply updates only to changed fields
        setChangedValues(changedFields, existingServiceAct);

        // Begin updating service activity record in the repository
        OperationResult updateResult = serviceActRepository.updateRecord(existingServiceAct);
        if (!updateResult.isValid()) { // If the update fails, return the error message
            return updateResult;
        }

        // Check if the service activity price has been updated
        boolean isPriceUpdated = false;
        for (FieldComparison<?> field : changedFields) {
            if (field.fieldName().equals("price")) {
                isPriceUpdated = true;
            }
        }

        // If the price has been updated, update the service packages that involve this service activity
        if (isPriceUpdated) {

            // Accumulate all the service packages that are updated
            List<ServicePackage> packagesToUpdate = new ArrayList<>();

            // Get all the service packages that involve this service activity
            List<ServicePackage> involvedPackages = servicePackageRepository
                    .getRecordsByServiceActivity(existingServiceAct);

            // Price is updated here
            for (ServicePackage pkg : involvedPackages) {
                double newBasePrice = (pkg.getPackageBasePrice() - oldPrice) +
                        serviceActivityDTO.getServiceActivityPrice();
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
     * Compare the values between the existing service activity and incoming inputs (DTO) to get the changed fields
     */
    private List<FieldComparison<?>> compareChanges(ServiceActivity existingServiceAct) {

        // Declare a list to store all the fields for comparison later
        List<FieldComparison<?>> serviceActFields = new ArrayList<>();

        serviceActFields.add(new FieldComparison<>("name", existingServiceAct.getServiceActivityName(),
                serviceActivityDTO.getServiceActivityName(), null));
        serviceActFields.add(new FieldComparison<>("description",
                existingServiceAct.getServiceActivityDescription(), serviceActivityDTO.getServiceActivityDescription(),
                null));
        serviceActFields.add(new FieldComparison<>("price", existingServiceAct.getServiceActivityPrice(),
                serviceActivityDTO.getServiceActivityPrice(), null));

        // Filter to get the changed fields
        return serviceActFields.stream()
                .filter(FieldComparison::isChanged)
                .toList();
    }

    /**
     * Set the new values of the changed fields in the existing service activity object
     * The object will be used to update the record in the file
     */
    private void setChangedValues(List<FieldComparison<?>> changedFields, ServiceActivity existingServiceAct) {
        for (FieldComparison<?> field : changedFields) {
            switch (field.fieldName()) {
                case "name" -> existingServiceAct.setServiceActivityName(
                        serviceActivityDTO.getServiceActivityName());
                case "description" -> existingServiceAct.setServiceActivityDescription(
                        serviceActivityDTO.getServiceActivityDescription());
                case "price" -> existingServiceAct.setServiceActivityPrice(
                        serviceActivityDTO.getServiceActivityPrice());
            }
        }
    }
}