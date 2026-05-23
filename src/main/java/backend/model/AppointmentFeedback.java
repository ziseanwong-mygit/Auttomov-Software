package main.java.backend.model;

import java.time.LocalDateTime;

public class AppointmentFeedback {

    private final String feedbackID;
    private final LocalDateTime feedbackCreationTime;
    private LocalDateTime feedbackUpdateTime;
    private final Appointment appointment;
    private final StaffAccount feedbackAuthor;
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

    public void setFeedbackContent(String feedbackContent) {
        this.feedbackContent = feedbackContent;
    }
}
