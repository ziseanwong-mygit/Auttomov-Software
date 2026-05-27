package main.java.backend.util;

import main.java.enums.data.*;
import main.java.records.PasswordHash;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DataTypeConverter {

    // Private constructor to prevent instantiation
    private DataTypeConverter() {
        throw new UnsupportedOperationException("This class is a utility class, which cannot be instantiated.");
    }

    public static double convertStringToDouble(String str) {
        return Double.parseDouble(str);
    }

    public static String convertDoubleToString(double value) {
        return String.format("%.2f", value);
    }

    public static int convertStringToInt(String str) {
        return Integer.parseInt(str);
    }

    public static String convertIntToString(int value) {
        return String.valueOf(value);
    }

    public static LocalDate convertStringToLocalDate(String str) {
        return LocalDate.parse(str);
    }

    public static LocalTime convertStringToLocalTime(String str) {
        return LocalTime.parse(str);
    }

    public static LocalDateTime convertStringToLocalDateTime(String str) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(str, dateTimeFormatter);
    }

    public static Role convertStringToRole(String str) {
        return Role.getRoleByValue(str);
    }

    public static PasswordHash convertStringToPwdHash(String str) {
        return PasswordHash.toRecord(str);
    }

    public static AccountStatus convertStringToAccStatus(String str) {
        return AccountStatus.getAccStatusByValue(str);
    }

    public static JobPosition convertStringToJobPos(String str) {
        return JobPosition.getJobPosByValue(str);
    }

    public static EmploymentStatus convertStringToEmpStatus(String str) {
        return EmploymentStatus.getEmpStatusByValue(str);
    }

    public static ServicePackageType convertStringToServicePackageType(String str) {
        return ServicePackageType.getServiceTypeByValue(str);
    }

    public static AppointmentStatus convertStringToAppointmentStatus(String str) {
        return AppointmentStatus.getApptStatusByValue(str);
    }

    public static InvoiceStatus convertStringToInvoiceStatus(String str) {
        return InvoiceStatus.getInvoiceStatusByValue(str);
    }

    public static PaymentMethod convertStringToPaymentMethod(String str) {
        return PaymentMethod.getPaymentMethodByValue(str);
    }

    public static PaymentStatus convertStringToPaymentStatus(String str) {
        return PaymentStatus.getPaymentStatusByValue(str);
    }

    public static ReportType convertStringToReportType(String str) {
        return ReportType.getReportTypeByValue(str);
    }
}
