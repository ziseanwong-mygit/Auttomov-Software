package main.java.backend.service.recordsreport;

import main.java.backend.model.RecordsReport;
import main.java.backend.repository.impl.RecordsReportRepository;
import main.java.backend.service.RecordWriter;
import main.java.records.OperationResult;

public class RecordsReportDeletion implements RecordWriter {

    private final RecordsReportRepository recordsReportRepository;
    private final String reportID;

    public RecordsReportDeletion(RecordsReportRepository recordsReportRepository, String reportID) {
        this.recordsReportRepository = recordsReportRepository;
        this.reportID = reportID;
    }

    @Override
    public OperationResult execute() {

        // Check if the report exists with the given ID
        RecordsReport existingReport = recordsReportRepository.getRecordById(reportID);
        if (existingReport == null) {
            return OperationResult.failed("The records report with ID: " + reportID +
                    " does not exist in the system.");
        }

        // Delete the records report in the text file
        return recordsReportRepository.deleteRecord(existingReport);
    }
}
