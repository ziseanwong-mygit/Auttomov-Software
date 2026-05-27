package main.java.frontend.appointment;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import main.java.backend.controller.AppointmentController;
import main.java.backend.model.Appointment;
import main.java.backend.model.StaffAccount;
import main.java.backend.model.UserAccount;
import main.java.dtos.AppointmentDTO;
import main.java.records.OperationResult;
import main.java.records.TechAvailabilityResult;

/**
 * AssignTechnicianUI — Modern 2-Step wizard for counter staff and manager.
 * 
 * Step 1: Browse all appointments (assigned & unassigned), select one, and click Next.
 * Step 2: Configure technician assignments. Displays appointment info at the top,
 * available/assigned technicians in the middle, unavailable technicians on the right,
 * and comprehensive actions (Assign, Unassign, Reset, Back, Finish) at the bottom.
 */
public class AssignTechnicianUI extends JFrame implements main.java.frontend.shared.Refreshable {

    private final CardLayout cardLayout;
    private final JPanel mainContainer;

    // --- Step 1 Components ---
    private DefaultTableModel apptModel;
    private JTable apptTable;
    private JButton nextBtn;

    // --- Step 2 Components ---
    private Appointment selectedAppointment;
    private JLabel infoOrderNoLabel, infoTitleLabel, infoDateLabel, infoCustomerLabel, infoPackageLabel;
    
    private DefaultTableModel availTechModel;
    private JTable availTechTable;
    
    private DefaultTableModel assignedTechModel;
    private JTable assignedTechTable;
    
    private DefaultTableModel unavailableTechModel;
    private JTable unavailableTechTable;

    // Session state for Step 2
    private List<StaffAccount> originalAssignedTechs = new ArrayList<>();
    private List<StaffAccount> currentAssignedTechs = new ArrayList<>();
    private List<StaffAccount> initialAvailableTechs = new ArrayList<>();

    public AssignTechnicianUI() {
        setTitle("Assign Technician - Auttomov Software");
        setSize(960, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        mainContainer.setBackground(new Color(241, 245, 249));

        // Build Step 1 and Step 2 panels
        JPanel step1Panel = buildStep1Panel();
        JPanel step2Panel = buildStep2Panel();

        mainContainer.add(step1Panel, "step1");
        mainContainer.add(step2Panel, "step2");

        setLayout(new BorderLayout());
        add(mainContainer, BorderLayout.CENTER);

        cardLayout.show(mainContainer, "step1");
        loadAppointments();
    }

    @Override
    public void refreshData() {
        loadAppointments();
    }

    // ── STEP 1: APPOINTMENT SELECTION SCREEN ─────────────────────────────────
    private JPanel buildStep1Panel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Subtitle/Header inside
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel title = new JLabel("Step 1: Select Appointment Record");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(30, 41, 59));
        
