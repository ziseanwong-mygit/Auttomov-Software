package main.java.enums;

public enum VehicleServiceType {
    NORMAL("Normal"),
    MAJOR("Major");

    private final String carServiceType;

    VehicleServiceType(String carServiceType) {
        this.carServiceType = carServiceType;
    }

    public String getCarServiceType() {
        return carServiceType;
    }

    public static VehicleServiceType getServiceTypeByValue(String value) {
        for (VehicleServiceType cst : VehicleServiceType.values()) {
            if (cst.carServiceType.equalsIgnoreCase(value)) {
                return cst;
            }
        }

        // If not found, throw an exception
        throw new IllegalArgumentException("Invalid car service type: " + value);
    }
}
