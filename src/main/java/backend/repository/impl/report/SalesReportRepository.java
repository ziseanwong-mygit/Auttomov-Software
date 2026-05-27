package main.java.backend.repository.impl.report;

import main.java.backend.model.SalesReport;
import main.java.backend.repository.impl.account.StaffAccountRepository;
import main.java.backend.util.CSVFormatter;
import main.java.backend.util.DataTypeConverter;
import main.java.enums.system.TextFilePath;

public class SalesReportRepository extends ReportRepository<SalesReport> {

    private static SalesReportRepository instance;
    private final String filePath = TextFilePath.SALES_REPORTS_FILE_PATH.getTextFilePath();

    // Private constructor to prevent instantiations from outside
    // Pre-populate the records into maps when the instance is created
    private SalesReportRepository() {
        retrieveRecords();
    }

    // Global access point by creating instance for once only
    // So all classes can access the same instance for reading and writing files
    public static SalesReportRepository getInstance() {
        if (instance == null) {
            instance = new SalesReportRepository();
        }
        return instance;
    }

    @Override
    public String getFilePath() {
        return filePath;
    }

    @Override
    protected String toCsvFormat(SalesReport reportToCreate) {
        return CSVFormatter.toCsvFormat(reportToCreate);
    }

    /**
     * Used for retrieving records from the text file
     * Here the line is split into columns using comma as the delimiter
     */
    @Override
    protected SalesReport parseRecord(String line) {

        // Get the staff account repository instance to fetch the respective objects
        StaffAccountRepository staffAccountRepository = StaffAccountRepository.getInstance();

        String[] columns = line.split("\\|\\s*");


        return new SalesReport(
                columns[0],
                columns[1],
                DataTypeConverter.convertStringToLocalDate(columns[2]),
                DataTypeConverter.convertStringToReportType(columns[3]),
                staffAccountRepository.getRecordById(columns[4]),
                DataTypeConverter.convertStringToInt(columns[5]),
                DataTypeConverter.convertStringToInt(columns[6]),
                DataTypeConverter.convertStringToInt(columns[7]),
                DataTypeConverter.convertStringToDouble(columns[8]),
                DataTypeConverter.convertStringToDouble(columns[9]),
                DataTypeConverter.convertStringToDouble(columns[10]),
                DataTypeConverter.convertStringToDouble(columns[11]),
                DataTypeConverter.convertStringToDouble(columns[12]),
                DataTypeConverter.convertStringToDouble(columns[13]),
                DataTypeConverter.convertStringToDouble(columns[14]),
                DataTypeConverter.convertStringToDouble(columns[15]),
                DataTypeConverter.convertStringToDouble(columns[16])
        );
    }
}
