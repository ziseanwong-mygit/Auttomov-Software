package main.java.backend.model;

import main.java.backend.util.DataTypeConverter;
import main.java.enums.data.ServicePackageType;

import java.util.List;

public class ServicePackage {

    private final String servicePackageID;
    private String servicePackageName;
    private String servicePackageDescription;
    private ServicePackageType servicePackageType;
    private List<String> serviceActivityList;
    private List<String> vehiclePartList;
    private double packageBasePrice;
    private int serviceDuration;

    public ServicePackage(String servicePackageID, String servicePackageName, String servicePackageDescription,
                          ServicePackageType servicePackageType, List<String> serviceActivityList,
                          List<String> vehiclePartList, double packageBasePrice, int serviceDuration) {
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

    public List<String> getServiceActivityList() {
        return serviceActivityList;
    }

    public List<String> getVehiclePartList() {
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

    public void setServiceActivityList(List<String> serviceActivityList) {
        this.serviceActivityList = serviceActivityList;
    }

    public void setVehiclePartList(List<String> vehiclePartList) {
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
        return String.join(", ",
                servicePackageID,
                servicePackageName,
                servicePackageDescription != null ? servicePackageDescription : "(no description)",
                servicePackageType.getServicePackageType(),
                "[" + String.join(", ", serviceActivityList) + "]", // E.g. [A0001, A0002, A0003]
                "[" + String.join(", ", vehiclePartList) + "]",
                DataTypeConverter.convertDoubleToString(packageBasePrice),
                DataTypeConverter.convertIntToString(serviceDuration)
        );
    }

    /**
     * Return a CSV format string representation of the object.
     * Used for saving the object to a text file.
     */
    public String toTextFile() {
        return String.join(", ",
                servicePackageID,
                servicePackageName,
                servicePackageDescription != null ? servicePackageDescription : "",
                servicePackageType.getServicePackageType(),
                "[" + String.join(", ", serviceActivityList) + "]",
                "[" + String.join(", ", vehiclePartList) + "]",
                DataTypeConverter.convertDoubleToString(packageBasePrice),
                DataTypeConverter.convertIntToString(serviceDuration)
        );
    }
}
