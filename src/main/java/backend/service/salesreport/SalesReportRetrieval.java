package main.java.backend.service.salesreport;

import main.java.backend.model.SalesReport;
import main.java.backend.repository.impl.report.SalesReportRepository;
import main.java.backend.service.RecordReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SalesReportRetrieval implements RecordReader<SalesReport> {

    private final SalesReportRepository salesReportRepository;

    public SalesReportRetrieval(SalesReportRepository salesReportRepository) {
        this.salesReportRepository = salesReportRepository;
    }

    /**
     * Read and return all sales reports from the repository
     */
    @Override
    public Optional<List<SalesReport>> getAllRecords() {

        List<SalesReport> existingReports = new ArrayList<>(salesReportRepository.getAllRecords());

        if (existingReports.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(existingReports);
    }

    /**
     * Read and return a single sales report based on the given ID
     */
    @Override
    public Optional<SalesReport> getRecordById(String reportId) {

        SalesReport existingReport = salesReportRepository.getRecordById(reportId);

        if (existingReport == null) {
            return Optional.empty();
        }

        return Optional.of(existingReport);
    }

    /**
     * Read and return multiple sales reports based on the given IDs
     */
    @Override
    public Optional<List<SalesReport>> getRecordsByIds(List<String> reportIds) {

        List<SalesReport> existingReports = salesReportRepository.getRecordsByIds(reportIds);

        if (existingReports.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(existingReports);
    }

    /**
     * Read and return a single sales report based on the given document number
     */
    public Optional<SalesReport> getRecordByReportDocNo(String reportDocNo) {

        SalesReport existingReport = salesReportRepository.getRecordByReportDocNo(reportDocNo);

        if (existingReport == null) {
            return Optional.empty();
        }

        return Optional.of(existingReport);
    }
}
