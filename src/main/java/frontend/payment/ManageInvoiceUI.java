package main.java.frontend.payment;

import main.java.backend.controller.AppointmentController;
import main.java.backend.controller.InvoiceController;
import main.java.backend.controller.PaymentRecordController;
import main.java.backend.model.Appointment;
import main.java.backend.model.Invoice;
import main.java.backend.model.UserAccount;
import main.java.dtos.InvoiceDTO;
import main.java.dtos.PaymentRecordDTO;
import main.java.enums.data.AppointmentStatus;
import main.java.enums.data.InvoiceStatus;
import main.java.enums.data.PaymentMethod;
import main.java.enums.data.Role;
import main.java.frontend.shared.DocumentViewerUI;
import main.java.frontend.shared.SessionManager;
import main.java.records.OperationResult;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ManageInvoiceUI — Handles Invoices for Customer, Counter Staff, and Manager.
 */
public class ManageInvoiceUI extends JFrame implements main.java.frontend.shared.Refreshable {

    private static final String[] COLUMNS = {
            "Invoice No", "Customer", "Service Details", "Subtotal (RM)", "Tax (RM)", "Add. Fee (RM)", "Total (RM)", "Status", "Note"
    };

    private final List<Invoice> invoiceList = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JTable invoiceTable;
    private final UserAccount currentUser;
    private final boolean isCustomer;

    private JButton btnGenerate;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnPay;

    public ManageInvoiceUI() {
        this.currentUser = SessionManager.getCurrentUser();
        this.isCustomer = currentUser != null && currentUser.getUserRole() == Role.CUSTOMER;
        loadInvoices();
        initUI();
    }

    @Override
    public void refreshData() {
        loadInvoices();
        refreshTable();
    }

    private void initUI() {
        setTitle("Invoices");
        setSize(950, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 0));

        // add(buildHeaderPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);

