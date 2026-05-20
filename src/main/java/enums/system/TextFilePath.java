package main.java.enums.system;

public enum TextFilePath {
    CUSTOMER_ACCOUNT_FILE_PATH("../APUASC-Auttomov/src/main/java/textfiles/customer-accounts.txt"),
    STAFF_ACCOUNT_FILE_PATH("../APUASC-Auttomov/src/main/java/textfiles/staff-accounts.txt"),
    SERVICE_ACTIVITIES_FILE_PATH("../APUASC-Auttomov/src/main/java/textfiles/service-activities.txt"),
    VEHICLE_PARTS_FILE_PATH("../APUASC-Auttomov/src/main/java/textfiles/vehicle-parts.txt"),
    SERVICE_PACKAGE_FILE_PATH("../APUASC-Auttomov/src/main/java/textfiles/service-packages.txt");

    private final String textFilePath;

    TextFilePath(String textFilePath) {
        this.textFilePath = textFilePath;
    }

    public String getTextFilePath() {
        return textFilePath;
    }
}
