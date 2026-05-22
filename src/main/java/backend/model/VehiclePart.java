package main.java.backend.model;

import main.java.backend.util.DataTypeConverter;

public class VehiclePart {

    private final String vehiclePartID;
    private String vehiclePartName;
    private String vehiclePartDescription;
    private String vehiclePartType;
    private String vehiclePartBrand;
    private String vehiclePartSpec;
    private double vehiclePartPrice;
    private int vehiclePartQuantity;
    
    public VehiclePart(String vehiclePartID, String vehiclePartName, String vehiclePartDescription,
                       String vehiclePartType, String vehiclePartBrand, String vehiclePartSpec,
                       double vehiclePartPrice, int vehiclePartQuantity) {
        this.vehiclePartID = vehiclePartID;
        this.vehiclePartName = vehiclePartName;
        this.vehiclePartDescription = vehiclePartDescription;
        this.vehiclePartType = vehiclePartType;
        this.vehiclePartBrand = vehiclePartBrand;
        this.vehiclePartSpec = vehiclePartSpec;
        this.vehiclePartPrice = vehiclePartPrice;
        this.vehiclePartQuantity = vehiclePartQuantity;
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

    public void setVehiclePartName(String vehiclePartName) {
        this.vehiclePartName = vehiclePartName;
    }

    public void setVehiclePartDescription(String vehiclePartDescription) {
        this.vehiclePartDescription = vehiclePartDescription;
    }

    public void setVehiclePartType(String vehiclePartType) {
        this.vehiclePartType = vehiclePartType;
    }

    public void setVehiclePartBrand(String vehiclePartBrand) {
        this.vehiclePartBrand = vehiclePartBrand;
    }

    public void setVehiclePartSpec(String vehiclePartSpec) {
        this.vehiclePartSpec = vehiclePartSpec;
    }

    public void setVehiclePartPrice(double vehiclePartPrice) {
        this.vehiclePartPrice = vehiclePartPrice;
    }

    public void setVehiclePartQuantity(int vehiclePartQuantity) {
        this.vehiclePartQuantity = vehiclePartQuantity;
    }

    /**
     * Return a human-readable string representation of the object.
     * Used for logging, debugging, testing, or printing overall information.
     */
    @Override
    public String toString() {
        return "Vehicle Part ID: " + vehiclePartID +
                "\nName: " + vehiclePartName +
                "\nDescription: " +
                (vehiclePartDescription != null ? vehiclePartDescription : "(no description)") +
                "\nType: " + vehiclePartType +
                "\nBrand: " + vehiclePartBrand +
                "\nSpecification: " + vehiclePartSpec +
                "\nPrice: RM" + DataTypeConverter.convertDoubleToString(vehiclePartPrice) +
                "\nQuantity: " + DataTypeConverter.convertIntToString(vehiclePartQuantity) +
                "\n";
    }
}
