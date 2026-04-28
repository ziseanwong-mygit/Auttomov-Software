package main.java.enums;

public enum ServiceItemType {
    ACTIVITY("Service Activity"),
    COMPONENT("Car Component");

    private final String serviceItemType;

    ServiceItemType(String serviceItemType) {
        this.serviceItemType = serviceItemType;
    }

    public String getServiceItemType() {
        return serviceItemType;
    }

    public static ServiceItemType getServiceItemTypeByValue(String value) {
        for (ServiceItemType sit : ServiceItemType.values()) {
            if (sit.serviceItemType.equalsIgnoreCase(value)) {
                return sit;
            }
        }

        // If not found, throw an exception
        throw new IllegalArgumentException("Invalid car service item type: " + value);
    }
}
