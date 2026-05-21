package main.java.dtos;

import main.java.enums.data.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentDTO {

    private String appointmentID;
    private String appointmentTitle;
    private String appointmentDescription;
    private String servicePackageID;
    private LocalDate serviceDate;
    private LocalTime serviceTime;
    private String technicianID;
    private AppointmentStatus appointmentStatus;

    /**
     * For creating a new appointment
     */
    public AppointmentDTO(String appointmentTitle, String appointmentDescription, String servicePackageID,
                          LocalDate serviceDate, LocalTime serviceTime) {
        this.appointmentTitle = appointmentTitle;
        this.appointmentDescription = appointmentDescription;
        this.serviceDate = serviceDate;
        this.serviceTime = serviceTime;
        this.servicePackageID = servicePackageID;
    }

    /**
     * For updating an existing appointment's details
     */
    public AppointmentDTO(String appointmentID, String appointmentTitle, String appointmentDescription,
                          String servicePackageID, LocalDate serviceDate, LocalTime serviceTime,
                          AppointmentStatus appointmentStatus) {
        this.appointmentID = appointmentID;
        this.appointmentTitle = appointmentTitle;
        this.appointmentDescription = appointmentDescription;
        this.serviceDate = serviceDate;
        this.serviceTime = serviceTime;
        this.servicePackageID = servicePackageID;
        this.appointmentStatus = appointmentStatus;
    }

    /**
     * For assigning a technician to an existing appointment
     */
    public AppointmentDTO(String appointmentID, String technicianID) {
        this.appointmentID = appointmentID;
        this.technicianID = technicianID;
    }

    /**
     * For updating the status of an existing appointment
     */
    public AppointmentDTO(String appointmentID, AppointmentStatus appointmentStatus) {
        this.appointmentID = appointmentID;
        this.appointmentStatus = appointmentStatus;
    }

    public String getAppointmentID() {
        return appointmentID;
    }

    public String getAppointmentTitle() {
        return appointmentTitle;
    }

    public String getAppointmentDescription() {
        return appointmentDescription;
    }

    public String getServicePackageID() {
        return servicePackageID;
    }

    public LocalDate getServiceDate() {
        return serviceDate;
    }

    public LocalTime getServiceTime() {
        return serviceTime;
    }

    public String getTechnicianID() {
        return technicianID;
    }

    public AppointmentStatus getAppointmentStatus() {
        return appointmentStatus;
    }
}
