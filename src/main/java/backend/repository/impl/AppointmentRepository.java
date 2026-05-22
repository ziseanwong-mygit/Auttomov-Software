package main.java.backend.repository.impl;

import main.java.backend.model.Appointment;
import main.java.backend.model.StaffAccount;
import main.java.backend.repository.AbstractRepository;
import main.java.backend.repository.impl.account.StaffAccountRepository;
import main.java.backend.util.CSVFormatter;
import main.java.backend.util.DataStructureOrganizer;
import main.java.backend.util.DataTypeConverter;
import main.java.enums.data.AppointmentStatus;
import main.java.enums.system.TextFilePath;
import main.java.records.OperationResult;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class AppointmentRepository extends AbstractRepository<Appointment> {

    private static AppointmentRepository instance;
    private final String filePath = TextFilePath.APPOINTMENT_RECORDS_FILE_PATH.getTextFilePath();

    // Map key: field value, map value: Appointment object
    private final Map<String, Appointment> appointmentById = new HashMap<>();

    // Private constructor to prevent instantiations from outside
    // Pre-populate the records into maps when the instance is created
    private AppointmentRepository() {
        retrieveRecords();
    }

    // Global access point by creating instance for once only
    // So all classes can access the same instance for reading and writing files
    public static AppointmentRepository getInstance() {
        if (instance == null) {
            instance = new AppointmentRepository();
        }
        return instance;
    }

    @Override
    public String getFilePath() {
        return filePath;
    }

    @Override
    public OperationResult createRecord(Appointment appointmentToCreate) {

        try (BufferedWriter bufferWrite = new BufferedWriter(new FileWriter(getFilePath(), true))) {

            // Write the new appointment record into the file
            bufferWrite.write(CSVFormatter.toCsvFormat(appointmentToCreate));
            bufferWrite.newLine();

            // Add the new appointment records into the maps to ensure the latest changes are reflected
            addRecordIntoMap(appointmentToCreate);

        } catch (IOException e) {
            System.out.println(e.getMessage()); // Print exception message for debugging
            return OperationResult.failed(
                    "Oops! There was an error writing the new appointment record. " +
                            "Please try again.");
        }

        return OperationResult.success(
                "The appointment record has been created successfully. " +
                        "Please check the record to verify its creation.");
    }

    @Override
    public OperationResult updateRecord(Appointment appointmentToUpdate) {

        String appointmentID = appointmentToUpdate.getAppointmentID();

        // Map the target appointment object by its ID
        Map<String, Function<String, Optional<String>>> modifier = Map.of(
                appointmentID, _ -> Optional.of(CSVFormatter.toCsvFormat(appointmentToUpdate))
        );

        // Rewrite the file to update the target record
        OperationResult result = rewriteFile(modifier);
        if (!result.isValid()) {
            return result;
        }

        // Replace the old record with the updated one to apply the latest changes
        removeRecordIntoMap(appointmentById.get(appointmentID));
        addRecordIntoMap(appointmentToUpdate);

        return OperationResult.success(
                "The appointment with ID " + appointmentID + " has been updated successfully. "
                        + "Please check the details to ensure the changes are applied.");
    }

    @Override
    public OperationResult deleteRecord(Appointment appointmentToDelete) {

        String appointmentID = appointmentToDelete.getAppointmentID();

        // Map the target appointment object by its ID
        Map<String, Function<String, Optional<String>>> modifier = Map.of(
                appointmentID, _ -> Optional.empty()
        );

        // Rewrite the file to delete the target record
        OperationResult result = rewriteFile(modifier);
        if (!result.isValid()) {
            return result;
        }

        // Remove the deleted record from the maps to apply the latest changes
        removeRecordIntoMap(appointmentToDelete);

        return OperationResult.success(
                "The appointment with ID " + appointmentID + " has been deleted successfully. "
                        + "Please check the records to verify its deletion.");
    }

    @Override
    public List<Appointment> getAllRecords() {
        return new ArrayList<>(appointmentById.values());
    }

    @Override
    public Appointment getRecordById(String appointmentId) {
        return appointmentById.get(appointmentId);
    }

    @Override
    public List<Appointment> getRecordsByIds(List<String> appointmentIds) {
        // Filter the appointment objects by their IDs
        return appointmentById.values().stream()
                .filter(appointment -> appointmentIds.contains(appointment.getAppointmentID()))
                .toList();
    }

    public List<Appointment> getRecordsByApptStatus(AppointmentStatus appointmentStatus) {
        // Filter the appointment objects by the appointment status
        return appointmentById.values().stream()
                .filter(appointment -> appointment.getAppointmentStatus().equals(appointmentStatus))
                .toList();
    }

    public List<Appointment> getRecordsByTechnician(StaffAccount technician) {
        return appointmentById.values().stream()
                .filter(appointment -> appointment.getTechnicianList().contains(technician))
                .toList();
    }

    /**
     * Used for retrieving records from the text file
     * Appointment CSV format (12 columns): appointmentID, appointmentOrderNo, appointmentCreationDate,
     * appointmentUpdateDate, appointmentTitle, appointmentDescription, servicePackage, serviceDate,
     * serviceStartTime, serviceEndTime, technicianList, appointmentStatus
     */
    @Override
    protected Appointment parseRecord(String line) {
        // Get the service package and staff account repository instances to fetch the respective objects
        ServicePackageRepository servicePackageRepository = ServicePackageRepository.getInstance();
        StaffAccountRepository staffAccountRepository = StaffAccountRepository.getInstance();

        // Extract the technician bracket list from the raw line before any splitting begins
        // Occurrence = 0 means the first and only bracket list
        List<String> technicianIds = DataStructureOrganizer.extractBracketList(line, 0);

        List<StaffAccount> technicianList = new ArrayList<>();
        if (!technicianIds.isEmpty()) {
            technicianList = staffAccountRepository.getRecordsByIds(technicianIds);
        }

        /*
        Remove the bracket lists from the line entirely, leaving only the simple comma-separated fields
        Example:
        ID, OrderNo, CreationTime, ..., ServiceEndTime, [A0001, A0002], Status ->
        ID, OrderNo, CreationTime, ..., ServiceEndTime, , Status
        */
        String strippedLine = line.replaceAll("\\[.*?]", "")
                .replaceAll(",\\s*,", ",");

        // Without the bracket lists, split the line into an array of columns
        String[] columns = strippedLine.split(",\\s*");

        return new Appointment(
                columns[0],
                columns[1],
                DataTypeConverter.convertStringToLocalDateTime(columns[2]),
                DataTypeConverter.convertStringToLocalDateTime(columns[3]),
                columns[4],
                columns[5].isBlank() ? null : columns[5], // If description is empty, set it to null
                servicePackageRepository.getRecordById(columns[6]), // Fetch the service package object here
                DataTypeConverter.convertStringToLocalDate(columns[7]),
                DataTypeConverter.convertStringToLocalTime(columns[8]),
                DataTypeConverter.convertStringToLocalTime(columns[9]),
                technicianList,
                DataTypeConverter.convertStringToAppointmentStatus(columns[10])
        );
    }

    /**
     * Add a new/updated appointment into all lookup maps
     */
    @Override
    protected void addRecordIntoMap(Appointment appointment) {

        // End the method early if the appointment is not provided
        if (appointment == null) {
            return;
        }

        appointmentById.put(appointment.getAppointmentID(), appointment);
    }

    /**
     * Remove the old/deleted appointment from all lookup maps
     */
    @Override
    protected void removeRecordIntoMap(Appointment appointment) {

        // End the method early if the appointment is not provided
        if (appointment == null) {
            return;
        }

        appointmentById.remove(appointment.getAppointmentID());
    }
}
