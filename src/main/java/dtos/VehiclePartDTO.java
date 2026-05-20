package main.java.dtos;

public class VehiclePartDTO {

    private String vehiclePartID;
    private final String vehiclePartName;
    private final String vehiclePartDescription;
    private final String vehiclePartType;
    private final String vehiclePartBrand;
    private final String vehiclePartSpec;
    private final double vehiclePartPrice;
    private final int vehiclePartQuantity;

    /**
     * For creating a new vehicle part
     */
    public VehiclePartDTO(String vehiclePartName, String vehiclePartDescription, String vehiclePartType,
                          String vehiclePartBrand, String vehiclePartSpec, double vehiclePartPrice,
                          int vehiclePartQuantity) {
        this.vehiclePartName = vehiclePartName;
        this.vehiclePartDescription = vehiclePartDescription;
        this.vehiclePartType = vehiclePartType;
        this.vehiclePartBrand = vehiclePartBrand;
        this.vehiclePartSpec = vehiclePartSpec;
        this.vehiclePartPrice = vehiclePartPrice;
        this.vehiclePartQuantity = vehiclePartQuantity;
    }

    /**
     * For updating an existing vehicle part's details
     */
    public VehiclePartDTO(String vehiclePartID, String vehiclePartName, String vehiclePartDescription,
                          String vehiclePartType, String vehiclePartBrand, String vehiclePartSpec,
                          double vehiclePartPrice, int vehiclePartQuantity) {
        this(vehiclePartName, vehiclePartDescription, vehiclePartType, vehiclePartBrand, vehiclePartSpec,
                vehiclePartPrice, vehiclePartQuantity); // Constructor chaining
        this.vehiclePartID = vehiclePartID;
    }

    public String getVehiclePartID() {
        return vehiclePartID;
    }

    public String getVehiclePartName() {
        return vehiclePartName;
    }

    public String getVehiclePartDescription() {
        return vehiclePartDescription;
    }

    public String getVehiclePartType() {
        return vehiclePartType;
    }

    public String getVehiclePartBrand() {
        return vehiclePartBrand;
    }

    public String getVehiclePartSpec() {
        return vehiclePartSpec;
    }

    public double getVehiclePartPrice() {
        return vehiclePartPrice;
    }

    public int getVehiclePartQuantity() {
        return vehiclePartQuantity;
    }
}
