package main.java.backend.service.recordsreport;

import main.java.backend.model.DataReport;
import main.java.backend.repository.impl.report.DataReportRepository;
import main.java.backend.service.RecordWriter;
import main.java.records.OperationResult;

public class RecordsReportDeletion implements RecordWriter {

    private final DataReportRepository dataReportRepository;
    private final String reportID;

    public RecordsReportDeletion(DataReportRepository dataReportRepository, String reportID) {
        this.dataReportRepository = dataReportRepository;
        this.reportID = reportID;
    }

    @Override
    public OperationResult execute() {

        // Check if the report exists with the given ID
        DataReport existingReport = dataReportRepository.getRecordById(reportID);
        if (existingReport == null) {
            return OperationResult.failed("The records report with ID: " + reportID +
                    " does not exist in the system.");
        }

        // Delete the records report in the text file
        return dataReportRepository.deleteRecord(existingReport);
    }
}
