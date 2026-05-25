package main.java.backend.model;

import main.java.enums.data.ReportType;

import java.time.LocalDate;

public class DataReport extends Report {

    private final int totalCustomers;
    private final int totalTechnicians;
    private final int totalCounterStaffs;
    private final int totalManagers;

    private final int totalServiceActivities;
    private final int totalVehicleParts;
    private final int totalServicePackages;
    private final int totalAppointments;

    private final int totalCustomersByThisMonth;
    private final int totalCustomersByThisYear;
    private final int totalCustomersLeftByThisMonth;
    private final int totalCustomersLeftByThisYear;

    public DataReport(String reportID, String reportDocNo, LocalDate reportCreationDate, ReportType reportType,
                      StaffAccount managerAccount, int totalCustomers, int TotalTechnicians, int totalCounterStaffs,
                      int totalManagers, int totalServiceActivities, int totalVehicleParts, int totalServicePackages,
                      int totalAppointments, int totalCustomersByThisMonth, int totalCustomersByThisYear,
                      int totalCustomersLeftByThisMonth, int totalCustomersLeftByThisYear) {

        super(reportID, reportDocNo, reportCreationDate, reportType, managerAccount);

        this.totalCustomers = totalCustomers;
        this.totalTechnicians = TotalTechnicians;
        this.totalCounterStaffs = totalCounterStaffs;
        this.totalManagers = totalManagers;
        this.totalServiceActivities = totalServiceActivities;
        this.totalVehicleParts = totalVehicleParts;
        this.totalServicePackages = totalServicePackages;
        this.totalAppointments = totalAppointments;
        this.totalCustomersByThisMonth = totalCustomersByThisMonth;
        this.totalCustomersByThisYear = totalCustomersByThisYear;
        this.totalCustomersLeftByThisMonth = totalCustomersLeftByThisMonth;
        this.totalCustomersLeftByThisYear = totalCustomersLeftByThisYear;
    }

    public int getTotalCustomers() {
        return totalCustomers;
    }

    public int getTotalTechnicians() {
        return totalTechnicians;
    }

    public int getTotalCounterStaffs() {
        return totalCounterStaffs;
    }

    public int getTotalManagers() {
        return totalManagers;
    }

    public int getTotalServiceActivities() {
        return totalServiceActivities;
    }

    public int getTotalVehicleParts() {
        return totalVehicleParts;
    }

    public int getTotalServicePackages() {
        return totalServicePackages;
    }

    public int getTotalAppointments() {
        return totalAppointments;
    }

    public int getTotalCustomersByThisMonth() {
        return totalCustomersByThisMonth;
    }

    public int getTotalCustomersByThisYear() {
        return totalCustomersByThisYear;
    }

    public int getTotalCustomersLeftByThisMonth() {
        return totalCustomersLeftByThisMonth;
    }

    public int getTotalCustomersLeftByThisYear() {
        return totalCustomersLeftByThisYear;
    }

    /**
     * Return a human-readable string representation of the object.
     * Used for logging, debugging, testing, or printing overall information.
     */
    @Override
    public String toString() {
        String basicDetails = super.toString();

        return basicDetails +
                "\nTotal Customers: " + totalCustomers +
                "\nTotal Technicians: " + totalTechnicians +
                "\nTotal Counter Staffs: " + totalCounterStaffs +
                "\nTotal Managers: " + totalManagers +
                "\nTotal Service Activities: " + totalServiceActivities +
                "\nTotal Vehicle Parts: " + totalVehicleParts +
                "\nTotal Service Packages: " + totalServicePackages +
                "\nTotal Appointments: " + totalAppointments +
                "\nTotal Customers by this month: " + totalCustomersByThisMonth +
                "\nTotal Customers by this year: " + totalCustomersByThisYear +
                "\nTotal Customers left by this month: " + totalCustomersLeftByThisMonth +
                "\nTotal Customers left by this year: " + totalCustomersLeftByThisYear;
    }
}
