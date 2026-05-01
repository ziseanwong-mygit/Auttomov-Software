package main.java.backend.model;

import main.java.enums.ServiceItemType;

public class ServiceItem {

    private String serviceItemID;
    private String serviceItemName;
    private String serviceItemDescription;
    private ServiceItemType serviceItemType;
    private double serviceItemPrice;

    public ServiceItem(String serviceItemID, String serviceItemName, String serviceItemDescription,
                       ServiceItemType serviceItemType, double serviceItemPrice) {
        this.serviceItemID = serviceItemID;
        this.serviceItemName = serviceItemName;
        this.serviceItemDescription = serviceItemDescription;
        this.serviceItemType = serviceItemType;
        this.serviceItemPrice = serviceItemPrice;
    }

    public String getServiceItemID() {
        return serviceItemID;
    }

    public String getServiceItemName() {
        return serviceItemName;
    }

    public String getServiceItemDescription() {
        return serviceItemDescription;
    }

    public ServiceItemType getServiceItemType() {
        return serviceItemType;
    }

    public double getServiceItemPrice() {
        return serviceItemPrice;
    }

    public void setServiceItemName(String serviceItemName) {
        this.serviceItemName = serviceItemName;
    }

    public void setServiceItemDescription(String serviceItemDescription) {
        this.serviceItemDescription = serviceItemDescription;
    }

    public void setServiceItemType(ServiceItemType serviceItemType) {
        this.serviceItemType = serviceItemType;
    }

    public void setServiceItemPrice(double serviceItemPrice) {
        this.serviceItemPrice = serviceItemPrice;
    }
}
