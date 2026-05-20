package main.java.dtos;

public class ServiceActivityDTO {

    private String serviceActivityID;
    private final String serviceActivityName;
    private final String serviceActivityDescription;
    private final double serviceActivityPrice;

    /**
     * For creating a new service activity
     */
    public ServiceActivityDTO(String serviceActivityName, String serviceActivityDescription,
                              double serviceActivityPrice) {
        this.serviceActivityName = serviceActivityName;
        this.serviceActivityDescription = serviceActivityDescription;
        this.serviceActivityPrice = serviceActivityPrice;
    }

    /**
     * For updating an existing service activity's details
     */
    public ServiceActivityDTO(String serviceActivityID, String serviceActivityName,
                              String serviceActivityDescription, double serviceActivityPrice) {
        this(serviceActivityName, serviceActivityDescription, serviceActivityPrice); // Constructor chaining
        this.serviceActivityID = serviceActivityID;
    }

    public String getServiceActivityID() {
        return serviceActivityID;
    }

    public String getServiceActivityName() {
        return serviceActivityName;
    }

    public String getServiceActivityDescription() {
        return serviceActivityDescription;
    }

    public double getServiceActivityPrice() {
        return serviceActivityPrice;
    }
}
