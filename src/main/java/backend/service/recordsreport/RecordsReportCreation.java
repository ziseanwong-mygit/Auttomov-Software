package main.java.backend.service.recordsreport;

import main.java.backend.model.DataReport;
import main.java.backend.model.StaffAccount;
import main.java.backend.repository.impl.*;
import main.java.backend.repository.impl.account.CustomerAccountRepository;
import main.java.backend.repository.impl.account.StaffAccountRepository;
import main.java.backend.repository.impl.report.DataReportRepository;
import main.java.backend.service.RecordWriter;
import main.java.backend.util.RecordNoGenerator;
import main.java.enums.data.JobPosition;
import main.java.enums.data.ReportType;
import main.java.records.OperationResult;

import java.time.LocalDate;
import java.util.UUID;

public class RecordsReportCreation implements RecordWriter {

    private final DataReportRepository dataReportRepository;
    private final CustomerAccountRepository customerAccountRepository;
    private final StaffAccountRepository staffAccountRepository;
    private final ServiceActivityRepository serviceActivityRepository;
    private final VehiclePartRepository vehiclePartRepository;
    private final ServicePackageRepository servicePackageRepository;
    private final AppointmentRepository appointmentRepository;
    private final StaffAccount existingManager;

    public RecordsReportCreation(DataReportRepository dataReportRepository,
                                 CustomerAccountRepository customerAccountRepository,
                                 StaffAccountRepository staffAccountRepository,
                                 ServiceActivityRepository serviceActivityRepository,
                                 VehiclePartRepository vehiclePartRepository,
                                 ServicePackageRepository servicePackageRepository,
                                 AppointmentRepository appointmentRepository,
                                 StaffAccount existingManager) {
        this.dataReportRepository = dataReportRepository;
        this.customerAccountRepository = customerAccountRepository;
        this.staffAccountRepository = staffAccountRepository;
        this.serviceActivityRepository = serviceActivityRepository;
        this.vehiclePartRepository = vehiclePartRepository;
        this.servicePackageRepository = servicePackageRepository;
        this.appointmentRepository = appointmentRepository;
        this.existingManager = existingManager;
    }

    @Override
    public OperationResult execute() {

        // Create a new records report object
        DataReport newReport = populateRecordsReportDetails();

        // Save the records report into the file
        return dataReportRepository.createRecord(newReport);
    }

    private DataReport populateRecordsReportDetails() {

        // Get the current month and year
        int currentMonth = LocalDate.now().getMonthValue();
        int currentYear = LocalDate.now().getYear();

        // Generate new report ID and document number for the new record
        String reportId = UUID.randomUUID().toString();
        String reportDocNo = "RPT" + RecordNoGenerator.generateRecordNo();

        // Get the required statistics to generating the report
        int totalCustomers = customerAccountRepository.getAllRecords().size();
        int totalTechnicians = staffAccountRepository.getRecordsByJobPosition(JobPosition.TECHNICIAN).size();
        int totalCounterStaffs = staffAccountRepository.getRecordsByJobPosition(JobPosition.COUNTER_STAFF).size();
        int totalManagers = staffAccountRepository.getRecordsByJobPosition(JobPosition.MANAGER).size();
        int totalServiceActivities = serviceActivityRepository.getAllRecords().size();
        int totalVehicleParts = vehiclePartRepository.getAllRecords().size();
        int totalServicePackages = servicePackageRepository.getAllRecords().size();
        int totalAppointments = appointmentRepository.getAllRecords().size();
        int totalCustomersByThisMonth = customerAccountRepository
                .getActiveRecordsByCreationMonth(currentMonth).size();
        int totalCustomersByThisYear = customerAccountRepository
                .getActiveRecordsByCreationYear(currentYear).size();
        int totalCustomersLeftByThisYear = customerAccountRepository
                .getInactiveRecordsByCreationYear(currentYear).size();
        int totalCustomersLeftByThisMonth = customerAccountRepository
                .getInactiveRecordsByCreationMonth(currentMonth).size();

        return new DataReport(reportId, reportDocNo, LocalDate.now(), ReportType.DATA_REPORT, existingManager,
                totalCustomers, totalTechnicians, totalCounterStaffs, totalManagers, totalServiceActivities,
                totalVehicleParts, totalServicePackages, totalAppointments, totalCustomersByThisMonth,
                totalCustomersByThisYear, totalCustomersLeftByThisMonth, totalCustomersLeftByThisYear);
    }
}
