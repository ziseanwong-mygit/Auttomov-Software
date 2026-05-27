package main.java.backend.service.recordsreport;

import main.java.backend.model.DataReport;
import main.java.backend.repository.impl.report.DataReportRepository;
import main.java.backend.service.RecordReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RecordsReportRetrieval implements RecordReader<DataReport> {

    private final DataReportRepository dataReportRepository;

    public RecordsReportRetrieval(DataReportRepository dataReportRepository) {
        this.dataReportRepository = dataReportRepository;
    }

    /**
     * Read and return all records reports from the repository
     */
    @Override
    public Optional<List<DataReport>> getAllRecords() {

        List<DataReport> existingReports = new ArrayList<>(dataReportRepository.getAllRecords());

        if (existingReports.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(existingReports);
    }

    /**
     * Read and return a single records report based on the given ID
     */
    @Override
    public Optional<DataReport> getRecordById(String reportId) {

        DataReport existingReport = dataReportRepository.getRecordById(reportId);

        if (existingReport == null) {
            return Optional.empty();
        }

        return Optional.of(existingReport);
    }

    /**
     * Read and return multiple records reports based on the given IDs
     */
    @Override
    public Optional<List<DataReport>> getRecordsByIds(List<String> reportIds) {

        List<DataReport> existingReports = dataReportRepository.getRecordsByIds(reportIds);

        if (existingReports.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(existingReports);
    }

    /**
     * Read and return a single records report based on the given document number
     */
    public Optional<DataReport> getRecordByReportDocNo(String reportDocNo) {

        DataReport existingReport = dataReportRepository.getRecordByReportDocNo(reportDocNo);

        if (existingReport == null) {
            return Optional.empty();
        }

        return Optional.of(existingReport);
    }
}
