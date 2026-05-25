package main.java.backend.controller;

import main.java.backend.model.Report;
import main.java.backend.model.StaffAccount;
import main.java.backend.repository.impl.*;
import main.java.backend.repository.impl.account.CustomerAccountRepository;
import main.java.backend.repository.impl.account.StaffAccountRepository;
import main.java.backend.repository.impl.report.DataReportRepository;
import main.java.backend.repository.impl.report.ReportRepository;
import main.java.backend.repository.impl.report.SalesReportRepository;
import main.java.backend.service.RecordReader;
import main.java.backend.service.RecordWriter;
import main.java.backend.service.report.ReportCreation;
import main.java.backend.service.report.ReportDeletion;
import main.java.backend.service.report.ReportRetrieval;
import main.java.backend.util.DataValidator;
import main.java.enums.data.JobPosition;
import main.java.enums.data.ReportType;
import main.java.enums.system.DataFieldName;
import main.java.records.OperationResult;

import java.util.List;
import java.util.Optional;

public class ReportController {

    // Singleton instances of the repositories
    private final DataReportRepository dataReportRepo = DataReportRepository.getInstance();
    private final SalesReportRepository salesReportRepo = SalesReportRepository.getInstance();
    private final CustomerAccountRepository customerAccountRepo = CustomerAccountRepository.getInstance();
    private final StaffAccountRepository staffAccountRepo = StaffAccountRepository.getInstance();
    private final ServiceActivityRepository serviceActivityRepo = ServiceActivityRepository.getInstance();
    private final VehiclePartRepository vehiclePartRepo= VehiclePartRepository.getInstance();
    private final ServicePackageRepository servicePackageRepo = ServicePackageRepository.getInstance();
    private final AppointmentRepository appointmentRepo = AppointmentRepository.getInstance();
    private final InvoiceRepository invoiceRepo = InvoiceRepository.getInstance();

    public OperationResult createReport(String managerID, ReportType reportType) {

        // Validate manager account ID
        OperationResult managerIdVerification = validateManagerId(managerID);
        if (!managerIdVerification.isValid()) {
            return managerIdVerification;
        }

        // Check if the report type is null
        if (reportType == null) {
            return OperationResult.failed("The report type must be provided to create a record. Please try again.");
        }

        // Retrieve the manager account record with the ID from the repository
        StaffAccount managerAcc = staffAccountRepo.getRecordById(managerID);

        // Instantiate the data/sales report repository for creating records
        // Wildcard is used so can accept either data or sales repository in one variable
        ReportRepository<? extends Report> reportRepo = resolveReportRepo(reportType);

        // Begin creating report procedure in service class
        RecordWriter createReport = new ReportCreation(
                reportRepo, customerAccountRepo, staffAccountRepo, serviceActivityRepo, vehiclePartRepo,
                servicePackageRepo, appointmentRepo, invoiceRepo, managerAcc);
        return createReport.execute();
    }

    public OperationResult deleteReport(String reportID, ReportType reportType) {

        // Check if the inputs are null or empty
        if (reportID == null || reportType == null || reportID.isBlank()) {
            return OperationResult.failed("The report ID must be provided to delete a record. Please try again.");
        }

        // Check if the report ID is in UUID format
        if (!DataValidator.validateRecordId(reportID).isValid()) {
            return OperationResult.failed(DataFieldName.RECORD_ID, "The report ID: " +
                    reportID + " is not an UUID. Please try again.");
        }

        // Instantiate the data/sales report repository for creating records
        // Wildcard is used so can accept either data or sales repository in one variable
        ReportRepository<? extends Report> reportRepo = resolveReportRepo(reportType);

        // Begin deleting report procedure in service class
        RecordWriter deleteReport = new ReportDeletion(reportRepo, reportID);
        return deleteReport.execute();
    }

    /**
     * Optional class is used to determine whether the records are present or not
     * This is for frontend logic in displaying the result message for UI
     */
    public Optional<List<Report>> retrieveAllReports(ReportType reportType) {

        // Instantiate the data/sales report repository for creating records
        // Wildcard is used so can accept either data or sales repository in one variable
        ReportRepository<? extends Report> reportRepo = resolveReportRepo(reportType);

        // Begin retrieving all reports procedures in service class
        RecordReader<Report> retrieveAllReports = new ReportRetrieval(reportRepo);
        return retrieveAllReports.getAllRecords();
    }

