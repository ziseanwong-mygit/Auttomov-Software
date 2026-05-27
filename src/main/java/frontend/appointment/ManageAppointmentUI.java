package main.java.frontend.appointment;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import main.java.backend.controller.AppointmentController;
import main.java.backend.model.Appointment;
import main.java.enums.data.AppointmentStatus;
import main.java.records.OperationResult;
import main.java.frontend.shared.SessionManager;

/**
 * Unified ManageAppointmentUI — Counter Staff / Manager
 *
 * Merges "View All" and "Manage" into a single, high-fidelity screen featuring:
 * 1. Create Appointment
 * 2. Update Appointment
 * 3. Delete Appointment
 * 4. View Appointment Details
 * 5. Status Filter Dropdown
 * 6. Refresh
 */
public class ManageAppointmentUI extends JFrame implements main.java.frontend.shared.Refreshable {

    private JTable apptTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterCombo;

    private static final int COL_APPT_ID  = 0;
    private static final int COL_ORDER_NO = 1;
    private static final int COL_TITLE    = 2;
    private static final int COL_DATE     = 3;
    private static final int COL_TIME     = 4;
    private static final int COL_SERVICE  = 5;
    private static final int COL_STATUS   = 6;

    public ManageAppointmentUI() {
        setTitle("Manage Appointments — Auttomov Software");
        setSize(960, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 244, 248));

        // ── Header Panel ──────────────────────────────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 5, 25));
        JLabel title = new JLabel("Service Appointments Board");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        headerPanel.add(title, BorderLayout.CENTER);

        // ── Filter Panel (North) ──────────────────────────────────────────────
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setOpaque(false);
        
        JLabel filterLbl = new JLabel("Filter by Status:");
        filterLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        filterLbl.setForeground(new Color(51, 65, 85));
        filterPanel.add(filterLbl);

        filterCombo = new JComboBox<>();
        filterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterCombo.addItem("All Statuses");
        for (AppointmentStatus s : AppointmentStatus.values()) {
            filterCombo.addItem(s.getAppointmentStatus());
        }
        filterCombo.addActionListener(e -> refreshRosterTable());
        filterPanel.add(filterCombo);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setOpaque(false);
        topContainer.add(headerPanel, BorderLayout.NORTH);
        topContainer.add(filterPanel, BorderLayout.SOUTH);

