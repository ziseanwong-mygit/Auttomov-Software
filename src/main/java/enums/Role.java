package main.java.enums;

public enum Role {
    CUSTOMER("Customer"),
    STAFF("Staff");

    private final String userRole;

    Role(String userRole) {
        this.userRole = userRole;
    }

    public String getUserRole() {
        return userRole;
    }

    public static Role getRoleByValue(String value) {
        for (Role r : Role.values()) {
            if (r.userRole.equalsIgnoreCase(value)) {
                return r;
            }
        }

        // If not found, return null
        return null;
    }
}
