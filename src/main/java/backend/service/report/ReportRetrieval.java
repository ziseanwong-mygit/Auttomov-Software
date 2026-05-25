package main.java.backend.service.report;

import main.java.backend.model.Report;
import main.java.backend.repository.impl.report.ReportRepository;
import main.java.backend.service.RecordReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReportRetrieval implements RecordReader<Report> {

    // Wildcard is used so can accept either data report or sales report repository in one variable
    private final ReportRepository<? extends Report> reportRepository;

    // Data or sales report repository
    public ReportRetrieval(ReportRepository<? extends Report> reportRepository) {
        this.reportRepository = reportRepository;
    }

    /**
     * Read and return all data or sales reports from the repository
     */
    @Override
    public Optional<List<Report>> getAllRecords() {

        List<Report> existingReports = new ArrayList<>(reportRepository.getAllRecords());

        if (existingReports.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(existingReports);
    }

    /**
     * Read and return a single report based on the given ID
     */
    @Override
    public Optional<Report> getRecordById(String reportId) {

        Report existingReport = reportRepository.getRecordById(reportId);

        if (existingReport == null) {
            return Optional.empty();
        }

        return Optional.of(existingReport);
    }

    /**
     * Read and return multiple reports based on the given IDs
     */
    @Override
    public Optional<List<Report>> getRecordsByIds(List<String> reportIds) {

        List<? extends Report> existingReports = reportRepository.getRecordsByIds(reportIds);

        if (existingReports.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new ArrayList<>(existingReports));
    }

    /**
     * Read and return a single report based on the given document number
     */
    public Optional<Report> getRecordByReportDocNo(String reportDocNo) {

        Report existingReport = reportRepository.getRecordByReportDocNo(reportDocNo);

        if (existingReport == null) {
            return Optional.empty();
        }

        return Optional.of(existingReport);
    }
}
