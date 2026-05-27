package main.java.backend.util;

import main.java.backend.model.*;
import main.java.enums.data.JobPosition;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Get and concatenate all the data from a model class to a single CSV line
 * Used during file writing for creating and updating record
 */
public class CSVFormatter {

    // Private constructor to prevent instantiation
    private CSVFormatter() {
        throw new UnsupportedOperationException("This class is a utility class, which cannot be instantiated.");
    }

    public static String toCsvFormat(UserAccount userAccount) {
        return buildUserAccountCsv(userAccount);
    }

    public static String toCsvFormat(StaffAccount staffAccount) {

        JobPosition jobPosition = staffAccount.getJobPosition();
        String basicDetails = buildUserAccountCsv(staffAccount);

        // If the staff is a technician, the specialization field will be added into the record
        if (jobPosition == JobPosition.TECHNICIAN) {
            return String.join("| ",
                    basicDetails,
                    jobPosition.getJobPosition(),
                    staffAccount.getEmploymentDate().toString(),
                    staffAccount.getEmploymentStatus().getEmpStatus(),
                    staffAccount.getSpecialization()
            );
        } else {
            return String.join("| ",
                    basicDetails,
                    jobPosition.getJobPosition(),
                    staffAccount.getEmploymentDate().toString(),
                    staffAccount.getEmploymentStatus().getEmpStatus()
            );
        }
    }

    public static String toCsvFormat(ServiceActivity serviceActivity) {

        String serviceActivityDescription = serviceActivity.getServiceActivityDescription();

        return String.join("| ",
                serviceActivity.getServiceActivityID(),
                serviceActivity.getServiceActivityName(),
                serviceActivityDescription != null ? serviceActivityDescription : "",
                DataTypeConverter.convertDoubleToString(serviceActivity.getServiceActivityPrice())
        );
    }

    public static String toCsvFormat(VehiclePart vehiclePart) {

        String vehiclePartDescription = vehiclePart.getVehiclePartDescription();

        return String.join("| ",
                vehiclePart.getVehiclePartID(),
                vehiclePart.getVehiclePartName(),
                vehiclePartDescription != null ? vehiclePartDescription : "",
                vehiclePart.getVehiclePartType(),
                vehiclePart.getVehiclePartBrand(),
                vehiclePart.getVehiclePartSpec(),
                DataTypeConverter.convertDoubleToString(vehiclePart.getVehiclePartPrice()),
                DataTypeConverter.convertIntToString(vehiclePart.getVehiclePartQuantity())
        );
    }

    public static String toCsvFormat(ServicePackage servicePackage) {

        String servicePackageDescription = servicePackage.getServicePackageDescription();
        List<ServiceActivity> serviceActivityList = servicePackage.getServiceActivityList();
        List<VehiclePart> vehiclePartList = servicePackage.getVehiclePartList();

        // Get the service activity IDs and concatenate them into a comma-separated value string
        String serviceActListLine = serviceActivityList.stream()
                .map(ServiceActivity::getServiceActivityID)
                .collect(Collectors.joining("| "));

        // If there are vehicle parts in the package,
        // get the vehicle part IDs and concatenate them into a comma-separated value string
        String vehiclePartListLine = "";
        if (servicePackage.getVehiclePartList() != null && !servicePackage.getVehiclePartList().isEmpty()) {
            vehiclePartListLine = vehiclePartList.stream()
                    .map(VehiclePart::getVehiclePartID)
                    .collect(Collectors.joining("| "));
        }

        return String.join("| ",
                servicePackage.getServicePackageID(),
                servicePackage.getServicePackageName(),
                servicePackageDescription != null ? servicePackageDescription : "",
                servicePackage.getServicePackageType().getServicePkgType(),
                "[" + serviceActListLine + "]",
                "[" + vehiclePartListLine + "]",
                DataTypeConverter.convertDoubleToString(servicePackage.getPackageBasePrice()),
                DataTypeConverter.convertIntToString(servicePackage.getServiceDuration())
        );
    }

