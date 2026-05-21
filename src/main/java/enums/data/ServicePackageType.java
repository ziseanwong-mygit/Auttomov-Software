package main.java.enums.data;

public enum ServicePackageType {
    NORMAL("Normal", 1),
    MAJOR("Major", 3);

    private final String servicePackageType;
    private final int serviceDuration;

    ServicePackageType(String servicePkgType, int serviceDuration) {
        this.servicePackageType = servicePkgType;
        this.serviceDuration = serviceDuration;
    }

    public String getServicePkgType() {
        return servicePackageType;
    }

    public int getServiceDuration() {
        return serviceDuration;
    }

    public static ServicePackageType getServiceTypeByValue(String value) {
        for (ServicePackageType cst : ServicePackageType.values()) {
            if (cst.servicePackageType.equalsIgnoreCase(value)) {
                return cst;
            }
        }

        // If not found, throw an exception
        throw new IllegalArgumentException("Invalid car service type: " + value);
    }
}
