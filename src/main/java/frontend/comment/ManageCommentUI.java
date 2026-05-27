package main.java.frontend.comment;
 
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*; 
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import main.java.backend.controller.AppointmentCommentController;
import main.java.backend.controller.AppointmentController;
import main.java.backend.model.Appointment;
import main.java.backend.model.AppointmentComment;
import main.java.backend.model.UserAccount;
import main.java.dtos.AppointmentCommentDTO;
import main.java.frontend.shared.SessionManager;
import main.java.records.OperationResult;
 
/**
 * ManageCommentUI — Customer
 *
 * Allows a Customer to Create, Read, Update, and Delete comments
 * they have written for their appointments.
 */
public class ManageCommentUI extends JFrame implements main.java.frontend.shared.Refreshable {
 
    private static final String[] COLUMNS = {
            "Comment ID", "Appointment", "Content", "Date"
    };
 
    private List<AppointmentComment> commentList = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JTable            commentTable;
 
    public ManageCommentUI() {
        loadComments();
        initUI();
    }

    @Override
    public void refreshData() {
        loadComments();
        refreshTable();
    }
 
    private void initUI() {
        setTitle("My Appointment Comments — Customer");
        setSize(820, 500);
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

        JLabel title = new JLabel("My Appointment Comments");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
 
        panel.add(title, BorderLayout.WEST);
        return panel;
    }
 
    private JScrollPane buildTablePanel() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
 
        commentTable = new JTable(tableModel);
        commentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        commentTable.setRowHeight(26);
        commentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        commentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
 
        commentTable.getColumnModel().getColumn(2).setPreferredWidth(400);
 
