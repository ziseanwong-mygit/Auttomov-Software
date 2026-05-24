package main.java.backend.service.appointment;

import main.java.backend.model.Appointment;
import main.java.backend.model.AppointmentComment;
import main.java.backend.model.AppointmentFeedback;
import main.java.backend.model.Invoice;
import main.java.backend.repository.impl.AppointmentCommentRepository;
import main.java.backend.repository.impl.AppointmentFeedbackRepository;
import main.java.backend.repository.impl.AppointmentRepository;
import main.java.backend.repository.impl.InvoiceRepository;
import main.java.backend.service.RecordWriter;
import main.java.records.OperationResult;

import java.util.List;

public class AppointmentDeletion implements RecordWriter {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentCommentRepository appointmentCommentRepository;
    private final AppointmentFeedbackRepository appointmentFeedbackRepository;
    private final InvoiceRepository invoiceRepository;
    private final String appointmentID;

    public AppointmentDeletion(AppointmentRepository appointmentRepository,
                               AppointmentCommentRepository appointmentCommentRepository,
                               AppointmentFeedbackRepository appointmentFeedbackRepository,
                               InvoiceRepository invoiceRepository, String appointmentID) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentCommentRepository = appointmentCommentRepository;
        this.appointmentFeedbackRepository = appointmentFeedbackRepository;
        this.invoiceRepository = invoiceRepository;
        this.appointmentID = appointmentID;
    }

    @Override
    public OperationResult execute() {

        // Check if the appointment exists with the given ID
        Appointment existingAppointment = appointmentRepository.getRecordById(appointmentID);
        if (existingAppointment == null) {
            return OperationResult.failed("The appointment with ID: " + appointmentID +
                    " does not exist in the system.");
        }

        // Get all the comments and feedbacks associated with this appointment
        List<AppointmentComment> comments = appointmentCommentRepository.getRecordsByAppointmentId(appointmentID);
        List<AppointmentFeedback> feedbacks = appointmentFeedbackRepository.getRecordsByAppointmentId(appointmentID);

        // Delete all comments and feedbacks associated with this appointment
        if (!comments.isEmpty()) {
            OperationResult commentDeletion = appointmentCommentRepository.bulkDeleteRecord(comments);
            if (!commentDeletion.isValid()) {
                return OperationResult.failed("Failed to delete comments for the appointment " + appointmentID
                                + ". Deletion process has stopped here.");
            }
        }
        if (!feedbacks.isEmpty()) {
            OperationResult feedbackDeletion = appointmentFeedbackRepository.bulkDeleteRecord(feedbacks);
            if (!feedbackDeletion.isValid()) {
                return OperationResult.failed("Failed to delete feedbacks for the appointment " + appointmentID
                                + ". Deletion process has stopped here.");
            }
        }

        // Get all the invoices that are associated with the appointment
        List<Invoice> involvedInvoices = invoiceRepository.getRecordsByAppointmentId(appointmentID);

        // For each invoice, set the appointment to null to remove the reference to the appointment
        List<Invoice> invoicesToUpdate = involvedInvoices.stream()
                .peek(invoice -> invoice.setAppointment(null))
                .toList();

        // Update all the invoices in the text file at once
        if (!invoicesToUpdate.isEmpty()) {
            OperationResult packageUpdateResult = invoiceRepository.bulkUpdateRecord(invoicesToUpdate);

            if (!packageUpdateResult.isValid()) {
                return OperationResult.failed("Failed to update invoices linked to appointment ID "
                        + appointmentID + ". Deletion process has stopped.");
            }
        }

        // Finally, delete the appointment record in the text file
        return appointmentRepository.deleteRecord(existingAppointment);
    }
}
