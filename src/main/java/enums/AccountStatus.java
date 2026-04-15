package main.java.enums;

public enum AccountStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    PENDING("Pending"),
    DENIED("Denied"),
    SUSPENDED("Suspended");

    private final String accStatus;

    AccountStatus(String accStatus) {
        this.accStatus = accStatus;
    }

    public String getAccStatus() {
        return accStatus;
    }

    public static AccountStatus getAccStatusByValue(String value) {
        for (AccountStatus as : AccountStatus.values()) {
            if (as.accStatus.equalsIgnoreCase(value)) {
                return as;
            }
        }

        // If not found, return null
        return null;
    }
}
