package main.java.dtos;

public class AppointmentCommentDTO {

    private String commentID;
    private String appointmentID;
    private String commentAuthorID;
    private final String commentContent;

    public AppointmentCommentDTO(String appointmentID, String commentAuthorID, String commentContent) {
        this.appointmentID = appointmentID;
        this.commentAuthorID = commentAuthorID;
        this.commentContent = commentContent;
    }

    public AppointmentCommentDTO(String commentID, String commentContent) {
        this.commentID = commentID;
        this.commentContent = commentContent;
    }

    public String getCommentID() {
        return commentID;
    }

    public String getAppointmentID() {
        return appointmentID;
    }

    public String getCommentAuthorID() {
        return commentAuthorID;
    }

    public String getCommentContent() {
        return commentContent;
    }
}
