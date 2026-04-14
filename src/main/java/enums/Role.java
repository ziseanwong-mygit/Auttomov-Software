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
}
