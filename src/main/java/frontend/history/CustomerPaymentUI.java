package main.java.frontend.history;
 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
 
import main.java.backend.controller.PaymentRecordController;
import main.java.backend.model.PaymentRecord;
import main.java.backend.model.UserAccount;
import main.java.frontend.shared.SessionManager;
 
public class CustomerPaymentUI extends JFrame implements main.java.frontend.shared.Refreshable {
 
    private static final String[] PAYMENT_COLUMNS = {
            "Payment ID", "Appointment", "Amount (RM)", "Method", "Date"
    };
 
    private List<PaymentRecord> paymentList = new ArrayList<>();
    private DefaultTableModel paymentTableModel;
    private JTable            paymentTable;
    private JLabel            totalLabel;
 
    public CustomerPaymentUI() {
        loadHistoryData();
        initUI();
    }

    @Override
    public void refreshData() {
        loadHistoryData();
        refreshPaymentTable();
    }
 
    private void initUI() {
        setLayout(new BorderLayout(0, 0));
        add(buildPaymentPanel(), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);
        refreshPaymentTable();
    }
 
    private JPanel buildPaymentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 0, 14, 0));
 
        paymentTableModel = new DefaultTableModel(PAYMENT_COLUMNS, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
 
        paymentTable = new JTable(paymentTableModel);
        paymentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        paymentTable.setRowHeight(26);
        paymentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        paymentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
 
        // Double-click a payment row to view details
        paymentTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int r = paymentTable.getSelectedRow();
                    if (r >= 0) {
                        showPaymentDetails(r);
                    }
                }
            }
        });

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalLabel = new JLabel("Total Paid: RM 0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalLabel.setForeground(new Color(30, 130, 80));
        footer.add(totalLabel);
 
        panel.add(new JScrollPane(paymentTable), BorderLayout.CENTER);
        panel.add(footer, BorderLayout.SOUTH);
        return panel;
    }
 
    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setOpaque(false);
  
        JButton btnPaymentDetails  = createBtn("View Payment Details", new Color(41, 128, 185));
        JButton refreshBtn         = createBtn("Refresh",              new Color(100, 116, 139));
  
        btnPaymentDetails.addActionListener(e -> {
            int row = paymentTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a payment row first.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            showPaymentDetails(row);
        });

        refreshBtn.addActionListener(e -> refreshData());
  
        panel.add(btnPaymentDetails);
        panel.add(refreshBtn);
        return panel;
    }
 
    private void showPaymentDetails(int row) {
        PaymentRecord p = paymentList.get(row);
 
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentWindow, "Payment Details & Receipt", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(420, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
 
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
 
        form.add(new JLabel("Payment ID:")); form.add(new JLabel(p.getPaymentRecordID()));
        form.add(new JLabel("Appointment:")); form.add(new JLabel(p.getInvoice() != null && p.getInvoice().getAppointment() != null ? p.getInvoice().getAppointment().getAppointmentTitle() : "N/A"));
        form.add(new JLabel("Amount Paid:")); form.add(new JLabel("RM " + String.format("%.2f", p.getPaymentAmount())));
        form.add(new JLabel("Payment Method:")); form.add(new JLabel(p.getPaymentMethod().getPaymentMethod()));
        form.add(new JLabel("Transaction Date:")); form.add(new JLabel(p.getPaymentDate().toString()));
        form.add(new JLabel("Receipt Status:")); form.add(new JLabel(p.getPaymentStatus().getPaymentStatus()));
 
        JButton closeBtn = createBtn("Close", new Color(108, 117, 125));
        closeBtn.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.add(closeBtn);
 
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
 
    private JButton createBtn(String text, Color color) {
        JButton b = new JButton(text); 
        b.setFont(new Font("Segoe UI", Font.BOLD, 13)); 
        b.setForeground(Color.WHITE);
        b.setBackground(color); 
        b.setFocusPainted(false); 
        b.setBorder(BorderFactory.createEmptyBorder(7,14,7,14)); 
        b.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
        return b;
    }
 
    private void loadHistoryData() {
        paymentList.clear();
        PaymentRecordController payCtrl = new PaymentRecordController();
        java.util.Optional<List<PaymentRecord>> paysOpt = payCtrl.retrieveAllPaymentRecords();
        UserAccount currentUser = SessionManager.getCurrentUser();
        if (paysOpt.isPresent() && currentUser != null) {
            for (PaymentRecord pay : paysOpt.get()) {
                if (pay.getPayerAccount() != null && pay.getPayerAccount().getAccountID().equals(currentUser.getAccountID())) {
                    paymentList.add(pay);
                }
            }
        }
    }
 
    private void refreshPaymentTable() {
        paymentTableModel.setRowCount(0);
        double total = 0.0;
        for (PaymentRecord p : paymentList) {
            paymentTableModel.addRow(new Object[]{
                p.getPaymentRecordID(),
                p.getInvoice() != null && p.getInvoice().getAppointment() != null ? p.getInvoice().getAppointment().getAppointmentTitle() : "N/A",
                String.format("%.2f", p.getPaymentAmount()),
                p.getPaymentMethod().getPaymentMethod(),
                p.getPaymentDate().toString()
            });
            total += p.getPaymentAmount();
        }
        if (totalLabel != null) {
            totalLabel.setText(String.format("Total Paid: RM %.2f", total));
        }
    }
}
