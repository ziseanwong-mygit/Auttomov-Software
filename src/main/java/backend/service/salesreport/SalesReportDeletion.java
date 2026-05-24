package main.java.backend.service.salesreport;

import main.java.backend.model.SalesReport;
import main.java.backend.repository.impl.SalesReportRepository;
import main.java.backend.service.RecordWriter;
import main.java.records.OperationResult;

public class SalesReportDeletion implements RecordWriter {

    private final SalesReportRepository salesReportRepository;
    private final String reportID;

    public SalesReportDeletion(SalesReportRepository salesReportRepository, String reportID) {
        this.salesReportRepository = salesReportRepository;
        this.reportID = reportID;
    }

    @Override
    public OperationResult execute() {

        // Check if the report exists with the given ID
        SalesReport existingReport = salesReportRepository.getRecordById(reportID);
        if (existingReport == null) {
            return OperationResult.failed("The sales report with ID: " + reportID +
                    " does not exist in the system.");
        }

        // Delete the sales report in the text file
        return salesReportRepository.deleteRecord(existingReport);
    }
}
