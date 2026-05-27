package main.java.frontend.account;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import main.java.backend.controller.AccountController;
import main.java.backend.model.StaffAccount;
import main.java.backend.model.UserAccount;
import main.java.dtos.StaffAccountDTO;
import main.java.dtos.UserAccountDTO;
import main.java.enums.data.AccountStatus;
import main.java.enums.data.Role;
import main.java.records.OperationResult;

public class UpdateAccStatusUI extends JFrame implements main.java.frontend.shared.Refreshable {

    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusFilterBox;
    private JComboBox<AccountStatus> newStatusBox;
    private List<UserAccount> allLoadedAccounts = new ArrayList<>();

    // Column Indices
    private static final int COL_ACC_ID   = 0;
    private static final int COL_USER_ID  = 1;
    private static final int COL_NAME     = 2;
    private static final int COL_TYPE     = 3;
    private static final int COL_EMAIL    = 4;
    private static final int COL_STATUS   = 5;

    public UpdateAccStatusUI() {
        setTitle("Account Approval & Status Manager - Auttomov Software");
        setSize(780, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 244, 248));

        // ── Header Panel (Title Card) ──────────────────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        JLabel title = new JLabel("Account Approval & Status Manager");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        headerPanel.add(title, BorderLayout.CENTER);

        // ── Top Control Panel (North, below header) ─────────────────────────────
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        topPanel.setOpaque(false);
        
        JLabel reviewLabel = new JLabel("Select Status to Review:");
        reviewLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        reviewLabel.setForeground(new Color(51, 65, 85)); // Slate-700
        
        statusFilterBox = new JComboBox<>(new String[]{"ALL", "ACTIVE", "INACTIVE", "PENDING", "DENIED", "SUSPENDED"});
        styleComboBox(statusFilterBox);
        
        topPanel.add(reviewLabel);
        topPanel.add(statusFilterBox);

        JPanel northContainer = new JPanel();
        northContainer.setLayout(new BoxLayout(northContainer, BoxLayout.Y_AXIS));
        northContainer.setOpaque(false);
        northContainer.add(headerPanel);
        northContainer.add(topPanel);

        // ── Center Display Panel (Table) ─────────────────────────────────────────
        String[] cols = {"Account ID", "User ID", "Full Name", "Account Type", "Email", "Current Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        
        loadAllAccountsData();

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(28); // Premium row height
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Prevent row cutting-off & cleanly fill scrolling viewport area
        table.setPreferredScrollableViewportSize(new Dimension(750, 280));
        table.setFillsViewportHeight(true);

        // Completely hide the Account ID column from view (preserves Model access)
        TableColumn accIdColumn = table.getColumnModel().getColumn(COL_ACC_ID);
        table.removeColumn(accIdColumn);

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
        table.getTableHeader().setDefaultRenderer(headerRenderer);

        // Apply a custom cell renderer to give left padding and prevent clashing with gridlines
        PaddingCellRenderer paddingRenderer = new PaddingCellRenderer();
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(paddingRenderer);
        }

        // Set Proportional Preferred Column Widths safely
        table.getColumnModel().getColumn(0).setPreferredWidth(120); // User ID
        table.getColumnModel().getColumn(1).setPreferredWidth(160); // Full Name
        table.getColumnModel().getColumn(2).setPreferredWidth(120); // Account Type
        table.getColumnModel().getColumn(3).setPreferredWidth(200); // Email
        table.getColumnModel().getColumn(4).setPreferredWidth(120); // Current Status

