package main.java.frontend.payment;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import main.java.backend.controller.PaymentRecordController;
import main.java.backend.controller.ReceiptController;
import main.java.backend.model.PaymentRecord;
import main.java.backend.model.Receipt;
import main.java.backend.model.UserAccount;
import main.java.dtos.ReceiptDTO;
import main.java.enums.data.Role;
import main.java.frontend.shared.DocumentViewerUI;
import main.java.frontend.shared.SessionManager;
import main.java.records.OperationResult;

/**
 * ManagePaymentReceiptUI — Handles Payment Records & Receipts for Customer, Staff, and Manager.
 */
public class ManagePaymentReceiptUI extends JFrame implements main.java.frontend.shared.Refreshable {

    private static final String[] PAYMENT_COLUMNS = {
        "Payment No", "Customer", "Amount (RM)", "Method", "Date", "Note"
    };

    private static final String[] RECEIPT_COLUMNS = {
        "Receipt No", "Payment No", "Customer", "Amount (RM)", "Issued By", "Date", "Note"
    };

    private final List<PaymentRecord> paymentList = new ArrayList<>();
    private final List<Receipt> receiptList = new ArrayList<>();

    private DefaultTableModel paymentModel;
    private DefaultTableModel receiptModel;

    private JTable paymentTable;
    private JTable receiptTable;

    private JTabbedPane tabbedPane;

    private final UserAccount currentUser;
    private final boolean isCustomer;

    public ManagePaymentReceiptUI() {
        this.currentUser = SessionManager.getCurrentUser();
        this.isCustomer = currentUser != null && currentUser.getUserRole() == Role.CUSTOMER;
        loadData();
        initUI();
    }

    @Override
    public void refreshData() {
        loadData();
        refreshPaymentTable();
        refreshReceiptTable();
    }

    private void initUI() {
        setTitle("Payments & Receipts");
        setSize(950, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 0));

        // add(buildHeaderPanel(), BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabbedPane.addTab("Payment Records", buildPaymentTabPanel());
        tabbedPane.addTab("Official Receipts", buildReceiptTabPanel());

