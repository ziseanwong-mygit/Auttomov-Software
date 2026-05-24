package main.java.backend.service.salesreport;

import main.java.backend.model.*;
import main.java.backend.repository.impl.AppointmentRepository;
import main.java.backend.repository.impl.InvoiceRepository;
import main.java.backend.repository.impl.SalesReportRepository;
import main.java.backend.service.RecordWriter;
import main.java.backend.util.RecordNoGenerator;
import main.java.enums.data.ReportType;
import main.java.enums.data.ServicePackageType;
import main.java.records.OperationResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SalesReportCreation implements RecordWriter {

    private final SalesReportRepository salesReportRepository;
    private final AppointmentRepository appointmentRepository;
    private final InvoiceRepository invoiceRepository;
    private final StaffAccount existingManager;

    public SalesReportCreation(SalesReportRepository salesReportRepository, AppointmentRepository appointmentRepository,
                               InvoiceRepository invoiceRepository, StaffAccount existingManager) {
        this.salesReportRepository = salesReportRepository;
        this.appointmentRepository = appointmentRepository;
        this.invoiceRepository = invoiceRepository;
        this.existingManager = existingManager;
    }

    @Override
    public OperationResult execute() {

        // Create a new sales report object
        SalesReport newReport = populateSalesReportDetails();

        // Save the sales report into the file
        return salesReportRepository.createRecord(newReport);
    }

    private SalesReport populateSalesReportDetails() {

        // Get the current month and year
        int currentMonth = LocalDate.now().getMonthValue();
        int currentYear = LocalDate.now().getYear();

        // Generate new report ID and document number for the new record
        String reportId = UUID.randomUUID().toString();
        String reportDocNo = "RPT" + RecordNoGenerator.generateRecordNo();

        // Get all the completed appointments of normal and major services
        List<Appointment> completedNormalServices = appointmentRepository.
                getCompletedRecordsByServiceType(ServicePackageType.NORMAL);
        List<Appointment> completedMajorServices = appointmentRepository.
                getCompletedRecordsByServiceType(ServicePackageType.MAJOR);

        // Get all the appointment IDs of the completed normal and major services
        List<String> normalAppointmentIds = new ArrayList<>();
        List<String> majorAppointmentIds = new ArrayList<>();
        for (Appointment appointment : completedNormalServices) {
            normalAppointmentIds.add(appointment.getAppointmentID());
        }
        for (Appointment appointment : completedMajorServices) {
            majorAppointmentIds.add(appointment.getAppointmentID());
        }

        // Get all the paid invoices of the completed normal and major services
        List<Invoice> invoicesOfNormalService = invoiceRepository.getPaidRecordsByAppointmentIds(normalAppointmentIds);
        List<Invoice> invoicesOfMajorService = invoiceRepository.getPaidRecordsByAppointmentIds(majorAppointmentIds);

        // Get all the completed appointments based on current month and year, regardless of service type
        List<Appointment> completedServicesByThisMonth = appointmentRepository.
                getCompletedRecordsByServiceMonth(currentMonth);
        List<Appointment> completedServicesByThisYear = appointmentRepository.
                getCompletedRecordsByServiceYear(currentYear);
        List<Appointment> completedServicesByLifetime = appointmentRepository.getAllCompletedRecords();

        // Get all the completed appointment IDs based on current month, year and lifetime
        List<String> appointmentIdsOfServicesThisMonth = new ArrayList<>();
        List<String> appointmentIdsOfServicesThisYear = new ArrayList<>();
        List<String> appointmentIdsOfServicesLifetime = new ArrayList<>();
        for (Appointment appointment : completedServicesByThisMonth) {
            appointmentIdsOfServicesThisMonth.add(appointment.getAppointmentID());
        }
        for (Appointment appointment : completedServicesByThisYear) {
            appointmentIdsOfServicesThisYear.add(appointment.getAppointmentID());
        }
        for (Appointment appointment : completedServicesByLifetime) {
            appointmentIdsOfServicesLifetime.add(appointment.getAppointmentID());
        }

        // Get all the invoices of the completed appointments based on current month, year and lifetime
        List<Invoice> invoicesOfServicesThisMonth = invoiceRepository.
                getPaidRecordsByAppointmentIds(appointmentIdsOfServicesThisMonth);
        List<Invoice> invoicesOfServicesThisYear = invoiceRepository.
                getPaidRecordsByAppointmentIds(appointmentIdsOfServicesThisYear);
        List<Invoice> invoicesOfServicesLifetime = invoiceRepository.
                getPaidRecordsByAppointmentIds(appointmentIdsOfServicesLifetime);

        // Calculate the total normal, major and all services done
        int totalNormalServicesDone = completedNormalServices.size();
        int totalMajorServicesDone = completedMajorServices.size();
        int totalAllServicesDone = totalNormalServicesDone + totalMajorServicesDone;

        // Calculate the total sales of normal, major and all services
        double totalNormalServiceSales = 0.00;
        double totalMajorServiceSales = 0.00;
        for (Invoice invoice : invoicesOfNormalService) {
            double totalAmount = invoice.getTotalAmount();
            totalNormalServiceSales += totalAmount;
        }
        for (Invoice invoice : invoicesOfMajorService) {
            double totalAmount = invoice.getTotalAmount();
            totalMajorServiceSales += totalAmount;
        }
        double totalAllServiceSales = totalNormalServiceSales + totalMajorServiceSales;

        // Calculate the total sales of services done in the current month, year and lifetime
        double totalSalesByThisMonth = 0.00;
        double totalSalesByThisYear = 0.00;
        double totalSalesByLifetime = 0.00;
        for (Invoice invoice : invoicesOfServicesThisMonth) {
            double totalAmount = invoice.getTotalAmount();
            totalSalesByThisMonth += totalAmount;
        }
        for (Invoice invoice : invoicesOfServicesThisYear) {
            double totalAmount = invoice.getTotalAmount();
            totalSalesByThisYear += totalAmount;
        }

        for (Invoice invoice : invoicesOfServicesLifetime) {
            double totalAmount = invoice.getTotalAmount();
            totalSalesByLifetime += totalAmount;
        }

        // Calculate the average sales of services done in the current month, year and lifetime
        double averageSalesByThisMonth = totalSalesByThisMonth / appointmentIdsOfServicesThisMonth.size();
        double averageSalesByThisYear = totalSalesByThisYear / appointmentIdsOfServicesThisYear.size();
        double averageSalesByLifetime = totalSalesByLifetime / appointmentIdsOfServicesLifetime.size();

        return new SalesReport(reportId, reportDocNo, LocalDate.now(), ReportType.SALES_REPORT, existingManager,
                totalNormalServicesDone, totalMajorServicesDone, totalAllServicesDone, totalNormalServiceSales,
                totalMajorServiceSales, totalAllServiceSales, totalSalesByThisMonth, totalSalesByThisYear,
                totalSalesByLifetime, averageSalesByThisMonth, averageSalesByThisYear, averageSalesByLifetime);
    }
}
