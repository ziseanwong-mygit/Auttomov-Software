package main.java.backend.service.servicepackage;

import main.java.backend.model.ServiceActivity;
import main.java.backend.model.ServicePackage;
import main.java.backend.model.VehiclePart;
import main.java.backend.repository.impl.ServicePackageRepository;
import main.java.backend.service.RecordWriter;
import main.java.dtos.ServicePackageDTO;
import main.java.records.FieldComparison;
import main.java.records.ListFieldComparison;
import main.java.records.OperationResult;

import java.util.ArrayList;
import java.util.List;

public class ServicePackageUpdate implements RecordWriter {

    private final ServicePackageRepository servicePackageRepository;
    private final ServicePackageDTO servicePackageDTO;
    private final List<ServiceActivity> newServiceActList;
    private final List<VehiclePart> newVehiclePartList;

    public ServicePackageUpdate(ServicePackageRepository servicePackageRepository,
                                ServicePackageDTO servicePackageDTO, List<ServiceActivity> newServiceActList,
                                List<VehiclePart> newVehiclePartList) {
        this.servicePackageRepository = servicePackageRepository;
        this.servicePackageDTO = servicePackageDTO;
        this.newServiceActList = newServiceActList;
        this.newVehiclePartList = newVehiclePartList;
    }

    @Override
    public OperationResult execute() {

        // Check if the service package exists with the given ID
        ServicePackage existingServicePackage = servicePackageRepository.getRecordById(
                servicePackageDTO.getServicePackageID());
        if (existingServicePackage == null) {
            return OperationResult.failed("The service package with ID: " + servicePackageDTO.getServicePackageID() +
                    " does not exist in the system.");
        }

        // Get the list of fields that are changed
        List<FieldComparison<?>> changedFields = compareChanges(existingServicePackage);

        // Get the list of lists that are changed
        List<ListFieldComparison> changedLists  = compareListChanges(existingServicePackage);

        // If nothing changed at all across both kinds
        if (changedFields.isEmpty() && changedLists.isEmpty()) {
            return OperationResult.failed(
                    "No fields have been changed. Please update one or more fields.");
        }

        // Apply updates only to changed fields
        setChangedValues(changedFields, changedLists, existingServicePackage);

        // Begin updating service package record in the repository
        return servicePackageRepository.updateRecord(existingServicePackage);
    }

    /**
     * Compare the values between the existing service package and incoming inputs (DTO) to get the changed fields
     */
    private List<FieldComparison<?>> compareChanges(ServicePackage existingServicePackage) {

        // Declare a list to store all the fields for comparison later
        List<FieldComparison<?>> servicePkgFields = new ArrayList<>();

        servicePkgFields.add(new FieldComparison<>("name", existingServicePackage.getServicePackageName(),
                servicePackageDTO.getServicePackageName(), null));
        servicePkgFields.add(new FieldComparison<>("description",
                existingServicePackage.getServicePackageDescription(), servicePackageDTO.getServicePackageDescription(),
                null));
        servicePkgFields.add(new FieldComparison<>("type", existingServicePackage.getServicePackageType(),
                servicePackageDTO.getServicePackageType(), null));
        servicePkgFields.add(new FieldComparison<>("price", existingServicePackage.getPackageBasePrice(),
                servicePackageDTO.getPackageBasePrice(), null));
        servicePkgFields.add(new FieldComparison<>("duration",
                existingServicePackage.getServicePackageType().getServiceDuration(),
                servicePackageDTO.getServicePackageType().getServiceDuration(), null));

        // Filter to get the changed fields
        return servicePkgFields.stream()
                .filter(FieldComparison::isChanged)
                .toList();
    }

    /**
     * Compare two lists between existing record and incoming inputs (DTO) to determine if any list is changed
     */
    private List<ListFieldComparison> compareListChanges(ServicePackage existingServicePackage) {

        // Declare a list to store all the fields for comparison later
        List<ListFieldComparison> listFields = new ArrayList<>();

        // Get the existing list of service activities and vehicle parts for comparison
        List<String> existingServiceActList = new ArrayList<>();
        List<String> existingVehiclePartList = new ArrayList<>();
        for (ServiceActivity serviceAct : existingServicePackage.getServiceActivityList() ) {
            existingServiceActList.add(serviceAct.getServiceActivityID());
        }
        for (VehiclePart vehiclePart : existingServicePackage.getVehiclePartList() ) {
            existingVehiclePartList.add(vehiclePart.getVehiclePartID());
        }

        // Get the new list of service activities and vehicle parts for comparison
        List<String > newServiceActList = new ArrayList<>();
        List<String > newVehiclePartList = new ArrayList<>();
        for (ServiceActivity serviceAct : this.newServiceActList) {
            newServiceActList.add(serviceAct.getServiceActivityID());
        }
        for (VehiclePart vehiclePart : this.newVehiclePartList) {
            newVehiclePartList.add(vehiclePart.getVehiclePartID());
        }

        listFields.add(new ListFieldComparison(
                "serviceActivities", existingServiceActList, newServiceActList));
        listFields.add(new ListFieldComparison(
                "vehicleParts", existingVehiclePartList, newVehiclePartList));

        // Filter to get the changed list fields
        return listFields.stream()
                .filter(ListFieldComparison::isChanged)
                .toList();
    }

    /**
     * Set the new values of the changed fields in the existing service package object
     * The object will be used to update the record in the file
     */
    private void setChangedValues(List<FieldComparison<?>> changedFields,
                                  List<ListFieldComparison> changedLists, ServicePackage existingServicePackage) {
        for (FieldComparison<?> field : changedFields) {
            switch (field.fieldName()) {
                case "name" -> existingServicePackage.setServicePackageName(servicePackageDTO.getServicePackageName());
                case "description" -> existingServicePackage.setServicePackageDescription(
                        servicePackageDTO.getServicePackageDescription());
                case "type" -> existingServicePackage.setServicePackageType(servicePackageDTO.getServicePackageType());
                case "price" -> existingServicePackage.setPackageBasePrice(servicePackageDTO.getPackageBasePrice());
                case "duration" -> existingServicePackage.setServiceDuration(
                        servicePackageDTO.getServicePackageType().getServiceDuration());
            }
        }

        // Get the actual objects from the repository first and store them into a list
        // Then set the list to the existing service package object
        for (ListFieldComparison listField : changedLists){
            switch (listField.fieldName()) {
                case "serviceActivities" -> existingServicePackage.setServiceActivityList(newServiceActList);
                case "vehicleParts" -> existingServicePackage.setVehiclePartList(newVehiclePartList);
            }
        }
    }
}
