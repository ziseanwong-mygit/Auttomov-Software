package main.java.backend.model;

import main.java.backend.util.DateTimeStringFormatter;
import main.java.enums.data.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class Appointment {

    private final String appointmentID;
    private final String appointmentOrderNo;
    private final LocalDateTime appointmentCreationTime;
    private LocalDateTime appointmentUpdateTime;
    private String appointmentTitle;
    private String appointmentDescription;
    private UserAccount customerAccount;
    private ServicePackage servicePackage;
    private LocalDate serviceDate;
    private LocalTime serviceStartTime;
    private LocalTime serviceEndTime;
    private List<StaffAccount> technicianList;
    private AppointmentStatus appointmentStatus;

    public Appointment(String appointmentID, String appointmentOrderNo, LocalDateTime appointmentCreationTime,
                       LocalDateTime appointmentUpdateTime, String appointmentTitle, String appointmentDescription,
                       UserAccount customerAccount, ServicePackage servicePackage, LocalDate serviceDate,
                       LocalTime serviceStartTime, LocalTime serviceEndTime, List<StaffAccount> technicianList,
                       AppointmentStatus appointmentStatus) {

        this.appointmentID = appointmentID;
        this.appointmentOrderNo = appointmentOrderNo;
        this.appointmentCreationTime = appointmentCreationTime;
        this.appointmentUpdateTime = appointmentUpdateTime;
        this.appointmentTitle = appointmentTitle;
        this.appointmentDescription = appointmentDescription;
        this.customerAccount = customerAccount;
        this.servicePackage = servicePackage;
        this.serviceDate = serviceDate;
        this.serviceStartTime = serviceStartTime;
        this.serviceEndTime = serviceEndTime;
        this.technicianList = technicianList;
        this.appointmentStatus = appointmentStatus;
    }

    public String getAppointmentID() {
        return appointmentID;
    }

    public String getAppointmentOrderNo() {
        return appointmentOrderNo;
    }

    public LocalDateTime getAppointmentCreationTime() {
        return appointmentCreationTime;
    }

    public LocalDateTime getAppointmentUpdateTime() {
        return appointmentUpdateTime;
    }

    public String getAppointmentTitle() {
        return appointmentTitle;
    }

    public String getAppointmentDescription() {
        return appointmentDescription;
    }

    public UserAccount getCustomerAccount() {
        return customerAccount;
    }

    public ServicePackage getServicePackage() {
        return servicePackage;
    }

    public LocalDate getServiceDate() {
        return serviceDate;
    }

    public LocalTime getServiceStartTime() {
        return serviceStartTime;
    }

    public LocalTime getServiceEndTime() {
        return serviceEndTime;
    }

    public List<StaffAccount> getTechnicianList() {
        return technicianList;
    }

    public AppointmentStatus getAppointmentStatus() {
        return appointmentStatus;
    }

    public void setAppointmentUpdateTime(LocalDateTime appointmentUpdateTime) {
        this.appointmentUpdateTime = appointmentUpdateTime;
    }

    public void setAppointmentTitle(String appointmentTitle){
        this.appointmentTitle = appointmentTitle;
    }

    public void setAppointmentDescription(String appointmentDescription){
        this.appointmentDescription = appointmentDescription;
    }

    public void setCustomerAccount(UserAccount customerAccount){
        this.customerAccount = customerAccount;
    }

    public void setServicePackage(ServicePackage servicePackage){
        this.servicePackage = servicePackage;
    }

    public void setServiceDate(LocalDate serviceDate){
        this.serviceDate = serviceDate;
    }

    public void setServiceStartTime(LocalTime serviceStartTime){
        this.serviceStartTime = serviceStartTime;
    }

    public void setServiceEndTime(LocalTime serviceEndTime){
        this.serviceEndTime = serviceEndTime;
    }

    public void setTechnicianList(List<StaffAccount> technicianList) {
        this.technicianList = technicianList;
    }

    public void setAppointmentStatus(AppointmentStatus appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }

    /**
     * Return a human-readable string representation of the object.
     * Used for logging, debugging, testing, or printing overall information.
     */
    @Override
    public String toString() {

        // Get the service activity and vehicle part names and concatenate them into a comma-separated value string
        String technicianListLine = technicianList.stream()
                .map(account -> account.getFirstName() + " " + account.getLastName())
                .collect(Collectors.joining(", "));

        // Format the appointment creation and update time to yyyyMMddHHmmss format (e.g., 2026-05-01 12:34:56)
        String creationTime = DateTimeStringFormatter.formatDateTime(appointmentCreationTime);
        String updateTime = DateTimeStringFormatter.formatDateTime(appointmentUpdateTime);

        // If the service package is null, it means that the service package has been deleted from the file;
        // when viewing completed, canceled or incomplete appointments, the service package is not displayed
        return "Appointment ID: " + appointmentID +
                "\nOrder No: " + appointmentOrderNo +
                "\nCreation Date: " + creationTime +
                "\nUpdate Date: " + updateTime +
                "\nTitle: " + appointmentTitle +
                "\nDescription: " +
                (appointmentDescription != null ? appointmentDescription : "(no description)") +
                "\nCustomer: " + customerAccount.getFirstName() + " " + customerAccount.getLastName() +
                "\nService Package: " +
                (servicePackage != null ? servicePackage.getServicePackageName() :
                        "(service package unavailable/deleted)") +
                "\nService Date: " + serviceDate +
                "\nService Time: " + serviceStartTime +
                "\nAssigned Technicians: " +
                (technicianListLine.isBlank() ? "(no technician assigned)" : technicianListLine) +
                "\nAppointment Status: " + appointmentStatus.getAppointmentStatus() +
                "\n";
    }
}
