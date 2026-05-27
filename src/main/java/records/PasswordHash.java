package main.java.records;

public record PasswordHash(String pswdHash, String hashSalt) {

    /**
     * Separate the colon pair to hash and salt
     * Used for reading data from file and comparing passwords for authentication
     */
    public static PasswordHash toRecord(String pswdColumn) {
        String[] data = pswdColumn.split(":");
        return new PasswordHash(data[0], data[1]);
    }

    /**
     * Combine both hash and salt with colon to act as one column
     * Used for inserting data into file
     */
    @Override
    public String toString() {
        return pswdHash + ":" + hashSalt;
    }
}
