package main.java.backend.service.appointment;

import main.java.backend.model.Appointment;
import main.java.backend.model.StaffAccount;
import main.java.backend.repository.impl.AppointmentRepository;
import main.java.backend.service.RecordWriter;
import main.java.dtos.AppointmentDTO;
import main.java.records.ListFieldComparison;
import main.java.records.OperationResult;

import java.util.ArrayList;
import java.util.List;

public class AppointmentTechAssignment implements RecordWriter {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentDTO appointmentDTO;
    private final List<StaffAccount> newTechnicianList;

    public AppointmentTechAssignment(AppointmentRepository appointmentRepository, AppointmentDTO appointmentDTO,
                                     List<StaffAccount> newTechnicianList) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentDTO = appointmentDTO;
        this.newTechnicianList = newTechnicianList;
    }

    @Override
    public OperationResult execute() {

        // Check if the appointment exists with the given ID
        Appointment existingAppointment = appointmentRepository.getRecordById(appointmentDTO.getAppointmentID());
        if (existingAppointment == null) {
            return OperationResult.failed("The appointment with ID: " + appointmentDTO.getAppointmentID() +
                    " does not exist in the system.");
        }

        // Get a list of assigned technician IDs for comparing technician list changes and
        // filtering out the assigned technicians from the newTechnicianList
        List<String> existingTechIdList = new ArrayList<>();
        for (StaffAccount technician : existingAppointment.getTechnicianList() ) {
            existingTechIdList.add(technician.getAccountID());
        }

        ListFieldComparison techListChange = compareTechListChanges(existingTechIdList);
        if (!techListChange.isChanged()) {
            return OperationResult.failed("The technicians in the input list are the same as the " +
                    "current assigned technicians. Please assign to different technicians for update. ");
        }

        // Get a list of new technicians that are not already assigned to the appointment
        List<StaffAccount> incomingTechnicians = newTechnicianList.stream()
                .filter(tech -> !existingTechIdList.contains(tech.getAccountID()))
                .toList();

        // If there are new technicians, check their work schedule with the existingAppointment's date and time
        if (!incomingTechnicians.isEmpty()) {
            OperationResult scheduleCheck = compareAppointmentSchedule(existingAppointment);

            // If there is a schedule conflict, return the error message
            if (!scheduleCheck.isValid()) {
                return scheduleCheck;
            }
        }

        // If all validations pass, set the new technician into the existing appointment record
        existingAppointment.setTechnicianList(newTechnicianList);

        // Begin updating appointment record with new technician list in the repository
        return appointmentRepository.updateRecord(existingAppointment);
    }

    /**
     * Compare the dates and times of the incoming appointment with the technicians' schedule
     * by referring to their assigned appointment's dates and times
     */
    private OperationResult compareAppointmentSchedule(Appointment targetAppointment) {

        // Check every technician's work schedule based on their assigned appointment's date and time
        for (StaffAccount technician : newTechnicianList) {

            // Get all appointments currently assigned to the technician
            List<Appointment> techAppointments = appointmentRepository.getRecordsByTechnician(technician);

            /*
            Get the assigned appointment that clashes with the
            target appointment's date and time range if one exists
            ----------------------------------------------------------------------------------------------------
            The main logic of comparing the technician's schedule with the target appointment date and time range
            is here
            */
            Appointment clashingAppointment = AppointmentScheduleChecker.findConflictingAppointment(
                    targetAppointment, techAppointments);

            if (clashingAppointment != null) {
                return OperationResult.failed(
                        "Oops! The selected technician " + technician.getFirstName() + " " +
                                technician.getLastName() + " with ID: " + technician.getAccountID() +
                                " has another appointment that clashed with the target appointment. " +
                                "Please assign with a different technician that is available, or locate " +
                                "the appointment to a different day and time. \nAssigned appointment ID: " +
                                clashingAppointment.getAppointmentID() + "\nConflicted date and time: " +
                                clashingAppointment.getServiceDate() + ", " +
                                clashingAppointment.getServiceStartTime() + " - " +
                                clashingAppointment.getServiceEndTime());
            }
        }

        // If there are no conflicting appointments, return success for comparison
        return OperationResult.success();
    }

    /**
     * Compare between the new technician list and the current technician list to detect changes
     */
    private ListFieldComparison compareTechListChanges(List<String> existingTechIdList) {

        // Get the IDs from the new technician list
        List<String> newTechIdList = new ArrayList<>();
        for (StaffAccount technician : newTechnicianList) {
            newTechIdList.add(technician.getAccountID());
        }

        // Compare the lists of technician IDs and return its result
        return new ListFieldComparison("technicianList", existingTechIdList, newTechIdList);
    }
}
