package main.java.frontend.account;

import main.java.backend.controller.AccountController;
import main.java.backend.model.StaffAccount;
import main.java.backend.model.UserAccount;
import main.java.dtos.StaffAccountDTO;
import main.java.enums.data.EmploymentStatus;
import main.java.enums.data.JobPosition;
import main.java.enums.data.Role;
import main.java.enums.data.AccountStatus;
import main.java.records.OperationResult;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ManageStaffUI extends JFrame implements main.java.frontend.shared.Refreshable {

    private JTable staffTable;
    private DefaultTableModel tableModel;

    // Column indices
    private static final int COL_ACC_ID    = 0;
    private static final int COL_NAME      = 1;
    private static final int COL_JOB_POS   = 3;
    private static final int COL_ACC_STATUS = 5;

    public ManageStaffUI() {
        setTitle("Manage Staff Accounts - Auttomov Software");
        setSize(780, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 244, 248));

        // ── Header Panel (Title Card) ──────────────────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        JLabel title = new JLabel("Manage Staff Accounts");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        headerPanel.add(title, BorderLayout.CENTER);

        // ── Table Setup ──────────────────────────────────────────────────────
        String[] cols = {"Account ID", "Full Name", "Email", "Job Position", "Emp. Status", "Account Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        loadStaffData();

        staffTable = new JTable(tableModel);
        staffTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        staffTable.setRowHeight(28); // Spacious premium row height
        staffTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Double-click to update staff details
        staffTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int r = staffTable.getSelectedRow();
                    if (r >= 0) {
                        int modelRow = staffTable.convertRowIndexToModel(r);
                        showUpdateStaffDialog(modelRow);
                    }
                }
            }
        });

        // Completely hide the Account ID column from view (preserves Model access)
        TableColumn accIdColumn = staffTable.getColumnModel().getColumn(COL_ACC_ID);
        staffTable.removeColumn(accIdColumn);

        // Setup custom header renderer: Bold text, light-slate background, left-aligned, 5px padding
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                setBackground(new Color(230, 235, 240)); // Premium light-slate background
                setForeground(new Color(51, 65, 85));     // Dark slate text
                setBorder(BorderFactory.createCompoundBorder(
                    getBorder(),
                    BorderFactory.createEmptyBorder(0, 5, 0, 0) // Left margin to align with cells
                ));
                setHorizontalAlignment(JLabel.LEFT);
                return this;
            }
        };
        staffTable.getTableHeader().setDefaultRenderer(headerRenderer);

        // Apply a custom cell renderer to give left padding and prevent clashing with gridlines
        PaddingCellRenderer paddingRenderer = new PaddingCellRenderer();
        for (int i = 0; i < staffTable.getColumnCount(); i++) {
            staffTable.getColumnModel().getColumn(i).setCellRenderer(paddingRenderer);
        }

        // Set Proportional Preferred Column Widths safely
        staffTable.getColumnModel().getColumn(0).setPreferredWidth(160); // Full Name
        staffTable.getColumnModel().getColumn(1).setPreferredWidth(220); // Email
        staffTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Job Position
        staffTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Emp. Status
        staffTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Account Status

        // Set up the local TableRowSorter for real-time search & filter
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        staffTable.setRowSorter(sorter);

        JScrollPane tableScroll = new JScrollPane(staffTable);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());

        // ── Filter / Search Panel (Top region below header) ────────────────────
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setOpaque(false);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(12, 10, 6, 10));

        // Clean flat labels (no box emojis/box indicators)
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchLabel.setForeground(new Color(74, 85, 104));
        
        JTextField searchField = new JTextField(12);
        styleField(searchField);

        JLabel jobLabel = new JLabel("Job:");
        jobLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        jobLabel.setForeground(new Color(74, 85, 104));

        JComboBox<String> jobFilter = new JComboBox<>();
        jobFilter.addItem("All Positions");
        for (JobPosition pos : JobPosition.values()) {
            jobFilter.addItem(pos.name());
        }
        styleComboBox(jobFilter);

        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setForeground(new Color(74, 85, 104));

        JComboBox<String> statusFilter = new JComboBox<>();
        statusFilter.addItem("All Statuses");
        for (AccountStatus status : AccountStatus.values()) {
            statusFilter.addItem(status.name());
        }
        styleComboBox(statusFilter);

        filterPanel.add(searchLabel);
        filterPanel.add(searchField);
        filterPanel.add(jobLabel);
        filterPanel.add(jobFilter);
        filterPanel.add(statusLabel);
        filterPanel.add(statusFilter);

        // ── Action Buttons Panel (Bottom / SOUTH region) ────────────────────────
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 12));
        actionPanel.setOpaque(false);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 12, 10));

        // Flat clean button text for layout elegance and zero rendering bugs
        JButton addBtn    = createBtn("Add Staff",    new Color(39, 174, 96)); // Vibrant green
        JButton updateBtn = createBtn("Update",       new Color(90, 107, 124)); // Neutral grey/blue
        JButton deleteBtn  = createBtn("Delete",    new Color(192, 57, 43)); // Subtle warning red
        JButton refreshBtn = createBtn("Refresh",   new Color(100, 116, 139)); // Elegant slate grey

        addBtn.addActionListener(e -> showAddStaffDialog());
        updateBtn.addActionListener(e -> {
            int row = staffTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a staff member first.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = staffTable.convertRowIndexToModel(row);
            showUpdateStaffDialog(modelRow);
        });
        deleteBtn.addActionListener(e -> {
            int row = staffTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a staff member first.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = staffTable.convertRowIndexToModel(row);
            handleDelete(modelRow);
        });
        refreshBtn.addActionListener(e -> loadStaffData());

        actionPanel.add(addBtn);
        actionPanel.add(updateBtn);
        actionPanel.add(deleteBtn);
        actionPanel.add(refreshBtn);

        // Wire search and filter events for real-time responsiveness
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                applyFilter(searchField, jobFilter, statusFilter, sorter);
            }
        });
        jobFilter.addActionListener(e -> applyFilter(searchField, jobFilter, statusFilter, sorter));
        statusFilter.addActionListener(e -> applyFilter(searchField, jobFilter, statusFilter, sorter));

        // Combine Header and filters vertically in the North layout space
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);
        topContainer.add(headerPanel);
        topContainer.add(filterPanel);

        add(topContainer, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH); // Stretched at absolute bottom, purely for acting on data
    }

    private void applyFilter(JTextField searchField, JComboBox<String> jobFilter, JComboBox<String> statusFilter, TableRowSorter<DefaultTableModel> sorter) {
        String text = searchField.getText().trim();
        
        RowFilter<DefaultTableModel, Object> rf = new RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                // 1. Text search filter (fuzzy matches across any field like ID, name, email)
                boolean textMatches = false;
                if (text.isEmpty()) {
                    textMatches = true;
                } else {
                    String searchLower = text.toLowerCase();
                    for (int i = 0; i < entry.getValueCount(); i++) {
                        Object val = entry.getValue(i);
                        if (val != null && val.toString().toLowerCase().contains(searchLower)) {
                            textMatches = true;
                            break;
                        }
                    }
                }
                
                // 2. Job Position filter
                boolean jobMatches = true;
                Object selectedJob = jobFilter.getSelectedItem();
                if (selectedJob != null && !"All Positions".equals(selectedJob)) {
                    Object cellJob = entry.getValue(COL_JOB_POS);
                    jobMatches = cellJob != null && cellJob.toString().equalsIgnoreCase(selectedJob.toString());
                }
                
                // 3. Account Status filter
                boolean statusMatches = true;
                Object selectedStatus = statusFilter.getSelectedItem();
                if (selectedStatus != null && !"All Statuses".equals(selectedStatus)) {
                    Object cellStatus = entry.getValue(COL_ACC_STATUS);
                    statusMatches = cellStatus != null && cellStatus.toString().equalsIgnoreCase(selectedStatus.toString());
                }
                
                return textMatches && jobMatches && statusMatches;
            }
        };
        sorter.setRowFilter(rf);
    }

    private void loadStaffData() {
        tableModel.setRowCount(0);
        AccountController accountController = new AccountController();
        java.util.Optional<java.util.List<UserAccount>> listOpt = accountController.retrieveAllAccounts(Role.STAFF);
        if (listOpt.isPresent()) {
            for (UserAccount acc : listOpt.get()) {
                if (acc instanceof StaffAccount staff) {
                    tableModel.addRow(new Object[]{
                        staff.getAccountID(),
                        staff.getFirstName() + " " + staff.getLastName(),
                        staff.getEmailAddress(),
                        staff.getJobPosition(),
                        staff.getEmploymentStatus(),
                        staff.getAccountStatus() != null ? staff.getAccountStatus().getAccStatus() : "PENDING"
                    });
                }
            }
        }
    }

    private void showAddStaffDialog() {
        JDialog dialog = new JDialog(this, "Add New Staff", true);
        dialog.setSize(540, 560); // Spacious desktop dimensions - eliminates vertical scrollbars
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(0, 2, 16, 12));
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        form.setBackground(new Color(248, 250, 252));

        Dimension formFieldSize = new Dimension(220, 32);
        JTextField firstF   = new JTextField(); firstF.setPreferredSize(formFieldSize); styleField(firstF);
        JTextField lastF    = new JTextField(); lastF.setPreferredSize(formFieldSize); styleField(lastF);
        JTextField userIdF  = new JTextField(); userIdF.setPreferredSize(formFieldSize); styleField(userIdF);
        JTextField emailF   = new JTextField(); emailF.setPreferredSize(formFieldSize); styleField(emailF);
        JTextField phoneF   = new JTextField(); phoneF.setPreferredSize(formFieldSize); styleField(phoneF);
        
        JComboBox<JobPosition> jobPosBox = new JComboBox<>(JobPosition.values());
        jobPosBox.setPreferredSize(formFieldSize);
        styleComboBox(jobPosBox);
        
        JPasswordField passF     = new JPasswordField(); passF.setPreferredSize(formFieldSize); styleField(passF);
        JPasswordField confPassF = new JPasswordField(); confPassF.setPreferredSize(formFieldSize); styleField(confPassF);
        
        JCheckBox showPassCheck = new JCheckBox("Show Passwords");
        showPassCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPassCheck.setOpaque(false);
        showPassCheck.setFocusPainted(false);
        char defaultEcho = passF.getEchoChar();
        showPassCheck.addActionListener(e -> {
            char echo = showPassCheck.isSelected() ? (char) 0 : defaultEcho;
            passF.setEchoChar(echo);
            confPassF.setEchoChar(echo);
        });
        
        JComboBox<EmploymentStatus> empStatusBox = new JComboBox<>(EmploymentStatus.values());
        empStatusBox.setPreferredSize(formFieldSize);
        styleComboBox(empStatusBox);
        
        JTextField specF = new JTextField(); specF.setPreferredSize(formFieldSize); styleField(specF);

        // Build side-by-side grouped fields (two-column form grid)
        form.add(createCompactFormField("First Name (e.g. John):", firstF));
        form.add(createCompactFormField("Last Name (e.g. Doe):", lastF));
        form.add(createCompactFormField("User ID (IC/Passport):", userIdF));
        form.add(createCompactFormField("Email Address:", emailF));
        form.add(createCompactFormField("Phone Number:", phoneF));
        form.add(createCompactFormField("Job Position:", jobPosBox));
        form.add(createCompactFormField("Password:", passF));
        form.add(createCompactFormField("Confirm Password:", confPassF));
        form.add(createCompactFormField("", showPassCheck));
        form.add(createCompactFormField("Employment Status:", empStatusBox));
        
        JPanel specPanel = createCompactFormField("Specialization (e.g. Engine Repair):", specF);
        form.add(specPanel);

        boolean isTechInit = jobPosBox.getSelectedItem() == JobPosition.TECHNICIAN;
        specPanel.setVisible(isTechInit);
        
        jobPosBox.addActionListener(e -> {
            boolean isTech = jobPosBox.getSelectedItem() == JobPosition.TECHNICIAN;
            specPanel.setVisible(isTech);
            if (!isTech) specF.setText("");
            form.revalidate();
            form.repaint();
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 15));
        btnPanel.setBackground(new Color(241, 245, 249));
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        JButton saveBtn = createBtn("Add Staff Member", new Color(39, 174, 96));
        JButton cancelBtn = createBtn("Cancel", new Color(148, 163, 184));
        
        saveBtn.addActionListener(e -> {
            AccountController accountController = new AccountController();
            JobPosition jobPos = (JobPosition) jobPosBox.getSelectedItem();
            EmploymentStatus empStatus = (EmploymentStatus) empStatusBox.getSelectedItem();
            String spec = (jobPos == JobPosition.TECHNICIAN) ? specF.getText().trim() : null;
            
            StaffAccountDTO staffDto = new StaffAccountDTO(
                userIdF.getText().trim(), Role.STAFF, firstF.getText().trim(), lastF.getText().trim(),
                emailF.getText().trim(), phoneF.getText().trim(),
                new String(passF.getPassword()), new String(confPassF.getPassword()),
                jobPos, empStatus, spec
            );
            OperationResult result = accountController.createAccount(staffDto);
            if (result.isValid()) {
                JOptionPane.showMessageDialog(dialog, "Staff account created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadStaffData();
                dialog.dispose();
            } else {
                StringBuilder sb = new StringBuilder();
                result.fieldErrors().forEach((f, msg) -> sb.append("- ").append(msg).append("\n"));
                if (sb.isEmpty()) sb.append(result.generalMessage());
                JOptionPane.showMessageDialog(dialog, sb.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        dialog.add(new JScrollPane(form), BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showUpdateStaffDialog(int row) {
        String accId = (String) tableModel.getValueAt(row, COL_ACC_ID);
        AccountController accountController = new AccountController();
        java.util.Optional<UserAccount> optAcc = accountController.retrieveAccountById(accId, Role.STAFF);
        if (!optAcc.isPresent()) {
            JOptionPane.showMessageDialog(this, "Staff not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        StaffAccount staff = (StaffAccount) optAcc.get();
        String fullName = staff.getFirstName() + " " + staff.getLastName();
        String email    = staff.getEmailAddress();
        JobPosition currentJob = staff.getJobPosition();
        EmploymentStatus currentEmp = staff.getEmploymentStatus();
 
        JDialog dialog = new JDialog(this, "Update Staff: " + fullName, true);
        dialog.setSize(540, 560); // Spacious desktop dimensions - eliminates vertical scrollbars
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
 
        JPanel form = new JPanel(new GridLayout(0, 2, 16, 12));
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        form.setBackground(new Color(248, 250, 252));
 
        JTextField firstF   = new JTextField(); styleField(firstF); firstF.setText(staff.getFirstName());
        JTextField lastF    = new JTextField(); styleField(lastF); lastF.setText(staff.getLastName());
        JTextField userIdF  = new JTextField(); styleField(userIdF); userIdF.setText(staff.getUserID());
        JTextField emailF   = new JTextField(); styleField(emailF); emailF.setText(email);
        JTextField phoneF   = new JTextField(); styleField(phoneF); phoneF.setText(staff.getPhoneNumber());
        JTextField empDateF = new JTextField(); styleField(empDateF); empDateF.setText(staff.getEmploymentDate().toString());
        
        JComboBox<JobPosition> jobPosBox = new JComboBox<>(JobPosition.values());
        styleComboBox(jobPosBox);
        jobPosBox.setSelectedItem(currentJob);
        
        JComboBox<EmploymentStatus> empStatusBox = new JComboBox<>(EmploymentStatus.values());
        styleComboBox(empStatusBox);
        empStatusBox.setSelectedItem(currentEmp);
        
        JTextField specF = new JTextField(); styleField(specF); specF.setText(staff.getSpecialization());
 
        // Build side-by-side grouped fields (two-column form grid)
        form.add(createFormField("First Name:", firstF));
        form.add(createFormField("Last Name:", lastF));
        form.add(createFormField("User ID (IC/Passport):", userIdF));
        form.add(createFormField("Email Address:", emailF));
        form.add(createFormField("Phone Number:", phoneF));
        form.add(createFormField("Employment Date (YYYY-MM-DD):", empDateF));
        form.add(createFormField("Job Position:", jobPosBox));
        form.add(createFormField("Employment Status:", empStatusBox));
        
        JPanel specPanel = createFormField("Specialization (e.g. Engine Repair):", specF);
        form.add(specPanel);
        
        JPanel fillerPanel = new JPanel();
        fillerPanel.setOpaque(false);
        form.add(fillerPanel);
 
        boolean isTechInit = jobPosBox.getSelectedItem() == JobPosition.TECHNICIAN;
        specPanel.setVisible(isTechInit);
        fillerPanel.setVisible(isTechInit);
        
        jobPosBox.addActionListener(e -> {
            boolean isTech = jobPosBox.getSelectedItem() == JobPosition.TECHNICIAN;
            specPanel.setVisible(isTech);
            fillerPanel.setVisible(isTech);
            if (!isTech) specF.setText("");
            form.revalidate();
            form.repaint();
        });
 
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 15));
        btnPanel.setBackground(new Color(241, 245, 249));
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        JButton saveBtn = createBtn("Save Changes", new Color(90, 107, 124));
        JButton cancelBtn = createBtn("Cancel", new Color(148, 163, 184));
        
        saveBtn.addActionListener(e -> {
            LocalDate empDate;
            try {
                empDate = LocalDate.parse(empDateF.getText().trim());
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid date format. Use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JobPosition newJob = (JobPosition) jobPosBox.getSelectedItem();
            EmploymentStatus newEmp = (EmploymentStatus) empStatusBox.getSelectedItem();
            String spec = (newJob == JobPosition.TECHNICIAN) ? specF.getText().trim() : null;
            
            StaffAccountDTO staffDto = new StaffAccountDTO(
                staff.getAccountID(), Role.STAFF, userIdF.getText().trim(),
                firstF.getText().trim(), lastF.getText().trim(),
                emailF.getText().trim(), phoneF.getText().trim(),
                staff.getAccountStatus(), newJob, empDate, newEmp, spec
            );
            OperationResult result = accountController.updateAccount(staffDto);
            if (result.isValid()) {
                JOptionPane.showMessageDialog(dialog, "Staff account updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadStaffData();
                dialog.dispose();
            } else {
                StringBuilder sb = new StringBuilder();
                result.fieldErrors().forEach((f, msg) -> sb.append("- ").append(msg).append("\n"));
                if (sb.isEmpty()) sb.append(result.generalMessage());
                JOptionPane.showMessageDialog(dialog, sb.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        
        dialog.add(new JScrollPane(form), BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void handleDelete(int row) {
        String accId = (String) tableModel.getValueAt(row, COL_ACC_ID);
        String fullName = (String) tableModel.getValueAt(row, COL_NAME);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete staff account for: " + fullName + "?\nThis cannot be undone.",
            "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            AccountController accountController = new AccountController();
            OperationResult result = accountController.deleteAccount(accId, Role.STAFF);
            if (result.isValid()) {
                JOptionPane.showMessageDialog(this, "Staff account deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadStaffData();
            } else {
                JOptionPane.showMessageDialog(this, result.generalMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel createFormField(String labelText, JComponent inputField) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(51, 65, 85)); // Slate-700
        panel.add(label, BorderLayout.NORTH);
        panel.add(inputField, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCompactFormField(String labelText, JComponent inputField) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(51, 65, 85)); // Slate-700
        panel.add(label, BorderLayout.NORTH);
        JPanel fieldWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        fieldWrapper.setOpaque(false);
        fieldWrapper.add(inputField);
        panel.add(fieldWrapper, BorderLayout.CENTER);
        return panel;
    }

    private void styleField(JTextField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225)), // Slate-300
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        f.setMargin(new Insets(5, 8, 5, 8)); // 5px top/bottom, 8px left/right
        f.setBackground(Color.WHITE);
    }

    private void styleComboBox(JComboBox<?> c) {
        c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        c.setBackground(Color.WHITE);
        c.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));
    }

    private JButton createBtn(String text, Color color) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setBackground(color);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    @Override
    public void refreshData() {
        loadStaffData();
    }

    // Custom Table Cell Renderer to give neat left inset padding
    private static class PaddingCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBorder(BorderFactory.createCompoundBorder(getBorder(), BorderFactory.createEmptyBorder(0, 5, 0, 0))); // 5px left margin
            return this;
        }
    }
}
