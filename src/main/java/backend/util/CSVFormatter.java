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
            return String.join(", ",
                    basicDetails,
                    jobPosition.getJobPosition(),
                    staffAccount.getEmploymentDate().toString(),
                    staffAccount.getEmploymentStatus().getEmpStatus(),
                    staffAccount.getSpecialization()
            );
        } else {
            return String.join(", ",
                    basicDetails,
                    jobPosition.getJobPosition(),
                    staffAccount.getEmploymentDate().toString(),
                    staffAccount.getEmploymentStatus().getEmpStatus()
            );
        }
    }

    public static String toCsvFormat(ServiceActivity serviceActivity) {

        String serviceActivityDescription = serviceActivity.getServiceActivityDescription();

        return String.join(", ",
                serviceActivity.getServiceActivityID(),
                serviceActivity.getServiceActivityName(),
                serviceActivityDescription != null ? serviceActivityDescription : "",
                DataTypeConverter.convertDoubleToString(serviceActivity.getServiceActivityPrice())
        );
    }

    public static String toCsvFormat(VehiclePart vehiclePart) {

        String vehiclePartDescription = vehiclePart.getVehiclePartDescription();

        return String.join(", ",
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
                .collect(Collectors.joining(", "));

        // If there are vehicle parts in the package,
        // get the vehicle part IDs and concatenate them into a comma-separated value string
        String vehiclePartListLine = "";
        if (servicePackage.getVehiclePartList() != null && !servicePackage.getVehiclePartList().isEmpty()) {
            vehiclePartListLine = vehiclePartList.stream()
                    .map(VehiclePart::getVehiclePartID)
                    .collect(Collectors.joining(", "));
        }

        return String.join(", ",
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
        if (technicianList != null) {
            technicianListLine = technicianList.stream()
                    .map(StaffAccount::getAccountID)
                    .collect(Collectors.joining(", "));
        }

        return String.join(", ",
                appointment.getAppointmentID(),
                appointment.getAppointmentOrderNo(),
                creationTime,
                updateTime,
                appointment.getAppointmentTitle(),
                appointmentDescription != null ? appointmentDescription : "",
                appointment.getCustomerAccount().getAccountID(), // Get the customer account ID from the object
                appointment.getServicePackage().getServicePackageID(), // Get the service package ID from the object
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

        return String.join(", ",
                appointmentComment.getCommentID(),
                creationTime,
                updateTime,
                appointmentComment.getAppointment().getAppointmentID(),
                appointmentComment.getCommentAuthor().getAccountID(),
                appointmentComment.getCommentContent()
        );
    }

    public static String toCsvFormat(AppointmentFeedback appointmentFeedback) {

        // Format the comment creation and update time to yyyyMMddHHmmss format (e.g., 2026-05-01 12:34:56)
        String creationTime = DateTimeStringFormatter.formatDateTime(appointmentFeedback.getFeedbackCreationTime());
        String updateTime = DateTimeStringFormatter.formatDateTime(appointmentFeedback.getFeedbackUpdateTime());

        return String.join(", ",
                appointmentFeedback.getFeedbackID(),
                creationTime,
                updateTime,
                appointmentFeedback.getAppointment().getAppointmentID(),
                appointmentFeedback.getFeedbackAuthor().getAccountID(),
                appointmentFeedback.getFeedbackContent()
        );
    }

    /**
     * Used in UserAccount and StaffAccount to build the CSV line for the basic user account information
     */
    private static String buildUserAccountCsv(UserAccount userAccount) {

        return String.join(", ",
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
