package main.java.backend.model;

import main.java.enums.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {

    private String appointmentID;
    private String appointmentOrderNo;
    private LocalDate appointmentCreationDate;
    private String appointmentTitle;
    private String appointmentDescription;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String serviceID;
    private AppointmentStatus appointmentStatus;

    public Appointment(String appointmentID, String appointmentOrderNo, LocalDate appointmentCreationDate,
                       String appointmentTitle, String appointmentDescription, LocalDate appointmentDate,
                       LocalTime appointmentTime, String serviceID, AppointmentStatus appointmentStatus) {

        this.appointmentID = appointmentID;
        this.appointmentOrderNo = appointmentOrderNo;
        this.appointmentCreationDate = appointmentCreationDate;
        this.appointmentTitle = appointmentTitle;
        this.appointmentDescription = appointmentDescription;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.serviceID = serviceID;
        this.appointmentStatus = appointmentStatus;
    }

    public String getAppointmentID() {
        return appointmentID;
    }

    public String getAppointmentOrderNo() {
        return appointmentOrderNo;
    }

    public LocalDate getAppointmentCreationDate() {
        return appointmentCreationDate;
    }

    public String getAppointmentTitle() {
        return appointmentTitle;
    }

    public String getAppointmentDescription() {
        return appointmentDescription;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public String getServiceID() {
        return serviceID;
    }

    public AppointmentStatus getAppointmentStatus() {
        return appointmentStatus;
    }

    /**
     * Gather all data into one single line, with each value separated by commas
     * To ease the process of writing new record into the file
     */
    @Override
    public String toString() {
        return String.join(", ",
                appointmentID,
                appointmentOrderNo,
                appointmentCreationDate.toString(),
                appointmentTitle,
                appointmentDescription,
                appointmentDate.toString(),
                appointmentTime.toString(),
                serviceID,
                appointmentStatus.getAppointmentStatus()
        );
    }
}