        add(tabbedPane, BorderLayout.CENTER);
        refreshData();
    }

    private JPanel buildHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 25, 10, 25));

        JLabel title = new JLabel(isCustomer ? "My Payments & Receipts" : "Payments & Receipts Ledger");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);

        panel.add(title, BorderLayout.WEST);
        return panel;
    }

    private JPanel buildPaymentTabPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setOpaque(false);

        paymentModel = new DefaultTableModel(PAYMENT_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        paymentTable = new JTable(paymentModel);
        paymentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        paymentTable.setRowHeight(28);
        paymentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        paymentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Double click showing details
        paymentTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int r = paymentTable.getSelectedRow();
                    if (r >= 0) {
                        showPaymentDetails(paymentList.get(r));
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(paymentTable);
        scroll.setBorder(new EmptyBorder(10, 14, 0, 14));
        panel.add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        btnPanel.setOpaque(false);

        JButton btnView = createBtn("View Details", new Color(100, 116, 139));
        btnView.addActionListener(e -> {
            int row = paymentTable.getSelectedRow();
            if (row < 0) {
                showNoSelectionWarning();
                return;
            }
            showPaymentDetails(paymentList.get(row));
        });
        btnPanel.add(btnView);

        // If staff/manager, add "Generate Receipt" button here too for convenience!
        if (!isCustomer) {
            JButton btnGenReceipt = createBtn("Generate Receipt", new Color(37, 99, 235));
            btnGenReceipt.addActionListener(e -> {
                int row = paymentTable.getSelectedRow();
                if (row < 0) {
                    showNoSelectionWarning();
                    return;
                }
                handleGenerateReceipt(paymentList.get(row));
            });
            btnPanel.add(btnGenReceipt);
        }

        JButton btnRefresh = createBtn("Refresh", new Color(100, 116, 139));
        btnRefresh.addActionListener(e -> refreshData());
        btnPanel.add(btnRefresh);

        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildReceiptTabPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setOpaque(false);

        receiptModel = new DefaultTableModel(RECEIPT_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        receiptTable = new JTable(receiptModel);
        receiptTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        receiptTable.setRowHeight(28);
        receiptTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        receiptTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Double click showing receipt doc
        receiptTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int r = receiptTable.getSelectedRow();
                    if (r >= 0) {
                        DocumentViewerUI.forReceipt(receiptList.get(r), ManagePaymentReceiptUI.this);
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(receiptTable);
        scroll.setBorder(new EmptyBorder(10, 14, 0, 14));
        panel.add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        btnPanel.setOpaque(false);

        JButton btnView = createBtn("View Receipt", new Color(100, 116, 139));
        btnView.addActionListener(e -> {
            int row = receiptTable.getSelectedRow();
            if (row < 0) {
                showNoSelectionWarning();
                return;
            }
            DocumentViewerUI.forReceipt(receiptList.get(row), this);
        });
        btnPanel.add(btnView);



        JButton btnRefresh = createBtn("Refresh", new Color(100, 116, 139));
        btnRefresh.addActionListener(e -> refreshData());
        btnPanel.add(btnRefresh);

        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void handleGenerateReceipt(PaymentRecord pr) {
        ReceiptController receiptCtrl = new ReceiptController();
        java.util.Optional<Receipt> optRec = receiptCtrl.retrieveReceiptByPaymentRecordId(pr.getPaymentRecordID());

        if (optRec.isPresent()) {
            JOptionPane.showMessageDialog(this, "A receipt already exists for this payment record:\n" + optRec.get().getReceiptDocNo(), "Receipt Exists", JOptionPane.INFORMATION_MESSAGE);
            DocumentViewerUI.forReceipt(optRec.get(), this);
            return;
        }

        // Prompt for receipt note using JTextArea
        JTextArea noteArea = new JTextArea(4, 30);
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        JScrollPane scrollNote = new JScrollPane(noteArea);
        int confirm = JOptionPane.showConfirmDialog(this, new Object[]{"Enter a note for this official receipt (optional):", scrollNote}, "Generate Receipt", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (confirm != JOptionPane.OK_OPTION) return; // user cancelled
        String note = noteArea.getText();

        ReceiptDTO rDto = new ReceiptDTO(currentUser.getAccountID(), pr.getInvoice().getInvoiceID(), pr.getPaymentRecordID(), note);
        OperationResult result = receiptCtrl.createReceipt(rDto);

        if (result.isValid()) {
            JOptionPane.showMessageDialog(this, "Receipt generated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
            // Automatically view the newly generated receipt
            java.util.Optional<Receipt> newRecOpt = receiptCtrl.retrieveReceiptByPaymentRecordId(pr.getPaymentRecordID());
            if (newRecOpt.isPresent()) {
                DocumentViewerUI.forReceipt(newRecOpt.get(), this);
            }
        } else {
            JOptionPane.showMessageDialog(this, result.generalMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    private void showPaymentDetails(PaymentRecord pr) {
        JDialog dialog = new JDialog(this, "Payment Record Details — " + pr.getPaymentRecordNo(), true);
        dialog.setSize(440, 360);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        form.add(new JLabel("Payment Record ID:")); form.add(new JLabel(pr.getPaymentRecordID()));
        form.add(new JLabel("Payment Record No:")); form.add(new JLabel(pr.getPaymentRecordNo()));
        form.add(new JLabel("Payment Date:")); form.add(new JLabel(pr.getPaymentDate().toString()));

        String customerName = pr.getPayerAccount() != null ? 
                pr.getPayerAccount().getFirstName() + " " + pr.getPayerAccount().getLastName() : "N/A";
        form.add(new JLabel("Customer:")); form.add(new JLabel(customerName));

        form.add(new JLabel("Amount Paid:")); 
        JLabel lblAmount = new JLabel("RM " + String.format("%.2f", pr.getPaymentAmount()));
        lblAmount.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblAmount.setForeground(new Color(16, 185, 129));
        form.add(lblAmount);

        form.add(new JLabel("Payment Method:")); form.add(new JLabel(pr.getPaymentMethod().getPaymentMethod()));
        form.add(new JLabel("Payment Status:")); form.add(new JLabel(pr.getPaymentStatus().getPaymentStatus()));
        form.add(new JLabel("Linked Invoice:")); form.add(new JLabel(pr.getInvoice() != null ? pr.getInvoice().getInvoiceDocNo() : "N/A"));
        form.add(new JLabel("Payment Note:")); form.add(new JLabel(pr.getPaymentRecordNote() != null ? pr.getPaymentRecordNote() : ""));

        JButton closeBtn = createBtn("Close", new Color(108, 117, 125));
        closeBtn.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.add(closeBtn);

        dialog.add(new JScrollPane(form), BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void loadData() {
        paymentList.clear();
        receiptList.clear();

        PaymentRecordController payCtrl = new PaymentRecordController();
        ReceiptController recCtrl = new ReceiptController();

        java.util.Optional<List<PaymentRecord>> payOpt = payCtrl.retrieveAllPaymentRecords();
        java.util.Optional<List<Receipt>> recOpt = recCtrl.retrieveAllReceipts();

        if (payOpt.isPresent()) {
            if (isCustomer) {
                for (PaymentRecord pr : payOpt.get()) {
                    if (pr.getPayerAccount() != null && pr.getPayerAccount().getAccountID().equals(currentUser.getAccountID())) {
                        paymentList.add(pr);
                    }
                }
            } else {
                paymentList.addAll(payOpt.get());
            }
        }

        if (recOpt.isPresent()) {
            if (isCustomer) {
                for (Receipt r : recOpt.get()) {
                    if (r.getPaymentRecord() != null &&
                        r.getPaymentRecord().getPayerAccount() != null &&
                        r.getPaymentRecord().getPayerAccount().getAccountID().equals(currentUser.getAccountID())) {
                        receiptList.add(r);
                    }
                }
            } else {
                receiptList.addAll(recOpt.get());
            }
        }
    }

    private void refreshPaymentTable() {
        paymentModel.setRowCount(0);
        for (PaymentRecord pr : paymentList) {
            String customerName = pr.getPayerAccount() != null ? 
                    pr.getPayerAccount().getFirstName() + " " + pr.getPayerAccount().getLastName() : "N/A";
            paymentModel.addRow(new Object[]{
                pr.getPaymentRecordNo(),
                customerName,
                String.format("%.2f", pr.getPaymentAmount()),
                pr.getPaymentMethod().getPaymentMethod(),
                pr.getPaymentDate().toString(),
                pr.getPaymentRecordNote() != null ? pr.getPaymentRecordNote() : ""
            });
        }
    }

    private void refreshReceiptTable() {
        receiptModel.setRowCount(0);
        for (Receipt r : receiptList) {
            String customerName = "N/A";
            double amount = 0.0;
            String payNo = "N/A";

            if (r.getPaymentRecord() != null) {
                payNo = r.getPaymentRecord().getPaymentRecordNo();
                amount = r.getPaymentRecord().getPaymentAmount();
                if (r.getPaymentRecord().getPayerAccount() != null) {
                    customerName = r.getPaymentRecord().getPayerAccount().getFirstName() + " " + r.getPaymentRecord().getPayerAccount().getLastName();
                }
            }

            String issuerName = r.getReceiptIssuer() != null ? 
                    r.getReceiptIssuer().getFirstName() + " " + r.getReceiptIssuer().getLastName() : "N/A";

            receiptModel.addRow(new Object[]{
                r.getReceiptDocNo(),
                payNo,
                customerName,
                String.format("%.2f", amount),
                issuerName,
                r.getReceiptCreationDate().toString(),
                r.getReceiptNote() != null ? r.getReceiptNote() : ""
            });
        }
    }

    private void showNoSelectionWarning() {
        JOptionPane.showMessageDialog(this, "Please select an item first.", "No Selection", JOptionPane.WARNING_MESSAGE);
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
