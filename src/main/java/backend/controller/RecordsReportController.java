package main.java.backend.controller;

import main.java.backend.model.DataReport;
import main.java.backend.model.StaffAccount;
import main.java.backend.repository.impl.*;
import main.java.backend.repository.impl.account.CustomerAccountRepository;
import main.java.backend.repository.impl.account.StaffAccountRepository;
import main.java.backend.repository.impl.report.DataReportRepository;
import main.java.backend.service.RecordReader;
import main.java.backend.service.RecordWriter;
import main.java.backend.service.recordsreport.RecordsReportCreation;
import main.java.backend.service.recordsreport.RecordsReportDeletion;
import main.java.backend.service.recordsreport.RecordsReportRetrieval;
import main.java.backend.util.DataValidator;
import main.java.enums.data.JobPosition;
import main.java.enums.system.DataFieldName;
import main.java.records.OperationResult;

import java.util.List;
import java.util.Optional;

public class RecordsReportController {

    // Singleton instances of the repositories
    private final DataReportRepository recordsReportRepo = DataReportRepository.getInstance();
    private final CustomerAccountRepository customerAccountRepo = CustomerAccountRepository.getInstance();
    private final StaffAccountRepository staffAccountRepo = StaffAccountRepository.getInstance();
    private final ServiceActivityRepository serviceActivityRepo = ServiceActivityRepository.getInstance();
    private final VehiclePartRepository vehiclePartRepo= VehiclePartRepository.getInstance();
    private final ServicePackageRepository servicePackageRepo = ServicePackageRepository.getInstance();
    private final AppointmentRepository appointmentRepo = AppointmentRepository.getInstance();

    public OperationResult createRecordsReport(String managerID) {

        // Validate manager account ID
        OperationResult managerIdVerification = validateManagerId(managerID);
        if (!managerIdVerification.isValid()) {
            return managerIdVerification;
        }

        // Retrieve the manager account record with the ID from the repository
        StaffAccount managerAcc = staffAccountRepo.getRecordById(managerID);

        // Begin creating records report procedure in service class
        RecordWriter createRecordsReport = new RecordsReportCreation(
                recordsReportRepo, customerAccountRepo, staffAccountRepo, serviceActivityRepo, vehiclePartRepo,
                servicePackageRepo, appointmentRepo, managerAcc);
        return createRecordsReport.execute();
    }

    public OperationResult deleteRecordsReport(String reportID) {

        // Check if the inputs are null or empty
        if (reportID == null || reportID.isBlank()) {
            return OperationResult.failed("The records report ID must be provided to delete a record. Please try again.");
        }

        // Check if the report ID is in UUID format
        if (!DataValidator.validateRecordId(reportID).isValid()) {
            return OperationResult.failed(DataFieldName.RECORD_ID, "The records report ID: " +
                    reportID + " is not an UUID. Please try again.");
        }

        // Begin deleting records report procedure in service class
        RecordWriter deleteRecordsReport = new RecordsReportDeletion(recordsReportRepo, reportID);
        return deleteRecordsReport.execute();
    }

    /**
     * Optional class is used to determine whether the records are present or not
     * This is for frontend logic in displaying the result message for UI
     */
    public Optional<List<DataReport>> retrieveAllRecordsReports() {

        // Begin retrieving all records reports procedures in service class
        RecordReader<DataReport> retrieveAllRecordsReports = new RecordsReportRetrieval(recordsReportRepo);
        return retrieveAllRecordsReports.getAllRecords();
    }

    public Optional<DataReport> retrieveRecordsReportById(String reportId) {

        // Begin retrieving records report by ID procedure in service class
        RecordReader<DataReport> retrieveRecordsReportById = new RecordsReportRetrieval(recordsReportRepo);
        return retrieveRecordsReportById.getRecordById(reportId);
    }

    public Optional<List<DataReport>> retrieveRecordsReportsByIds(List<String> invoiceIds) {

        // Begin retrieving multiple records reports by IDs procedure in service class
        RecordReader<DataReport> retrieveRecordsReportsByIds = new RecordsReportRetrieval(recordsReportRepo);
        return retrieveRecordsReportsByIds.getRecordsByIds(invoiceIds);
    }

    public Optional<DataReport> retrieveRecordsReportByReportDocNo(String reportDocNo) {

        // Begin retrieving records report by document number procedure in service class
        RecordsReportRetrieval retrieveRecordsReportByReportDocNo = new RecordsReportRetrieval(recordsReportRepo);
        return retrieveRecordsReportByReportDocNo.getRecordByReportDocNo(reportDocNo);
    }

    /**
     * Validate the format and length of records report ID
     * This validation is done before record retrieval, so the frontend can first display the validation result
     */
    public OperationResult validateRecordsReportId(String reportId) {

        // Check if the records report ID and its type are null or empty
        if (reportId == null || reportId.isBlank()) {
            return OperationResult.failed("The records report ID must be provided to retrieve records. " +
                    "Please try again.");
        }

        if (!DataValidator.validateRecordId(reportId).isValid()) {
            return OperationResult.failed("The records report ID: " + reportId +
                    " is not an UUID. Please try again.");
        }

        return OperationResult.success();
    }

    public OperationResult validateRecordsReportIds(List<String> reportIds) {

        // Check if the ID list and its type are null or empty
        if (reportIds == null || reportIds.isEmpty()) {
            return OperationResult.failed("The records report IDs must be provided to retrieve records. " +
                    "Please try again.");
        }

        // Check if any of the invoice IDs are null or empty
        for (String invoiceId : reportIds) {
            if (invoiceId == null || invoiceId.isBlank() ) {
                return OperationResult.failed("One of the records report IDs is null or empty. Please try again.");
            }
        }

        // Check if any of the invoice IDs are invalid
        for (String invoiceId : reportIds) {
            if (!DataValidator.validateRecordId(invoiceId).isValid()) {
                return OperationResult.failed("The records report ID: " + invoiceId +
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
