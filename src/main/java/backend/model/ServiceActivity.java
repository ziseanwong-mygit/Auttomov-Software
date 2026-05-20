package main.java.backend.model;

import main.java.backend.util.DataFormatter;

public class ServiceActivity {

    private final String serviceActivityID;
    private String serviceActivityName;
    private String serviceActivityDescription;
    private double serviceActivityPrice;

    public ServiceActivity(String serviceActivityID, String serviceActivityName, String serviceActivityDescription, 
                           double serviceActivityPrice) {
        this.serviceActivityID = serviceActivityID;
        this.serviceActivityName = serviceActivityName;

        // If the description is null or blank, set its value as null
        // So the codebase can view it as either string or null value, not ""
        this.serviceActivityDescription =
                (serviceActivityDescription == null  || serviceActivityDescription.isBlank())
                        ? null
                        : serviceActivityDescription;

        this.serviceActivityPrice = serviceActivityPrice;
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

    public void setServiceActivityName(String serviceActivityName) {
        this.serviceActivityName = serviceActivityName;
    }

    public void setServiceActivityDescription(String serviceActivityDescription) {
        this.serviceActivityDescription = serviceActivityDescription;
    }

    public void setServiceActivityPrice(double serviceActivityPrice) {
        this.serviceActivityPrice = serviceActivityPrice;
    }

    /**
     * Return a human-readable string representation of the object.
     * Used for debugging and logging.
     */
    @Override
    public String toString() {
        return String.join(", ",
                serviceActivityID,
                serviceActivityName,
                serviceActivityDescription != null ? serviceActivityDescription : "(no description)",
                DataFormatter.roundOffDecimal(serviceActivityPrice)
        );
    }

    /**
     * Return a CSV format string representation of the object.
     * Used for saving the object to a text file.
     */
    public String toTextFile() {
        return String.join(", ",
                serviceActivityID,
                serviceActivityName,
                serviceActivityDescription != null ? serviceActivityDescription : "",
                DataFormatter.roundOffDecimal(serviceActivityPrice)
        );
    }
}
