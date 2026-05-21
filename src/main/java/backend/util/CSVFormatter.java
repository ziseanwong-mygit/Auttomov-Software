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

        // Get the service activity and vehicle part IDs and concatenate them into a comma-separated value string
        String serviceActListLine = serviceActivityList.stream()
                .map(ServiceActivity::getServiceActivityID)
                .collect(Collectors.joining(", "));

        String vehiclePartListLine = vehiclePartList.stream()
                .map(VehiclePart::getVehiclePartID)
                .collect(Collectors.joining(", "));

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

        String appointmentDescription = appointment.getAppointmentDescription();

        return String.join(", ",
                appointment.getAppointmentID(),
                appointment.getAppointmentOrderNo(),
                appointment.getAppointmentCreationDate().toString(),
                appointment.getAppointmentTitle(),
                appointmentDescription != null ? appointmentDescription : "",
                appointment.getAppointmentDate().toString(),
                appointment.getAppointmentTime().toString(),
                appointment.getServicePackage().getServicePackageID(),
                appointment.getAppointmentStatus().getAppointmentStatus()
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
