package main.java.dtos;

import main.java.enums.data.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AppointmentDTO {

    private String appointmentID;
    private String appointmentTitle;
    private String appointmentDescription;
    private String servicePackageID;
    private LocalDate serviceDate;
    private LocalTime serviceStartTime;
    private List<String> technicianIDs;
    private AppointmentStatus appointmentStatus;

    /**
     * For creating a new appointment
     */
    public AppointmentDTO(String appointmentTitle, String appointmentDescription, String servicePackageID,
                          LocalDate serviceDate, LocalTime serviceStartTime) {
        this.appointmentTitle = appointmentTitle;
        this.appointmentDescription = appointmentDescription;
        this.servicePackageID = servicePackageID;
        this.serviceDate = serviceDate;
        this.serviceStartTime = serviceStartTime;
    }

    /**
     * For updating an existing appointment's details
     */
    public AppointmentDTO(String appointmentID, String appointmentTitle, String appointmentDescription,
                          String servicePackageID, LocalDate serviceDate, LocalTime serviceStartTime,
                          AppointmentStatus appointmentStatus) {
        this(appointmentTitle, appointmentDescription, servicePackageID, serviceDate, serviceStartTime);
        this.appointmentID = appointmentID;
        this.appointmentStatus = appointmentStatus;
    }

    /**
     * For assigning a technician to an existing appointment
     */
    public AppointmentDTO(String appointmentID, List<String> technicianIDs) {
        this.appointmentID = appointmentID;
        this.technicianIDs = technicianIDs;
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

    public LocalTime getServiceStartTime() {
        return serviceStartTime;
    }

    public List<String> getTechnicianIDs() {
        return technicianIDs;
    }

    public AppointmentStatus getAppointmentStatus() {
        return appointmentStatus;
    }
}