    public Optional<Report> retrieveReportById(String reportId, ReportType reportType) {

        // Instantiate the data/sales report repository for creating records
        // Wildcard is used so can accept either data or sales repository in one variable
        ReportRepository<? extends Report> reportRepo = resolveReportRepo(reportType);

        // Begin retrieving report by ID procedure in service class
        RecordReader<Report> retrieveReportById = new ReportRetrieval(reportRepo);
        return retrieveReportById.getRecordById(reportId);
    }

    public Optional<List<Report>> retrieveReportsByIds(List<String> reportIds, ReportType reportType) {

        // Instantiate the data/sales report repository for creating records
        // Wildcard is used so can accept either data or sales repository in one variable
        ReportRepository<? extends Report> reportRepo = resolveReportRepo(reportType);

        // Begin retrieving multiple reports by IDs procedure in service class
        RecordReader<Report> retrieveReportsByIds = new ReportRetrieval(reportRepo);
        return retrieveReportsByIds.getRecordsByIds(reportIds);
    }

    public Optional<Report> retrieveReportByReportDocNo(String reportDocNo, ReportType reportType) {

        // Instantiate the data/sales report repository for creating records
        // Wildcard is used so can accept either data or sales repository in one variable
        ReportRepository<? extends Report> reportRepo = resolveReportRepo(reportType);

        // Begin retrieving report by document number procedure in service class
        ReportRetrieval retrieveReportByReportDocNo = new ReportRetrieval(reportRepo);
        return retrieveReportByReportDocNo.getRecordByReportDocNo(reportDocNo);
    }

    /**
     * Validate the format and length of report ID
     * This validation is done before record retrieval, so the frontend can first display the validation result
     */
    public OperationResult validateReportIdAndType(String reportId, ReportType reportType) {

        // Check if the report ID and its type are null or empty
        if (reportId == null || reportType == null || reportId.isBlank()) {
            return OperationResult.failed("The report ID and type must be provided to retrieve records. " +
                    "Please try again.");
        }

        if (!DataValidator.validateRecordId(reportId).isValid()) {
            return OperationResult.failed("The report ID: " + reportId + " is not an UUID. Please try again.");
        }

        return OperationResult.success();
    }

    public OperationResult validateReportIdsAndType(List<String> reportIds, ReportType reportType) {

        // Check if the ID list and its type are null or empty
        if (reportIds == null || reportType == null || reportIds.isEmpty()) {
            return OperationResult.failed("The report IDs and type must be provided to retrieve records. " +
                    "Please try again.");
        }

        // Check if any of the report IDs are null or empty
        for (String reportId : reportIds) {
            if (reportId == null || reportId.isBlank() ) {
                return OperationResult.failed("One of the report IDs is null or empty. Please try again.");
            }
        }

        // Check if any of the report IDs are invalid
        for (String reportId : reportIds) {
            if (!DataValidator.validateRecordId(reportId).isValid()) {
                return OperationResult.failed("The report ID: " + reportId + " is not an UUID. Please try again.");
            }
        }

        return OperationResult.success();
    }

    /**
     * Full validation of the manager account ID in checking nullity, format, and linking record
     */
    private OperationResult validateManagerId(String managerId) {

        // 1. Check if the manager account ID is null or blank
        if (managerId == null || managerId.isBlank()) {
            return OperationResult.failed(DataFieldName.STAFF_ID,
                    "The manager account ID must be provided. Please try again.");
        }

        // 2. Check if the manager account ID in UUID format
        if (!DataValidator.validateRecordId(managerId).isValid()) {
            return OperationResult.failed(DataFieldName.STAFF_ID,
                    "The manager account ID: " + managerId + " is not an UUID. Please try again.");
        }

        // 3. Check if the manager account ID exists in the repository
        StaffAccount managerAcc = staffAccountRepo.getRecordById(managerId);
        if (managerAcc == null) {
            return OperationResult.failed(DataFieldName.STAFF_ID,
                    "The manager account with ID " + managerId + " does not exist in the records.");
        }

        // 4. If the account is not a manager, return an error message.
        if (managerAcc.getJobPosition() != JobPosition.MANAGER) {
            return OperationResult.failed(DataFieldName.STAFF_ID,
                    "Only managers can strictly create and manage reports.");
        }

        // If all validations pass, return success
        return OperationResult.success();
    }

    /**
     * Resolve the data or sales report repository based on the report type
     */
    private ReportRepository<? extends Report> resolveReportRepo(ReportType reportType) {
        return reportType == ReportType.DATA_REPORT ? dataReportRepo : salesReportRepo;
    }
}
