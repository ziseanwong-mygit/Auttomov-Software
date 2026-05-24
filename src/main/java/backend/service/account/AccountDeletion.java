package main.java.backend.service.account;

import main.java.backend.model.*;
import main.java.backend.repository.impl.*;
import main.java.backend.repository.impl.account.AccountRepository;
import main.java.backend.service.RecordWriter;
import main.java.enums.data.JobPosition;
import main.java.records.OperationResult;

import java.util.ArrayList;
import java.util.List;

public class AccountDeletion implements RecordWriter {

    // Wildcard is used so can accept either customer or staff repository in one variable
    private final AccountRepository<? extends UserAccount> accountRepository;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentCommentRepository appointmentCommentRepository;
    private final PaymentRecordRepository paymentRecordRepository;
    private final AppointmentFeedbackRepository appointmentFeedbackRepository;
    private final InvoiceRepository invoiceRepository;
    private final ReceiptRepository receiptRepository;
    private final RecordsReportRepository recordsReportRepository;
    private final SalesReportRepository salesReportRepository;
    private final String accountID;

    public AccountDeletion(AccountRepository<? extends UserAccount> accountRepository,
                           AppointmentRepository appointmentRepository,
                           AppointmentCommentRepository appointmentCommentRepository,
                           AppointmentFeedbackRepository appointmentFeedbackRepository,
                           InvoiceRepository invoiceRepository, PaymentRecordRepository paymentRecordRepository,
                           ReceiptRepository receiptRepository, RecordsReportRepository recordsReportRepository,
                           SalesReportRepository salesReportRepository, String accountID) {
        this.accountRepository = accountRepository;
        this.appointmentRepository = appointmentRepository;
        this.appointmentCommentRepository = appointmentCommentRepository;
        this.appointmentFeedbackRepository = appointmentFeedbackRepository;
        this.invoiceRepository = invoiceRepository;
        this.paymentRecordRepository = paymentRecordRepository;
        this.receiptRepository = receiptRepository;
        this.recordsReportRepository = recordsReportRepository;
        this.salesReportRepository = salesReportRepository;
        this.accountID = accountID;
    }

    /**
     * Account deletion process for customer, technician, counter-staff, and manager
     */
    @Override
    public OperationResult execute() {

        // Check if the account exists with the given ID
        UserAccount existingAcc = accountRepository.getRecordById(accountID);
        if (existingAcc == null) {
            return OperationResult.failed("The account with ID: " + accountID +
                    " does not exist in the system.");
        }

        // Unlink the account from all the involved records
        OperationResult unlinkAccountFromRecords = unlinkInvolvedRecords(existingAcc);
        if (!unlinkAccountFromRecords.isValid()) {
            return unlinkAccountFromRecords;
        }

        // Cast the existingAcc to specific account type, then only delete the record; base implementation
        return accountRepository.deleteRecordBase(existingAcc);
    }

