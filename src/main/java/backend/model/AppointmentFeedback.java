package main.java.backend.model;

import main.java.backend.util.DateTimeStringFormatter;

import java.time.LocalDateTime;

public class AppointmentFeedback {

    private final String feedbackID;
    private final LocalDateTime feedbackCreationTime;
    private LocalDateTime feedbackUpdateTime;
    private final Appointment appointment;
    private StaffAccount feedbackAuthor;
    private String feedbackContent;

    public AppointmentFeedback(String feedbackID, LocalDateTime feedbackCreationTime, LocalDateTime feedbackUpdateTime,
                               Appointment appointment, StaffAccount feedbackAuthor, String feedbackContent) {
        this.feedbackID = feedbackID;
        this.feedbackCreationTime = feedbackCreationTime;
        this.feedbackUpdateTime = feedbackUpdateTime;
        this.appointment = appointment;
        this.feedbackAuthor = feedbackAuthor;
        this.feedbackContent = feedbackContent;
    }

    public String getFeedbackID() {
        return feedbackID;
    }

    public LocalDateTime getFeedbackCreationTime() {
        return feedbackCreationTime;
    }

    public LocalDateTime getFeedbackUpdateTime() {
        return feedbackUpdateTime;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public StaffAccount getFeedbackAuthor() {
        return feedbackAuthor;
    }

    public String getFeedbackContent() {
        return feedbackContent;
    }

    public void setFeedbackUpdateTime(LocalDateTime feedbackUpdateTime) {
        this.feedbackUpdateTime = feedbackUpdateTime;
    }

    public void setFeedbackAuthor(StaffAccount feedbackAuthor) {
        this.feedbackAuthor = feedbackAuthor;
    }

    public void setFeedbackContent(String feedbackContent) {
        this.feedbackContent = feedbackContent;
    }

    /**
     * Return a human-readable string representation of the object.
     * Used for logging, debugging, testing, or printing overall information.
     */
    @Override
    public String toString() {

        // Format the feedback creation and update time to yyyyMMddHHmmss format (e.g., 2026-05-01 12:34:56)
        String creationTime = DateTimeStringFormatter.formatDateTime(feedbackCreationTime);
        String updateTime = DateTimeStringFormatter.formatDateTime(feedbackUpdateTime);

        // Get the author's first and last name
        String authorName = feedbackAuthor.getFirstName() + " " + feedbackAuthor.getLastName();

        // Get the appointment title
        String appointmentTitle = appointment.getAppointmentTitle();

        return "Feedback ID: " + feedbackID +
                "\nCreation Date: " + creationTime +
                "\nUpdate Date: " + updateTime +
                "\nAppointment: " + appointmentTitle +
                "\nAuthor: " + authorName +
                "\nContent: " + feedbackContent;

    }
}