        JLabel subtitle = new JLabel("Choose any appointment (unassigned or already assigned) to manage its technician roster.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(100, 116, 139));
        
        headerPanel.add(title, BorderLayout.NORTH);
        headerPanel.add(subtitle, BorderLayout.SOUTH);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Appointment Table
        String[] cols = {"Appt. ID", "Order No.", "Title", "Date & Time", "Status", "Assigned Technicians"};
        apptModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        apptTable = new JTable(apptModel);
        styleTable(apptTable);
        
        // Double-click an appointment to proceed to next step
        apptTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = apptTable.getSelectedRow();
                    if (row >= 0) {
                        handleNextStep();
                    }
                }
            }
        });

        // Hide Appt ID column
        apptTable.getColumnModel().getColumn(0).setMinWidth(0);
        apptTable.getColumnModel().getColumn(0).setMaxWidth(0);
        apptTable.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(apptTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Action Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setOpaque(false);
        
        JButton refreshBtn = createBtn("Refresh",    new Color(100, 116, 139));
        nextBtn = createBtn("Next Step", new Color(37, 99, 235));
        
        refreshBtn.addActionListener(e -> refreshData());
        nextBtn.addActionListener(e -> handleNextStep());
        
        bottomPanel.add(refreshBtn);
        bottomPanel.add(nextBtn);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ── STEP 2: ASSIGNMENT DASHBOARD SCREEN ──────────────────────────────────
    private JPanel buildStep2Panel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // 1. Top Panel: Appointment Info
        JPanel topInfoPanel = new JPanel(new GridBagLayout());
        topInfoPanel.setBackground(new Color(255, 255, 255));
        topInfoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            new EmptyBorder(12, 18, 12, 18)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.weightx = 0.5;

        // Row 1: Order No & Customer Name
        infoOrderNoLabel = createInfoLabel("");
        infoCustomerLabel = createInfoLabel("");
        addInfoRow(topInfoPanel, gbc, 0, 0, "Order Number:", infoOrderNoLabel);
        addInfoRow(topInfoPanel, gbc, 0, 1, "Customer Name:", infoCustomerLabel);

        // Row 2: Title & Service Package
        infoTitleLabel = createInfoLabel("");
        infoPackageLabel = createInfoLabel("");
        addInfoRow(topInfoPanel, gbc, 1, 0, "Appointment Title:", infoTitleLabel);
        addInfoRow(topInfoPanel, gbc, 1, 1, "Service Package:", infoPackageLabel);

        // Row 3: Service Date/Time
        infoDateLabel = createInfoLabel("");
        addInfoRow(topInfoPanel, gbc, 2, 0, "Scheduled Date/Time:", infoDateLabel);

        panel.add(topInfoPanel, BorderLayout.NORTH);

        // 2. Middle Panel: 3-Column Technician Status Layout
        JPanel columnsPanel = new JPanel(new GridLayout(1, 3, 14, 0));
        columnsPanel.setOpaque(false);

        // --- Column 1: Available Technicians ---
        String[] availCols = {"Staff ID", "Name", "Specialization"};
        availTechModel = new DefaultTableModel(availCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        availTechTable = new JTable(availTechModel);
        styleTable(availTechTable);
        availTechTable.getColumnModel().getColumn(0).setMinWidth(0);
        availTechTable.getColumnModel().getColumn(0).setMaxWidth(0);
        availTechTable.getColumnModel().getColumn(0).setWidth(0);
        columnsPanel.add(wrapTableWithTitle(availTechTable, " Available Technicians", new Color(59, 130, 246)));

        // --- Column 2: Assigned Technicians (Original & New) ---
        String[] assignedCols = {"Staff ID", "Name", "Specialization", "Assignment Status"};
        assignedTechModel = new DefaultTableModel(assignedCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        assignedTechTable = new JTable(assignedTechModel);
        styleTable(assignedTechTable);
        assignedTechTable.getColumnModel().getColumn(0).setMinWidth(0);
        assignedTechTable.getColumnModel().getColumn(0).setMaxWidth(0);
        assignedTechTable.getColumnModel().getColumn(0).setWidth(0);
        columnsPanel.add(wrapTableWithTitle(assignedTechTable, " Assigned Technicians", new Color(16, 185, 129)));

        // --- Column 3: Unavailable Technicians ---
        String[] unavailCols = {"Staff ID", "Name", "Clash details"};
        unavailableTechModel = new DefaultTableModel(unavailCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        unavailableTechTable = new JTable(unavailableTechModel);
        styleTable(unavailableTechTable);
        unavailableTechTable.getColumnModel().getColumn(0).setMinWidth(0);
        unavailableTechTable.getColumnModel().getColumn(0).setMaxWidth(0);
        unavailableTechTable.getColumnModel().getColumn(0).setWidth(0);
        columnsPanel.add(wrapTableWithTitle(unavailableTechTable, " Unavailable Technicians (Busy)", new Color(239, 68, 68)));

        panel.add(columnsPanel, BorderLayout.CENTER);

        // 3. Bottom Panel: Action Buttons
        JPanel bottomActions = new JPanel(new BorderLayout());
        bottomActions.setOpaque(false);
        bottomActions.setBorder(new EmptyBorder(8, 0, 0, 0));

        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftActions.setOpaque(false);
        JButton backBtn = createBtn("Back", new Color(100, 116, 139));
        backBtn.addActionListener(e -> cardLayout.show(mainContainer, "step1"));
        JButton resetBtn = createBtn("Reset Assignment", new Color(245, 158, 11));
        resetBtn.addActionListener(e -> resetAssignmentState());
        leftActions.add(backBtn);
        leftActions.add(resetBtn);

        JPanel centerActions = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        centerActions.setOpaque(false);
        JButton assignBtn = createBtn("Assign Tech", new Color(37, 99, 235));
        assignBtn.addActionListener(e -> handleAssignTech());
        JButton unassignBtn = createBtn("Unassign Tech", new Color(239, 68, 68));
        unassignBtn.addActionListener(e -> handleUnassignTech());
        centerActions.add(assignBtn);
        centerActions.add(unassignBtn);

        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightActions.setOpaque(false);
        JButton finishBtn = createBtn("Finish Assignment", new Color(16, 185, 129));
        finishBtn.addActionListener(e -> handleFinishAssignment());
        rightActions.add(finishBtn);

        bottomActions.add(leftActions, BorderLayout.WEST);
        bottomActions.add(centerActions, BorderLayout.CENTER);
        bottomActions.add(rightActions, BorderLayout.EAST);

        panel.add(bottomActions, BorderLayout.SOUTH);

        return panel;
    }

    // ── EVENT & LOGIC HANDLERS ───────────────────────────────────────────────

    private void loadAppointments() {
        apptModel.setRowCount(0);
        AppointmentController apptController = new AppointmentController();
        java.util.Optional<java.util.List<Appointment>> listOpt = apptController.retrieveAllAppointments();
        if (listOpt.isPresent()) {
            for (Appointment appt : listOpt.get()) {
                String techsString = "Unassigned";
                if (appt.getTechnicianList() != null) {
                    techsString = appt.getTechnicianList().stream()
                            .map(tech -> tech.getFirstName() + " " + tech.getLastName())
                            .collect(Collectors.joining(", "));
                    if (techsString.isBlank()) {
                        techsString = "Unassigned";
                    }
                }

                apptModel.addRow(new Object[]{
                    appt.getAppointmentID(),
                    appt.getAppointmentOrderNo(),
                    appt.getAppointmentTitle(),
                    appt.getServiceDate() + " @ " + appt.getServiceStartTime(),
                    appt.getAppointmentStatus().getAppointmentStatus(),
                    techsString
                });
            }
        }
    }

    private void handleNextStep() {
        int selectedRow = apptTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an appointment from the list first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String apptId = (String) apptModel.getValueAt(selectedRow, 0);
        AppointmentController apptController = new AppointmentController();
        java.util.Optional<Appointment> apptOpt = apptController.retrieveAppointmentById(apptId);
        
        if (apptOpt.isPresent()) {
            selectedAppointment = apptOpt.get();
            loadStep2Data();
            cardLayout.show(mainContainer, "step2");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to load appointment details.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadStep2Data() {
        if (selectedAppointment == null) return;

        // Fill top info cards
        infoOrderNoLabel.setText(selectedAppointment.getAppointmentOrderNo());
        infoTitleLabel.setText(selectedAppointment.getAppointmentTitle());
        infoDateLabel.setText(selectedAppointment.getServiceDate() + " (" + selectedAppointment.getServiceStartTime() + " - " + selectedAppointment.getServiceEndTime() + ")");
        infoCustomerLabel.setText(selectedAppointment.getCustomerAccount().getFirstName() + " " + selectedAppointment.getCustomerAccount().getLastName());
        infoPackageLabel.setText(selectedAppointment.getServicePackage() != null ? selectedAppointment.getServicePackage().getServicePackageName() : "N/A");

        // Set session lists
        originalAssignedTechs = new ArrayList<>();
        if (selectedAppointment.getTechnicianList() != null) {
            originalAssignedTechs.addAll(selectedAppointment.getTechnicianList());
        }
        currentAssignedTechs = new ArrayList<>(originalAssignedTechs);

        // Fetch availabilities
        AppointmentController apptController = new AppointmentController();
        java.util.Optional<TechAvailabilityResult> availOpt = apptController.retrieveTechAvailabilityList(selectedAppointment.getAppointmentID());
        
        initialAvailableTechs.clear();
        unavailableTechModel.setRowCount(0);

        if (availOpt.isPresent()) {
            TechAvailabilityResult result = availOpt.get();
            
            // Available
            initialAvailableTechs.addAll(result.availableTechnicians());

            // Unavailable
            for (TechAvailabilityResult.TechnicianConflict conflict : result.unavailableTechnicians()) {
                StaffAccount tech = conflict.technician();
                unavailableTechModel.addRow(new Object[]{
                    tech.getAccountID(),
                    tech.getFirstName() + " " + tech.getLastName(),
                    "Clash: " + conflict.conflictingAppointment().getAppointmentTitle() + " (" + conflict.conflictingAppointment().getAppointmentOrderNo() + ")"
                });
            }
        }

        refreshTechTables();
    }

    private void refreshTechTables() {
        availTechModel.setRowCount(0);
        assignedTechModel.setRowCount(0);

        // Fill Available Techs (initial available minus current assigned)
        List<String> assignedIds = currentAssignedTechs.stream().map(UserAccount::getAccountID).toList();
        for (StaffAccount tech : initialAvailableTechs) {
            if (!assignedIds.contains(tech.getAccountID())) {
                availTechModel.addRow(new Object[]{
                    tech.getAccountID(),
                    tech.getFirstName() + " " + tech.getLastName(),
                    tech.getSpecialization() != null ? tech.getSpecialization() : "General"
                });
            }
        }

        // Fill Assigned Techs
        List<String> originalIds = originalAssignedTechs.stream().map(UserAccount::getAccountID).toList();
        for (StaffAccount tech : currentAssignedTechs) {
            boolean isOriginal = originalIds.contains(tech.getAccountID());
            assignedTechModel.addRow(new Object[]{
                tech.getAccountID(),
                tech.getFirstName() + " " + tech.getLastName(),
                tech.getSpecialization() != null ? tech.getSpecialization() : "General",
                isOriginal ? "Original" : "New"
            });
        }
    }

    private void handleAssignTech() {
        int row = availTechTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a technician from the Available list to assign.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String techId = (String) availTechModel.getValueAt(row, 0);
        StaffAccount target = initialAvailableTechs.stream()
                .filter(tech -> tech.getAccountID().equals(techId))
                .findFirst().orElse(null);

        if (target != null) {
            currentAssignedTechs.add(target);
            refreshTechTables();
        }
    }

    private void handleUnassignTech() {
        int row = assignedTechTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a technician from the Assigned list to unassign.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String techId = (String) assignedTechModel.getValueAt(row, 0);
        currentAssignedTechs.removeIf(tech -> tech.getAccountID().equals(techId));
        refreshTechTables();
    }

    private void resetAssignmentState() {
        currentAssignedTechs = new ArrayList<>(originalAssignedTechs);
        refreshTechTables();
    }

    private void handleFinishAssignment() {
        List<String> techIds = currentAssignedTechs.stream().map(UserAccount::getAccountID).toList();
        
        AppointmentController apptController = new AppointmentController();
        AppointmentDTO dto = new AppointmentDTO(selectedAppointment.getAppointmentID(), techIds);
        
        OperationResult result = apptController.assignTechnicianToAppointment(dto);
        if (result.isValid()) {
            JOptionPane.showMessageDialog(this, "Technician assignments updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            cardLayout.show(mainContainer, "step1");
            loadAppointments();
        } else {
            // If result error matches "same as current", we treat it as fine or show simple message
            if (result.generalMessage() != null && result.generalMessage().contains("same as the current assigned")) {
                JOptionPane.showMessageDialog(this, "No changes made to technician assignments.", "Finish", JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(mainContainer, "step1");
                loadAppointments();
            } else {
                JOptionPane.showMessageDialog(this, result.generalMessage(), "Assignment Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ── STYLING HELPERS ──────────────────────────────────────────────────────

    private void addInfoRow(JPanel panel, GridBagConstraints gbc, int row, int col, String labelText, JLabel valLabel) {
        gbc.gridx = col * 2; gbc.gridy = row; gbc.weightx = 0.15;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(71, 85, 105));
        panel.add(lbl, gbc);

        gbc.gridx = col * 2 + 1; gbc.weightx = 0.35;
        panel.add(valLabel, gbc);
    }

    private JLabel createInfoLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(new Color(15, 23, 42));
        return l;
    }

    private JPanel wrapTableWithTitle(JTable table, String title, Color borderHeaderColor) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(borderHeaderColor, 2),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 13),
            borderHeaderColor
        ));
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    private void styleTable(JTable t) {
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setRowHeight(26);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
}
