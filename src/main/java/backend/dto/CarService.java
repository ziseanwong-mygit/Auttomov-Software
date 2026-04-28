package main.java.backend.dto;

import main.java.enums.CarServiceType;

import java.util.List;

public class CarService {

    private String serviceID;
    private String serviceName;
    private String serviceDescription;
    private CarServiceType serviceType;
    private List<String> serviceItemList;
    private double basePrice;
    private int serviceDuration;

    public CarService(String serviceID, String serviceName, String serviceDescription, CarServiceType serviceType,
                      List<String> serviceItemList, double basePrice, int serviceDuration) {
        this.serviceID = serviceID;
        this.serviceName = serviceName;
        this.serviceDescription = serviceDescription;
        this.serviceType = serviceType;
        this.serviceItemList = serviceItemList;
        this.basePrice = basePrice;
        this.serviceDuration = serviceDuration;
    }

    public String getServiceID() {
        return serviceID;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public CarServiceType getServiceType() {
        return serviceType;
    }

    public List<String> getServiceItemList() {
        return serviceItemList;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public int getServiceDuration() {
        return serviceDuration;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    public void setServiceType(CarServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public void setServiceItemList(List<String> serviceItemList) {
        this.serviceItemList = serviceItemList;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public void setServiceDuration(int serviceDuration) {
        this.serviceDuration = serviceDuration;
    }
}
