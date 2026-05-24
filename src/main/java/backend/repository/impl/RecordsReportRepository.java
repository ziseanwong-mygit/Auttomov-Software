package main.java.backend.repository.impl;

import main.java.backend.model.RecordsReport;
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

public class RecordsReportRepository extends AbstractRepository<RecordsReport> {

    private static RecordsReportRepository instance;
    private final String filePath = TextFilePath.RECORDS_REPORTS_FILE_PATH.getTextFilePath();

    // Map key: field value, map value: RecordsReport object
    private final Map<String, RecordsReport> recordsReportById = new HashMap<>();

    // Private constructor to prevent instantiations from outside
    // Pre-populate the records into maps when the instance is created
    private RecordsReportRepository() {
        retrieveRecords();
    }

    // Global access point by creating instance for once only
    // So all classes can access the same instance for reading and writing files
    public static RecordsReportRepository getInstance() {
        if (instance == null) {
            instance = new RecordsReportRepository();
        }
        return instance;
    }

    @Override
    public String getFilePath() {
        return filePath;
    }

    @Override
    public OperationResult createRecord(RecordsReport reportToCreate) {

        try (BufferedWriter bufferWrite = new BufferedWriter(new FileWriter(getFilePath(), true))) {

            // Write the new records report into the file
            bufferWrite.write(CSVFormatter.toCsvFormat(reportToCreate));
            bufferWrite.newLine();

            // Add the new records reports into the maps to ensure the latest changes are reflected
            addRecordIntoMap(reportToCreate);

        } catch (IOException e) {
            System.out.println(e.getMessage()); // Print exception message for debugging
            return OperationResult.failed(
                    "Oops! There was an error writing the new records report. " +
                            "Please try again.");
        }

        return OperationResult.success(
                "The records report has been created successfully. " +
                        "Please check the record to verify its creation.");
    }

    @Override
    public OperationResult updateRecord(RecordsReport notAllowedToUpdate) {
        return OperationResult.failed("Reports cannot be modified to preserve the record integrity.");
    }

    /**
     * Update all the records reports at once
     * Used when deleting a staff account, which will affect all reports that use them
     */
    public OperationResult bulkUpdateRecord(List<RecordsReport> reportsToUpdate) {

        // Build a map to store the CSV line of newly updated report by its ID
        Map<String, Function<String, Optional<String>>> modifier = new HashMap<>();

        // For each report to update, create a modifier entry
        for (RecordsReport report : reportsToUpdate) {
            String reportId = report.getReportID();
            modifier.put(reportId, _ -> Optional.of(CSVFormatter.toCsvFormat(report)));
        }

        // Single file rewrite covers all reports simultaneously
        OperationResult result = rewriteFile(modifier);
        if (!result.isValid()) {
            return result;
        }

        // Replace all the old records with the updated ones to apply the latest changes
        for (RecordsReport report : reportsToUpdate) {
            removeRecordFromMap(recordsReportById.get(report.getReportID()));
            addRecordIntoMap(report);
        }

        return OperationResult.success(
                reportsToUpdate.size() + " report(s) are updated successfully.");
    }

    @Override
    public OperationResult deleteRecord(RecordsReport reportToDelete) {

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
                "The records report with ID " + recordsReportID + " has been deleted successfully. "
                        + "Please check the records to verify its deletion.");
    }

    @Override
    public List<RecordsReport> getAllRecords() {
        return new ArrayList<>(recordsReportById.values());
    }

    @Override
    public RecordsReport getRecordById(String recordsReportId) {
        return recordsReportById.get(recordsReportId);
    }

    @Override
    public List<RecordsReport> getRecordsByIds(List<String> recordsReportIds) {
        // Filter the records report objects by their IDs
        return recordsReportById.values().stream()
                .filter(report -> recordsReportIds.contains(report.getReportID()))
                .toList();
    }

    public List<RecordsReport> getRecordsByManager(StaffAccount manager) {
        return recordsReportById.values().stream()
                .filter(report -> report.getManagerAccount().equals(manager))
                .toList();
    }

    public RecordsReport getRecordByReportDocNo(String reportDocNo) {
        return recordsReportById.values().stream()
                .filter(report -> report.getReportDocNo().equals(reportDocNo))
                .findFirst()
                .orElse(null);
    }

    /**
     * Used for retrieving records from the text file
     * Here the line is split into columns using comma as the delimiter
     */
    @Override
    protected RecordsReport parseRecord(String line) {

        // Get the staff account repository instance to fetch the respective objects
        StaffAccountRepository staffAccountRepository = StaffAccountRepository.getInstance();

        String[] columns = line.split(",\\s*");

        return new RecordsReport(
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

    /**
     * Add a new/updated records report into all lookup maps
     */
    @Override
    protected void addRecordIntoMap(RecordsReport recordsReport) {

        // End the method early if the records report is not provided
        if (recordsReport == null) {
            return;
        }

        recordsReportById.put(recordsReport.getReportID(), recordsReport);
    }

    /**
     * Remove the old/deleted records report from all lookup maps
     */
    @Override
    protected void removeRecordFromMap(RecordsReport recordsReport) {

        // End the method early if the records report is not provided
        if (recordsReport == null) {
            return;
        }

        recordsReportById.remove(recordsReport.getReportID());
    }
}
