package main.java.backend.controller;

import main.java.backend.model.SalesReport;
import main.java.backend.model.StaffAccount;
import main.java.backend.repository.impl.AppointmentRepository;
import main.java.backend.repository.impl.InvoiceRepository;
import main.java.backend.repository.impl.report.SalesReportRepository;
import main.java.backend.repository.impl.account.StaffAccountRepository;
import main.java.backend.service.RecordReader;
import main.java.backend.service.RecordWriter;
import main.java.backend.service.salesreport.SalesReportCreation;
import main.java.backend.service.salesreport.SalesReportDeletion;
import main.java.backend.service.salesreport.SalesReportRetrieval;
import main.java.backend.util.DataValidator;
import main.java.enums.data.JobPosition;
import main.java.enums.system.DataFieldName;
import main.java.records.OperationResult;

import java.util.List;
import java.util.Optional;

public class SalesReportController {

    // Singleton instances of the repositories
    private final SalesReportRepository salesReportRepo = SalesReportRepository.getInstance();
    private final AppointmentRepository appointmentRepo = AppointmentRepository.getInstance();
    private final InvoiceRepository invoiceRepo = InvoiceRepository.getInstance();
    private final StaffAccountRepository staffAccountRepo = StaffAccountRepository.getInstance();

    public OperationResult createSalesReport(String managerID) {

        // Validate manager account ID
        OperationResult managerIdVerification = validateManagerId(managerID);
        if (!managerIdVerification.isValid()) {
            return managerIdVerification;
        }

        // Retrieve the manager account record with the ID from the repository
        StaffAccount managerAcc = staffAccountRepo.getRecordById(managerID);

        // Begin creating sales report procedure in service class
        RecordWriter createSalesReport = new SalesReportCreation(
                salesReportRepo, appointmentRepo, invoiceRepo, managerAcc);
        return createSalesReport.execute();
    }

    public OperationResult deleteSalesReport(String reportID) {

        // Check if the inputs are null or empty
        if (reportID == null || reportID.isBlank()) {
            return OperationResult.failed("The sales report ID must be provided to delete a record. Please try again.");
        }

        // Check if the report ID is in UUID format
        if (!DataValidator.validateRecordId(reportID).isValid()) {
            return OperationResult.failed(DataFieldName.RECORD_ID, "The sales report ID: " +
                    reportID + " is not an UUID. Please try again.");
        }

        // Begin deleting sales report procedure in service class
        RecordWriter deleteSalesReport = new SalesReportDeletion(salesReportRepo, reportID);
        return deleteSalesReport.execute();
    }

    /**
     * Optional class is used to determine whether the records are present or not
     * This is for frontend logic in displaying the result message for UI
     */
    public Optional<List<SalesReport>> retrieveAllSalesReports() {

        // Begin retrieving all sales reports procedures in service class
        RecordReader<SalesReport> retrieveAllSalesReports = new SalesReportRetrieval(salesReportRepo);
        return retrieveAllSalesReports.getAllRecords();
    }

    public Optional<SalesReport> retrieveSalesReportById(String reportId) {

        // Begin retrieving sales report by ID procedure in service class
        RecordReader<SalesReport> retrieveSalesReportById = new SalesReportRetrieval(salesReportRepo);
        return retrieveSalesReportById.getRecordById(reportId);
    }

    public Optional<List<SalesReport>> retrieveSalesReportsByIds(List<String> invoiceIds) {

        // Begin retrieving multiple sales reports by IDs procedure in service class
        RecordReader<SalesReport> retrieveSalesReportsByIds = new SalesReportRetrieval(salesReportRepo);
        return retrieveSalesReportsByIds.getRecordsByIds(invoiceIds);
    }

    public Optional<SalesReport> retrieveSalesReportByReportDocNo(String reportDocNo) {

        // Begin retrieving sales report by document number procedure in service class
        SalesReportRetrieval retrieveSalesReportByReportDocNo = new SalesReportRetrieval(salesReportRepo);
        return retrieveSalesReportByReportDocNo.getRecordByReportDocNo(reportDocNo);
    }

    /**
     * Validate the format and length of sales report ID
     * This validation is done before record retrieval, so the frontend can first display the validation result
     */
    public OperationResult validateSalesReportId(String reportId) {

        // Check if the sales report ID and its type are null or empty
        if (reportId == null || reportId.isBlank()) {
            return OperationResult.failed("The sales report ID must be provided to retrieve sales. " +
                    "Please try again.");
        }

        if (!DataValidator.validateRecordId(reportId).isValid()) {
            return OperationResult.failed("The sales report ID: " + reportId +
                    " is not an UUID. Please try again.");
        }

        return OperationResult.success();
    }

    public OperationResult validateSalesReportIds(List<String> reportIds) {

        // Check if the ID list and its type are null or empty
        if (reportIds == null || reportIds.isEmpty()) {
            return OperationResult.failed("The sales report IDs must be provided to retrieve sales. " +
                    "Please try again.");
        }

        // Check if any of the invoice IDs are null or empty
        for (String invoiceId : reportIds) {
            if (invoiceId == null || invoiceId.isBlank() ) {
                return OperationResult.failed("One of the sales report IDs is null or empty. Please try again.");
            }
        }

        // Check if any of the invoice IDs are invalid
        for (String invoiceId : reportIds) {
            if (!DataValidator.validateRecordId(invoiceId).isValid()) {
                return OperationResult.failed("The sales report ID: " + invoiceId +
                        " is not an UUID. Please try again.");
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
}
