package main.java.backend.repository;

import main.java.backend.model.ServiceActivity;
import main.java.backend.model.ServicePackage;
import main.java.backend.model.VehiclePart;
import main.java.backend.repository.general.AbstractRepository;
import main.java.backend.util.DataStructureOrganizer;
import main.java.backend.util.DataTypeConverter;
import main.java.enums.system.TextFilePath;
import main.java.records.OperationResult;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class ServicePackageRepository extends AbstractRepository<ServicePackage>  {

    private static ServicePackageRepository instance;
    private final String filePath = TextFilePath.SERVICE_PACKAGE_FILE_PATH.getTextFilePath();

    // Map key: field value, map value: ServicePackage object
    private final Map<String, ServicePackage> servicePackageById = new HashMap<>();

    // Private constructor to prevent instantiations from outside
    // Pre-populate the records into maps when the instance is created
    private ServicePackageRepository() {
        retrieveRecords();
    }

    // Global access point by creating instance for once only
    // So all classes can access the same instance for reading and writing files
    public static ServicePackageRepository getInstance() {
        if (instance == null) {
            instance = new ServicePackageRepository();
        }
        return instance;
    }

    @Override
    public String getFilePath() {
        return filePath;
    }

    @Override
    public OperationResult createRecord(ServicePackage servicePkgToCreate) {

        try (BufferedWriter bufferWrite = new BufferedWriter(new FileWriter(getFilePath(), true))) {

            // Write the new service package record into the file
            bufferWrite.write(servicePkgToCreate.toTextFile());
            bufferWrite.newLine();

            // Add the new service package records into the maps to ensure the latest changes are reflected
            addRecordIntoMap(servicePkgToCreate);

        } catch (IOException e) {
            System.out.println(e.getMessage()); // Print exception message for debugging
            return OperationResult.failed(
                    "Oops! There was an error writing the new service package record. " +
                            "Please try again.");
        }

        return OperationResult.success(
                "The service package record has been created successfully. " +
                        "Please check the record to verify its creation.");
    }

    @Override
    public OperationResult updateRecord(ServicePackage servicePkgToUpdate) {

        String servicePkgID = servicePkgToUpdate.getServicePackageID();

        // Map the target service package object by its ID
        Map<String, Function<String, Optional<String>>> modifier = Map.of(
                servicePkgID, _ -> Optional.of(servicePkgToUpdate.toTextFile())
        );

        // Rewrite the file to update the target record
        OperationResult result = rewriteFile(modifier);
        if (!result.isValid()) {
            return result;
        }

        // Replace the old record with the updated one to apply the latest changes
        removeRecordIntoMap(servicePackageById.get(servicePkgID));
        addRecordIntoMap(servicePkgToUpdate);

        return OperationResult.success(
                "The service package with ID " + servicePkgID + " has been updated successfully. "
                        + "Please check the details to ensure the changes are applied.");
    }

    @Override
    public OperationResult deleteRecord(ServicePackage servicePkgToDelete) {

        String servicePkgID = servicePkgToDelete.getServicePackageID();

        // Map the target service package object by its ID
        Map<String, Function<String, Optional<String>>> modifier = Map.of(
                servicePkgID, _ -> Optional.empty()
        );

        // Rewrite the file to delete the target record
        OperationResult result = rewriteFile(modifier);
        if (!result.isValid()) {
            return result;
        }

        // Remove the deleted record from the maps to apply the latest changes
        removeRecordIntoMap(servicePkgToDelete);

        return OperationResult.success(
                "The service package with ID " + servicePkgID + " has been deleted successfully. "
                        + "Please check the records to verify its deletion.");
    }

    @Override
    public List<ServicePackage> getAllRecords() {
        return new ArrayList<>(servicePackageById.values());
    }

    @Override
    public ServicePackage getRecordById(String serviceActId) {
        return servicePackageById.get(serviceActId);
    }

    @Override
    public List<ServicePackage> getRecordsByIds(List<String> servicePkgIds) {
        // Filter the service activity objects by their IDs
        return servicePackageById.values().stream()
                .filter(serviceAct -> servicePkgIds.contains(serviceAct.getServicePackageID()))
                .toList();
    }

    /**
     * Used for retrieving records from the text file
     * Service Package CSV format (8 columns): servicePackageID, servicePackageName, servicePackageDescription,
     * servicePackageType, serviceActivityList, vehiclePartList, packageBasePrice, serviceDuration
     */
    @Override
    protected ServicePackage parseRecord(String line) {

        ServiceActivityRepository serviceActivityRepository = ServiceActivityRepository.getInstance();
        VehiclePartRepository vehiclePartRepository = VehiclePartRepository.getInstance();

        // Extract both bracket lists from the raw line before any splitting begins
        // 0th occurrence = 1st bracket list, 1st occurrence = 2nd bracket list; nth occurrence = nth bracket list
        List<String> serviceActivityIds = DataStructureOrganizer.extractBracketList(line, 0);
        List<String> vehiclePartIds = DataStructureOrganizer.extractBracketList(line, 1);

        List<ServiceActivity> serviceActivityList = new ArrayList<>();
        List<VehiclePart> vehiclePartList = new ArrayList<>();
        if (!serviceActivityIds.isEmpty()) {
            serviceActivityList = serviceActivityRepository.getRecordsByIds(serviceActivityIds);
        }
        if (!vehiclePartIds.isEmpty()) {
            vehiclePartList = vehiclePartRepository.getRecordsByIds(vehiclePartIds);
        }

        // Remove the bracket lists from the line entirely, leaving only the simple comma-separated fields
        // Example: ID, Name, Desc, Type, [A0001, A0002], [B0001, B0002], 20.00, 1 ->
        // ID, Name, Desc, Type, , 20.00, 1
        String strippedLine = line.replaceAll("\\[.*?]", "")
                .replaceAll(",\\s*,", ",");

        // Without the bracket lists, split the line into an array of columns
        String[] columns = strippedLine.split(",\\s*");

        // If the description field is empty, set it to null, otherwise keep it as-is
        String description = columns[2].isBlank() ? null : columns[2];

        return new ServicePackage(
                columns[0],
                columns[1],
                description,
                DataTypeConverter.convertStringToServicePackageType(columns[3]),
                serviceActivityList,
                vehiclePartList,
                Double.parseDouble(columns[5]),
                Integer.parseInt(columns[6])
        );
    }

    /**
     * Add a new/updated service activity into all lookup maps
     */
    protected void addRecordIntoMap(ServicePackage servicePackage) {

        // End the method early if the service activity is not provided
        if (servicePackage == null) {
            return;
        }

        servicePackageById.put(servicePackage.getServicePackageID(), servicePackage);
    }

    /**
     * Remove the old/deleted service activity from all lookup maps
     */
    protected void removeRecordIntoMap(ServicePackage serviceActivity) {

        // End the method early if the service activity is not provided
        if (serviceActivity == null) {
            return;
        }

        servicePackageById.remove(serviceActivity.getServicePackageID());
    }
}
