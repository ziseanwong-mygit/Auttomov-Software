package main.java.enums;

public enum CarServiceType {
    NORMAL("Normal"),
    MAJOR("Major");

    private final String carServiceType;

    CarServiceType(String carServiceType) {
        this.carServiceType = carServiceType;
    }

    public String getCarServiceType() {
        return carServiceType;
    }

    public static CarServiceType getServiceTypeByValue(String value) {
        for (CarServiceType cst : CarServiceType.values()) {
            if (cst.carServiceType.equalsIgnoreCase(value)) {
                return cst;
            }
        }

        // If not found, throw an exception
        throw new IllegalArgumentException("Invalid car service type: " + value);
    }
}
