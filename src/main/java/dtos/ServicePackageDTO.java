package main.java.dtos;

import main.java.enums.data.ServicePackageType;

import java.util.List;

public class ServicePackageDTO {

    private String servicePackageID;
    private String servicePackageName;
    private String servicePackageDescription;
    private ServicePackageType servicePackageType;
    private List<String> serviceActivityList;
    private List<String> vehiclePartList;
    private final double packageBasePrice;

    /**
     * For creating a new service package
     */
    public ServicePackageDTO(String servicePackageName, String servicePackageDescription,
                             ServicePackageType servicePackageType, List<String> serviceActivityList,
                             List<String> vehiclePartList, double packageBasePrice) {
        this.servicePackageName = servicePackageName;
        this.servicePackageDescription = servicePackageDescription;
        this.servicePackageType = servicePackageType;
        this.serviceActivityList = serviceActivityList;
        this.vehiclePartList = vehiclePartList;
        this.packageBasePrice = packageBasePrice;
    }

    /**
     * For updating an existing service package's details
     */
    public ServicePackageDTO(String servicePackageID, String servicePackageName, String servicePackageDescription,
                             ServicePackageType servicePackageType, List<String> serviceActivityList,
                             List<String> vehiclePartList, double packageBasePrice) {
        this(servicePackageName, servicePackageDescription, servicePackageType, serviceActivityList, vehiclePartList,
                packageBasePrice); // Constructor chaining
        this.servicePackageID = servicePackageID;
    }

    /**
     * For updating the price of an existing service package
     */
    public ServicePackageDTO(String servicePackageID, double packageBasePrice) {
        this.servicePackageID = servicePackageID;
        this.packageBasePrice = packageBasePrice;
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
}
