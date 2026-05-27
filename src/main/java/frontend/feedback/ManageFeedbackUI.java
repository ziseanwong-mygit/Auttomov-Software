package main.java.frontend.feedback;
 
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import main.java.backend.controller.AppointmentController;
import main.java.backend.controller.AppointmentFeedbackController;
import main.java.backend.model.Appointment;
import main.java.backend.model.AppointmentFeedback;
import main.java.backend.model.StaffAccount;
import main.java.backend.model.UserAccount;
import main.java.dtos.AppointmentFeedbackDTO;
import main.java.enums.data.JobPosition;
import main.java.frontend.shared.SessionManager;
import main.java.records.OperationResult;
 
/**
 * ManageFeedbackUI — Technician & Manager
 *
 * Technicians: Create, Read, Update, and Delete feedbacks for their assigned appointments.
 * Managers: View all feedbacks (Read-only access).
 */
public class ManageFeedbackUI extends JFrame implements main.java.frontend.shared.Refreshable {
 
    private final JobPosition userJobPosition;
 
    private static final String[] COLUMNS = {
            "Feedback ID", "Appointment", "Author", "Content", "Date"
    };
 
    private final List<AppointmentFeedback> feedbackList = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JTable            feedbackTable;
 
    public ManageFeedbackUI(JobPosition jobPosition) {
        this.userJobPosition = jobPosition;
        loadFeedbacks();
        initUI();
    }

    @Override
    public void refreshData() {
        loadFeedbacks();
        refreshTable();
    }
 
