package main.java.enums;

public enum AccountStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    SUSPENDED("Suspended");

    private final String accStatus;

    AccountStatus(String accStatus) {
        this.accStatus = accStatus;
    }

    public String getAccStatus() {
        return accStatus;
    }
}
