package main.java.frontend.appointment;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import main.java.backend.controller.AppointmentController;
import main.java.backend.model.Appointment;
import main.java.backend.model.StaffAccount;
import main.java.enums.data.AppointmentStatus;
import main.java.records.OperationResult;
import main.java.frontend.shared.Refreshable;

/**
 * Technician and manager can mark an appointment as COMPLETED.
 */
public class UpdateApptStatusUI extends JFrame implements Refreshable {

    private JTable apptTable;
    private DefaultTableModel tableModel;

    public UpdateApptStatusUI() {
        setTitle("Update Appointment Status - Auttomov Software");
        setSize(760, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 244, 248));

        // ── Header ──────────────────────────────────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        JLabel title = new JLabel("Update Appointment Status");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        headerPanel.add(title, BorderLayout.CENTER);

        // ── Table ────────────────────────────────────────────────────────────
        String[] cols = {"Appt. ID", "Order No.", "Title", "Date", "Time", "Current Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        loadAppointments();

        apptTable = new JTable(tableModel);
        apptTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        apptTable.setRowHeight(26);
        apptTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        apptTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Completely remove Appt. ID column from visual view (remains in model)
        apptTable.removeColumn(apptTable.getColumnModel().getColumn(0));

        // ── Status Selector + Buttons ─────────────────────────────────────────
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.setOpaque(false);

        JPanel statusRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        statusRow.setOpaque(false);
        statusRow.add(new JLabel("Set selected appointment to:"));
        JComboBox<AppointmentStatus> statusCombo = new JComboBox<>(new AppointmentStatus[]{
            AppointmentStatus.COMPLETED, AppointmentStatus.IN_PROGRESS,
            AppointmentStatus.INCOMPLETE, AppointmentStatus.CANCELLED
        });
        statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusRow.add(statusCombo);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        buttonRow.setOpaque(false);
        JButton updateBtn = createBtn("Update Status", new Color(39, 174, 96));
        JButton refreshBtn = createBtn("Refresh",       new Color(100, 116, 139));

        updateBtn.addActionListener(e -> {
            int row = apptTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select an appointment first.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String apptId = (String) tableModel.getValueAt(row, 0);
            AppointmentStatus newStatus = (AppointmentStatus) statusCombo.getSelectedItem();
 
            AppointmentController apptController = new AppointmentController();
            main.java.dtos.AppointmentDTO dto = new main.java.dtos.AppointmentDTO(apptId, newStatus);
            OperationResult result = apptController.updateAppointmentStatus(dto);
 
            if (result.isValid()) {
                JOptionPane.showMessageDialog(this, "Appointment status updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAppointments();
            } else {
                JOptionPane.showMessageDialog(this, result.generalMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        refreshBtn.addActionListener(e -> loadAppointments());

        buttonRow.add(updateBtn);
        buttonRow.add(refreshBtn);
        southPanel.add(statusRow); southPanel.add(buttonRow);

        add(headerPanel, BorderLayout.NORTH);
        add(new JScrollPane(apptTable), BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }

    @Override
    public void refreshData() {
        loadAppointments();
    }

    private void loadAppointments() {
        tableModel.setRowCount(0);
        AppointmentController apptController = new AppointmentController();
        java.util.Optional<java.util.List<Appointment>> listOpt = apptController.retrieveAllAppointments();
        if (listOpt.isPresent()) {
            main.java.backend.model.UserAccount current = main.java.frontend.shared.SessionManager.getCurrentUser();
            boolean isTech = false;
            String currentId = "";
            if (current instanceof StaffAccount staff) {
                isTech = (staff.getJobPosition() == main.java.enums.data.JobPosition.TECHNICIAN);
                currentId = staff.getAccountID();
            }
 
            for (Appointment appt : listOpt.get()) {
                boolean show = !isTech;
                if (isTech && appt.getTechnicianList() != null) {
                    for (StaffAccount tech : appt.getTechnicianList()) {
                        if (tech.getAccountID().equals(currentId)) {
                            show = true;
                            break;
                        }
                    }
                }
                if (show) {
                    tableModel.addRow(new Object[]{
                        appt.getAppointmentID(),
                        appt.getAppointmentOrderNo(),
                        appt.getAppointmentTitle(),
                        appt.getServiceDate().toString(),
                        appt.getServiceStartTime().toString(),
                        appt.getAppointmentStatus().getAppointmentStatus()
                    });
                }
            }
        }
    }

    private JButton createBtn(String text, Color color) {
        JButton b = new JButton(text); b.setFont(new Font("Segoe UI", Font.BOLD, 13)); b.setForeground(Color.WHITE);
        b.setBackground(color); b.setFocusPainted(false); b.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18)); b.setCursor(new Cursor(Cursor.HAND_CURSOR)); return b;
    }
}