        refreshTable();
    }

    private JPanel buildHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JLabel title = new JLabel(isCustomer ? "My Invoices" : "Manage Invoices");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);

        panel.add(title, BorderLayout.WEST);
        return panel;
    }

    private JScrollPane buildTablePanel() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        invoiceTable = new JTable(tableModel);
        invoiceTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        invoiceTable.setRowHeight(28);
        invoiceTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        invoiceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Double-click an invoice row to view document-style invoice details
        invoiceTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int r = invoiceTable.getSelectedRow();
                    if (r >= 0) {
                        Invoice inv = invoiceList.get(r);
                        DocumentViewerUI.forInvoice(inv, ManageInvoiceUI.this);
                    }
                }
            }
        });

        invoiceTable.getColumnModel().getColumn(2).setPreferredWidth(200);

        JScrollPane scroll = new JScrollPane(invoiceTable);
        scroll.setBorder(new EmptyBorder(10, 14, 0, 14));
        return scroll;
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setOpaque(false);

        JButton btnView = createBtn("View Invoice", new Color(100, 116, 139));
        btnView.addActionListener(e -> {
            int row = invoiceTable.getSelectedRow();
            if (row < 0) {
                showNoSelectionWarning();
                return;
            }
            DocumentViewerUI.forInvoice(invoiceList.get(row), this);
        });
        panel.add(btnView);

        if (isCustomer) {
            btnPay = createBtn("Make Payment", new Color(16, 185, 129)); // Emerald green
            btnPay.addActionListener(e -> {
                int row = invoiceTable.getSelectedRow();
                if (row < 0) {
                    showNoSelectionWarning();
                    return;
                }
                Invoice inv = invoiceList.get(row);
                if (inv.getInvoiceStatus() == InvoiceStatus.PAID) {
                    JOptionPane.showMessageDialog(this, "Invoice has already been paid.", "Information", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                if (inv.getInvoiceStatus() == InvoiceStatus.CANCELLED) {
                    JOptionPane.showMessageDialog(this, "This invoice is cancelled.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                openPaymentDialog(inv);
            });
            panel.add(btnPay);
        } else {
            btnGenerate = createBtn("Generate Invoice", new Color(37, 99, 235)); // Blue
            btnGenerate.addActionListener(e -> openGenerateInvoiceDialog());

            btnEdit = createBtn("Edit Invoice", new Color(245, 158, 11)); // Amber
            btnEdit.addActionListener(e -> {
                int row = invoiceTable.getSelectedRow();
                if (row < 0) {
                    showNoSelectionWarning();
                    return;
                }
                Invoice inv = invoiceList.get(row);
                openEditInvoiceDialog(inv);
            });

            btnDelete = createBtn("Delete Invoice", new Color(239, 68, 68)); // Red
            btnDelete.addActionListener(e -> {
                int row = invoiceTable.getSelectedRow();
                if (row < 0) {
                    showNoSelectionWarning();
                    return;
                }
                Invoice inv = invoiceList.get(row);
                if (inv.getInvoiceStatus() == InvoiceStatus.PAID) {
                    JOptionPane.showMessageDialog(this, "Cannot delete a paid invoice.", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to permanently delete invoice " + inv.getInvoiceDocNo() + "?\n"
                                + "This will cascade delete any linked Payment Record and Receipt.\nThis action cannot be undone.",
                        "Confirm Delete Invoice", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }

                InvoiceController ctrl = new InvoiceController();
                OperationResult res = ctrl.deleteInvoice(inv.getInvoiceID());

                if (res.isValid()) {
                    JOptionPane.showMessageDialog(this, "Invoice deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(this, res.generalMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            panel.add(btnGenerate);
            panel.add(btnEdit);
            panel.add(btnDelete);
        }

        JButton btnRefresh = createBtn("Refresh", new Color(100, 116, 139));
        btnRefresh.addActionListener(e -> refreshData());
        panel.add(btnRefresh);

        return panel;
    }

    private void openGenerateInvoiceDialog() {
        JDialog dialog = new JDialog(this, "Generate New Invoice", true);
        dialog.setSize(650, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(16, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.BOTH;

        // Fetch in-progress or completed appointments that don't have an invoice yet
        AppointmentController apptCtrl = new AppointmentController();
        InvoiceController invCtrl = new InvoiceController();
        List<Appointment> eligibleAppts = new ArrayList<>();
        java.util.Optional<List<Appointment>> allApptsOpt = apptCtrl.retrieveAllAppointments();

        if (allApptsOpt.isPresent()) {
            for (Appointment appt : allApptsOpt.get()) {
                AppointmentStatus status = appt.getAppointmentStatus();
                if (status == AppointmentStatus.COMPLETED || status == AppointmentStatus.IN_PROGRESS) {
                    eligibleAppts.add(appt);
                }
            }
        }

        if (eligibleAppts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "There are no in-progress or completed appointments awaiting invoice generation.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] cols = {"Order No.", "Customer", "Title", "Date"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Appointment appt : eligibleAppts) {
            String cust = appt.getCustomerAccount() != null ? appt.getCustomerAccount().getFirstName() + " " + appt.getCustomerAccount().getLastName() : "Unknown";
            model.addRow(new Object[]{appt.getAppointmentOrderNo(), cust, appt.getAppointmentTitle(), appt.getServiceDate()});
        }
        JTable apptTable = new JTable(model);
        apptTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        apptTable.setRowHeight(25);
        JScrollPane tableScroll = new JScrollPane(apptTable);
        tableScroll.setPreferredSize(new Dimension(450, 150));

        JTextField feeField = new JTextField("0.00");
        JTextArea noteArea = new JTextArea(3, 20);
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        JScrollPane noteScroll = new JScrollPane(noteArea);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.2; gbc.weighty = 0.6;
        form.add(new JLabel("Select Appointment:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        form.add(tableScroll, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.2; gbc.weighty = 0.0;
        form.add(new JLabel("Additional Fee (RM):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        form.add(feeField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.2; gbc.weighty = 0.4;
        form.add(new JLabel("Invoice Note:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        form.add(noteScroll, gbc);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnConfirm = createBtn("Generate", new Color(37, 99, 235));
        JButton btnCancel = createBtn("Cancel", new Color(108, 117, 125));

        btnConfirm.addActionListener(e -> {
            int selectedRow = apptTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(dialog, "Please select an appointment from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Appointment selectedAppt = eligibleAppts.get(selectedRow);

            double addFee;
            try {
                addFee = Double.parseDouble(feeField.getText().trim());
                if (addFee < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid non-negative number for additional fee.", "Invalid Price", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String note = noteArea.getText().trim();
            InvoiceDTO dto = new InvoiceDTO(currentUser.getAccountID(), selectedAppt.getAppointmentID(), addFee, note);
            OperationResult result = invCtrl.createInvoice(dto);

            if (result.isValid()) {
                JOptionPane.showMessageDialog(dialog, "Invoice generated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
                dialog.dispose(); // Universal enhancement: dispose dialog on success!
            } else {
                JOptionPane.showMessageDialog(dialog, result.generalMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());
        btnRow.add(btnConfirm);
        btnRow.add(btnCancel);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void openEditInvoiceDialog(Invoice inv) {
        JDialog dialog = new JDialog(this, "Edit Invoice — " + inv.getInvoiceDocNo(), true);
        dialog.setSize(440, 340);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(16, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField feeField = new JTextField(String.format("%.2f", inv.getAdditionalFeeAmount()));
        JTextArea noteArea = new JTextArea(3, 20);
        noteArea.setText(inv.getInvoiceNote() != null ? inv.getInvoiceNote() : "");
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        JScrollPane noteScroll = new JScrollPane(noteArea);

        JComboBox<InvoiceStatus> statusCombo = new JComboBox<>(InvoiceStatus.values());
        statusCombo.setSelectedItem(inv.getInvoiceStatus());

        addFormRow(form, gbc, 0, "Additional Fee (RM):", feeField);
        addFormRow(form, gbc, 1, "Invoice Note:", noteScroll);
        addFormRow(form, gbc, 2, "Invoice Status:", statusCombo);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnSave = createBtn("Save Changes", new Color(245, 158, 11));
        JButton btnCancel = createBtn("Cancel", new Color(108, 117, 125));

        btnSave.addActionListener(e -> {
            double addFee;
            try {
                addFee = Double.parseDouble(feeField.getText().trim());
                if (addFee < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid non-negative number for additional fee.", "Invalid Price", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String note = noteArea.getText().trim();
            InvoiceStatus selectedStatus = (InvoiceStatus) statusCombo.getSelectedItem();

            boolean detailsChanged = (addFee != inv.getAdditionalFeeAmount()) ||
                    !(note.equals(inv.getInvoiceNote() == null ? "" : inv.getInvoiceNote()));
            boolean statusChanged = (selectedStatus != inv.getInvoiceStatus());

            if (!detailsChanged && !statusChanged) {
                JOptionPane.showMessageDialog(dialog, "No fields have been changed.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            InvoiceController ctrl = new InvoiceController();
            OperationResult result = null;

            if (statusChanged && selectedStatus == InvoiceStatus.UNPAID) {
                // Update status to UNPAID first
                InvoiceDTO statusDto = new InvoiceDTO(inv.getInvoiceID(), selectedStatus);
                result = ctrl.updateInvoiceStatus(statusDto);
                if (result.isValid()) {
                    if (detailsChanged) {
                        InvoiceDTO detailsDto = new InvoiceDTO(inv.getInvoiceID(), addFee, note);
                        result = ctrl.updateInvoice(detailsDto);
                    }
                }
            } else {
                // Update details first, then status
                if (detailsChanged) {
                    InvoiceDTO detailsDto = new InvoiceDTO(inv.getInvoiceID(), addFee, note);
                    result = ctrl.updateInvoice(detailsDto);
                    if (result.isValid() && statusChanged) {
                        InvoiceDTO statusDto = new InvoiceDTO(inv.getInvoiceID(), selectedStatus);
                        result = ctrl.updateInvoiceStatus(statusDto);
                    }
                } else if (statusChanged) {
                    InvoiceDTO statusDto = new InvoiceDTO(inv.getInvoiceID(), selectedStatus);
                    result = ctrl.updateInvoiceStatus(statusDto);
                }
            }

            if (result != null && result.isValid()) {
                JOptionPane.showMessageDialog(dialog, "Invoice updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
                dialog.dispose(); // Universal enhancement: dispose dialog on success!
            } else if (result != null) {
                JOptionPane.showMessageDialog(dialog, result.generalMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());
        btnRow.add(btnSave);
        btnRow.add(btnCancel);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void openPaymentDialog(Invoice inv) {
        JDialog dialog = new JDialog(this, "Make Payment", true);
        dialog.setSize(440, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(16, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblAppt = new JLabel(inv.getAppointment() != null ? inv.getAppointment().getAppointmentTitle() : "N/A");
        JLabel lblAmount = new JLabel("RM " + String.format("%.2f", inv.getTotalAmount()));
        lblAmount.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblAmount.setForeground(new Color(16, 185, 129));

        JComboBox<PaymentMethod> methodCombo = new JComboBox<>(PaymentMethod.values());

        JTextArea noteArea = new JTextArea(2, 20);
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        JScrollPane noteScroll = new JScrollPane(noteArea);

        addFormRow(form, gbc, 0, "Appointment:", lblAppt);
        addFormRow(form, gbc, 1, "Amount Due:", lblAmount);
        addFormRow(form, gbc, 2, "Payment Method:", methodCombo);
        addFormRow(form, gbc, 3, "Payment Note:", noteScroll);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnConfirm = createBtn("Confirm Payment", new Color(16, 185, 129));
        JButton btnCancel = createBtn("Cancel", new Color(108, 117, 125));

        btnConfirm.addActionListener(e -> {
            PaymentMethod method = (PaymentMethod) methodCombo.getSelectedItem();
            String note = noteArea.getText().trim();

            PaymentRecordController payCtrl = new PaymentRecordController();
            PaymentRecordDTO dto = new PaymentRecordDTO(inv.getInvoiceID(), currentUser.getAccountID(), inv.getTotalAmount(), method, note);
            OperationResult result = payCtrl.createPaymentRecord(dto);

            if (result.isValid()) {
                JOptionPane.showMessageDialog(dialog, "Payment submitted successfully! Your invoice is now PAID.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
                dialog.dispose(); // Universal enhancement: dispose dialog on success!
            } else {
                JOptionPane.showMessageDialog(dialog, result.generalMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());
        btnRow.add(btnConfirm);
        btnRow.add(btnCancel);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void loadInvoices() {
        invoiceList.clear();
        InvoiceController ctrl = new InvoiceController();
        java.util.Optional<List<Invoice>> listOpt = ctrl.retrieveAllInvoices();

        if (listOpt.isPresent()) {
            if (isCustomer) {
                // Filter by customer ID
                for (Invoice inv : listOpt.get()) {
                    if (inv.getAppointment() != null &&
                            inv.getAppointment().getCustomerAccount() != null &&
                            inv.getAppointment().getCustomerAccount().getAccountID().equals(currentUser.getAccountID())) {
                        invoiceList.add(inv);
                    }
                }
            } else {
                invoiceList.addAll(listOpt.get());
            }
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Invoice inv : invoiceList) {
            String customerName = "N/A";
            String serviceTitle = "N/A";
            if (inv.getAppointment() != null) {
                if (inv.getAppointment().getCustomerAccount() != null) {
                    customerName = inv.getAppointment().getCustomerAccount().getFirstName() + " " + inv.getAppointment().getCustomerAccount().getLastName();
                }
                serviceTitle = inv.getAppointment().getAppointmentTitle();
            }
            tableModel.addRow(new Object[]{
                    inv.getInvoiceDocNo(),
                    customerName,
                    serviceTitle,
                    String.format("%.2f", inv.getSubtotalAmount()),
                    String.format("%.2f", inv.getServiceTaxAmount()),
                    String.format("%.2f", inv.getAdditionalFeeAmount()),
                    String.format("%.2f", inv.getTotalAmount()),
                    inv.getInvoiceStatus().getInvoiceStatus(),
                    inv.getInvoiceNote() != null ? inv.getInvoiceNote() : ""
            });
        }
    }

    private void showNoSelectionWarning() {
        JOptionPane.showMessageDialog(this, "Please select an invoice first.", "No Selection", JOptionPane.WARNING_MESSAGE);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.25;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.75;
        panel.add(field, gbc);
    }

    private JButton createBtn(String text, Color color) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setBackground(color);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}
