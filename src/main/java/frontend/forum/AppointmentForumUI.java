package main.java.frontend.forum;

import main.java.backend.controller.AppointmentCommentController;
import main.java.backend.controller.AppointmentController;
import main.java.backend.controller.AppointmentFeedbackController;
import main.java.backend.model.Appointment;
import main.java.backend.model.AppointmentComment;
import main.java.backend.model.AppointmentFeedback;
import main.java.backend.model.StaffAccount;
import main.java.backend.model.UserAccount;
import main.java.dtos.AppointmentCommentDTO;
import main.java.dtos.AppointmentFeedbackDTO;
import main.java.enums.data.JobPosition;
import main.java.frontend.shared.Refreshable;
import main.java.frontend.shared.SessionManager;
import main.java.records.OperationResult;
import main.java.enums.system.DataFieldName;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * AppointmentForumUI — Unified appointment-centric Service Review Hub.
 *
 * Roster view: each row is one appointment. Double-click (or click "Open") to
 * open a thread-detail screen showing:
 *   LEFT  — Customer Comments  (Customer: CRUD own; others: read-only)
 *   RIGHT — Staff Feedbacks    (Technician: CRUD own; others: read-only)
 *
 * Role permissions:
 *   CUSTOMER      : CRUD own comments | read feedbacks
 *   TECHNICIAN    : read comments     | CRUD own feedbacks
 *   COUNTER_STAFF : read comments     | read feedbacks
 *   MANAGER       : read comments     | read feedbacks
 */
public class AppointmentForumUI extends JFrame implements Refreshable {

    // ── Design tokens ────────────────────────────────────────────────────────
    private static final Color BG_DARK      = new Color(15,  23,  42);
    private static final Color BG_PANEL     = new Color(241, 245, 249);
    private static final Color BG_COMMENT   = new Color(224, 242, 254);  // sky-50
    private static final Color BG_FEEDBACK  = new Color(240, 253, 244);  // green-50
    private static final Color BG_WHITE     = Color.WHITE;
    private static final Color ACCENT_BLUE  = new Color(37,  99,  235);
    private static final Color ACCENT_GREEN = new Color(22,  163, 74);
    private static final Color ACCENT_RED   = new Color(220, 38,  38);
    private static final Color ACCENT_AMBER = new Color(217, 119, 6);
    private static final Color TEXT_MUTED   = new Color(100, 116, 139);
    private static final Color DIVIDER      = new Color(226, 232, 240);
    private static final Color BUBBLE_COMMENT  = new Color(219, 234, 254);  // blue-100
    private static final Color BUBBLE_FEEDBACK = new Color(209, 250, 229);  // green-100
    private static final Font  FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font  FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font  FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font  FONT_META    = new Font("Segoe UI", Font.ITALIC, 11);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    // ── Controllers ──────────────────────────────────────────────────────────
    private final AppointmentController        apptCtrl    = new AppointmentController();
    private final AppointmentCommentController  commentCtrl  = new AppointmentCommentController();
    private final AppointmentFeedbackController feedbackCtrl = new AppointmentFeedbackController();

    // ── Session info ─────────────────────────────────────────────────────────
    private final UserAccount   currentUser;
    private final JobPosition   jobPos;     // null for customers
    private final boolean       isCustomer;
    private final boolean       isManager;
    private final boolean       isTechnician;
    private final boolean       isCounterStaff;

    // ── Roster state ─────────────────────────────────────────────────────────
    private final List<Appointment> appointmentList = new ArrayList<>();
    private DefaultTableModel rosterModel;
    private JTable            rosterTable;

    // ── Two-panel layout (CardLayout: "roster" ↔ "thread") ──────────────────
    private final CardLayout   rootCards     = new CardLayout();
    private final JPanel       rootContainer = new JPanel(rootCards);

    // Thread state
    private Appointment selectedAppointment;
    private JPanel      commentThreadPanel;
    private JPanel      feedbackThreadPanel;
    private JLabel      threadTitleLabel;
    private JLabel      threadSubLabel;

    // Editing states
    private AppointmentComment editingComment = null;
    private AppointmentFeedback editingFeedback = null;

    // Selection states
    private AppointmentComment selectedComment = null;
    private JPanel selectedCommentBubble = null;
    private AppointmentFeedback selectedFeedback = null;
    private JPanel selectedFeedbackBubble = null;

