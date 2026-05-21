package main.java.backend.model;

import main.java.enums.data.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Appointment {

    private final String appointmentID;
    private final String appointmentOrderNo;
    private final LocalDateTime appointmentCreationTime;
    private LocalDateTime appointmentUpdateTime;
    private String appointmentTitle;
    private String appointmentDescription;
    private ServicePackage servicePackage;
    private LocalDate serviceDate;
    private LocalTime serviceTime;
    private StaffAccount assignedTechnician;
    private AppointmentStatus appointmentStatus;

    public Appointment(String appointmentID, String appointmentOrderNo, LocalDateTime appointmentCreationTime,
                       LocalDateTime appointmentUpdateTime, String appointmentTitle, String appointmentDescription,
                       ServicePackage servicePackage, LocalDate serviceDate, LocalTime serviceTime,
                       StaffAccount assignedTechnician, AppointmentStatus appointmentStatus) {

        this.appointmentID = appointmentID;
        this.appointmentOrderNo = appointmentOrderNo;
        this.appointmentCreationTime = appointmentCreationTime;
        this.appointmentUpdateTime = appointmentUpdateTime;
        this.appointmentTitle = appointmentTitle;
        this.appointmentDescription = appointmentDescription;
        this.servicePackage = servicePackage;
        this.serviceDate = serviceDate;
        this.serviceTime = serviceTime;
        this.assignedTechnician = assignedTechnician;
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

    public ServicePackage getServicePackage() {
        return servicePackage;
    }

    public LocalDate getServiceDate() {
        return serviceDate;
    }

    public LocalTime getServiceTime() {
        return serviceTime;
    }

    public StaffAccount getAssignedTechnician() {
        return assignedTechnician;
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

    public void setServicePackage(ServicePackage servicePackage){
        this.servicePackage = servicePackage;
    }

    public void setServiceDate(LocalDate serviceDate){
        this.serviceDate = serviceDate;
    }

    public void setServiceTime(LocalTime serviceTime){
        this.serviceTime = serviceTime;
    }

    public void setAssignedTechnician(StaffAccount assignedTechnician) {
        this.assignedTechnician = assignedTechnician;
    }

    public void setAppointmentStatus(AppointmentStatus appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }

    /**
     * Gather all data into one single line, with each value separated by commas
     * To ease the process of writing new record into the file
     */
    @Override
    public String toString() {
        return "Appointment ID: " + appointmentID +
                "\nAppointment Order No: " + appointmentOrderNo +
                "\nAppointment Creation Date: " + appointmentCreationTime +
                "\nAppointment Update Date: " + appointmentUpdateTime +
                "\nAppointment Title: " + appointmentTitle +
                "\nAppointment Description: " + appointmentDescription +
                "\nService Package: " + servicePackage.getServicePackageName() +
                "\nService Date: " + serviceDate +
                "\nService Time: " + serviceTime +
                "\nAssigned Technician: " + assignedTechnician.getFirstName() + " " + assignedTechnician.getLastName() +
                "\nAppointment Status: " + appointmentStatus.getAppointmentStatus() +
                "\n";
    }
}
