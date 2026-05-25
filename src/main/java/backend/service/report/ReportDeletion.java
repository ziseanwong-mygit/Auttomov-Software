package main.java.backend.service.report;

import main.java.backend.model.Report;
import main.java.backend.repository.impl.report.ReportRepository;
import main.java.backend.service.RecordWriter;
import main.java.records.OperationResult;

public class ReportDeletion implements RecordWriter {

    // Wildcard is used so can accept either data report or sales report repository in one variable
    private final ReportRepository<? extends Report> reportRepository;
    private final String reportID;

    public ReportDeletion(ReportRepository<? extends Report> reportRepository, String reportID) {
        this.reportRepository = reportRepository;
        this.reportID = reportID;
    }

    /**
     * Report deletion process for data reports and sales reports
     */
    @Override
    public OperationResult execute() {

        // Check if the report exists with the given ID
        Report existingReport = reportRepository.getRecordById(reportID);
        if (existingReport == null) {
            return OperationResult.failed("The report with ID: " + reportID +
                    " does not exist in the system.");
        }

        // Cast the existingReport to specific report type, then only delete the record; base implementation
        return reportRepository.deleteRecordBase(existingReport);
    }
}