        // ── Table (Center) ────────────────────────────────────────────────────
        String[] cols = {"Appt. ID", "Order No.", "Title", "Date", "Time", "Service Package", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        apptTable = new JTable(tableModel);
        apptTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        apptTable.setRowHeight(28);
        apptTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        apptTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Double-click an appointment to view details
        apptTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = apptTable.getSelectedRow();
                    if (row >= 0) {
                        showAppointmentDetails(row);
                    }
                }
            }
        });
        
        // Completely remove Appt. ID column from JTable visual view (remains in TableModel)
        apptTable.removeColumn(apptTable.getColumnModel().getColumn(COL_APPT_ID));

        JScrollPane scrollPane = new JScrollPane(apptTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        // Load initial records
        refreshRosterTable();

        // ── Button Action Bar (South) ─────────────────────────────────────────
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 15));
        buttonBar.setOpaque(false);

        JButton createBtn = createBtn("Create",             new Color(22, 160, 133));
        JButton updateBtn = createBtn("Update",             new Color(52, 152, 219));
        JButton deleteBtn = createBtn("Delete",             new Color(231, 76, 60));
        JButton detailsBtn = createBtn("View Details",       new Color(100, 110, 120));
        JButton refreshBtn = createBtn("Refresh",           new Color(100, 116, 139));

        createBtn.addActionListener(e -> {
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Create Appointment", true);
            dialog.setSize(540, 600);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());
            
            CreateAppointmentUI createUI = new CreateAppointmentUI();
            dialog.add(createUI.getContentPane(), BorderLayout.CENTER);
            
            dialog.setVisible(true);
            refreshRosterTable(); // Refresh table when done
        });

        updateBtn.addActionListener(e -> {
            int row = apptTable.getSelectedRow();
            if (row < 0) { showNoSelection(); return; }
            showUpdateDialog(row);
        });

        deleteBtn.addActionListener(e -> {
            int row = apptTable.getSelectedRow();
            if (row < 0) { showNoSelection(); return; }
            handleDelete(row);
        });

        detailsBtn.addActionListener(e -> {
            int row = apptTable.getSelectedRow();
            if (row < 0) { showNoSelection(); return; }
            showAppointmentDetails(row);
        });

        refreshBtn.addActionListener(e -> refreshRosterTable());

        buttonBar.add(createBtn);
        buttonBar.add(updateBtn);
        buttonBar.add(deleteBtn);
        buttonBar.add(detailsBtn);
        buttonBar.add(refreshBtn);

        add(topContainer, BorderLayout.NORTH);
        add(scrollPane,   BorderLayout.CENTER);
        add(buttonBar,    BorderLayout.SOUTH);
    }

    private void refreshRosterTable() {
        tableModel.setRowCount(0);
        String selectedStatus = (String) filterCombo.getSelectedItem();

        AppointmentController apptController = new AppointmentController();
        java.util.Optional<List<Appointment>> listOpt = apptController.retrieveAllAppointments();

        if (listOpt.isPresent()) {
            for (Appointment appt : listOpt.get()) {
                String statusStr = appt.getAppointmentStatus().getAppointmentStatus();
                boolean show = selectedStatus.equals("All Statuses") || statusStr.equalsIgnoreCase(selectedStatus);
                
                if (show) {
                    tableModel.addRow(new Object[]{
                        appt.getAppointmentID(),
                        appt.getAppointmentOrderNo(),
                        appt.getAppointmentTitle(),
                        appt.getServiceDate().toString(),
                        appt.getServiceStartTime().toString(),
                        appt.getServicePackage() != null ? appt.getServicePackage().getServicePackageName() : "N/A",
                        statusStr
                    });
                }
            }
        }
    }

    private void showAppointmentDetails(int row) {
        String apptId = (String) tableModel.getValueAt(row, COL_APPT_ID);
        AppointmentController apptController = new AppointmentController();
        java.util.Optional<Appointment> optAppt = apptController.retrieveAppointmentById(apptId);
        
        if (!optAppt.isPresent()) {
            JOptionPane.showMessageDialog(this, "Appointment details not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Appointment appt = optAppt.get();

        JDialog dialog = new JDialog(this, "Appointment Detailed Specifications", true);
        dialog.setSize(460, 460);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        form.add(new JLabel("Appointment ID:")); form.add(new JLabel(appt.getAppointmentID()));
        form.add(new JLabel("Order Number:")); form.add(new JLabel(appt.getAppointmentOrderNo()));
        form.add(new JLabel("Service Title:")); form.add(new JLabel(appt.getAppointmentTitle()));
        form.add(new JLabel("Description:")); form.add(new JLabel(appt.getAppointmentDescription() != null ? appt.getAppointmentDescription() : "N/A"));
        
        String customerName = appt.getCustomerAccount() != null 
                ? appt.getCustomerAccount().getFirstName() + " " + appt.getCustomerAccount().getLastName()
                : "N/A";
        form.add(new JLabel("Customer Name:")); form.add(new JLabel(customerName));
        
        form.add(new JLabel("Scheduled Date:")); form.add(new JLabel(appt.getServiceDate().toString()));
        form.add(new JLabel("Scheduled Time:")); form.add(new JLabel(appt.getServiceStartTime().toString()));
        form.add(new JLabel("Service Package:")); form.add(new JLabel(appt.getServicePackage() != null ? appt.getServicePackage().getServicePackageName() : "N/A"));
        form.add(new JLabel("Package Price:")); form.add(new JLabel(appt.getServicePackage() != null ? "RM " + String.format("%.2f", appt.getServicePackage().getPackageBasePrice()) : "N/A"));
        form.add(new JLabel("Status:")); form.add(new JLabel(appt.getAppointmentStatus().getAppointmentStatus()));

        JButton closeBtn = createBtn("Close", new Color(108, 117, 125));
        closeBtn.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.add(closeBtn);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showUpdateDialog(int row) {
        String apptId = (String) tableModel.getValueAt(row, COL_APPT_ID);
        AppointmentController apptController = new AppointmentController();
        java.util.Optional<Appointment> optAppt = apptController.retrieveAppointmentById(apptId);
        
        if (!optAppt.isPresent()) {
            JOptionPane.showMessageDialog(this, "Appointment not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Appointment appt = optAppt.get();

        JDialog dialog = new JDialog(this, "Update Appointment Parameters", true);
        dialog.setSize(440, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JTextField titleF = addField(form, "Title *:", appt.getAppointmentTitle());
        JTextField descF  = addField(form, "Description:", appt.getAppointmentDescription());
        JTextField dateF  = addField(form, "Date (YYYY-MM-DD) *:", appt.getServiceDate().toString());
        JTextField timeF  = addField(form, "Time (HH:MM) *:", appt.getServiceStartTime().toString());
        
        // Read-only fields
        form.add(new JLabel("Customer:"));
        form.add(new JLabel(appt.getCustomerAccount() != null ? appt.getCustomerAccount().getFirstName() + " " + appt.getCustomerAccount().getLastName() : "N/A"));

        form.add(new JLabel("Service Package:"));
        form.add(new JLabel(appt.getServicePackage() != null ? appt.getServicePackage().getServicePackageName() : "N/A"));

        JComboBox<AppointmentStatus> statusBox = new JComboBox<>(AppointmentStatus.values());
        statusBox.setSelectedItem(appt.getAppointmentStatus());
        form.add(new JLabel("Status *:")); form.add(statusBox);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton save   = createBtn("Save Changes", new Color(52, 152, 219));
        JButton cancel = createBtn("Cancel",       new Color(108, 117, 125));
        
        save.addActionListener(e -> {
            LocalDate serviceDate;
            LocalTime startTime;
            try {
                serviceDate = LocalDate.parse(dateF.getText().trim());
                startTime = LocalTime.parse(timeF.getText().trim());
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid date or time format. Use YYYY-MM-DD and HH:MM.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            main.java.dtos.AppointmentDTO dto = new main.java.dtos.AppointmentDTO(
                appt.getAppointmentID(),
                titleF.getText().trim(),
                descF.getText().trim(),
                appt.getCustomerAccount() != null ? appt.getCustomerAccount().getAccountID() : "",
                appt.getServicePackage() != null ? appt.getServicePackage().getServicePackageID() : "",
                serviceDate,
                startTime,
                (AppointmentStatus) statusBox.getSelectedItem()
            );

            OperationResult result = apptController.updateAppointment(dto);
            if (result.isValid()) {
                JOptionPane.showMessageDialog(dialog, "Appointment updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshRosterTable();
                dialog.dispose();
            } else {
                StringBuilder sb = new StringBuilder();
                if (!result.fieldErrors().isEmpty())
                    result.fieldErrors().forEach((f, msg) -> sb.append("- ").append(msg).append("\n"));
                else
                    sb.append(result.generalMessage());
                JOptionPane.showMessageDialog(dialog, sb.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancel.addActionListener(e -> dialog.dispose());
        btns.add(save); btns.add(cancel);

        dialog.add(new JScrollPane(form), BorderLayout.CENTER);
        dialog.add(btns, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void handleDelete(int row) {
        String apptId = (String) tableModel.getValueAt(row, COL_APPT_ID);
        String title  = (String) tableModel.getValueAt(row, COL_TITLE);
        
        if (JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete appointment: \"" + title + "\"?\nThis cannot be undone.",
            "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            
            AppointmentController apptController = new AppointmentController();
            OperationResult result = apptController.deleteAppointment(apptId);
            if (result.isValid()) {
                JOptionPane.showMessageDialog(this, "Appointment deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshRosterTable();
            } else {
                JOptionPane.showMessageDialog(this, result.generalMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JTextField addField(JPanel p, String label, String value) {
        JTextField f = new JTextField(value);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)), 
                BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));
        p.add(new JLabel(label));
        p.add(f);
        return f;
    }

    private void showNoSelection() {
        JOptionPane.showMessageDialog(this, "Please select an appointment from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
    }

    private JButton createBtn(String text, Color color) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setBackground(color);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    @Override
    public void refreshData() {
        refreshRosterTable();
    }
}