    /**
     * Unlink the target account from all the associated records
     * 1. Check if the account belongs to a customer or staff
     * 2. Then, get all the records linked to the account
     * 3. Set the account field of all the records to null to unlink them
     * 4. Update all the records in the respective text files at once
     * 5. If all records are successfully updated, return success
     * 6. If any update fails, return failure
     */
    private OperationResult unlinkInvolvedRecords(UserAccount existingAcc) {

        if (existingAcc instanceof StaffAccount staffAcc) {

            if (staffAcc.getJobPosition() == JobPosition.COUNTER_STAFF ||
                    staffAcc.getJobPosition() == JobPosition.MANAGER) {

                List<Invoice> involvedInvoices = invoiceRepository.getRecordsByInvoiceIssuer(staffAcc);
                List<Receipt> involvedReceipts = receiptRepository.getRecordsByReceiptIssuer(staffAcc);

                List<Invoice> invoicesToUpdate = involvedInvoices.stream()
                        .peek(invoice -> invoice.setInvoiceIssuer(null))
                        .toList();
                List<Receipt> receiptsToUpdate = involvedReceipts.stream()
                        .peek(receipt -> receipt.setReceiptIssuer(null))
                        .toList();

                if (!invoicesToUpdate.isEmpty()) {
                    OperationResult invoiceUpdateResult = invoiceRepository.bulkUpdateRecord(invoicesToUpdate);
                    if (!invoiceUpdateResult.isValid()) {
                        return OperationResult.failed("Failed to update invoices linked to the account ID "
                                + existingAcc.getAccountID() + ". Deletion process has stopped here.");
                    }
                }
                if (!receiptsToUpdate.isEmpty()) {
                    OperationResult receiptUpdateResult = receiptRepository.bulkUpdateRecord(receiptsToUpdate);
                    if (!receiptUpdateResult.isValid()) {}
                }
            }

            if (staffAcc.getJobPosition() == JobPosition.TECHNICIAN) {
                List<Appointment> involvedAppointments = appointmentRepository.getRecordsByTechnician(staffAcc);
                List<AppointmentFeedback> involvedFeedback = appointmentFeedbackRepository
                        .getRecordsFromFeedbackAuthor(staffAcc);

                List<Appointment> appointmentsToUpdate = involvedAppointments.stream()
                        .peek(appt -> {
                            List<StaffAccount> updatedTechs = new ArrayList<>(appt.getTechnicianList());
                            updatedTechs.remove(existingAcc);
                            appt.setTechnicianList(updatedTechs);
                        })
                        .toList();
                List<AppointmentFeedback> feedbackToUpdate = involvedFeedback.stream()
                        .peek(feedback -> feedback.setFeedbackAuthor(null))
                        .toList();

                if (!appointmentsToUpdate.isEmpty()) {
                    OperationResult appointmentUpdateResult = appointmentRepository.
                            bulkUpdateRecord(appointmentsToUpdate);
                    if (!appointmentUpdateResult.isValid()) {
                        return OperationResult.failed("Failed to update appointments linked to the account ID "
                                + existingAcc.getAccountID() + ". Deletion process has stopped here.");
                    }
                }
                if (!feedbackToUpdate.isEmpty()) {
                    OperationResult feedbackUpdateResult = appointmentFeedbackRepository.
                            bulkUpdateRecord(feedbackToUpdate);
                    if (!feedbackUpdateResult.isValid()) {
                        return OperationResult.failed("Failed to update feedback linked to the account ID "
                                + existingAcc.getAccountID() + ". Deletion process has stopped here.");
                    }
                }

            }

            if (staffAcc.getJobPosition() == JobPosition.MANAGER) {
                List<RecordsReport> recordsReports = recordsReportRepository.getRecordsByManager(staffAcc);
                List<SalesReport> salesReports = salesReportRepository.getRecordsByManager(staffAcc);

                List<RecordsReport> recordsReportsToUpdate = recordsReports.stream()
                        .peek(report -> report.setManagerAccount(null))
                        .toList();
                List<SalesReport> salesReportsToUpdate = salesReports.stream()
                        .peek(report -> report.setManagerAccount(null))
                        .toList();

                if (!recordsReportsToUpdate.isEmpty()) {
                    OperationResult reportUpdateResult = recordsReportRepository
                            .bulkUpdateRecord(recordsReportsToUpdate);
                    if (!reportUpdateResult.isValid()) {
                        return OperationResult.failed("Failed to update records reports linked to the account ID "
                                + existingAcc.getAccountID() + ". Deletion process has stopped here.");
                    }
                }
                if (!salesReportsToUpdate.isEmpty()) {
                    OperationResult receiptUpdateResult = salesReportRepository
                            .bulkUpdateRecord(salesReportsToUpdate);
                    if (!receiptUpdateResult.isValid()) {
                        return OperationResult.failed("Failed to update sales reports linked to the account ID "
                                + existingAcc.getAccountID() + ". Deletion process has stopped here.");
                    }
                }
            }

        } else {
            List<Appointment> involvedAppointments = appointmentRepository.
                    getRecordsByCustomer(existingAcc);
            List<AppointmentComment> involvedComments = appointmentCommentRepository.
                    getRecordsByCommentAuthor(existingAcc);
            List<PaymentRecord> involvedPayments = paymentRecordRepository.
                    getRecordsByPayerAccount(existingAcc);

            List<Appointment> appointmentsToUpdate = involvedAppointments.stream()
                    .peek(appointment -> appointment.setServicePackage(null))
                    .toList();
            List<AppointmentComment> commentsToUpdate = involvedComments.stream()
                    .peek(comment -> comment.setCommentAuthor(null))
                    .toList();
            List<PaymentRecord> paymentsToUpdate = involvedPayments.stream()
                    .peek(payment -> payment.setPayerAccount(null))
                    .toList();

            if (!appointmentsToUpdate.isEmpty()) {
                OperationResult appointmentUpdateResult = appointmentRepository.bulkUpdateRecord(appointmentsToUpdate);
                if (!appointmentUpdateResult.isValid()) {
                    return OperationResult.failed("Failed to update appointments linked to the account ID "
                            + existingAcc.getAccountID() + ". Deletion process has stopped here.");
                }
            }
            if (!commentsToUpdate.isEmpty()) {
                OperationResult commentUpdateResult = appointmentCommentRepository.bulkUpdateRecord(commentsToUpdate);
                if (!commentUpdateResult.isValid()) {
                    return OperationResult.failed("Failed to update comments linked to the account ID "
                            + existingAcc.getAccountID() + ". Deletion process has stopped here.");
                }
            }
            if (!paymentsToUpdate.isEmpty()) {
                OperationResult paymentUpdateResult = paymentRecordRepository.bulkUpdateRecord(paymentsToUpdate);
                if (!paymentUpdateResult.isValid()) {
                    return OperationResult.failed("Failed to update payment records linked to the account ID "
                            + existingAcc.getAccountID() + ". Deletion process has stopped here.");
                }
            }
        }

        // If all involved records are successfully updated, return success
        return OperationResult.success();
    }
}
