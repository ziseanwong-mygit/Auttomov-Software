package main.java.backend.repository.impl.account;

import main.java.backend.model.UserAccount;
import main.java.backend.util.DataTypeConverter;
import main.java.enums.system.TextFilePath;

/**
 * This repository class follows the singleton pattern to declare a single instance
 */
public class CustomerAccountRepository extends AccountRepository<UserAccount> {

    private static CustomerAccountRepository instance;
    private final String filePath = TextFilePath.CUSTOMER_ACCOUNT_FILE_PATH.getTextFilePath();

    // Private constructor to prevent instantiations from outside
    // Pre-populate the records into maps when the instance is created
    private CustomerAccountRepository() {
        retrieveRecords();
    }

    // Global access point by creating instance for once only
    // So all classes can access the same instance for reading and writing files
    public static CustomerAccountRepository getInstance() {
        if (instance == null) {
            instance = new CustomerAccountRepository();
        }
        return instance;
    }

    @Override
    public String getFilePath() {
        return filePath;
    }

    /**
     * Used for retrieving records from the text file
     * Here the line is split into columns using comma as the delimiter
     * Customer CSV format (10 columns): accountID, accountCreationDate, userID, userRole, firstName, lastName,
     * emailAddress, phoneNumber, passwordHash, accountStatus
     */
    @Override
    protected UserAccount parseRecord(String line) {
        String[] columns = line.split("\\|\\s*");

        return new UserAccount(
                columns[0],
                DataTypeConverter.convertStringToLocalDate(columns[1]),
                columns[2],
                DataTypeConverter.convertStringToRole(columns[3]),
                columns[4],
                columns[5],
                columns[6],
                columns[7],
                DataTypeConverter.convertStringToPwdHash(columns[8]),
                DataTypeConverter.convertStringToAccStatus(columns[9])
        );
    }
}
