package main.java.backend.service.report;

import main.java.backend.model.*;
import main.java.backend.repository.impl.*;
import main.java.backend.repository.impl.account.CustomerAccountRepository;
import main.java.backend.repository.impl.account.StaffAccountRepository;
import main.java.backend.repository.impl.report.DataReportRepository;
import main.java.backend.repository.impl.report.ReportRepository;
import main.java.backend.service.RecordWriter;
import main.java.backend.util.RecordNoGenerator;
import main.java.enums.data.JobPosition;
import main.java.enums.data.ReportType;
import main.java.enums.data.ServicePackageType;
import main.java.records.OperationResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReportCreation implements RecordWriter {

    // Wildcard is used so can accept either data report or sales report repository in one variable
    private final ReportRepository<? extends Report> reportRepository;

    private final CustomerAccountRepository customerAccountRepository;
    private final StaffAccountRepository staffAccountRepository;
    private final ServiceActivityRepository serviceActivityRepository;
    private final VehiclePartRepository vehiclePartRepository;
    private final ServicePackageRepository servicePackageRepository;
    private final AppointmentRepository appointmentRepository;
    private final InvoiceRepository invoiceRepository;
    private final StaffAccount existingManager;

    public ReportCreation(ReportRepository<? extends Report> dataReportRepository,
                          CustomerAccountRepository customerAccountRepository,
                          StaffAccountRepository staffAccountRepository,
                          ServiceActivityRepository serviceActivityRepository,
                          VehiclePartRepository vehiclePartRepository,
                          ServicePackageRepository servicePackageRepository,
                          AppointmentRepository appointmentRepository,
                          InvoiceRepository invoiceRepository,
                          StaffAccount existingManager) {
        this.reportRepository = dataReportRepository;
        this.customerAccountRepository = customerAccountRepository;
        this.staffAccountRepository = staffAccountRepository;
        this.serviceActivityRepository = serviceActivityRepository;
        this.vehiclePartRepository = vehiclePartRepository;
        this.servicePackageRepository = servicePackageRepository;
        this.appointmentRepository = appointmentRepository;
        this.invoiceRepository = invoiceRepository;
        this.existingManager = existingManager;
    }

    @Override
    public OperationResult execute() {

        // Create a new report object
        Report newReport = populateReportDetails();

        // Cast the newReport to specific report type, then only create the record; base implementation
        return reportRepository.createRecordBase(newReport);
    }

    private Report populateReportDetails() {

        // Generate new report ID and document number for the new record
        String reportId = UUID.randomUUID().toString();
        String reportDocNo = "RPT" + RecordNoGenerator.generateRecordNo();

        if (reportRepository instanceof DataReportRepository) {
            return populateDataReportDetails(reportId, reportDocNo);
        } else {
            return populateSalesReportDetails(reportId, reportDocNo);
        }
    }

    private DataReport populateDataReportDetails(String reportId, String reportDocNo) {

        // Get the current month and year
        int currentMonth = LocalDate.now().getMonthValue();
        int currentYear = LocalDate.now().getYear();

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

    private SalesReport populateSalesReportDetails(String reportId, String reportDocNo) {

        // Get the current month and year
        int currentMonth = LocalDate.now().getMonthValue();
        int currentYear = LocalDate.now().getYear();

        // Get all the completed appointments of normal and major services
        List<Appointment> completedNormalServices = appointmentRepository.
                getCompletedRecordsByServiceType(ServicePackageType.NORMAL);
        List<Appointment> completedMajorServices = appointmentRepository.
                getCompletedRecordsByServiceType(ServicePackageType.MAJOR);

        // Get all the paid invoices of the completed normal and major services
        List<Invoice> invoicesOfNormalService = getPaidInvoicesByAppointments(completedNormalServices);
        List<Invoice> invoicesOfMajorService = getPaidInvoicesByAppointments(completedMajorServices);

        // Get all the completed appointments based on current month and year, regardless of service type
        List<Appointment> completedServicesByThisMonth = appointmentRepository.
                getCompletedRecordsByServiceMonth(currentMonth);
        List<Appointment> completedServicesByThisYear = appointmentRepository.
                getCompletedRecordsByServiceYear(currentYear);
        List<Appointment> completedServicesByLifetime = appointmentRepository.getAllCompletedRecords();

        // Get all the invoices for the completed appointments based on current month, year and lifetime
        List<Invoice> invoicesOfServicesThisMonth = getPaidInvoicesByAppointments(completedServicesByThisMonth);
        List<Invoice> invoicesOfServicesThisYear = getPaidInvoicesByAppointments(completedServicesByThisYear);
        List<Invoice> invoicesOfServicesLifetime = getPaidInvoicesByAppointments(completedServicesByLifetime);

        // Calculate the total normal, major and all services done
        int totalNormalServicesDone = completedNormalServices.size();
        int totalMajorServicesDone = completedMajorServices.size();
        int totalAllServicesDone = totalNormalServicesDone + totalMajorServicesDone;

        // Calculate the total sales of normal, major and all services
        double totalNormalServiceSales = calculateTotalSales(invoicesOfNormalService);
        double totalMajorServiceSales = calculateTotalSales(invoicesOfMajorService);
        double totalAllServiceSales = totalNormalServiceSales + totalMajorServiceSales;

        // Calculate the total sales of services done in the current month, year and lifetime
        double totalSalesByThisMonth = calculateTotalSales(invoicesOfServicesThisMonth);
        double totalSalesByThisYear = calculateTotalSales(invoicesOfServicesThisYear);
        double totalSalesByLifetime = calculateTotalSales(invoicesOfServicesLifetime);

        // Calculate the average sales of services done in the current month, year and lifetime
        double averageSalesByThisMonth = calculateAverageSales(
                totalSalesByThisMonth, completedServicesByThisMonth.size());
        double averageSalesByThisYear = calculateAverageSales(
                totalSalesByThisYear, completedServicesByThisYear.size());
        double averageSalesByLifetime = calculateAverageSales(
                totalSalesByLifetime, completedServicesByLifetime.size());

        return new SalesReport(reportId, reportDocNo, LocalDate.now(), ReportType.SALES_REPORT, existingManager,
                totalNormalServicesDone, totalMajorServicesDone, totalAllServicesDone, totalNormalServiceSales,
                totalMajorServiceSales, totalAllServiceSales, totalSalesByThisMonth, totalSalesByThisYear,
                totalSalesByLifetime, averageSalesByThisMonth, averageSalesByThisYear, averageSalesByLifetime);
    }

    /**
     * Extract the appointment IDs from a list of appointments
     */
    private List<String> getAppointmentIds(List<Appointment> appointments) {

        List<String> appointmentIds = new ArrayList<>();

        for (Appointment appointment : appointments) {
            appointmentIds.add(appointment.getAppointmentID());
        }

        return appointmentIds;
    }

    /**
     * Calculate the total sales of a list of invoices
     */
    private double calculateTotalSales(List<Invoice> invoices) {

        double totalSales = 0.00;

        for (Invoice invoice : invoices) {
            totalSales += invoice.getTotalAmount();
        }

        return totalSales;
    }

    /**
     * Calculate the average sales of completed services
     */
    private double calculateAverageSales(double totalSales, int completedServicesCount) {

        if (completedServicesCount == 0) {
            return 0.00;
        }

        return totalSales / completedServicesCount;
    }

    /**
     * Extract all the paid invoices with a list of appointments
     */
    private List<Invoice> getPaidInvoicesByAppointments(List<Appointment> appointments) {

        List<String> appointmentIds = getAppointmentIds(appointments);

        return invoiceRepository.getPaidRecordsByAppointmentIds(appointmentIds);
    }
}
