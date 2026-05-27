package main.java.frontend.comment;
 
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.*; 
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import main.java.backend.controller.AppointmentCommentController;
import main.java.backend.model.AppointmentComment;
import main.java.backend.model.StaffAccount;
import main.java.backend.model.UserAccount;
import main.java.enums.data.JobPosition;
import main.java.frontend.shared.SessionManager;
 
/**
 * ViewCommentUI — All Staff (Manager, Counter Staff, Technician)
 *
 * Managers: View ALL customer comments across all appointments.
 * Technicians: Check comments and details of INDIVIDUAL assigned appointments.
 */
public class ViewCommentUI extends JFrame implements main.java.frontend.shared.Refreshable {
 
    private final JobPosition userJobPosition;
 
    private static final String[] COLUMNS = {
            "Comment ID", "Appointment", "Customer", "Comment", "Date"
    };
 
    private List<AppointmentComment> allComments = new ArrayList<>();
    private List<String> apptIds = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JTable            commentTable;
    private JComboBox<String> filterCombo;
 
    public ViewCommentUI(JobPosition jobPosition) {
        this.userJobPosition = jobPosition;
        loadComments();
        initUI();
    }

    @Override
    public void refreshData() {
        loadComments();
        // Keep the filter selection
        String selectedApptId = "ALL";
        if (filterCombo != null && filterCombo.getSelectedIndex() >= 0 && filterCombo.getSelectedIndex() < apptIds.size()) {
            selectedApptId = apptIds.get(filterCombo.getSelectedIndex());
        }
        refreshTable(selectedApptId);
    }
 
    private void initUI() {
        setTitle("View Customer Comments — Staff");
        setSize(860, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 0));
 
        add(buildHeaderPanel(), BorderLayout.NORTH);
        add(buildTablePanel(),  BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);
 
        refreshTable("ALL");
    }
 
    private JPanel buildHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JLabel title = new JLabel("Customer Comments");
        if (userJobPosition == JobPosition.TECHNICIAN) {
            title.setText("My Assigned Appointment Comments");
        }
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
 
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 6));
        filterRow.setOpaque(false);
 
        JLabel filterLabel = new JLabel("Filter by Appointment: ");
        if (userJobPosition == JobPosition.TECHNICIAN) {
            filterLabel.setText("Filter by My Appointment: ");
        }
        filterLabel.setForeground(new Color(200, 200, 220));
        filterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
 
        List<String> apptLabels = new ArrayList<>();
        apptLabels.add("All Appointments");
        apptIds.clear();
        apptIds.add("ALL");
 
        Set<String> processedApptIds = new HashSet<>();
        for (AppointmentComment comment : allComments) {
            if (comment.getAppointment() != null) {
                String id = comment.getAppointment().getAppointmentID();
                if (!processedApptIds.contains(id)) {
                    processedApptIds.add(id);
                    apptLabels.add(comment.getAppointment().getAppointmentTitle() + " (" + comment.getAppointment().getServiceDate() + ")");
                    apptIds.add(id);
                }
            }
        }
 
        filterCombo = new JComboBox<>(apptLabels.toArray(new String[0]));
        filterCombo.setName("comboFilterAppointment");
        filterCombo.setPreferredSize(new Dimension(280, 28));
        filterCombo.addActionListener(e -> {
            int idx = filterCombo.getSelectedIndex();
            if (idx >= 0 && idx < apptIds.size()) {
                String apptID = apptIds.get(idx);
                refreshTable(apptID);
            }
        });
 
        filterRow.add(filterLabel);
        filterRow.add(filterCombo);
 
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);
        topSection.add(title,     BorderLayout.WEST);
        topSection.add(filterRow, BorderLayout.EAST);
 
        panel.add(topSection, BorderLayout.CENTER);
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
 
        commentTable.getColumnModel().getColumn(3).setPreferredWidth(420);
 
        JScrollPane scroll = new JScrollPane(commentTable);
        scroll.setBorder(new EmptyBorder(10, 14, 14, 14));
        return scroll;
    }
 
    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setOpaque(false);
 
        JButton btnDetails = createBtn("👁 View Details", new Color(142, 68, 173));

        btnDetails.addActionListener(e -> {
            int row = commentTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a comment first.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            showCommentDetails(row);
        });
 
        panel.add(btnDetails);
        return panel;
    }
 
    private void refreshTable(String apptIDFilter) {
        tableModel.setRowCount(0);
 
        for (AppointmentComment c : allComments) {
            boolean show = "ALL".equals(apptIDFilter) || 
                    (c.getAppointment() != null && c.getAppointment().getAppointmentID().equals(apptIDFilter));
            if (show) {
                tableModel.addRow(new Object[]{
                    c.getCommentID(),
                    c.getAppointment() != null ? c.getAppointment().getAppointmentTitle() : "N/A",
                    c.getCommentAuthor() != null ? c.getCommentAuthor().getFirstName() + " " + c.getCommentAuthor().getLastName() : "N/A",
                    c.getCommentContent(),
                    c.getCommentCreationTime().toLocalDate().toString()
                });
            }
        }
    }
 
    private void showCommentDetails(int row) {
        String commentId = (String) tableModel.getValueAt(row, 0);
        AppointmentComment target = null;
        for (AppointmentComment c : allComments) {
            if (c.getCommentID().equals(commentId)) {
                target = c;
                break;
            }
        }
        if (target == null) return;
 
        JDialog dialog = new JDialog(this, "Comment Details", true);
        dialog.setSize(480, 340);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
 
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
 
        addDetailRow(form, gbc, 0, "Comment ID:", target.getCommentID());
        addDetailRow(form, gbc, 1, "Appointment:", target.getAppointment() != null ? target.getAppointment().getAppointmentTitle() : "N/A");
        addDetailRow(form, gbc, 2, "Customer Name:", target.getCommentAuthor() != null ? target.getCommentAuthor().getFirstName() + " " + target.getCommentAuthor().getLastName() : "N/A");
        addDetailRow(form, gbc, 3, "Date:", target.getCommentCreationTime().toLocalDate().toString());
 
        JTextArea contentArea = new JTextArea(target.getCommentContent(), 4, 25);
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setBackground(new Color(245, 245, 245));
        contentArea.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        JScrollPane scroll = new JScrollPane(contentArea);
 
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.25;
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
 
    private void loadComments() {
        allComments.clear();
        AppointmentCommentController ctrl = new AppointmentCommentController();
        java.util.Optional<List<AppointmentComment>> listOpt = ctrl.retrieveAllApptComments();
        if (listOpt.isPresent()) {
            UserAccount user = SessionManager.getCurrentUser();
            boolean isTech = (userJobPosition == JobPosition.TECHNICIAN);
            String techId = user != null ? user.getAccountID() : "";
 
            for (AppointmentComment comment : listOpt.get()) {
                boolean show = !isTech;
                if (isTech && comment.getAppointment() != null && comment.getAppointment().getTechnicianList() != null) {
                    for (StaffAccount tech : comment.getAppointment().getTechnicianList()) {
                        if (tech.getAccountID().equals(techId)) {
                            show = true;
                            break;
                        }
                    }
                }
                if (show) {
                    allComments.add(comment);
                }
            }
        }
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