    public static String toCsvFormat(Appointment appointment) {

        // Format the appointment creation and update time to yyyyMMddHHmmss format (e.g., 2026-05-01 12:34:56)
        String creationTime = DateTimeStringFormatter.formatDateTime(appointment.getAppointmentCreationTime());
        String updateTime = DateTimeStringFormatter.formatDateTime(appointment.getAppointmentUpdateTime());

        String appointmentDescription = appointment.getAppointmentDescription();
        List<StaffAccount> technicianList = appointment.getTechnicianList();

        // If the technician list is not null,
        // get all the technician account IDs and concatenate them into a comma-separated value string
        String technicianListLine = "";
        if (!technicianList.isEmpty()) {
            technicianListLine = technicianList.stream()
                    .map(StaffAccount::getAccountID)
                    .collect(Collectors.joining("| "));
        }

        // If the service package still exists (not deleted), get the service package ID
        String servicePackageID = "";
        if (appointment.getServicePackage() != null) {
            servicePackageID = appointment.getServicePackage().getServicePackageID();
        }

        // If the customer account still exists (not deleted), get the customer account ID
        String customerAccountID = "";
        if (appointment.getCustomerAccount() != null) {
            customerAccountID = appointment.getCustomerAccount().getAccountID();
        }

        return String.join("| ",
                appointment.getAppointmentID(),
                appointment.getAppointmentOrderNo(),
                creationTime,
                updateTime,
                appointment.getAppointmentTitle(),
                appointmentDescription != null ? appointmentDescription : "",
                customerAccountID,
                servicePackageID,
                appointment.getServiceDate().toString(),
                appointment.getServiceStartTime().toString(),
                appointment.getServiceEndTime().toString(),
                "[" + technicianListLine + "]",
                appointment.getAppointmentStatus().getAppointmentStatus()
        );

    }

    public static String toCsvFormat(AppointmentComment appointmentComment) {

        // Format the comment creation and update time to yyyyMMddHHmmss format (e.g., 2026-05-01 12:34:56)
        String creationTime = DateTimeStringFormatter.formatDateTime(appointmentComment.getCommentCreationTime());
        String updateTime = DateTimeStringFormatter.formatDateTime(appointmentComment.getCommentUpdateTime());

        // If the author account (customer) still exists (not deleted), get the author account ID
        String authorAccountID = "";
        if (appointmentComment.getCommentAuthor() != null) {
            authorAccountID = appointmentComment.getCommentAuthor().getAccountID();
        }

        return String.join("| ",
                appointmentComment.getCommentID(),
                creationTime,
                updateTime,
                appointmentComment.getAppointment().getAppointmentID(),
                authorAccountID,
                appointmentComment.getCommentContent()
        );
    }

    public static String toCsvFormat(AppointmentFeedback appointmentFeedback) {

        // Format the comment creation and update time to yyyyMMddHHmmss format (e.g., 2026-05-01 12:34:56)
        String creationTime = DateTimeStringFormatter.formatDateTime(appointmentFeedback.getFeedbackCreationTime());
        String updateTime = DateTimeStringFormatter.formatDateTime(appointmentFeedback.getFeedbackUpdateTime());

        // If the author account (technician) still exists (not deleted), get the author account ID
        String authorAccountID = "";
        if (appointmentFeedback.getFeedbackAuthor() != null) {
            authorAccountID = appointmentFeedback.getFeedbackAuthor().getAccountID();
        }

        return String.join("| ",
                appointmentFeedback.getFeedbackID(),
                creationTime,
                updateTime,
                appointmentFeedback.getAppointment().getAppointmentID(),
                authorAccountID,
                appointmentFeedback.getFeedbackContent()
        );
    }

    public static String toCsvFormat(Invoice invoice) {

        // If the appointment still exists (not deleted), get the appointment ID
        String appointmentID = "";
        if (invoice.getAppointment() != null) {
            appointmentID = invoice.getAppointment().getAppointmentID();
        }

        // If the invoice issuer (staff) still exists (not deleted), get the invoice issuer ID
        String issuerAccountID = "";
        if (invoice.getInvoiceIssuer() != null) {
            issuerAccountID = invoice.getInvoiceIssuer().getAccountID();
        }

        return String.join("| ",
                invoice.getInvoiceID(),
                invoice.getInvoiceDocNo(),
                invoice.getInvoiceCreationDate().toString(),
                issuerAccountID,
                appointmentID,
                DataTypeConverter.convertDoubleToString(invoice.getSubtotalAmount()),
                DataTypeConverter.convertDoubleToString(invoice.getServiceTaxAmount()),
                DataTypeConverter.convertDoubleToString(invoice.getAdditionalFeeAmount()),
                DataTypeConverter.convertDoubleToString(invoice.getTotalAmount()),
                invoice.getInvoiceNote(),
                invoice.getInvoiceStatus().getInvoiceStatus()
        );
    }

    public static String toCsvFormat(PaymentRecord paymentRecord) {

        // If the payer account (customer) still exists (not deleted), get the payer ID
        String payerAccountID = "";
        if (paymentRecord.getPayerAccount() != null) {
            payerAccountID = paymentRecord.getPayerAccount().getAccountID();
        }

        return String.join("| ",
                paymentRecord.getPaymentRecordID(),
                paymentRecord.getPaymentRecordNo(),
                paymentRecord.getPaymentDate().toString(),
                paymentRecord.getInvoice().getInvoiceID(),
                payerAccountID,
                DataTypeConverter.convertDoubleToString(paymentRecord.getPaymentAmount()),
                paymentRecord.getPaymentMethod().getPaymentMethod(),
                paymentRecord.getPaymentRecordNote(),
                paymentRecord.getPaymentStatus().getPaymentStatus()
        );
    }

