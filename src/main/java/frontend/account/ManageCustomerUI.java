package main.java.frontend.account;

import main.java.backend.controller.AccountController;
import main.java.backend.model.UserAccount;
import main.java.dtos.UserAccountDTO;
import main.java.enums.data.Role;
import main.java.enums.data.AccountStatus;
import main.java.records.OperationResult;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class ManageCustomerUI extends JFrame implements main.java.frontend.shared.Refreshable {

    private JTable customerTable;
    private DefaultTableModel tableModel;
    private static final int COL_ACC_ID = 0;
    private static final int COL_NAME = 1;

    public ManageCustomerUI() {
        setTitle("Manage Customer Accounts - Auttomov Software");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 244, 248));

        // ── Header Panel (Title Card) ──────────────────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        JLabel title = new JLabel("Manage Customer Accounts");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.BLACK);
        headerPanel.add(title, BorderLayout.CENTER);

        // ── Table Setup ──────────────────────────────────────────────────────
        String[] cols = {"Account ID", "Full Name", "Email", "User ID", "Account Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        loadCustomerData();

        customerTable = new JTable(tableModel);
        customerTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        customerTable.setRowHeight(28); // Spacious row height
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Double-click to update customer details
        customerTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int r = customerTable.getSelectedRow();
                    if (r >= 0) {
                        int modelRow = customerTable.convertRowIndexToModel(r);
                        showUpdateDialog(modelRow);
                    }
                }
            }
        });

        // Completely hide the Account ID column from view (preserves Model access)
        TableColumn accIdColumn = customerTable.getColumnModel().getColumn(COL_ACC_ID);
        customerTable.removeColumn(accIdColumn);

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
        customerTable.getTableHeader().setDefaultRenderer(headerRenderer);

        // Apply a custom cell renderer to give left padding and prevent clashing with gridlines
        PaddingCellRenderer paddingRenderer = new PaddingCellRenderer();
        for (int i = 0; i < customerTable.getColumnCount(); i++) {
            customerTable.getColumnModel().getColumn(i).setCellRenderer(paddingRenderer);
        }

        // Set Proportional Preferred Column Widths safely
        customerTable.getColumnModel().getColumn(0).setPreferredWidth(180); // Full Name
        customerTable.getColumnModel().getColumn(1).setPreferredWidth(240); // Email
        customerTable.getColumnModel().getColumn(2).setPreferredWidth(140); // User ID
        customerTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Account Status

        // Set up the local TableRowSorter for real-time search & filter
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        customerTable.setRowSorter(sorter);

        JScrollPane tableScroll = new JScrollPane(customerTable);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());

        // ── Filter / Search Panel (Top region below header) ────────────────────
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setOpaque(false);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(12, 10, 6, 10));

        // Clean flat labels (no box emojis/box indicators)
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchLabel.setForeground(new Color(74, 85, 104));
        
        JTextField searchField = new JTextField(15);
        styleField(searchField);

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
        filterPanel.add(statusLabel);
        filterPanel.add(statusFilter);

        // ── Action Buttons Panel (Bottom / SOUTH region) ────────────────────────
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 12));
        actionPanel.setOpaque(false);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 12, 10));

        // Flat clean button text for layout elegance and zero rendering bugs
        JButton addBtn    = createBtn("Add Customer", new Color(39, 174, 96)); // Vibrant green
        JButton updateBtn = createBtn("Update",       new Color(90, 107, 124)); // Neutral grey/blue
        JButton deleteBtn  = createBtn("Delete",       new Color(192, 57, 43)); // Subtle warning red
        JButton refreshBtn = createBtn("Refresh",      new Color(100, 116, 139)); // Elegant slate grey

        addBtn.addActionListener(e -> showAddDialog());
        updateBtn.addActionListener(e -> {
            int row = customerTable.getSelectedRow();
            if (row < 0) { showNoSelection(); return; }
            int modelRow = customerTable.convertRowIndexToModel(row);
            showUpdateDialog(modelRow);
        });
        deleteBtn.addActionListener(e -> {
            int row = customerTable.getSelectedRow();
            if (row < 0) { showNoSelection(); return; }
            int modelRow = customerTable.convertRowIndexToModel(row);
            handleDelete(modelRow);
        });
        refreshBtn.addActionListener(e -> loadCustomerData());

        actionPanel.add(addBtn);
        actionPanel.add(updateBtn);
        actionPanel.add(deleteBtn);
        actionPanel.add(refreshBtn);

        // Wire search and filter events for real-time responsiveness
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                applyFilter(searchField, statusFilter, sorter);
            }
        });
        statusFilter.addActionListener(e -> applyFilter(searchField, statusFilter, sorter));

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

    private void applyFilter(JTextField searchField, JComboBox<String> statusFilter, TableRowSorter<DefaultTableModel> sorter) {
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
                
                // 2. Account Status filter
                boolean statusMatches = true;
                Object selectedStatus = statusFilter.getSelectedItem();
                if (selectedStatus != null && !"All Statuses".equals(selectedStatus)) {
                    // Account Status is index 4 in Customer columns
                    Object cellStatus = entry.getValue(4);
                    statusMatches = cellStatus != null && cellStatus.toString().equalsIgnoreCase(selectedStatus.toString());
                }
                
                return textMatches && statusMatches;
            }
        };
        sorter.setRowFilter(rf);
    }

    private void loadCustomerData() {
        tableModel.setRowCount(0);
        AccountController accountController = new AccountController();
        java.util.Optional<java.util.List<UserAccount>> listOpt = accountController.retrieveAllAccounts(Role.CUSTOMER);
        if (listOpt.isPresent()) {
            for (UserAccount customer : listOpt.get()) {
                tableModel.addRow(new Object[]{
                    customer.getAccountID(),
                    customer.getFirstName() + " " + customer.getLastName(),
                    customer.getEmailAddress(),
                    customer.getUserID(),
                    customer.getAccountStatus() != null ? customer.getAccountStatus().getAccStatus() : "PENDING"
                });
            }
        }
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog(this, "Add New Customer", true);
        dialog.setSize(540, 500); // Spacious desktop dimensions - eliminates vertical scrollbars
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
        
        JPasswordField passF = new JPasswordField(); passF.setPreferredSize(formFieldSize); styleField(passF);
        JPasswordField confF = new JPasswordField(); confF.setPreferredSize(formFieldSize); styleField(confF);

        JCheckBox showPassCheck = new JCheckBox("Show Passwords");
        showPassCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPassCheck.setOpaque(false);
        showPassCheck.setFocusPainted(false);
        char defaultEcho = passF.getEchoChar();
        showPassCheck.addActionListener(e -> {
            char echo = showPassCheck.isSelected() ? (char) 0 : defaultEcho;
            passF.setEchoChar(echo);
            confF.setEchoChar(echo);
        });

        // Build side-by-side grouped fields (two-column form grid)
        form.add(createCompactFormField("First Name:", firstF));
        form.add(createCompactFormField("Last Name:", lastF));
        form.add(createCompactFormField("User ID (IC/Passport):", userIdF));
        form.add(createCompactFormField("Email Address:", emailF));
        form.add(createCompactFormField("Phone Number:", phoneF));
        form.add(createCompactFormField("Password:", passF));
        form.add(createCompactFormField("Confirm Password:", confF));
        form.add(createCompactFormField("", showPassCheck));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 15));
        btnPanel.setBackground(new Color(241, 245, 249));
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        JButton save = createBtn("Add Customer", new Color(39, 174, 96));
        JButton cancel = createBtn("Cancel", new Color(148, 163, 184));
        
        save.addActionListener(e -> {
            AccountController accountController = new AccountController();
            UserAccountDTO userDto = new UserAccountDTO(
                userIdF.getText().trim(), Role.CUSTOMER, firstF.getText().trim(), lastF.getText().trim(),
                emailF.getText().trim(), phoneF.getText().trim(),
                new String(passF.getPassword()), new String(confF.getPassword())
            );
            OperationResult result = accountController.createAccount(userDto);
            if (result.isValid()) {
                JOptionPane.showMessageDialog(dialog, "Customer created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCustomerData();
                dialog.dispose();
            } else {
                showErrors(dialog, result);
            }
        });
        cancel.addActionListener(e -> dialog.dispose());
        
        btnPanel.add(save);
        btnPanel.add(cancel);
        
        dialog.add(new JScrollPane(form), BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showUpdateDialog(int row) {
        String accId = (String) tableModel.getValueAt(row, COL_ACC_ID);
        AccountController accountController = new AccountController();
        java.util.Optional<UserAccount> optAcc = accountController.retrieveAccountById(accId, Role.CUSTOMER);
        if (!optAcc.isPresent()) {
            JOptionPane.showMessageDialog(this, "Customer not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        UserAccount customer = optAcc.get();
        String name  = customer.getFirstName() + " " + customer.getLastName();
        String email = customer.getEmailAddress();
        String userId = customer.getUserID();
 
        JDialog dialog = new JDialog(this, "Update Customer: " + name, true);
        dialog.setSize(540, 400); // Spacious desktop dimensions - eliminates vertical scrollbars
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
 
        JPanel form = new JPanel(new GridLayout(0, 2, 16, 12));
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        form.setBackground(new Color(248, 250, 252));
 
        JTextField firstF  = new JTextField(); styleField(firstF); firstF.setText(customer.getFirstName());
        JTextField lastF   = new JTextField(); styleField(lastF); lastF.setText(customer.getLastName());
        JTextField userIdF = new JTextField(); styleField(userIdF); userIdF.setText(userId);
        JTextField emailF  = new JTextField(); styleField(emailF); emailF.setText(email);
        JTextField phoneF  = new JTextField(); styleField(phoneF); phoneF.setText(customer.getPhoneNumber());
 
        // Build side-by-side grouped fields (two-column form grid)
        form.add(createFormField("First Name:", firstF));
        form.add(createFormField("Last Name:", lastF));
        form.add(createFormField("User ID (IC/Passport):", userIdF));
        form.add(createFormField("Email Address:", emailF));
        form.add(createFormField("Phone Number:", phoneF));
        
        JPanel fillerPanel = new JPanel();
        fillerPanel.setOpaque(false);
        form.add(fillerPanel);
 
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 15));
        btnPanel.setBackground(new Color(241, 245, 249));
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        JButton save = createBtn("Save Changes", new Color(90, 107, 124));
        JButton cancel = createBtn("Cancel", new Color(148, 163, 184));
        
        save.addActionListener(e -> {
            UserAccountDTO userDto = new UserAccountDTO(
                customer.getAccountID(), Role.CUSTOMER, userIdF.getText().trim(),
                firstF.getText().trim(), lastF.getText().trim(),
                emailF.getText().trim(), phoneF.getText().trim(),
                customer.getAccountStatus()
            );
            OperationResult result = accountController.updateAccount(userDto);
            if (result.isValid()) {
                JOptionPane.showMessageDialog(dialog, "Customer updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCustomerData();
                dialog.dispose();
            } else {
                showErrors(dialog, result);
            }
        });
        cancel.addActionListener(e -> dialog.dispose());
        
        btnPanel.add(save);
        btnPanel.add(cancel);
        
        dialog.add(new JScrollPane(form), BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void handleDelete(int row) {
        String accId = (String) tableModel.getValueAt(row, COL_ACC_ID);
        String name  = (String) tableModel.getValueAt(row, COL_NAME);
        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete customer: " + name + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            AccountController accountController = new AccountController();
            OperationResult result = accountController.deleteAccount(accId, Role.CUSTOMER);
            if (result.isValid()) {
                JOptionPane.showMessageDialog(this, "Customer deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCustomerData();
            } else {
                JOptionPane.showMessageDialog(this, result.generalMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showErrors(JDialog d, OperationResult r) {
        StringBuilder sb = new StringBuilder();
        r.fieldErrors().forEach((f, msg) -> sb.append("- ").append(msg).append("\n"));
        if (sb.isEmpty()) sb.append(r.generalMessage());
        JOptionPane.showMessageDialog(d, sb.toString(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showNoSelection() { JOptionPane.showMessageDialog(this, "Please select a customer first.", "No Selection", JOptionPane.WARNING_MESSAGE); }

    @Override
    public void refreshData() {
        loadCustomerData();
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

    private JButton createBtn(String t, Color c) {
        JButton b = new JButton(t);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setBackground(c);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
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
