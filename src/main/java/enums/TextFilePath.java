package main.java.enums;

public enum TextFilePath {
    CUSTOMER_ACCOUNT_FILE_PATH("../APUASC-Auttomov/src/main/java/textfiles/customer-accounts.txt"),
    STAFF_ACCOUNT_FILE_PATH("../APUASC-Auttomov/src/main/java/textfiles/staff-accounts.txt");

    private final String textFilePath;

    TextFilePath(String textFilePath) {
        this.textFilePath = textFilePath;
    }

    public String getTextFilePath() {
        return textFilePath;
    }
}
