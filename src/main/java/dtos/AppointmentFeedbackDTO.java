package main.java.dtos;

public class AppointmentFeedbackDTO {

    private String feedbackID;
    private String appointmentID;
    private String feedbackAuthorID;
    private final String feedbackContent;

    public AppointmentFeedbackDTO(String appointmentID, String feedbackAuthorID, String feedbackContent) {
        this.appointmentID = appointmentID;
        this.feedbackAuthorID = feedbackAuthorID;
        this.feedbackContent = feedbackContent;
    }

    public AppointmentFeedbackDTO(String feedbackID, String feedbackContent) {
        this.feedbackID = feedbackID;
        this.feedbackContent = feedbackContent;
    }

    public String getFeedbackID() {
        return feedbackID;
    }

    public String getAppointmentID() {
        return appointmentID;
    }

    public String getFeedbackAuthorID() {
        return feedbackAuthorID;
    }

    public String getFeedbackContent() {
        return feedbackContent;
    }
}