    public static String toCsvFormat(Receipt receipt) {

        // If the receipt issuer (staff) still exists (not deleted), get the receipt issuer ID
        String issuerAccountID = "";
        if (receipt.getReceiptIssuer() != null) {
            issuerAccountID = receipt.getReceiptIssuer().getAccountID();
        }

        return String.join("| ",
                receipt.getReceiptID(),
                receipt.getReceiptDocNo(),
                receipt.getReceiptCreationDate().toString(),
                issuerAccountID,
                receipt.getInvoice().getInvoiceID(),
                receipt.getPaymentRecord().getPaymentRecordID(),
                receipt.getReceiptNote()
        );
    }

    public static String toCsvFormat(DataReport dataReport) {

        // If the manager account still exists (not deleted), get the manager ID
        String managerAccountID = "";
        if (dataReport.getManagerAccount() != null) {
            managerAccountID = dataReport.getManagerAccount().getAccountID();
        }

        return String.join("| ",
                dataReport.getReportID(),
                dataReport.getReportDocNo(),
                dataReport.getReportCreationDate().toString(),
                dataReport.getReportType().getReportType(),
                managerAccountID,
                DataTypeConverter.convertIntToString(dataReport.getTotalCustomers()),
                DataTypeConverter.convertIntToString(dataReport.getTotalTechnicians()),
                DataTypeConverter.convertIntToString(dataReport.getTotalCounterStaffs()),
                DataTypeConverter.convertIntToString(dataReport.getTotalManagers()),
                DataTypeConverter.convertIntToString(dataReport.getTotalServiceActivities()),
                DataTypeConverter.convertIntToString(dataReport.getTotalVehicleParts()),
                DataTypeConverter.convertIntToString(dataReport.getTotalServicePackages()),
                DataTypeConverter.convertIntToString(dataReport.getTotalAppointments()),
                DataTypeConverter.convertIntToString(dataReport.getTotalCustomersByThisMonth()),
                DataTypeConverter.convertIntToString(dataReport.getTotalCustomersByThisYear()),
                DataTypeConverter.convertIntToString(dataReport.getTotalCustomersLeftByThisMonth()),
                DataTypeConverter.convertIntToString(dataReport.getTotalCustomersLeftByThisYear())
        );
    }

    public static String toCsvFormat(SalesReport salesReport) {

        // If the manager account still exists (not deleted), get the manager ID
        String managerAccountID = "";
        if (salesReport.getManagerAccount() != null) {
            managerAccountID = salesReport.getManagerAccount().getAccountID();
        }

        return String.join("| ",
                salesReport.getReportID(),
                salesReport.getReportDocNo(),
                salesReport.getReportCreationDate().toString(),
                salesReport.getReportType().getReportType(),
                managerAccountID,
                DataTypeConverter.convertIntToString(salesReport.getTotalNormalServicesDone()),
                DataTypeConverter.convertIntToString(salesReport.getTotalMajorServicesDone()),
                DataTypeConverter.convertIntToString(salesReport.getTotalAllServicesDone()),
                DataTypeConverter.convertDoubleToString(salesReport.getTotalNormalServiceSales()),
                DataTypeConverter.convertDoubleToString(salesReport.getTotalMajorServiceSales()),
                DataTypeConverter.convertDoubleToString(salesReport.getTotalAllServiceSales()),
                DataTypeConverter.convertDoubleToString(salesReport.getTotalSalesByThisMonth()),
                DataTypeConverter.convertDoubleToString(salesReport.getTotalSalesByThisYear()),
                DataTypeConverter.convertDoubleToString(salesReport.getTotalSalesByLifetime()),
                DataTypeConverter.convertDoubleToString(salesReport.getAverageSalesByThisMonth()),
                DataTypeConverter.convertDoubleToString(salesReport.getAverageSalesByThisYear()),
                DataTypeConverter.convertDoubleToString(salesReport.getAverageSalesByLifetime())
        );

    }

    /**
     * Used in UserAccount and StaffAccount to build the CSV line for the basic user account information
     */
    private static String buildUserAccountCsv(UserAccount userAccount) {

        return String.join("| ",
                userAccount.getAccountID(),
                userAccount.getAccountCreationDate().toString(),
                userAccount.getUserID(),
                userAccount.getUserRole().getUserRole(),
                userAccount.getFirstName(),
                userAccount.getLastName(),
                userAccount.getEmailAddress(),
                userAccount.getPhoneNumber(),
                userAccount.getPasswordHash().toString(),
                userAccount.getAccountStatus().getAccStatus()
        );
    }
}
