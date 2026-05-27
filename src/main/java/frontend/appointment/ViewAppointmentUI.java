package main.java.frontend.appointment;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import main.java.backend.controller.AppointmentController;
import main.java.backend.model.Appointment;
import main.java.backend.model.UserAccount;
import main.java.backend.model.StaffAccount;
import main.java.enums.data.AppointmentStatus;
import main.java.enums.data.Role;
import main.java.frontend.shared.SessionManager;

/**
 * Displays all appointments.
 * - Customer: sees only their own appointments.
 * - All Staff: sees all appointments.
 */
public class ViewAppointmentUI extends JFrame implements main.java.frontend.shared.Refreshable {

    private DefaultTableModel tableModel;
    private JComboBox<String> filterCombo;

    public ViewAppointmentUI() {
        UserAccount user = SessionManager.getCurrentUser();
        boolean isCustomer = SessionManager.getCurrentRole() == Role.CUSTOMER;

        setTitle("View Appointments - Auttomov Software");
        setSize(820, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 244, 248));


        // ── Header ──────────────────────────────────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        JLabel title = new JLabel(isCustomer ? "My Appointments" : "All Appointments");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        headerPanel.add(title, BorderLayout.CENTER);

        // ── Table ────────────────────────────────────────────────────────────
        String[] cols = {"Appt. ID", "Order No.", "Title", "Date", "Time", "Service Details", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(26);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Double-click an appointment to view details
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        showAppointmentDetails(row);
                    }
                }
            }
        });

        // Completely remove Appt. ID column from JTable visual view (remains in TableModel)
        table.removeColumn(table.getColumnModel().getColumn(0));

        // ── Status Filter ─────────────────────────────────────────────────────
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        filterPanel.setOpaque(false);
        filterPanel.add(new JLabel("Filter by status:"));
        
        filterCombo = new JComboBox<>();
        filterCombo.addItem("All");
        for (AppointmentStatus s : AppointmentStatus.values()) filterCombo.addItem(s.getAppointmentStatus());
        filterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterCombo.addActionListener(e -> loadAppointments(isCustomer, user));
        filterPanel.add(filterCombo);

        // Load initial records
        loadAppointments(isCustomer, user);

        // ── Buttons ──────────────────────────────────────────────────────────
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);
        JButton detailsBtn = createBtn("View Details", new Color(142, 68, 173));
        JButton refreshBtn = createBtn("Refresh",      new Color(100, 116, 139));

        detailsBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select an appointment first.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            showAppointmentDetails(row);
        });

        refreshBtn.addActionListener(e -> refreshData());

        buttonPanel.add(detailsBtn);
        buttonPanel.add(refreshBtn);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setOpaque(false);
        southPanel.add(filterPanel, BorderLayout.NORTH);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        // add(headerPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }

    private void loadAppointments(boolean isCustomer, UserAccount user) {
        tableModel.setRowCount(0);
        String selectedStatus = "All";
        if (filterCombo != null && filterCombo.getSelectedItem() != null) {
            selectedStatus = (String) filterCombo.getSelectedItem();
        }

        AppointmentController apptController = new AppointmentController();
        java.util.Optional<java.util.List<Appointment>> listOpt = apptController.retrieveAllAppointments();
        
        if (listOpt.isPresent()) {
            boolean isTechnician = false;
            String currentId = "";
            if (user instanceof main.java.backend.model.StaffAccount staff) {
                isTechnician = (staff.getJobPosition() == main.java.enums.data.JobPosition.TECHNICIAN);
                currentId = staff.getAccountID();
            }

            for (Appointment appt : listOpt.get()) {
                boolean showRole = false;
                if (isCustomer) {
                    showRole = (appt.getCustomerAccount() != null && appt.getCustomerAccount().getAccountID().equals(user.getAccountID()));
                } else if (isTechnician) {
                    if (appt.getTechnicianList() != null) {
                        for (StaffAccount tech : appt.getTechnicianList()) {
                            if (tech.getAccountID().equals(currentId)) {
                                showRole = true;
                                break;
                            }
                        }
                    }
                } else {
                    showRole = true;
                }

                boolean showStatus = selectedStatus.equals("All") || appt.getAppointmentStatus().getAppointmentStatus().equalsIgnoreCase(selectedStatus);
                
                if (showRole && showStatus) {
                    tableModel.addRow(new Object[]{
                        appt.getAppointmentID(),
                        appt.getAppointmentOrderNo(),
                        appt.getAppointmentTitle(),
                        appt.getServiceDate().toString(),
                        appt.getServiceStartTime().toString(),
                        appt.getServicePackage() != null ? appt.getServicePackage().getServicePackageName() : "N/A",
                        appt.getAppointmentStatus().getAppointmentStatus()
                    });
                }
            }
        }
    }

    private void showAppointmentDetails(int row) {
        String apptId   = (String) tableModel.getValueAt(row, 0);
        AppointmentController apptController = new AppointmentController();
        java.util.Optional<Appointment> optAppt = apptController.retrieveAppointmentById(apptId);
        if (!optAppt.isPresent()) {
            JOptionPane.showMessageDialog(this, "Appointment details not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Appointment appt = optAppt.get();
 
        JDialog dialog = new JDialog(this, "Appointment Details", true);
        dialog.setSize(440, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
 
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
 
        form.add(new JLabel("Appointment ID:")); form.add(new JLabel(appt.getAppointmentID()));
        form.add(new JLabel("Order Number:")); form.add(new JLabel(appt.getAppointmentOrderNo()));
        form.add(new JLabel("Service Title:")); form.add(new JLabel(appt.getAppointmentTitle()));
        form.add(new JLabel("Description:")); form.add(new JLabel(appt.getAppointmentDescription() != null ? appt.getAppointmentDescription() : "N/A"));
        form.add(new JLabel("Scheduled Date:")); form.add(new JLabel(appt.getServiceDate().toString()));
        form.add(new JLabel("Scheduled Time:")); form.add(new JLabel(appt.getServiceStartTime().toString()));
        form.add(new JLabel("Service Package:")); form.add(new JLabel(appt.getServicePackage() != null ? appt.getServicePackage().getServicePackageName() : "N/A"));
        form.add(new JLabel("Price:")); form.add(new JLabel(appt.getServicePackage() != null ? "RM " + String.format("%.2f", appt.getServicePackage().getPackageBasePrice()) : "N/A"));
        form.add(new JLabel("Status:")); form.add(new JLabel(appt.getAppointmentStatus().getAppointmentStatus()));
 
        JButton closeBtn = createBtn("Close", new Color(108, 117, 125));
        closeBtn.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.add(closeBtn);
 
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JButton createBtn(String text, Color color) {
        JButton b = new JButton(text); b.setFont(new Font("Segoe UI", Font.BOLD, 13)); b.setForeground(Color.WHITE);
        b.setBackground(color); b.setFocusPainted(false); b.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18)); b.setCursor(new Cursor(Cursor.HAND_CURSOR)); return b;
    }

    @Override
    public void refreshData() {
        UserAccount user = SessionManager.getCurrentUser();
        boolean isCustomer = SessionManager.getCurrentRole() == Role.CUSTOMER;
        loadAppointments(isCustomer, user);
    }
}