    // ── Constructor ──────────────────────────────────────────────────────────
    public AppointmentForumUI() {
        currentUser = SessionManager.getCurrentUser();

        // Determine role
        boolean cust = false, mgr = false, tech = false, cs = false;
        if (currentUser instanceof StaffAccount sa) {
            switch (sa.getJobPosition()) {
                case MANAGER       -> { mgr  = true; }
                case TECHNICIAN    -> { tech = true; }
                case COUNTER_STAFF -> { cs   = true; }
            }
        } else {
            cust = true;
        }
        isCustomer    = cust;
        isManager     = mgr;
        isTechnician  = tech;
        isCounterStaff = cs;
        jobPos = (currentUser instanceof StaffAccount sa2) ? sa2.getJobPosition() : null;

        setTitle("Service Review Hub — Auttomov");
        setSize(1200, 720);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        buildRootView();
        loadAppointments();
        refreshRosterTable();
    }

    // ── Refreshable ──────────────────────────────────────────────────────────
    @Override
    public void refreshData() {
        loadAppointments();
        refreshRosterTable();
        // If thread is visible, refresh it too
        if (selectedAppointment != null) {
            refreshCommentThread();
            refreshFeedbackThread();
        }
    }

    // ── Root view (roster + thread combined via CardLayout) ──────────────────
    private void buildRootView() {
        rootContainer.setBackground(BG_PANEL);
        rootContainer.add(buildRosterPanel(), "roster");
        rootContainer.add(buildThreadPanel(), "thread");
        add(rootContainer, BorderLayout.CENTER);
        rootCards.show(rootContainer, "roster");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ROSTER PANEL
    // ─────────────────────────────────────────────────────────────────────────
    private JPanel buildRosterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_PANEL);

        // ── Top toolbar ───────────────────────────────────────────────────
        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setBackground(BG_WHITE);
        toolbar.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel hint = new JLabel("Double-click an appointment to open its discussion thread");
        hint.setFont(FONT_SMALL);
        hint.setForeground(TEXT_MUTED);

        JButton btnOpen = createBtn("Open Thread", ACCENT_BLUE);
        JButton btnRefresh = createBtn("Refresh", new Color(100, 116, 139));

        JPanel rightBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightBtns.setOpaque(false);
        rightBtns.add(btnRefresh);
        rightBtns.add(btnOpen);

        toolbar.add(hint,      BorderLayout.WEST);
        toolbar.add(rightBtns, BorderLayout.EAST);

        // ── Table ─────────────────────────────────────────────────────────
        String[] cols = {"#", "Title", "Customer", "Service Package", "Date", "Technician(s)", "Status"};
        rosterModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        rosterTable = new JTable(rosterModel);
        styleTable(rosterTable);
        rosterTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        rosterTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        rosterTable.getColumnModel().getColumn(2).setPreferredWidth(130);
        rosterTable.getColumnModel().getColumn(3).setPreferredWidth(160);
        rosterTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        rosterTable.getColumnModel().getColumn(5).setPreferredWidth(160);
        rosterTable.getColumnModel().getColumn(6).setPreferredWidth(100);

