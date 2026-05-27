package main.java.frontend.history;
 
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import main.java.backend.controller.AppointmentFeedbackController;
import main.java.backend.controller.PaymentRecordController;
import main.java.backend.model.AppointmentFeedback;
import main.java.backend.model.PaymentRecord;
import main.java.backend.model.UserAccount;
import main.java.frontend.shared.SessionManager;
 
/**
 * CustomerHistoryUI — Customer
 *
 * Displays two read-only panels for the logged-in customer:
 *   1. Feedbacks written by staff for their appointments
 *   2. Payment history for their completed appointments
 */
public class CustomerHistoryUI extends JFrame {
 
    private static final String[] FEEDBACK_COLUMNS = {
            "Appointment", "Author", "Feedback", "Date"
    };
 
    private static final String[] PAYMENT_COLUMNS = {
            "Payment ID", "Appointment", "Amount (RM)", "Method", "Date"
    };
 
    private List<AppointmentFeedback> feedbackList = new ArrayList<>();
    private List<PaymentRecord> paymentList = new ArrayList<>();
 
    private DefaultTableModel feedbackTableModel;
    private DefaultTableModel paymentTableModel;
    private JTable            feedbackTable;
    private JTable            paymentTable;
    private JLabel            totalLabel;
 
    public CustomerHistoryUI() {
        loadHistoryData();
        initUI();
    }
 
    private void initUI() {
        setTitle("My Appointment History — Customer");
        setSize(900, 680);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 0));
 
        add(buildHeaderPanel(), BorderLayout.NORTH);
 
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                buildFeedbackPanel(), buildPaymentPanel());
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(new EmptyBorder(10, 14, 14, 14));
 
        add(splitPane, BorderLayout.CENTER);
 
        refreshFeedbackTable();
        refreshPaymentTable();
        
        JPanel buttonPanel = buildButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
 
    private JPanel buildHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JLabel title = new JLabel("My Appointment History");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
 
        panel.add(title, BorderLayout.WEST);
        return panel;
    }
 
    private JPanel buildFeedbackPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 120, 200), 1),
                "Staff Feedbacks on My Appointments",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13), new Color(52, 80, 180)
        ));
 
        feedbackTableModel = new DefaultTableModel(FEEDBACK_COLUMNS, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
 
        feedbackTable = new JTable(feedbackTableModel);
        feedbackTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        feedbackTable.setRowHeight(26);
        feedbackTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        feedbackTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
 
        feedbackTable.getColumnModel().getColumn(2).setPreferredWidth(300);
 
        panel.add(new JScrollPane(feedbackTable), BorderLayout.CENTER);
        return panel;
    }
 
    private JPanel buildPaymentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(50, 160, 100), 1),
                "Payment History",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13), new Color(30, 130, 80)
        ));
 
        paymentTableModel = new DefaultTableModel(PAYMENT_COLUMNS, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
 
        paymentTable = new JTable(paymentTableModel);
        paymentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        paymentTable.setRowHeight(26);
        paymentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        paymentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
 
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
 
        JButton btnFeedbackDetails = createBtn("👁 View Feedback Details", new Color(142, 68, 173));
        JButton btnPaymentDetails  = createBtn("👁 View Payment Details", new Color(41, 128, 185));
 
        btnFeedbackDetails.addActionListener(e -> {
            int row = feedbackTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a feedback row first.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            showFeedbackDetails(row);
        });
 
        btnPaymentDetails.addActionListener(e -> {
            int row = paymentTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a payment row first.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            showPaymentDetails(row);
        });
 
        panel.add(btnFeedbackDetails);
        panel.add(btnPaymentDetails);
        return panel;
    }
 
    private void showFeedbackDetails(int row) {
        AppointmentFeedback fb = feedbackList.get(row);
 
        JDialog dialog = new JDialog(this, "Feedback Details", true);
        dialog.setSize(485, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
 
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
 
        addDetailRow(form, gbc, 0, "Appointment:", fb.getAppointment() != null ? fb.getAppointment().getAppointmentTitle() : "N/A");
        addDetailRow(form, gbc, 1, "Staff Member:", fb.getFeedbackAuthor() != null ? fb.getFeedbackAuthor().getFirstName() + " " + fb.getFeedbackAuthor().getLastName() : "N/A");
        addDetailRow(form, gbc, 2, "Written Date:", fb.getFeedbackCreationTime().toLocalDate().toString());
 
        JTextArea contentArea = new JTextArea(fb.getFeedbackContent(), 4, 25);
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setBackground(new Color(245, 245, 245));
        contentArea.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        JScrollPane scroll = new JScrollPane(contentArea);
 
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.25;
        form.add(new JLabel("Feedback:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.75;
        form.add(scroll, gbc);
 
        JButton closeBtn = createBtn("Close", new Color(108, 117, 125));
        closeBtn.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.add(closeBtn);
 
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
 
    private void showPaymentDetails(int row) {
        PaymentRecord p = paymentList.get(row);
 
        JDialog dialog = new JDialog(this, "Payment Details & Receipt", true);
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
 
    private void addDetailRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.25; gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(100, 110, 120));
        panel.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 0.75; gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel val = new JLabel("<html>" + value + "</html>");
        val.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        val.setForeground(new Color(30, 40, 50));
        panel.add(val, gbc);
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
        feedbackList.clear();
        AppointmentFeedbackController fbCtrl = new AppointmentFeedbackController();
        java.util.Optional<List<AppointmentFeedback>> fbsOpt = fbCtrl.retrieveAllApptFeedback();
        UserAccount currentUser = SessionManager.getCurrentUser();
        if (fbsOpt.isPresent() && currentUser != null) {
            for (AppointmentFeedback fb : fbsOpt.get()) {
                if (fb.getAppointment() != null && 
                    fb.getAppointment().getCustomerAccount() != null && 
                    fb.getAppointment().getCustomerAccount().getAccountID().equals(currentUser.getAccountID())) {
                    feedbackList.add(fb);
                }
            }
        }
 
        paymentList.clear();
        PaymentRecordController payCtrl = new PaymentRecordController();
        java.util.Optional<List<PaymentRecord>> paysOpt = payCtrl.retrieveAllPaymentRecords();
        if (paysOpt.isPresent() && currentUser != null) {
            for (PaymentRecord pay : paysOpt.get()) {
                if (pay.getPayerAccount() != null && pay.getPayerAccount().getAccountID().equals(currentUser.getAccountID())) {
                    paymentList.add(pay);
                }
            }
        }
    }
 
    private void refreshFeedbackTable() {
        feedbackTableModel.setRowCount(0);
        for (AppointmentFeedback fb : feedbackList) {
            feedbackTableModel.addRow(new Object[]{
                fb.getAppointment() != null ? fb.getAppointment().getAppointmentTitle() : "N/A",
                fb.getFeedbackAuthor() != null ? fb.getFeedbackAuthor().getFirstName() + " " + fb.getFeedbackAuthor().getLastName() : "N/A",
                fb.getFeedbackContent(),
                fb.getFeedbackCreationTime().toLocalDate().toString()
            });
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
