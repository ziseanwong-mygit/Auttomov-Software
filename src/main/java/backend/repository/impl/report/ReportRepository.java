package main.java.backend.repository.impl.report;

import main.java.backend.model.Report;
import main.java.backend.model.StaffAccount;
import main.java.backend.repository.AbstractRepository;
import main.java.enums.data.ReportType;
import main.java.records.OperationResult;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public abstract class ReportRepository<T extends Report> extends AbstractRepository<T> {

    private final Map<String, T> reportByRepId = new HashMap<>();

    // Base implementation to cast the report to a generic type before CRUD
    // Safe because each repository only ever receives its own report type
    @SuppressWarnings("unchecked")
    public OperationResult createRecordBase(Report report) {
        try {
            return createRecord((T) report);
        } catch (ClassCastException e) {
            System.out.println(e.getMessage()); // Print exception message for debugging
            return OperationResult.failed("Oops! There was an error in handling the report for " +
                    "creation process. Please try again.");
        }
    }

    @SuppressWarnings("unchecked")
    public OperationResult updateRecordBase(Report report) {
        try {
            return updateRecord((T) report);
        } catch (ClassCastException e) {
            System.out.println(e.getMessage()); // Print exception message for debugging
            return OperationResult.failed("Oops! There was an error in handling the report for " +
                    "update process. Please try again.");
        }
    }

    @SuppressWarnings("unchecked")
    public OperationResult deleteRecordBase(Report report) {
        try {
            return deleteRecord((T) report);
        } catch (ClassCastException e) {
            System.out.println(e.getMessage()); // Print exception message for debugging
            return OperationResult.failed("Oops! There was an error in handling the report for " +
                    "deletion process. Please try again.");
        }
    }

    // Get the instance of report repository for CRUD operations
    // Static method - can be used by other classes without instantiation/implementation
    public static ReportRepository<? extends Report> getInstance(ReportType reportType) {
        if (reportType == ReportType.DATA_REPORT) {
            return DataReportRepository.getInstance();
        } else if (reportType == ReportType.SALES_REPORT) {
            return SalesReportRepository.getInstance();
        } else {
            throw new IllegalArgumentException("Invalid report type:" +  reportType);
        }
    }

    /**
     * Convert the report object to CSV format string
     * Subclass to provide implementation to this method
     */
    protected abstract String toCsvFormat(T reportToCreate);

    @Override
    public OperationResult createRecord(T reportToCreate) {

        try (BufferedWriter bufferWrite = new BufferedWriter(new FileWriter(getFilePath(), true))) {

            // Determine the CSV format based on the report type
            String repCsvLine = toCsvFormat(reportToCreate);
            
            // Write the new report into the file
            bufferWrite.write(repCsvLine);
            bufferWrite.newLine();

            // Add the new reports into the maps to ensure the latest changes are reflected
            addRecordIntoMap(reportToCreate);

        } catch (IOException e) {
            System.out.println(e.getMessage()); // Print exception message for debugging
            return OperationResult.failed(
                    "Oops! There was an error writing the new report. " +
                            "Please try again.");
        }

        return OperationResult.success(
                "The report has been created successfully. " +
                        "Please check the record to verify its creation.");
    }

    @Override
    public OperationResult updateRecord(T notAllowedToUpdate) {
        return OperationResult.failed("Reports cannot be modified to preserve the record integrity.");
    }

    /**
     * Update all the reports at once
     * Used when deleting a staff account, which will affect all reports that use them
     */
    public OperationResult bulkUpdateRecord(List<T> reportsToUpdate) {

        // Build a map to store the CSV line of newly updated report by its ID
        Map<String, Function<String, Optional<String>>> modifier = new HashMap<>();

        // For each report to update, create a modifier entry
        for (T report : reportsToUpdate) {
            String reportId = report.getReportID();
            modifier.put(reportId, _ -> Optional.of(toCsvFormat(report)));
        }

        // Single file rewrite covers all reports simultaneously
        OperationResult result = rewriteFile(modifier);
        if (!result.isValid()) {
            return result;
        }

        // Replace all the old records with the updated ones to apply the latest changes
        for (T report : reportsToUpdate) {
            removeRecordFromMap(reportByRepId.get(report.getReportID()));
            addRecordIntoMap(report);
        }

        return OperationResult.success(
                reportsToUpdate.size() + " report(s) are updated successfully.");
    }

    @Override
    public OperationResult deleteRecord(T reportToDelete) {

        String reportId = reportToDelete.getReportID();

        // Map the target report object by its reportID
        Map<String, Function<String, Optional<String>>> modifier = Map.of(
                reportId, _ -> Optional.empty()
        );

        // Rewrite the file to delete the target record
        OperationResult result = rewriteFile(modifier);
        if (!result.isValid()) {
            return result;
        }

        // Remove the deleted record from the maps to apply the latest changes
        removeRecordFromMap(reportToDelete);

        return OperationResult.success(
                "The report with ID " + reportId + " has been deleted successfully. "
                        + "Please check the records to verify its deletion.");
    }

    @Override
    public List<T> getAllRecords() {
        return new ArrayList<>(reportByRepId.values());
    }

    @Override
    public T getRecordById(String reportId) {
        return reportByRepId.get(reportId);
    }

    @Override
    public List<T> getRecordsByIds(List<String> reportIds) {
        // Filter the report objects by their IDs
        return reportByRepId.values().stream()
                .filter(report -> reportIds.contains(report.getReportID()))
                .toList();
    }

    public List<T> getRecordsByManager(StaffAccount manager) {
        return reportByRepId.values().stream()
                .filter(report -> report.getManagerAccount().equals(manager))
                .toList();
    }

    public T getRecordByReportDocNo(String reportDocNo) {
        return reportByRepId.values().stream()
                .filter(report -> report.getReportDocNo().equals(reportDocNo))
                .findFirst()
                .orElse(null);
    }

    /**
     * Parse a single CSV line into a specific data object
     * The subclass provides implementation to parse the record into its concrete product
     */
    @Override
    protected abstract T parseRecord(String line);

    /**
     * Add a new/updated report into all lookup maps
     */
    @Override
    protected void addRecordIntoMap(T report) {

        // End the method early if the report is not provided
        if (report == null) {
            return;
        }

        reportByRepId.put(report.getReportID(), report);
    }

    /**
     * Remove the old/deleted report from all lookup maps
     */
    @Override
    protected void removeRecordFromMap(T report) {

        // End the method early if the report is not provided
        if (report == null) {
            return;
        }

        reportByRepId.remove(report.getReportID());
    }
}