        JScrollPane scroll = new JScrollPane(commentTable);
        scroll.setBorder(new EmptyBorder(10, 14, 0, 14));
        return scroll;
    }
 
    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setOpaque(false);
 
        JButton btnCreate = createBtn("Add",     new Color(34, 139, 87));
        JButton btnEdit   = createBtn("Update",  new Color(52, 152, 219));
        JButton btnDelete = createBtn("Delete",  new Color(190, 50, 50));
        JButton btnDetails = createBtn("View Details", new Color(142, 68, 173));
        JButton btnBack   = createBtn("Back",    new Color(108, 117, 125));
 
        btnCreate.addActionListener(e -> openCommentDialog(null));
        btnEdit.addActionListener(e -> {
            int row = commentTable.getSelectedRow();
            if (row < 0) { showNoSelectionWarning(); return; }
            openCommentDialog(commentList.get(row));
        });
        btnDelete.addActionListener(e -> {
            int row = commentTable.getSelectedRow();
            if (row < 0) { showNoSelectionWarning(); return; }
            deleteComment(row);
        });
        btnDetails.addActionListener(e -> {
            int row = commentTable.getSelectedRow();
            if (row < 0) { showNoSelectionWarning(); return; }
            showCommentDetails(row);
        });
        panel.add(btnCreate);
        panel.add(btnEdit);
        panel.add(btnDelete);
        panel.add(btnDetails);
        return panel;
    }
 
    private void openCommentDialog(AppointmentComment existing) {
        boolean isEdit = (existing != null);
 
        // Retrieve customer's appointments
        List<Appointment> customerAppts = new ArrayList<>();
        AppointmentController apptCtrl = new AppointmentController();
        java.util.Optional<List<Appointment>> apptsOpt = apptCtrl.retrieveAllAppointments();
        if (apptsOpt.isPresent()) {
            UserAccount user = SessionManager.getCurrentUser();
            for (Appointment appt : apptsOpt.get()) {
                if (appt.getCustomerAccount() != null && appt.getCustomerAccount().getAccountID().equals(user.getAccountID())) {
                    customerAppts.add(appt);
                }
            }
        }
 
        if (customerAppts.isEmpty() && !isEdit) {
            JOptionPane.showMessageDialog(this, "You have no appointments to comment on.", "No Appointments", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        JDialog dialog = new JDialog(this, isEdit ? "Edit Comment" : "Add Comment", true);
        dialog.setSize(500, 260);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
 
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(16, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
 
        JComboBox<String> apptCombo = null;
        if (!isEdit) {
            String[] apptLabels = new String[customerAppts.size()];
            for (int i = 0; i < customerAppts.size(); i++) {
                Appointment a = customerAppts.get(i);
                apptLabels[i] = a.getAppointmentTitle() + " (" + a.getServiceDate() + ")";
            }
            apptCombo = new JComboBox<>(apptLabels);
            addFormRow(form, gbc, 0, "Appointment:", apptCombo);
        } else {
            JLabel lblApptName = new JLabel(existing.getAppointment() != null ? existing.getAppointment().getAppointmentTitle() : "N/A");
            addFormRow(form, gbc, 0, "Appointment:", lblApptName);
        }
 
        JTextArea contentArea = new JTextArea(4, 30);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JScrollPane contentScroll = new JScrollPane(contentArea);
 
        if (isEdit) {
            contentArea.setText(existing.getCommentContent());
        }
 
        addFormRow(form, gbc, 1, "Comment:", contentScroll);
 
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnSave   = createBtn(isEdit ? "Save Changes" : "Submit", new Color(52, 152, 219));
        JButton btnCancel = createBtn("Cancel", new Color(108, 117, 125));
 
        final JComboBox<String> finalApptCombo = apptCombo;
        btnSave.addActionListener(e -> {
            String content = contentArea.getText().trim();
            if (content.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Comment cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
 
            AppointmentCommentController ctrl = new AppointmentCommentController();
            OperationResult result;
            if (isEdit) {
                AppointmentCommentDTO dto = new AppointmentCommentDTO(existing.getCommentID(), content);
                result = ctrl.updateAppointmentComment(dto);
            } else {
                int apptIdx = finalApptCombo.getSelectedIndex();
                Appointment appt = customerAppts.get(apptIdx);
                UserAccount user = SessionManager.getCurrentUser();
                AppointmentCommentDTO dto = new AppointmentCommentDTO(appt.getAppointmentID(), user.getAccountID(), content);
                result = ctrl.createAppointmentComment(dto);
            }
 
            if (result.isValid()) {
                JOptionPane.showMessageDialog(dialog, isEdit ? "Comment updated successfully!" : "Comment submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadComments();
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
 
    private void deleteComment(int row) {
        AppointmentComment target = commentList.get(row);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete comment \"" + target.getCommentID() + "\"?\nThis cannot be undone.",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
 
        if (confirm == JOptionPane.YES_OPTION) {
            AppointmentCommentController ctrl = new AppointmentCommentController();
            OperationResult result = ctrl.deleteAppointmentComment(target.getCommentID());
            if (result.isValid()) {
                JOptionPane.showMessageDialog(this, "Comment deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadComments();
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, result.generalMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
 
    private void loadComments() {
        commentList.clear();
        AppointmentCommentController ctrl = new AppointmentCommentController();
        java.util.Optional<List<AppointmentComment>> listOpt = ctrl.retrieveAllApptComments();
        if (listOpt.isPresent()) {
            UserAccount user = SessionManager.getCurrentUser();
            for (AppointmentComment comment : listOpt.get()) {
                if (comment.getCommentAuthor() != null && comment.getCommentAuthor().getAccountID().equals(user.getAccountID())) {
                    commentList.add(comment);
                }
            }
        }
    }
 
    private void refreshTable() {
        tableModel.setRowCount(0);
        for (AppointmentComment c : commentList) {
            tableModel.addRow(new Object[]{
                c.getCommentID(),
                c.getAppointment() != null ? c.getAppointment().getAppointmentTitle() : "N/A",
                c.getCommentContent(),
                c.getCommentCreationTime().toLocalDate().toString()
            });
        }
    }
 
    private void showCommentDetails(int row) {
        AppointmentComment c = commentList.get(row);
 
        JDialog dialog = new JDialog(this, "Comment Details", true);
        dialog.setSize(480, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
 
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
 
        addDetailRow(form, gbc, 0, "Comment ID:", c.getCommentID());
        addDetailRow(form, gbc, 1, "Appointment:", c.getAppointment() != null ? c.getAppointment().getAppointmentTitle() : "N/A");
        addDetailRow(form, gbc, 2, "Date:", c.getCommentCreationTime().toLocalDate().toString());
 
        JTextArea contentArea = new JTextArea(c.getCommentContent(), 4, 25);
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setBackground(new Color(245, 245, 245));
        contentArea.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        JScrollPane scroll = new JScrollPane(contentArea);
 
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.25;
        form.add(new JLabel("Comment:"), gbc);
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
        JOptionPane.showMessageDialog(this, "Please select a comment first.", "No Selection", JOptionPane.WARNING_MESSAGE);
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
