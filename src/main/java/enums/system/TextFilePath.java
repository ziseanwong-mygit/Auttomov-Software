package main.java.enums.system;

public enum TextFilePath {
    CUSTOMER_ACCOUNT_FILE_PATH("../APUASC-Auttomov/src/main/java/textfiles/customer-accounts.txt"),
    STAFF_ACCOUNT_FILE_PATH("../APUASC-Auttomov/src/main/java/textfiles/staff-accounts.txt"),
    SERVICE_ACTIVITIES_FILE_PATH("../APUASC-Auttomov/src/main/java/textfiles/service-activities.txt"),
    VEHICLE_PARTS_FILE_PATH("../APUASC-Auttomov/src/main/java/textfiles/vehicle-parts.txt"),
    SERVICE_PACKAGE_FILE_PATH("../APUASC-Auttomov/src/main/java/textfiles/service-packages.txt"),
    APPOINTMENT_RECORDS_FILE_PATH("../APUASC-Auttomov/src/main/java/textfiles/appointment-records.txt"),
    APPOINTMENT_COMMENTS_FILE_PATH("../APUASC-Auttomov/src/main/java/textfiles/appointment-comments.txt"),
    APPOINTMENT_FEEDBACK_FILE_PATH("../APUASC-Auttomov/src/main/java/textfiles/appointment-feedback.txt"),
    INVOICE_RECORDS_FILE_PATH("../APUASC-Auttomov/src/main/java/textfiles/invoice-records.txt"),
    PAYMENT_RECORDS_FILE_PATH("../APUASC-Auttomov/src/main/java/textfiles/payment-records.txt"),
    RECEIPT_RECORDS_FILE_PATH("../APUASC-Auttomov/src/main/java/textfiles/receipt-records.txt");

    private final String textFilePath;

    TextFilePath(String textFilePath) {
        this.textFilePath = textFilePath;
    }

    public String getTextFilePath() {
        return textFilePath;
    }
}
