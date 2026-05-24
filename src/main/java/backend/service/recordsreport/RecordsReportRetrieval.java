package main.java.backend.service.recordsreport;

import main.java.backend.model.RecordsReport;
import main.java.backend.repository.impl.RecordsReportRepository;
import main.java.backend.service.RecordReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RecordsReportRetrieval implements RecordReader<RecordsReport> {

    private final RecordsReportRepository recordsReportRepository;

    public RecordsReportRetrieval(RecordsReportRepository recordsReportRepository) {
        this.recordsReportRepository = recordsReportRepository;
    }

    /**
     * Read and return all records reports from the repository
     */
    @Override
    public Optional<List<RecordsReport>> getAllRecords() {

        List<RecordsReport> existingReports = new ArrayList<>(recordsReportRepository.getAllRecords());

        if (existingReports.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(existingReports);
    }

    /**
     * Read and return a single records report based on the given ID
     */
    @Override
    public Optional<RecordsReport> getRecordById(String reportId) {

        RecordsReport existingReport = recordsReportRepository.getRecordById(reportId);

        if (existingReport == null) {
            return Optional.empty();
        }

        return Optional.of(existingReport);
    }

    /**
     * Read and return multiple records reports based on the given IDs
     */
    @Override
    public Optional<List<RecordsReport>> getRecordsByIds(List<String> reportIds) {

        List<RecordsReport> existingReports = recordsReportRepository.getRecordsByIds(reportIds);

        if (existingReports.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(existingReports);
    }

    /**
     * Read and return a single records report based on the given document number
     */
    public Optional<RecordsReport> getRecordByReportDocNo(String reportDocNo) {

        RecordsReport existingReport = recordsReportRepository.getRecordByReportDocNo(reportDocNo);

        if (existingReport == null) {
            return Optional.empty();
        }

        return Optional.of(existingReport);
    }
}
