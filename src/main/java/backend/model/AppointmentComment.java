package main.java.backend.model;

import main.java.backend.util.DateTimeStringFormatter;

import java.time.LocalDateTime;

public class AppointmentComment {

    private final String commentID;
    private final LocalDateTime commentCreationTime;
    private LocalDateTime commentUpdateTime;
    private final Appointment appointment;
    private UserAccount commentAuthor;
    private String commentContent;

    public AppointmentComment(String commentID, LocalDateTime commentCreationTime, LocalDateTime commentUpdateTime,
                              Appointment appointment, UserAccount commentAuthor, String commentContent) {
        this.commentID = commentID;
        this.commentCreationTime = commentCreationTime;
        this.commentUpdateTime = commentUpdateTime;
        this.appointment = appointment;
        this.commentAuthor = commentAuthor;
        this.commentContent = commentContent;
    }

    public String getCommentID() {
        return commentID;
    }

    public LocalDateTime getCommentCreationTime() {
        return commentCreationTime;
    }

    public LocalDateTime getCommentUpdateTime() {
        return commentUpdateTime;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public UserAccount getCommentAuthor() {
        return commentAuthor;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentUpdateTime(LocalDateTime commentUpdateTime) {
        this.commentUpdateTime = commentUpdateTime;
    }

    public void setCommentAuthor(UserAccount commentAuthor) {
        this.commentAuthor = commentAuthor;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    /**
     * Return a human-readable string representation of the object.
     * Used for logging, debugging, testing, or printing overall information.
     */
    @Override
    public String toString() {

        // Format the comment creation and update time to yyyyMMddHHmmss format (e.g., 2026-05-01 12:34:56)
        String creationTime = DateTimeStringFormatter.formatDateTime(commentCreationTime);
        String updateTime = DateTimeStringFormatter.formatDateTime(commentUpdateTime);

        // Get the author's first and last name
        String authorName = commentAuthor.getFirstName() + " " + commentAuthor.getLastName();

        // Get the appointment title
        String appointmentTitle = appointment.getAppointmentTitle();

        return "Comment ID: " + commentID +
                "\nCreation Date: " + creationTime +
                "\nUpdate Date: " + updateTime +
                "\nAppointment: " + appointmentTitle +
                "\nAuthor: " + authorName +
                "\nContent: " + commentContent;

    }
}
