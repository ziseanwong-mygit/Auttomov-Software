package main.java.frontend.history;
 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
 
import main.java.backend.controller.AppointmentFeedbackController;
import main.java.backend.model.AppointmentFeedback;
import main.java.backend.model.UserAccount;
import main.java.frontend.shared.SessionManager;
 
public class CustomerFeedbackUI extends JFrame implements main.java.frontend.shared.Refreshable {
 
    private static final String[] FEEDBACK_COLUMNS = {
            "Appointment", "Author", "Feedback", "Date"
    };
 
    private List<AppointmentFeedback> feedbackList = new ArrayList<>();
    private DefaultTableModel feedbackTableModel;
    private JTable            feedbackTable;
 
    public CustomerFeedbackUI() {
        loadHistoryData();
        initUI();
    }

    @Override
    public void refreshData() {
        loadHistoryData();
        refreshFeedbackTable();
    }
 
    private void initUI() {
        setLayout(new BorderLayout(0, 0));
        add(buildFeedbackPanel(), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);
        refreshFeedbackTable();
    }
 
    private JPanel buildFeedbackPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 0, 14, 0));
 
        feedbackTableModel = new DefaultTableModel(FEEDBACK_COLUMNS, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
 
        feedbackTable = new JTable(feedbackTableModel);
        feedbackTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        feedbackTable.setRowHeight(26);
        feedbackTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        feedbackTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Double click to view details
        feedbackTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int r = feedbackTable.getSelectedRow();
                    if (r >= 0) {
                        showFeedbackDetails(r);
                    }
                }
            }
        });

        feedbackTable.getColumnModel().getColumn(2).setPreferredWidth(300);
 
        panel.add(new JScrollPane(feedbackTable), BorderLayout.CENTER);
        return panel;
    }
 
    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setOpaque(false);
 
        JButton btnFeedbackDetails = createBtn("View Feedback Details", new Color(142, 68, 173));
        JButton refreshBtn         = createBtn("Refresh",              new Color(100, 116, 139));
 
        btnFeedbackDetails.addActionListener(e -> {
            int row = feedbackTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a feedback row first.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            showFeedbackDetails(row);
        });

        refreshBtn.addActionListener(e -> refreshData());
 
        panel.add(btnFeedbackDetails);
        panel.add(refreshBtn);
        return panel;
    }
 
    private void showFeedbackDetails(int row) {
        AppointmentFeedback fb = feedbackList.get(row);
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentWindow, "Feedback Details", Dialog.ModalityType.APPLICATION_MODAL);
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
}