    private void initUI() {
        setTitle("Manage Feedbacks — Technician / Manager");
        setSize(900, 560);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 0));
 
        add(buildHeaderPanel(), BorderLayout.NORTH);
        add(buildTablePanel(),  BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);
 
        refreshTable();
    }
 
    private JPanel buildHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JLabel title = new JLabel("Manage Feedbacks");
        if (userJobPosition == JobPosition.MANAGER) {
            title.setText("View Feedbacks");
        }
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
 
        panel.add(title, BorderLayout.WEST);
        return panel;
    }
 
    private JScrollPane buildTablePanel() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
 
        feedbackTable = new JTable(tableModel);
        feedbackTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        feedbackTable.setRowHeight(26);
        feedbackTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        feedbackTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
 
        feedbackTable.getColumnModel().getColumn(3).setPreferredWidth(340);
 
        JScrollPane scroll = new JScrollPane(feedbackTable);
        scroll.setBorder(new EmptyBorder(10, 14, 0, 14));
        return scroll;
    }
 
    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setOpaque(false);
 
        JButton btnCreate = createBtn("➕ Create",  new Color(34, 139, 87));
        JButton btnEdit   = createBtn("✏ Update",  new Color(52, 152, 219));
        JButton btnDelete = createBtn("🗑 Delete",  new Color(192, 57, 43));
        JButton btnDetails = createBtn("👁 View Details", new Color(142, 68, 173));
        JButton btnBack   = createBtn("← Back",    new Color(108, 117, 125));
 
        btnCreate.addActionListener(e -> openFeedbackDialog(null));
        btnBack.addActionListener(e -> dispose());
        btnEdit.addActionListener(e -> {
            int row = feedbackTable.getSelectedRow();
            if (row < 0) { showNoSelectionWarning(); return; }
            openFeedbackDialog(feedbackList.get(row));
        });
        btnDelete.addActionListener(e -> {
            int row = feedbackTable.getSelectedRow();
            if (row < 0) { showNoSelectionWarning(); return; }
            deleteFeedback(row);
        });
        btnDetails.addActionListener(e -> {
            int row = feedbackTable.getSelectedRow();
            if (row < 0) { showNoSelectionWarning(); return; }
            showFeedbackDetails(feedbackList.get(row));
        });
        if (userJobPosition != JobPosition.MANAGER) {
            panel.add(btnCreate);
            panel.add(btnEdit);
            panel.add(btnDelete);
        }
        panel.add(btnDetails);
        panel.add(btnBack);
        
        return panel;
    }
 
    private void openFeedbackDialog(AppointmentFeedback existing) {
        boolean isEdit = (existing != null);
 
        // Retrieve technician's assigned appointments
        List<Appointment> techAppts = new ArrayList<>();
        AppointmentController apptCtrl = new AppointmentController();
        java.util.Optional<List<Appointment>> apptsOpt = apptCtrl.retrieveAllAppointments();
        if (apptsOpt.isPresent()) {
            UserAccount user = SessionManager.getCurrentUser();
            for (Appointment appt : apptsOpt.get()) {
                if (appt.getTechnicianList() != null) {
                    for (StaffAccount tech : appt.getTechnicianList()) {
                        if (tech.getAccountID().equals(user.getAccountID())) {
                            techAppts.add(appt);
                            break;
                        }
                    }
                }
            }
        }
 
        if (techAppts.isEmpty() && !isEdit) {
            JOptionPane.showMessageDialog(this, "You have no assigned appointments to write feedback for.", "No Appointments", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        JDialog dialog = new JDialog(this, isEdit ? "Edit Feedback" : "Create Feedback", true);
        dialog.setSize(520, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
 
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(16, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
 
        JComboBox<String> apptCombo = null;
        if (!isEdit) {
            String[] apptLabels = new String[techAppts.size()];
            for (int i = 0; i < techAppts.size(); i++) {
                Appointment a = techAppts.get(i);
                apptLabels[i] = a.getAppointmentTitle() + " (" + a.getServiceDate() + ")";
            }
            apptCombo = new JComboBox<>(apptLabels);
            addFormRow(form, gbc, 0, "Appointment:", apptCombo);
        } else {
            String apptTitle = "N/A";
            if (existing != null && existing.getAppointment() != null) {
                apptTitle = existing.getAppointment().getAppointmentTitle();
            }
            JLabel lblApptName = new JLabel(apptTitle);
            addFormRow(form, gbc, 0, "Appointment:", lblApptName);
        }
 
        JTextArea contentArea = new JTextArea(5, 30);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JScrollPane contentScroll = new JScrollPane(contentArea);
 
        if (isEdit && existing != null) {
            contentArea.setText(existing.getFeedbackContent());
        }
 
        addFormRow(form, gbc, 1, "Content:", contentScroll);
 
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnSave   = createBtn(isEdit ? "Save Changes" : "Create", new Color(52, 152, 219));
        JButton btnCancel = createBtn("Cancel", new Color(108, 117, 125));
 
        final JComboBox<String> finalApptCombo = apptCombo;
        btnSave.addActionListener(e -> {
            String content = contentArea.getText().trim();
            if (content.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Content cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
 
            AppointmentFeedbackController ctrl = new AppointmentFeedbackController();
            OperationResult result;
            if (isEdit && existing != null) {
                AppointmentFeedbackDTO dto = new AppointmentFeedbackDTO(existing.getFeedbackID(), content);
                // The update method is named updateAppointmentComment in the backend controller facade
                result = ctrl.updateAppointmentFeedback(dto);
            } else if (finalApptCombo != null) {
                int apptIdx = finalApptCombo.getSelectedIndex();
                Appointment appt = techAppts.get(apptIdx);
                UserAccount user = SessionManager.getCurrentUser();
                AppointmentFeedbackDTO dto = new AppointmentFeedbackDTO(appt.getAppointmentID(), user.getAccountID(), content);
                result = ctrl.createAppointmentFeedback(dto);
            } else {
                result = OperationResult.failed("No appointment selected.");
            }
 
            if (result.isValid()) {
                JOptionPane.showMessageDialog(dialog, isEdit ? "Feedback updated successfully!" : "Feedback created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadFeedbacks();
                refreshTable();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, result.generalMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
 
        btnCancel.addActionListener(e -> dialog.dispose());
        btnRow.add(btnSave);
        btnRow.add(btnCancel);
 
        dialog.add(form,   BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
 
    private void deleteFeedback(int row) {
        AppointmentFeedback target = feedbackList.get(row);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete feedback \"" + target.getFeedbackID() + "\"?\nThis cannot be undone.",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
 
        if (confirm == JOptionPane.YES_OPTION) {
            AppointmentFeedbackController ctrl = new AppointmentFeedbackController();
            OperationResult result = ctrl.deleteAppointmentFeedback(target.getFeedbackID());
            if (result.isValid()) {
                JOptionPane.showMessageDialog(this, "Feedback deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadFeedbacks();
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, result.generalMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
 
    private void loadFeedbacks() {
        feedbackList.clear();
        AppointmentFeedbackController ctrl = new AppointmentFeedbackController();
        java.util.Optional<List<AppointmentFeedback>> listOpt = ctrl.retrieveAllApptFeedback();
        if (listOpt.isPresent()) {
            UserAccount user = SessionManager.getCurrentUser();
            boolean isTech = (userJobPosition == JobPosition.TECHNICIAN);
            String techId = user != null ? user.getAccountID() : "";
 
            for (AppointmentFeedback fb : listOpt.get()) {
                boolean show = !isTech;
                if (isTech && fb.getFeedbackAuthor() != null && fb.getFeedbackAuthor().getAccountID().equals(techId)) {
                    show = true;
                }
                if (show) {
                    feedbackList.add(fb);
                }
            }
        }
    }
 
    private void refreshTable() {
        tableModel.setRowCount(0);
        for (AppointmentFeedback fb : feedbackList) {
            tableModel.addRow(new Object[]{
                fb.getFeedbackID(),
                fb.getAppointment() != null ? fb.getAppointment().getAppointmentTitle() : "N/A",
                fb.getFeedbackAuthor() != null ? fb.getFeedbackAuthor().getFirstName() + " " + fb.getFeedbackAuthor().getLastName() : "N/A",
                fb.getFeedbackContent(),
                fb.getFeedbackCreationTime().toLocalDate().toString()
            });
        }
    }
 
    private void showFeedbackDetails(AppointmentFeedback fb) {
        JDialog dialog = new JDialog(this, "Feedback Details", true);
        dialog.setSize(480, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
 
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
 
        addDetailRow(form, gbc, 0, "Feedback ID:", fb.getFeedbackID());
        addDetailRow(form, gbc, 1, "Appointment:", fb.getAppointment() != null ? fb.getAppointment().getAppointmentTitle() : "N/A");
        addDetailRow(form, gbc, 2, "Author Name:", fb.getFeedbackAuthor() != null ? fb.getFeedbackAuthor().getFirstName() + " " + fb.getFeedbackAuthor().getLastName() : "N/A");
        addDetailRow(form, gbc, 3, "Created Date:", fb.getFeedbackCreationTime().toLocalDate().toString());
 
        JTextArea contentArea = new JTextArea(fb.getFeedbackContent(), 4, 25);
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setBackground(new Color(245, 245, 245));
        contentArea.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        JScrollPane scroll = new JScrollPane(contentArea);
 
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.25;
        form.add(new JLabel("Content:"), gbc);
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
 
    private void showNoSelectionWarning() {
        JOptionPane.showMessageDialog(this, "Please select a feedback record first.", "No Selection", JOptionPane.WARNING_MESSAGE);
    }
 
    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.25;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1; gbc.weightx = 0.75;
        panel.add(field, gbc);
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
}
