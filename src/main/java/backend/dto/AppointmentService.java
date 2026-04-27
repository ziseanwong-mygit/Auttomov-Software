package main.java.backend.dto;

public class AppointmentService {

    private String serviceID;
    private String serviceName;
    private String serviceDescription;
    private double servicePrice;
    private int serviceDuration;

    public AppointmentService(String serviceID, String serviceName, String serviceDescription,
                              double servicePrice, int serviceDuration) {
        this.serviceID = serviceID;
        this.serviceName = serviceName;
        this.serviceDescription = serviceDescription;
        this.servicePrice = servicePrice;
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

    public double getServicePrice() {
        return servicePrice;
    }

    public int getServiceDuration() {
        return serviceDuration;
    }
}
