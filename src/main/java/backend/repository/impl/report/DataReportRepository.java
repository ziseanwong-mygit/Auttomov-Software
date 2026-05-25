package main.java.backend.repository.impl.report;

import main.java.backend.model.DataReport;
import main.java.backend.repository.impl.account.StaffAccountRepository;
import main.java.backend.util.CSVFormatter;
import main.java.backend.util.DataTypeConverter;
import main.java.enums.system.TextFilePath;

public class DataReportRepository extends ReportRepository<DataReport> {

    private static DataReportRepository instance;
    private final String filePath = TextFilePath.RECORDS_REPORTS_FILE_PATH.getTextFilePath();

    // Private constructor to prevent instantiations from outside
    // Pre-populate the records into maps when the instance is created
    private DataReportRepository() {
        retrieveRecords();
    }

    // Global access point by creating instance for once only
    // So all classes can access the same instance for reading and writing files
    public static DataReportRepository getInstance() {
        if (instance == null) {
            instance = new DataReportRepository();
        }
        return instance;
    }

    @Override
    public String getFilePath() {
        return filePath;
    }

    @Override
    protected String toCsvFormat(DataReport reportToCreate) {
        return CSVFormatter.toCsvFormat(reportToCreate);
    }

    /**
     * Used for retrieving records from the text file
     * Here the line is split into columns using comma as the delimiter
     */
    @Override
    protected DataReport parseRecord(String line) {

        // Get the staff account repository instance to fetch the respective objects
        StaffAccountRepository staffAccountRepository = StaffAccountRepository.getInstance();

        String[] columns = line.split(",\\s*");

        return new DataReport(
                columns[0],
                columns[1],
                DataTypeConverter.convertStringToLocalDate(columns[2]),
                DataTypeConverter.convertStringToReportType(columns[3]),
                staffAccountRepository.getRecordById(columns[4]),
                DataTypeConverter.convertStringToInt(columns[5]),
                DataTypeConverter.convertStringToInt(columns[6]),
                DataTypeConverter.convertStringToInt(columns[7]),
                DataTypeConverter.convertStringToInt(columns[8]),
                DataTypeConverter.convertStringToInt(columns[9]),
                DataTypeConverter.convertStringToInt(columns[10]),
                DataTypeConverter.convertStringToInt(columns[11]),
                DataTypeConverter.convertStringToInt(columns[12]),
                DataTypeConverter.convertStringToInt(columns[13]),
                DataTypeConverter.convertStringToInt(columns[14]),
                DataTypeConverter.convertStringToInt(columns[15]),
                DataTypeConverter.convertStringToInt(columns[16])
        );
    }
}