        // Set up the local TableRowSorter for real-time sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        // ── Bottom Action Panel (South) ─────────────────────────────────────────
        // Redesigned to be a single, uniform centered horizontal flow row panel for perfect balance
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 12));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));
        
        JLabel updatePromptLabel = new JLabel("Update Action for Selected Row: Set New Status:");
        updatePromptLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        updatePromptLabel.setForeground(new Color(51, 65, 85)); // Slate-700
        
        newStatusBox = new JComboBox<>(AccountStatus.values());
        styleComboBox(newStatusBox);
        
        JButton applyBtn = createBtn("Apply Status Change", new Color(39, 174, 96)); // Vibrant green

        bottomPanel.add(updatePromptLabel);
        bottomPanel.add(newStatusBox);
        bottomPanel.add(applyBtn);

        // ── Wire Listeners ──────────────────────────────────────────────────────
        // Dropdown status filter event
        statusFilterBox.addActionListener(e -> {
            String selected = (String) statusFilterBox.getSelectedItem();
            populateTable(selected);
        });

        // Apply Status Change Button Action
        applyBtn.addActionListener(e -> {
            int selectedVisualRow = table.getSelectedRow();
            if (selectedVisualRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select an account from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // CRITICAL: Convert visual index to model index to prevent data-integrity bugs under sorting/filtering
            int modelRow = table.convertRowIndexToModel(selectedVisualRow);
            String accId = (String) tableModel.getValueAt(modelRow, COL_ACC_ID);

            // Find the account object from loaded accounts list
            UserAccount targetAcc = null;
            for (UserAccount acc : allLoadedAccounts) {
                if (acc.getAccountID().equals(accId)) {
                    targetAcc = acc;
                    break;
                }
            }

            if (targetAcc == null) {
                JOptionPane.showMessageDialog(this, "Target account not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            AccountStatus newStatus = (AccountStatus) newStatusBox.getSelectedItem();
            AccountController accountController = new AccountController();
            OperationResult result;

            if (targetAcc instanceof StaffAccount staffAcc) {
                result = accountController.updateStaffAccountStatus(new StaffAccountDTO(staffAcc.getAccountID(), newStatus));
            } else {
                UserAccountDTO userDto = new UserAccountDTO(
                    targetAcc.getAccountID(), targetAcc.getUserRole(), targetAcc.getUserID(),
                    targetAcc.getFirstName(), targetAcc.getLastName(),
                    targetAcc.getEmailAddress(), targetAcc.getPhoneNumber(), newStatus
                );
                result = accountController.updateAccount(userDto);
            }

            if (result.isValid()) {
                targetAcc.setAccountStatus(newStatus);
                JOptionPane.showMessageDialog(this, "Account status updated to: " + newStatus.getAccStatus(), "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Repopulate table with current filter to reflect the update immediately
                String currentFilter = (String) statusFilterBox.getSelectedItem();
                populateTable(currentFilter);
            } else {
                JOptionPane.showMessageDialog(this, result.generalMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(northContainer, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Initial table load
        populateTable("ALL");
    }

    private void loadAllAccountsData() {
        allLoadedAccounts.clear();
        AccountController accountController = new AccountController();
        
        // Retrieve Staff Accounts
        java.util.Optional<java.util.List<UserAccount>> staffOpt = accountController.retrieveAllAccounts(Role.STAFF);
        staffOpt.ifPresent(allLoadedAccounts::addAll);
        
        // Retrieve Customer Accounts
        java.util.Optional<java.util.List<UserAccount>> custOpt = accountController.retrieveAllAccounts(Role.CUSTOMER);
        custOpt.ifPresent(allLoadedAccounts::addAll);
    }

    private void populateTable(String statusFilter) {
        tableModel.setRowCount(0);
        // Refresh account list from database to ensure fresh status reads
        loadAllAccountsData();

        for (UserAccount acc : allLoadedAccounts) {
            String status = acc.getAccountStatus() != null ? acc.getAccountStatus().name() : "PENDING";
            
            // Check status filter
            if (!"ALL".equalsIgnoreCase(statusFilter) && !status.equalsIgnoreCase(statusFilter)) {
                continue;
            }
            
            String accountType = (acc instanceof StaffAccount staff) ? staff.getJobPosition().name() : "CUSTOMER";
            String fullName = acc.getFirstName() + " " + acc.getLastName();
            
            tableModel.addRow(new Object[]{
                acc.getAccountID(),
                acc.getUserID(),
                fullName,
                accountType,
                acc.getEmailAddress(),
                acc.getAccountStatus() != null ? acc.getAccountStatus().getAccStatus() : "PENDING"
            });
        }
    }

    @Override
    public void refreshData() {
        String currentFilter = (String) statusFilterBox.getSelectedItem();
        populateTable(currentFilter);
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
