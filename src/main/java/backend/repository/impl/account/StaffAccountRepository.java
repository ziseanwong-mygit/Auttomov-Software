package main.java.backend.repository.impl.account;

import main.java.backend.model.StaffAccount;
import main.java.backend.util.DataTypeConverter;
import main.java.enums.data.JobPosition;
import main.java.enums.system.TextFilePath;

import java.util.List;

/**
 * This repository class follows the singleton pattern to declare a single instance when the application starts.
 * Instance is usually created in the Main class.
 */
public class StaffAccountRepository extends AccountRepository<StaffAccount> {

    private static StaffAccountRepository instance;
    private final String filePath = TextFilePath.STAFF_ACCOUNT_FILE_PATH.getTextFilePath();

    // Private constructor to prevent instantiations from outside
    // Pre-populate the records into maps when the instance is created
    private StaffAccountRepository() {
        retrieveRecords();
    }

    // Global access point by creating instance for once only
    // So all classes can access the same instance for reading and writing files
    public static StaffAccountRepository getInstance() {
        if (instance == null) {
            instance = new StaffAccountRepository();
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
     * Staff CSV format (10 columns): accountID, accountCreationDate, userID, userRole, firstName, lastName,
     * emailAddress, phoneNumber, passwordHash, accountStatus, jobPosition, employmentDate, employmentStatus,
     * specialization
     */
    @Override
    protected StaffAccount parseRecord(String line) {
        String[] columns = line.split(",\\s*");

        // If the account record is a technician, the specialization field will be included
        String specialization = null;
        if (columns.length == 14) {
            specialization = columns[13];
        }

        return new StaffAccount(
                columns[0],
                DataTypeConverter.convertStringToLocalDate(columns[1]),
                columns[2],
                DataTypeConverter.convertStringToRole(columns[3]),
                columns[4],
                columns[5],
                columns[6],
                columns[7],
                DataTypeConverter.convertStringToPwdHash(columns[8]),
                DataTypeConverter.convertStringToAccStatus(columns[9]),
                DataTypeConverter.convertStringToJobPos(columns[10]),
                DataTypeConverter.convertStringToLocalDate(columns[11]),
                DataTypeConverter.convertStringToEmpStatus(columns[12]),
                specialization
        );
    }

    /**
     * Get the staff account records by job position
     * Used for generating reports
     */
    public List<StaffAccount> getRecordsByJobPosition(JobPosition jobPosition) {
        return getAllRecords().stream()
                .filter(staff -> staff.getJobPosition().equals(jobPosition))
                .toList();
    }
}
