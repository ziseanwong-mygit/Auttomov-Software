package main.java.backend.model;

import main.java.enums.data.ReportType;

import java.time.LocalDate;

public class SalesReport extends Report {

    private final int totalNormalServicesDone;
    private final int totalMajorServicesDone;
    private final int totalAllServicesDone;

    private final double totalNormalServiceSales;
    private final double totalMajorServiceSales;
    private final double totalAllServiceSales;

    private final double totalSalesByThisMonth;
    private final double totalSalesByThisYear;
    private final double totalSalesByLifetime;

    private final double averageSalesByThisMonth;
    private final double averageSalesByThisYear;
    private final double averageSalesByLifetime;

    public SalesReport(String reportID, String reportDocNo, LocalDate reportCreationDate, ReportType reportType,
                       StaffAccount managerAccount, int totalNormalServicesDone, int totalMajorServicesDone,
                       int totalAllServicesDone, double totalNormalServiceSales, double totalMajorServiceSales,
                       double totalAllServiceSales, double totalSalesByThisMonth, double totalSalesByThisYear,
                       double totalSalesByLifetime, double averageSalesByThisMonth, double averageSalesByThisYear,
                       double averageSalesByLifetime) {

        super(reportID, reportDocNo, reportCreationDate, reportType, managerAccount);

        this.totalNormalServicesDone = totalNormalServicesDone;
        this.totalMajorServicesDone = totalMajorServicesDone;
        this.totalAllServicesDone = totalAllServicesDone;
        this.totalNormalServiceSales = totalNormalServiceSales;
        this.totalMajorServiceSales = totalMajorServiceSales;
        this.totalAllServiceSales = totalAllServiceSales;
        this.totalSalesByThisMonth = totalSalesByThisMonth;
        this.totalSalesByThisYear = totalSalesByThisYear;
        this.totalSalesByLifetime = totalSalesByLifetime;
        this.averageSalesByThisMonth = averageSalesByThisMonth;
        this.averageSalesByThisYear = averageSalesByThisYear;
        this.averageSalesByLifetime = averageSalesByLifetime;
    }

    public int getTotalNormalServicesDone() {
        return totalNormalServicesDone;
    }

    public int getTotalMajorServicesDone() {
        return totalMajorServicesDone;
    }

    public int getTotalAllServicesDone() {
        return totalAllServicesDone;
    }

    public double getTotalNormalServiceSales() {
        return totalNormalServiceSales;
    }

    public double getTotalMajorServiceSales() {
        return totalMajorServiceSales;
    }

    public double getTotalAllServiceSales() {
        return totalAllServiceSales;
    }

    public double getTotalSalesByThisMonth() {
        return totalSalesByThisMonth;
    }

    public double getTotalSalesByThisYear() {
        return totalSalesByThisYear;
    }

    public double getTotalSalesByLifetime() {
        return totalSalesByLifetime;
    }

    public double getAverageSalesByThisMonth() {
        return averageSalesByThisMonth;
    }

    public double getAverageSalesByThisYear() {
        return averageSalesByThisYear;
    }

    public double getAverageSalesByLifetime() {
        return averageSalesByLifetime;
    }

    /**
     * Return a human-readable string representation of the object.
     * Used for logging, debugging, testing, or printing overall information.
     */
    @Override
    public String toString() {
        String basicDetails = super.toString();

        return basicDetails +
                "\nTotal Normal Services Done: " + totalNormalServicesDone +
                "\nTotal Major Services Done: " + totalMajorServicesDone +
                "\nTotal All Services Done: " + totalAllServicesDone +
                "\nTotal Normal Service Sales: RM" + totalNormalServiceSales +
                "\nTotal Major Service Sales: RM" + totalMajorServiceSales +
                "\nTotal All Service Sales: RM" + totalAllServiceSales +
                "\nTotal Sales by This Month: RM" + totalSalesByThisMonth +
                "\nTotal Sales by This Year: RM" + totalSalesByThisYear +
                "\nTotal Sales by Lifetime: RM" + totalSalesByLifetime +
                "\nAverage Sales by This Month: RM" + averageSalesByThisMonth +
                "\nAverage Sales by This Year: RM" + averageSalesByThisYear +
                "\nAverage Sales by Lifetime: RM" + averageSalesByLifetime;
    }
}