        rosterTable.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) openThread();
            }
        });

        JScrollPane scroll = new JScrollPane(rosterTable);
        scroll.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, DIVIDER));

        // ── Status bar ────────────────────────────────────────────────────
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        statusBar.setBackground(new Color(248, 250, 252));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, DIVIDER));
        JLabel statusLbl = new JLabel("Showing all appointments. Click once to select, double-click to open.");
        statusLbl.setFont(FONT_SMALL);
        statusLbl.setForeground(TEXT_MUTED);
        statusBar.add(statusLbl);

        // ── Actions ───────────────────────────────────────────────────────
        btnOpen.addActionListener(e -> openThread());
        btnRefresh.addActionListener(e -> {
            loadAppointments();
            refreshRosterTable();
        });

        panel.add(toolbar,    BorderLayout.NORTH);
        panel.add(scroll,     BorderLayout.CENTER);
        panel.add(statusBar,  BorderLayout.SOUTH);
        return panel;
    }

    private void openThread() {
        int row = rosterTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select an appointment first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        selectedAppointment = appointmentList.get(row);
        updateThreadHeader();
        refreshCommentThread();
        refreshFeedbackThread();
        rootCards.show(rootContainer, "thread");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // THREAD PANEL (split-pane: comments LEFT | feedbacks RIGHT)
    // ─────────────────────────────────────────────────────────────────────────
    private JPanel buildThreadPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_PANEL);

        // ── Thread header ─────────────────────────────────────────────────
        JPanel headerBar = new JPanel(new BorderLayout(10, 0));
        headerBar.setBackground(BG_DARK);
        headerBar.setBorder(new EmptyBorder(12, 16, 12, 16));

        JButton btnBack = new JButton("← Back to Appointments");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBack.setForeground(Color.WHITE);
        btnBack.setBackground(new Color(30, 41, 59));
        btnBack.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(71, 85, 105), 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.setFocusPainted(false);
        btnBack.addActionListener(e -> {
            selectedAppointment = null;
            rootCards.show(rootContainer, "roster");
        });

        JPanel titleGroup = new JPanel();
        titleGroup.setOpaque(false);
        titleGroup.setLayout(new BoxLayout(titleGroup, BoxLayout.Y_AXIS));

        threadTitleLabel = new JLabel("Appointment Thread");
        threadTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        threadTitleLabel.setForeground(Color.WHITE);

        threadSubLabel = new JLabel(" ");
        threadSubLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        threadSubLabel.setForeground(new Color(148, 163, 184));

        titleGroup.add(threadTitleLabel);
        titleGroup.add(threadSubLabel);

        headerBar.add(btnBack,     BorderLayout.WEST);
        headerBar.add(titleGroup,  BorderLayout.CENTER);

        // ── Split columns ─────────────────────────────────────────────────
        commentThreadPanel  = new JPanel(new BorderLayout());
        feedbackThreadPanel = new JPanel(new BorderLayout());

        commentThreadPanel.setBackground(BG_PANEL);
        feedbackThreadPanel.setBackground(BG_PANEL);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                wrapColumn("💬  Customer Comments", BG_COMMENT, commentThreadPanel),
                wrapColumn("⭐  Staff Feedbacks",   BG_FEEDBACK, feedbackThreadPanel));
        split.setDividerLocation(0.5);
        split.setResizeWeight(0.5);
        split.setBorder(null);
        split.setDividerSize(6);

        panel.add(headerBar, BorderLayout.NORTH);
        panel.add(split,     BorderLayout.CENTER);
        return panel;
    }

    /** Wrap a thread panel in a titled column header + action bar. */
    private JPanel wrapColumn(String headerText, Color headerColor, JPanel contentSlot) {
        JPanel col = new JPanel(new BorderLayout());
        col.setBackground(BG_PANEL);

        // Column header
        JPanel colHeader = new JPanel(new BorderLayout());
        colHeader.setBackground(headerColor);
        colHeader.setBorder(new EmptyBorder(8, 14, 8, 14));
        JLabel lbl = new JLabel(headerText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(30, 40, 60));
        colHeader.add(lbl, BorderLayout.WEST);

        col.add(colHeader,   BorderLayout.NORTH);
        col.add(contentSlot, BorderLayout.CENTER);
        return col;
    }

    private void updateThreadHeader() {
        if (selectedAppointment == null) return;
        threadTitleLabel.setText(selectedAppointment.getAppointmentTitle());
        String cust = selectedAppointment.getCustomerAccount() != null
                ? selectedAppointment.getCustomerAccount().getFirstName() + " "
                  + selectedAppointment.getCustomerAccount().getLastName()
                : "N/A";
        String status = selectedAppointment.getAppointmentStatus() != null
                ? selectedAppointment.getAppointmentStatus().getAppointmentStatus()
                : "Unknown";
        threadSubLabel.setText("Customer: " + cust + "  ·  Date: " + selectedAppointment.getServiceDate()
                + "  ·  Status: " + status);
    }

    private void refreshCommentThread() {
        commentThreadPanel.removeAll();
        if (selectedAppointment == null) { commentThreadPanel.revalidate(); return; }

        List<AppointmentComment> comments = new ArrayList<>();
        Optional<List<AppointmentComment>> opt =
                commentCtrl.retrieveApptCommentsByApptId(selectedAppointment.getAppointmentID());
        if (opt.isPresent()) {
            for (AppointmentComment c : opt.get()) {
                if (isCustomer) {
                    if (c.getCommentAuthor() != null &&
                            c.getCommentAuthor().getAccountID().equals(currentUser.getAccountID())) {
                        comments.add(c);
                    }
                } else {
                    comments.add(c);
                }
            }
            comments.sort((c1, c2) -> {
                if (c1.getCommentCreationTime() == null) return 1;
                if (c2.getCommentCreationTime() == null) return -1;
                return c2.getCommentCreationTime().compareTo(c1.getCommentCreationTime());
            });
        }
        
        selectedComment = null;
        selectedCommentBubble = null;

        JPanel bubblePanel = buildBubbleList(comments, true);
        JScrollPane scroll = new JScrollPane(bubblePanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = scroll.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });

        JPanel actionBar = buildCommentActionBar(comments);

        commentThreadPanel.add(scroll,    BorderLayout.CENTER);
        commentThreadPanel.add(actionBar, BorderLayout.SOUTH);
        commentThreadPanel.revalidate();
        commentThreadPanel.repaint();
    }

    private JPanel buildCommentActionBar(List<AppointmentComment> comments) {
        JPanel bar = new JPanel(new BorderLayout(8, 0));
        bar.setBackground(BG_WHITE);
        bar.setBorder(new EmptyBorder(10, 14, 10, 14));

        boolean canWrite = isCustomer;
        if (!canWrite) {
            JLabel readOnly = new JLabel("Read-only — only customers can post comments");
            readOnly.setFont(FONT_SMALL);
            readOnly.setForeground(TEXT_MUTED);
            bar.add(readOnly, BorderLayout.CENTER);
            return bar;
        }

        JTextArea inputArea = new JTextArea(2, 30);
        inputArea.setFont(FONT_BODY);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DIVIDER),
                new EmptyBorder(5, 8, 5, 8)
        ));
        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setBorder(null);

        JButton btnPost = createBtn(editingComment != null ? "Update" : "Post", ACCENT_BLUE);
        JButton btnCancelEdit = createBtn("Cancel", new Color(108, 117, 125));
        btnCancelEdit.setVisible(editingComment != null);
        JButton btnEdit = createBtn("Edit", ACCENT_AMBER);
        JButton btnDel  = createBtn("Delete", ACCENT_RED);

        JPanel btnCol = new JPanel();
        btnCol.setOpaque(false);
        btnCol.setLayout(new BoxLayout(btnCol, BoxLayout.Y_AXIS));
        btnCol.add(btnPost);
        btnCol.add(Box.createRigidArea(new Dimension(0, 4)));
        btnCol.add(btnCancelEdit);
        btnCol.add(Box.createRigidArea(new Dimension(0, 4)));
        if (isCustomer) {
            btnCol.add(btnEdit);
            btnCol.add(Box.createRigidArea(new Dimension(0, 4)));
            btnCol.add(btnDel);
        }

        btnCancelEdit.addActionListener(e -> {
            editingComment = null;
            btnPost.setText("Post");
            inputArea.setText("");
            btnCancelEdit.setVisible(false);
        });

        btnPost.addActionListener(e -> {
            String text = inputArea.getText().trim();
            if (text.isEmpty()) return;
            if (editingComment != null) {
                OperationResult r = commentCtrl.updateAppointmentComment(
                        new AppointmentCommentDTO(editingComment.getCommentID(), text));
                if (r.isValid()) {
                    editingComment = null;
                    btnPost.setText("Post");
                    btnCancelEdit.setVisible(false);
                    inputArea.setText("");
                    refreshCommentThread();
                } else {
                    JOptionPane.showMessageDialog(this, r.generalMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                AppointmentCommentDTO dto = new AppointmentCommentDTO(
                        selectedAppointment.getAppointmentID(),
                        currentUser.getAccountID(),
                        text);
                OperationResult r = commentCtrl.createAppointmentComment(dto);
                if (r.isValid()) {
                    inputArea.setText("");
                    refreshCommentThread();
                } else {
                    JOptionPane.showMessageDialog(this, r.generalMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnEdit.addActionListener(e -> {
            if (selectedComment == null) { noRecordsWarning("Please select a comment from the list first."); return; }
            editingComment = selectedComment;
            inputArea.setText(editingComment.getCommentContent());
            btnPost.setText("Update");
            btnCancelEdit.setVisible(true);
        });

        btnDel.addActionListener(e -> {
            if (selectedComment == null) { noRecordsWarning("Please select a comment from the list first."); return; }
            AppointmentComment target = selectedComment;
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this comment? This cannot be undone.",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;
            OperationResult r = commentCtrl.deleteAppointmentComment(target.getCommentID());
            if (r.isValid()) {
                if (editingComment != null && editingComment.getCommentID().equals(target.getCommentID())) {
                    editingComment = null;
                }
                selectedComment = null;
                selectedCommentBubble = null;
                JOptionPane.showMessageDialog(this, "Comment deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshCommentThread();
            } else {
                JOptionPane.showMessageDialog(this, r.generalMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        bar.add(inputScroll, BorderLayout.CENTER);
        bar.add(btnCol,      BorderLayout.EAST);
        return bar;
    }

    private void editCommentDialog(List<AppointmentComment> comments) {
        if (comments.isEmpty()) { noRecordsWarning("No comments to edit."); return; }
        String[] labels = buildCommentLabels(comments);
        String choice = (String) JOptionPane.showInputDialog(this,
                "Select comment to edit:", "Edit Comment",
                JOptionPane.PLAIN_MESSAGE, null, labels, labels[0]);
        if (choice == null) return;
        int idx = indexOfLabel(labels, choice);
        AppointmentComment target = comments.get(idx);

        JTextArea area = new JTextArea(target.getCommentContent(), 5, 35);
        area.setLineWrap(true); area.setWrapStyleWord(true);
        int res = JOptionPane.showConfirmDialog(this, new JScrollPane(area),
                "Edit Comment Content", JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) return;
        String newText = area.getText().trim();
        if (newText.isEmpty()) return;
        OperationResult r = commentCtrl.updateAppointmentComment(
                new AppointmentCommentDTO(target.getCommentID(), newText));
        showResult(r, "Comment updated successfully.");
        refreshCommentThread();
    }

    private void deleteCommentDialog(List<AppointmentComment> comments) {
        if (comments.isEmpty()) { noRecordsWarning("No comments to delete."); return; }
        String[] labels = buildCommentLabels(comments);
        String choice = (String) JOptionPane.showInputDialog(this,
                "Select comment to delete:", "Delete Comment",
                JOptionPane.PLAIN_MESSAGE, null, labels, labels[0]);
        if (choice == null) return;
        int idx = indexOfLabel(labels, choice);
        AppointmentComment target = comments.get(idx);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this comment? This cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        OperationResult r = commentCtrl.deleteAppointmentComment(target.getCommentID());
        showResult(r, "Comment deleted.");
        refreshCommentThread();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FEEDBACK THREAD (RIGHT)
    // ─────────────────────────────────────────────────────────────────────────
    private void refreshFeedbackThread() {
        feedbackThreadPanel.removeAll();
        if (selectedAppointment == null) { feedbackThreadPanel.revalidate(); return; }

        List<AppointmentFeedback> feedbacks = new ArrayList<>();
        Optional<List<AppointmentFeedback>> opt =
                feedbackCtrl.retrieveApptFeedbackByApptId(selectedAppointment.getAppointmentID());
        if (opt.isPresent()) {
            // Technician sees only own feedbacks; others see all
            for (AppointmentFeedback fb : opt.get()) {
                if (isTechnician) {
                    if (fb.getFeedbackAuthor() != null &&
                            fb.getFeedbackAuthor().getAccountID().equals(currentUser.getAccountID())) {
                        feedbacks.add(fb);
                    }
                } else {
                    feedbacks.add(fb);
                }
            }
            feedbacks.sort((f1, f2) -> {
                if (f1.getFeedbackCreationTime() == null) return 1;
                if (f2.getFeedbackCreationTime() == null) return -1;
                return f2.getFeedbackCreationTime().compareTo(f1.getFeedbackCreationTime());
            });
        }
        
        selectedFeedback = null;
        selectedFeedbackBubble = null;

        JPanel bubblePanel = buildBubbleList(feedbacks, false);
        JScrollPane scroll = new JScrollPane(bubblePanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = scroll.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });

        JPanel actionBar = buildFeedbackActionBar(feedbacks);

        feedbackThreadPanel.add(scroll,    BorderLayout.CENTER);
        feedbackThreadPanel.add(actionBar, BorderLayout.SOUTH);
        feedbackThreadPanel.revalidate();
        feedbackThreadPanel.repaint();
    }

    private JPanel buildFeedbackActionBar(List<AppointmentFeedback> feedbacks) {
        JPanel bar = new JPanel(new BorderLayout(8, 0));
        bar.setBackground(BG_WHITE);
        bar.setBorder(new EmptyBorder(10, 14, 10, 14));

        boolean canWrite = isTechnician;
        if (!canWrite) {
            JLabel readOnly = new JLabel("Read-only — only technicians can post feedback");
            readOnly.setFont(FONT_SMALL);
            readOnly.setForeground(TEXT_MUTED);
            bar.add(readOnly, BorderLayout.CENTER);
            return bar;
        }

        JTextArea inputArea = new JTextArea(2, 30);
        inputArea.setFont(FONT_BODY);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DIVIDER),
                new EmptyBorder(5, 8, 5, 8)
        ));
        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setBorder(null);

        JButton btnPost = createBtn(editingFeedback != null ? "Update" : "Post", ACCENT_GREEN);
        JButton btnCancelEdit = createBtn("Cancel", new Color(108, 117, 125));
        btnCancelEdit.setVisible(editingFeedback != null);
        JButton btnEdit = createBtn("Edit", ACCENT_AMBER);
        JButton btnDel  = createBtn("Delete", ACCENT_RED);

        JPanel btnCol = new JPanel();
        btnCol.setOpaque(false);
        btnCol.setLayout(new BoxLayout(btnCol, BoxLayout.Y_AXIS));
        btnCol.add(btnPost);
        btnCol.add(Box.createRigidArea(new Dimension(0, 4)));
        btnCol.add(btnCancelEdit);
        btnCol.add(Box.createRigidArea(new Dimension(0, 4)));
        if (isTechnician) {
            btnCol.add(btnEdit);
            btnCol.add(Box.createRigidArea(new Dimension(0, 4)));
            btnCol.add(btnDel);
        }

        btnCancelEdit.addActionListener(e -> {
            editingFeedback = null;
            btnPost.setText("Post");
            inputArea.setText("");
            btnCancelEdit.setVisible(false);
        });

        btnPost.addActionListener(e -> {
            String text = inputArea.getText().trim();
            if (text.isEmpty()) return;
            if (editingFeedback != null) {
                OperationResult r = feedbackCtrl.updateAppointmentFeedback(
                        new AppointmentFeedbackDTO(editingFeedback.getFeedbackID(), text));
                if (r.isValid()) {
                    editingFeedback = null;
                    btnPost.setText("Post");
                    btnCancelEdit.setVisible(false);
                    inputArea.setText("");
                    refreshFeedbackThread();
                } else {
                    JOptionPane.showMessageDialog(this, r.generalMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                AppointmentFeedbackDTO dto = new AppointmentFeedbackDTO(
                        selectedAppointment.getAppointmentID(),
                        currentUser.getAccountID(),
                        text);
                OperationResult r = feedbackCtrl.createAppointmentFeedback(dto);
                if (r.isValid()) {
                    inputArea.setText("");
                    refreshFeedbackThread();
                } else {
                    if (r.fieldErrors().containsKey(DataFieldName.TECHNICIAN_ID)) {
                        JOptionPane.showMessageDialog(this, r.fieldErrors().get(DataFieldName.TECHNICIAN_ID), "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, r.generalMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        btnEdit.addActionListener(e -> {
            if (selectedFeedback == null) { noRecordsWarning("Please select a feedback from the list first."); return; }
            editingFeedback = selectedFeedback;
            inputArea.setText(editingFeedback.getFeedbackContent());
            btnPost.setText("Update");
            btnCancelEdit.setVisible(true);
        });

        btnDel.addActionListener(e -> {
            if (selectedFeedback == null) { noRecordsWarning("Please select a feedback from the list first."); return; }
            AppointmentFeedback target = selectedFeedback;
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this feedback? This cannot be undone.",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;
            OperationResult r = feedbackCtrl.deleteAppointmentFeedback(target.getFeedbackID());
            if (r.isValid()) {
                if (editingFeedback != null && editingFeedback.getFeedbackID().equals(target.getFeedbackID())) {
                    editingFeedback = null;
                }
                selectedFeedback = null;
                selectedFeedbackBubble = null;
                JOptionPane.showMessageDialog(this, "Feedback deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshFeedbackThread();
            } else {
                JOptionPane.showMessageDialog(this, r.generalMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        bar.add(inputScroll, BorderLayout.CENTER);
        bar.add(btnCol,      BorderLayout.EAST);
        return bar;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // BUBBLE LIST BUILDER
    // ─────────────────────────────────────────────────────────────────────────

    /** Build a scrollable vertical list of chat-bubble-style entries. */
    private JPanel buildBubbleList(List<?> items, boolean isComment) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(isComment ? new Color(248, 252, 255) : new Color(248, 255, 250));
        container.setBorder(new EmptyBorder(12, 14, 12, 14));

        if (items.isEmpty()) {
            JLabel empty = new JLabel(isComment
                    ? "No comments yet — be the first to comment!"
                    : "No feedback yet.");
            empty.setFont(FONT_SMALL);
            empty.setForeground(TEXT_MUTED);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            container.add(Box.createVerticalGlue());
            container.add(empty);
            container.add(Box.createVerticalGlue());
            return container;
        }

        for (Object item : items) {
            container.add(buildBubble(item, isComment));
            container.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        return container;
    }

    private JPanel buildBubble(Object item, boolean isComment) {
        String author, content, timestamp;
        if (isComment) {
            AppointmentComment c = (AppointmentComment) item;
            author    = c.getCommentAuthor() != null
                    ? c.getCommentAuthor().getFirstName() + " " + c.getCommentAuthor().getLastName()
                    : "Unknown";
            content   = c.getCommentContent();
            timestamp = c.getCommentCreationTime() != null ? c.getCommentCreationTime().format(FMT) : "";
        } else {
            AppointmentFeedback fb = (AppointmentFeedback) item;
            author    = fb.getFeedbackAuthor() != null
                    ? fb.getFeedbackAuthor().getFirstName() + " " + fb.getFeedbackAuthor().getLastName()
                    : "Unknown";
            content   = fb.getFeedbackContent();
            timestamp = fb.getFeedbackCreationTime() != null ? fb.getFeedbackCreationTime().format(FMT) : "";
        }

        // Bubble card
        JPanel bubble = new JPanel(new BorderLayout(6, 4));
        bubble.setBackground(isComment ? BUBBLE_COMMENT : BUBBLE_FEEDBACK);
        bubble.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(isComment
                        ? new Color(191, 219, 254) : new Color(167, 243, 208), 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));
        bubble.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        bubble.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Author + timestamp top row
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JLabel authorLbl = new JLabel(author);
        authorLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        authorLbl.setForeground(isComment ? new Color(30, 64, 175) : new Color(21, 128, 61));

        JLabel timeLbl = new JLabel(timestamp);
        timeLbl.setFont(FONT_META);
        timeLbl.setForeground(TEXT_MUTED);

        topRow.add(authorLbl, BorderLayout.WEST);
        topRow.add(timeLbl,   BorderLayout.EAST);

        // Content
        JTextArea contentArea = new JTextArea(content);
        contentArea.setFont(FONT_BODY);
        contentArea.setForeground(new Color(30, 40, 60));
        contentArea.setEditable(false);
        contentArea.setOpaque(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setBorder(null);

        bubble.add(topRow,      BorderLayout.NORTH);
        bubble.add(contentArea, BorderLayout.CENTER);
        
        MouseAdapter clickAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isComment) {
                    if (selectedCommentBubble != null) {
                        selectedCommentBubble.setBackground(BUBBLE_COMMENT);
                    }
                    selectedComment = (AppointmentComment) item;
                    selectedCommentBubble = bubble;
                    bubble.setBackground(new Color(254, 240, 138));
                } else {
                    if (selectedFeedbackBubble != null) {
                        selectedFeedbackBubble.setBackground(BUBBLE_FEEDBACK);
                    }
                    selectedFeedback = (AppointmentFeedback) item;
                    selectedFeedbackBubble = bubble;
                    bubble.setBackground(new Color(254, 240, 138));
                }
            }
        };
        bubble.addMouseListener(clickAdapter);
        topRow.addMouseListener(clickAdapter);
        authorLbl.addMouseListener(clickAdapter);
        timeLbl.addMouseListener(clickAdapter);
        contentArea.addMouseListener(clickAdapter);

        return bubble;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ROSTER DATA
    // ─────────────────────────────────────────────────────────────────────────
    private void loadAppointments() {
        appointmentList.clear();
        Optional<List<Appointment>> opt = apptCtrl.retrieveAllAppointments();
        if (opt.isEmpty()) return;

        List<Appointment> all = opt.get();
        for (Appointment a : all) {
            if (isCustomer) {
                // Customer only sees their own
                if (a.getCustomerAccount() != null &&
                        a.getCustomerAccount().getAccountID().equals(currentUser.getAccountID())) {
                    appointmentList.add(a);
                }
            } else if (isTechnician) {
                // Technician only sees assigned appointments
                if (a.getTechnicianList() != null) {
                    for (StaffAccount tech : a.getTechnicianList()) {
                        if (tech.getAccountID().equals(currentUser.getAccountID())) {
                            appointmentList.add(a);
                            break;
                        }
                    }
                }
            } else {
                // Manager and Counter Staff see all
                appointmentList.add(a);
            }
        }
    }

    private void refreshRosterTable() {
        rosterModel.setRowCount(0);
        int no = 1;
        for (Appointment a : appointmentList) {
            String cust = a.getCustomerAccount() != null
                    ? a.getCustomerAccount().getFirstName() + " " + a.getCustomerAccount().getLastName() : "N/A";
            String pkg  = a.getServicePackage() != null ? a.getServicePackage().getServicePackageName() : "N/A";
            String date = a.getServiceDate() != null ? a.getServiceDate().toString() : "N/A";
            String techs = "";
            if (a.getTechnicianList() != null && !a.getTechnicianList().isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (StaffAccount t : a.getTechnicianList()) {
                    if (!sb.isEmpty()) sb.append(", ");
                    sb.append(t.getFirstName()).append(" ").append(t.getLastName());
                }
                techs = sb.toString();
            } else {
                techs = "(unassigned)";
            }
            String status = a.getAppointmentStatus() != null
                    ? a.getAppointmentStatus().getAppointmentStatus() : "Unknown";

            rosterModel.addRow(new Object[]{no++, a.getAppointmentTitle(), cust, pkg, date, techs, status});
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────────────────────
    private void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(new Color(30, 64, 175));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(248, 250, 252));
        table.getTableHeader().setForeground(new Color(71, 85, 105));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, DIVIDER));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Alternating rows via custom renderer
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                if (!sel) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return c;
            }
        });
    }

    private JButton createBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setForeground(Color.WHITE);
        b.setBackground(bg);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(7, 14, 7, 14));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private String[] buildCommentLabels(List<AppointmentComment> comments) {
        String[] labels = new String[comments.size()];
        for (int i = 0; i < comments.size(); i++) {
            AppointmentComment c = comments.get(i);
            String author = c.getCommentAuthor() != null
                    ? c.getCommentAuthor().getFirstName() + " " + c.getCommentAuthor().getLastName() : "Unknown";
            String snippet = c.getCommentContent().length() > 40
                    ? c.getCommentContent().substring(0, 40) + "…" : c.getCommentContent();
            labels[i] = "[" + (i + 1) + "] " + author + ": " + snippet;
        }
        return labels;
    }

    private String[] buildFeedbackLabels(List<AppointmentFeedback> feedbacks) {
        String[] labels = new String[feedbacks.size()];
        for (int i = 0; i < feedbacks.size(); i++) {
            AppointmentFeedback fb = feedbacks.get(i);
            String author = fb.getFeedbackAuthor() != null
                    ? fb.getFeedbackAuthor().getFirstName() + " " + fb.getFeedbackAuthor().getLastName() : "Unknown";
            String snippet = fb.getFeedbackContent().length() > 40
                    ? fb.getFeedbackContent().substring(0, 40) + "…" : fb.getFeedbackContent();
            labels[i] = "[" + (i + 1) + "] " + author + ": " + snippet;
        }
        return labels;
    }

    private int indexOfLabel(String[] labels, String choice) {
        for (int i = 0; i < labels.length; i++) {
            if (labels[i].equals(choice)) return i;
        }
        return 0;
    }

    private void showResult(OperationResult r, String successMsg) {
        if (r.isValid()) {
            JOptionPane.showMessageDialog(this, successMsg, "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, r.generalMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void noRecordsWarning(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Nothing to Action", JOptionPane.INFORMATION_MESSAGE);
    }
}
