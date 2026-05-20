package main.java.backend.model;

import main.java.backend.util.DataTypeConverter;
import main.java.enums.data.ServicePackageType;

import java.util.List;
import java.util.stream.Collectors;

public class ServicePackage {

    private final String servicePackageID;
    private String servicePackageName;
    private String servicePackageDescription;
    private ServicePackageType servicePackageType;
    private List<ServiceActivity> serviceActivityList;
    private List<VehiclePart> vehiclePartList;
    private double packageBasePrice;
    private int serviceDuration;

    public ServicePackage(String servicePackageID, String servicePackageName, String servicePackageDescription,
                          ServicePackageType servicePackageType, List<ServiceActivity> serviceActivityList,
                          List<VehiclePart> vehiclePartList, double packageBasePrice, int serviceDuration) {
        this.servicePackageID = servicePackageID;
        this.servicePackageName = servicePackageName;
        this.servicePackageDescription = servicePackageDescription;
        this.servicePackageType = servicePackageType;
        this.serviceActivityList = serviceActivityList;
        this.vehiclePartList = vehiclePartList;
        this.packageBasePrice = packageBasePrice;
        this.serviceDuration = serviceDuration;
    }

    public String getServicePackageID() {
        return servicePackageID;
    }

    public String getServicePackageName() {
        return servicePackageName;
    }

    public String getServicePackageDescription() {
        return servicePackageDescription;
    }

    public ServicePackageType getServicePackageType() {
        return servicePackageType;
    }

    public List<ServiceActivity> getServiceActivityList() {
        return serviceActivityList;
    }

    public List<VehiclePart> getVehiclePartList() {
        return vehiclePartList;
    }

    public double getPackageBasePrice() {
        return packageBasePrice;
    }

    public int getServiceDuration() {
        return serviceDuration;
    }

    public void setServicePackageName(String servicePackageName) {
        this.servicePackageName = servicePackageName;
    }

    public void setServicePackageDescription(String servicePackageDescription) {
        this.servicePackageDescription = servicePackageDescription;
    }

    public void setServicePackageType(ServicePackageType servicePackageType) {
        this.servicePackageType = servicePackageType;
    }

    public void setServiceActivityList(List<ServiceActivity> serviceActivityList) {
        this.serviceActivityList = serviceActivityList;
    }

    public void setVehiclePartList(List<VehiclePart> vehiclePartList) {
        this.vehiclePartList = vehiclePartList;
    }

    public void setPackageBasePrice(double packageBasePrice) {
        this.packageBasePrice = packageBasePrice;
    }

    public void setServiceDuration(int serviceDuration) {
        this.serviceDuration = serviceDuration;
    }

    /**
     * Return a human-readable string representation of the object.
     * Used for debugging and logging.
     */
    @Override
    public String toString() {

        // Get the service activity and vehicle part names and concatenate them into a comma-separated value string
        String serviceActListLine = serviceActivityList.stream()
                .map(ServiceActivity::getServiceActivityName)
                .collect(Collectors.joining(", "));

        String vehiclePartListLine = vehiclePartList.stream()
                .map(VehiclePart::getVehiclePartName)
                .collect(Collectors.joining(", "));

        return "Service Package ID: " +
                servicePackageID +
                "\nService Package Name: " + servicePackageName +
                "\nService Package Description: " +
                (servicePackageDescription != null ? servicePackageDescription : "(no description)") +
                "\nService Package Type: " + servicePackageType.getServicePackageType() +
                "\nService Activities: " + serviceActListLine +
                "\nVehicle Parts: " + vehiclePartListLine +
                "\nPackage Base Price: RM" + DataTypeConverter.convertDoubleToString(packageBasePrice) +
                "\nService Duration: " + serviceDuration +
                "\n";
    }

    /**
     * Return a CSV format string representation of the object.
     * Used for saving the object to a text file.
     */
    public String toTextFile() {

        // Get the service activity and vehicle part IDs and concatenate them into a comma-separated value string
        String serviceActListLine = serviceActivityList.stream()
                .map(ServiceActivity::getServiceActivityID)
                .collect(Collectors.joining(", "));

        String vehiclePartListLine = vehiclePartList.stream()
                .map(VehiclePart::getVehiclePartID)
                .collect(Collectors.joining(", "));

        return String.join(", ",
                servicePackageID,
                servicePackageName,
                servicePackageDescription != null ? servicePackageDescription : "",
                servicePackageType.getServicePackageType(),
                "[" + serviceActListLine + "]",
                "[" + vehiclePartListLine + "]",
                DataTypeConverter.convertDoubleToString(packageBasePrice),
                DataTypeConverter.convertIntToString(serviceDuration)
        );
    }
}
