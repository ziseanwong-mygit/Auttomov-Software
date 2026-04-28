package main.java.backend.dto;

import main.java.enums.ServiceItemType;

public class CarComponent extends ServiceItem {

    private String componentType;
    private String componentBrand;
    private String componentSpec;

    public CarComponent(String serviceItemID, String serviceItemName, String serviceItemDescription,
                        ServiceItemType serviceItemType, double serviceItemPrice, String componentType,
                        String componentBrand, String componentSpec) {

        super(serviceItemID, serviceItemName, serviceItemDescription, serviceItemType, serviceItemPrice);

        this.componentType = componentType;
        this.componentBrand = componentBrand;
        this.componentSpec = componentSpec;
    }

    public String getComponentType() {
        return componentType;
    }

    public String getComponentBrand() {
        return componentBrand;
    }

    public String getComponentSpec() {
        return componentSpec;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    public void setComponentBrand(String componentBrand) {
        this.componentBrand = componentBrand;
    }

    public void setComponentSpec(String componentSpec) {
        this.componentSpec = componentSpec;
    }
}
