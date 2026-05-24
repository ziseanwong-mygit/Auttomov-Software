package main.java.backend.repository.impl;

import main.java.backend.model.SalesReport;
import main.java.backend.model.StaffAccount;
import main.java.backend.repository.AbstractRepository;
import main.java.backend.repository.impl.account.StaffAccountRepository;
import main.java.backend.util.CSVFormatter;
import main.java.backend.util.DataTypeConverter;
import main.java.enums.system.TextFilePath;
import main.java.records.OperationResult;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class SalesReportRepository extends AbstractRepository<SalesReport> {

    private static SalesReportRepository instance;
    private final String filePath = TextFilePath.SALES_REPORTS_FILE_PATH.getTextFilePath();

    // Map key: field value, map value: SalesReport object
    private final Map<String, SalesReport> salesReportById = new HashMap<>();

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
    public OperationResult createRecord(SalesReport reportToCreate) {

        try (BufferedWriter bufferWrite = new BufferedWriter(new FileWriter(getFilePath(), true))) {

            // Write the new sales report into the file
            bufferWrite.write(CSVFormatter.toCsvFormat(reportToCreate));
            bufferWrite.newLine();

            // Add the new sales reports into the maps to ensure the latest changes are reflected
            addRecordIntoMap(reportToCreate);

        } catch (IOException e) {
            System.out.println(e.getMessage()); // Print exception message for debugging
            return OperationResult.failed(
                    "Oops! There was an error writing the new sales report. " +
                            "Please try again.");
        }

        return OperationResult.success(
                "The sales report has been created successfully. " +
                        "Please check the record to verify its creation.");
    }

    @Override
    public OperationResult updateRecord(SalesReport notAllowedToUpdate) {
        return OperationResult.failed("Reports cannot be modified to preserve the record integrity.");
    }

    /**
     * Update all the sales reports at once
     * Used when deleting a staff account, which will affect all reports that use them
     */
    public OperationResult bulkUpdateRecord(List<SalesReport> reportsToUpdate) {

        // Build a map to store the CSV line of newly updated report by its ID
        Map<String, Function<String, Optional<String>>> modifier = new HashMap<>();

        // For each report to update, create a modifier entry
        for (SalesReport report : reportsToUpdate) {
            String reportId = report.getReportID();
            modifier.put(reportId, _ -> Optional.of(CSVFormatter.toCsvFormat(report)));
        }

        // Single file rewrite covers all reports simultaneously
        OperationResult result = rewriteFile(modifier);
        if (!result.isValid()) {
            return result;
        }

        // Replace all the old records with the updated ones to apply the latest changes
        for (SalesReport report : reportsToUpdate) {
            removeRecordFromMap(salesReportById.get(report.getReportID()));
            addRecordIntoMap(report);
        }

        return OperationResult.success(
                reportsToUpdate.size() + " report(s) are updated successfully.");
    }

    @Override
    public OperationResult deleteRecord(SalesReport reportToDelete) {

        String recordsReportID = reportToDelete.getReportID();

        // Map Optional.empty() by its ID
        Map<String, Function<String, Optional<String>>> modifier = Map.of(
                recordsReportID, _ -> Optional.empty()
        );

        // Rewrite the file to delete the target record
        OperationResult result = rewriteFile(modifier);
        if (!result.isValid()) {
            return result;
        }

        // Remove the deleted record from the maps to apply the latest changes
        removeRecordFromMap(reportToDelete);

        return OperationResult.success(
                "The sales report with ID " + recordsReportID + " has been deleted successfully. "
                        + "Please check the records to verify its deletion.");
    }

    @Override
    public List<SalesReport> getAllRecords() {
        return new ArrayList<>(salesReportById.values());
    }

    @Override
    public SalesReport getRecordById(String recordsReportId) {
        return salesReportById.get(recordsReportId);
    }

    @Override
    public List<SalesReport> getRecordsByIds(List<String> recordsReportIds) {
        // Filter the sales report objects by their IDs
        return salesReportById.values().stream()
                .filter(report -> recordsReportIds.contains(report.getReportID()))
                .toList();
    }

    public List<SalesReport> getRecordsByManager(StaffAccount manager) {
        return salesReportById.values().stream()
                .filter(report -> report.getManagerAccount().equals(manager))
                .toList();
    }

    public SalesReport getRecordByReportDocNo(String reportDocNo) {
        return salesReportById.values().stream()
                .filter(report -> report.getReportDocNo().equals(reportDocNo))
                .findFirst()
                .orElse(null);
    }

    /**
     * Used for retrieving records from the text file
     * Here the line is split into columns using comma as the delimiter
     */
    @Override
    protected SalesReport parseRecord(String line) {

        // Get the staff account repository instance to fetch the respective objects
        StaffAccountRepository staffAccountRepository = StaffAccountRepository.getInstance();

        String[] columns = line.split(",\\s*");

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

    /**
     * Add a new/updated sales report into all lookup maps
     */
    @Override
    protected void addRecordIntoMap(SalesReport salesReport) {

        // End the method early if the sales report is not provided
        if (salesReport == null) {
            return;
        }

        salesReportById.put(salesReport.getReportID(), salesReport);
    }

    /**
     * Remove the old/deleted sales report from all lookup maps
     */
    @Override
    protected void removeRecordFromMap(SalesReport salesReport) {

        // End the method early if the sales report is not provided
        if (salesReport == null) {
            return;
        }

        salesReportById.remove(salesReport.getReportID());
    }

}
