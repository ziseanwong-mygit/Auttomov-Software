package main.java.frontend.shared;

import main.java.backend.controller.AccountController;
import main.java.backend.model.StaffAccount;
import main.java.backend.model.UserAccount;
import main.java.backend.util.PswdHasher;
import main.java.dtos.StaffAccountDTO;
import main.java.dtos.UserAccountDTO;
import main.java.enums.data.EmploymentStatus;
import main.java.enums.data.JobPosition;
import main.java.records.OperationResult;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * EditProfilePanel — Reusable workspace panel embedded in every role's
 * dashboard CardLayout under the "Edit Profile" sidebar item.
 *
 * Contains three sections side-by-side:
 *   1. Personal Details  (editable via UpdateAccount backend logic)
 *   2. Change Password   (via UpdatePassword backend logic)
 *   3. Danger Zone       (Delete Account — two-step confirm)
 *
 * On success the panel refreshes itself; it never opens a new JFrame.
 * The owning dashboard supplies a Runnable that is called when the account
 * is deleted (so the dashboard can dispose itself and return to login).
 */
public class EditProfilePanel extends JPanel {

    private final Runnable onDeleteAccount;

    // Personal details fields
    private JTextField userIdField, firstNameField, lastNameField, emailField, phoneField;
    private JComboBox<JobPosition>       jobPositionComboBox;
    private JComboBox<EmploymentStatus>  empStatusComboBox;
    private JTextField employmentDateField, specializationField;
    private JPanel specializationRow;

    // Password fields
    private JPasswordField currentPassField, newPassField, confirmPassField;

    public EditProfilePanel(Runnable onDeleteAccount) {
        this(onDeleteAccount, true);
    }

    public EditProfilePanel(Runnable onDeleteAccount, boolean showTitle) {
        this.onDeleteAccount = onDeleteAccount;
        setBackground(new Color(241, 245, 249));
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(28, 28, 28, 28));

        // Title
        if (showTitle) {
            JLabel title = new JLabel("Edit Profile");
            title.setFont(new Font("Segoe UI", Font.BOLD, 22));
            title.setForeground(new Color(15, 23, 42));
            title.setBorder(new EmptyBorder(0, 0, 20, 0));
            add(title, BorderLayout.NORTH);
        }

        // Three columns
        JPanel cols = new JPanel(new GridLayout(1, 3, 20, 0));
        cols.setOpaque(false);
        cols.add(buildPersonalSection());
        cols.add(buildPasswordSection());
        cols.add(buildDangerSection());

        JScrollPane scroll = new JScrollPane(cols,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        add(scroll, BorderLayout.CENTER);
    }

    // ── Personal Details ─────────────────────────────────────────────────────

    private JPanel buildPersonalSection() {
        JPanel card = makeCard("Personal Details");
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        UserAccount user  = SessionManager.getCurrentUser();
        boolean    isStaff = user instanceof StaffAccount;

        userIdField    = field(user.getUserID());
        firstNameField = field(user.getFirstName());
        lastNameField  = field(user.getLastName());
        emailField     = field(user.getEmailAddress());
        phoneField     = field(user.getPhoneNumber());

        card.add(row("User ID (IC / Passport):", userIdField));
        card.add(gap());
        card.add(row("First Name:", firstNameField));
        card.add(gap());
        card.add(row("Last Name:", lastNameField));
        card.add(gap());
        card.add(row("Email Address:", emailField));
        card.add(gap());
        card.add(row("Phone Number:", phoneField));

        // Staff-only extra fields
        if (isStaff) {
            StaffAccount staff = (StaffAccount) user;

            card.add(gap());
            JSeparator sep = new JSeparator();
            sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            sep.setForeground(new Color(220, 220, 220));
            card.add(sep);
            card.add(gap());

            jobPositionComboBox = new JComboBox<>(JobPosition.values());
            jobPositionComboBox.setSelectedItem(staff.getJobPosition());

            empStatusComboBox = new JComboBox<>(EmploymentStatus.values());
            empStatusComboBox.setSelectedItem(staff.getEmploymentStatus());

            employmentDateField = field(staff.getEmploymentDate().toString());

            specializationField = field(staff.getSpecialization() != null ? staff.getSpecialization() : "");
            specializationRow   = row("Specialization:", specializationField);
            specializationRow.setVisible(staff.getJobPosition() == JobPosition.TECHNICIAN);

            jobPositionComboBox.addActionListener(e -> {
                boolean isTech = jobPositionComboBox.getSelectedItem() == JobPosition.TECHNICIAN;
                specializationRow.setVisible(isTech);
                if (!isTech) specializationField.setText("");
                revalidate();
            });

            card.add(row("Job Position:", jobPositionComboBox));
            card.add(gap());
            card.add(row("Employment Status:", empStatusComboBox));
            card.add(gap());
            card.add(row("Employment Date (YYYY-MM-DD):", employmentDateField));
            card.add(gap());
            card.add(specializationRow);
        }

        card.add(Box.createVerticalGlue());
        card.add(gap());

        JButton saveBtn = btn("Save Details", new Color(37, 99, 235));
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> handleUpdate(isStaff, user));
        card.add(saveBtn);

        return wrapCard(card);
    }

    // ── Password ─────────────────────────────────────────────────────────────

    private JPanel buildPasswordSection() {
        JPanel card = makeCard("Change Password");
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        currentPassField = passField();
        newPassField     = passField();
        confirmPassField = passField();

        card.add(row("Current Password:", currentPassField));
        card.add(gap());
        card.add(row("New Password:", newPassField));
        card.add(gap());
        card.add(row("Confirm New Password:", confirmPassField));
        card.add(Box.createVerticalGlue());
        card.add(gap());

        JButton changeBtn = btn("Change Password", new Color(124, 58, 237));
        changeBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        changeBtn.addActionListener(e -> handlePasswordChange());
        card.add(changeBtn);

        return wrapCard(card);
    }

    // ── Danger Zone ───────────────────────────────────────────────────────────

    private JPanel buildDangerSection() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);

        JPanel dangerCard = new JPanel();
        dangerCard.setLayout(new BoxLayout(dangerCard, BoxLayout.Y_AXIS));
        dangerCard.setBackground(new Color(255, 245, 245));
        dangerCard.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 38, 38), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel dangerTitle = new JLabel("⚠  Danger Zone");
        dangerTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dangerTitle.setForeground(new Color(185, 28, 28));
        dangerTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dangerDesc = new JLabel("<html><body style='width:160px;color:#7f1d1d;font-size:11px'>"
                + "Deleting your account is permanent and cannot be undone. "
                + "All your data will be erased immediately.</body></html>");
        dangerDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton deleteBtn = new JButton("🗑  Delete My Account");
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setBackground(new Color(220, 38, 38));
        deleteBtn.setFocusPainted(false);
        deleteBtn.setBorder(new EmptyBorder(10, 16, 10, 16));
        deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        deleteBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        deleteBtn.addActionListener(e -> onDeleteAccount.run());

        dangerCard.add(dangerTitle);
        dangerCard.add(Box.createRigidArea(new Dimension(0, 10)));
        dangerCard.add(dangerDesc);
        dangerCard.add(Box.createRigidArea(new Dimension(0, 20)));
        dangerCard.add(deleteBtn);

        outer.add(dangerCard, BorderLayout.NORTH);
        return outer;
    }

    // ── Backend Handlers ──────────────────────────────────────────────────────

    private void handleUpdate(boolean isStaff, UserAccount user) {
        OperationResult result;
        AccountController ctrl = new AccountController();

        if (isStaff) {
            LocalDate empDate;
            try {
                empDate = LocalDate.parse(employmentDateField.getText().trim());
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid date format. Use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JobPosition jobPos = (JobPosition) jobPositionComboBox.getSelectedItem();
            String spec = (jobPos == JobPosition.TECHNICIAN) ? specializationField.getText().trim() : null;
            result = ctrl.updateAccount(new StaffAccountDTO(
                    user.getAccountID(), SessionManager.getCurrentRole(),
                    userIdField.getText().trim(), firstNameField.getText().trim(),
                    lastNameField.getText().trim(), emailField.getText().trim(),
                    phoneField.getText().trim(), user.getAccountStatus(),
                    jobPos, empDate, (EmploymentStatus) empStatusComboBox.getSelectedItem(), spec
            ));
        } else {
            result = ctrl.updateAccount(new UserAccountDTO(
                    user.getAccountID(), SessionManager.getCurrentRole(),
                    userIdField.getText().trim(), firstNameField.getText().trim(),
                    lastNameField.getText().trim(), emailField.getText().trim(),
                    phoneField.getText().trim(), user.getAccountStatus()
            ));
        }

        if (result.isValid()) {
            JOptionPane.showMessageDialog(this,
                    "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            // Refresh session
            ctrl.retrieveAccountById(emailField.getText().trim(), SessionManager.getCurrentRole())
                    .ifPresent(SessionManager::setCurrentUser);
        } else {
            showErrors(result);
        }
    }

    private void handlePasswordChange() {
        UserAccount user = SessionManager.getCurrentUser();
        String currentPass = new String(currentPassField.getPassword());
        String newPass     = new String(newPassField.getPassword());
        String confirmPass = new String(confirmPassField.getPassword());

        if (!PswdHasher.verifyPassword(currentPass, user)) {
            JOptionPane.showMessageDialog(this,
                    "Current password is incorrect.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        AccountController ctrl = new AccountController();
        UserAccountDTO dto = new UserAccountDTO(
                user.getAccountID(), SessionManager.getCurrentRole(), newPass, confirmPass);
        OperationResult result = ctrl.updateAccountPassword(dto);

        if (result.isValid()) {
            JOptionPane.showMessageDialog(this,
                    "Password updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            // Refresh session
            ctrl.retrieveAccountById(user.getEmailAddress(), SessionManager.getCurrentRole())
                    .ifPresent(SessionManager::setCurrentUser);
            currentPassField.setText("");
            newPassField.setText("");
            confirmPassField.setText("");
        } else {
            showErrors(result);
        }
    }

    private void showErrors(OperationResult result) {
        StringBuilder sb = new StringBuilder();
        if (!result.fieldErrors().isEmpty())
            result.fieldErrors().forEach((f, msg) -> sb.append("• ").append(msg).append("\n"));
        else
            sb.append(result.generalMessage());
        JOptionPane.showMessageDialog(this, sb.toString(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ── Tiny builder helpers ──────────────────────────────────────────────────

    private JPanel makeCard(String sectionTitle) {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));
        TitledBorder tb = BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(), sectionTitle,
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13), new Color(71, 85, 105));
        p.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new CompoundBorder(new EmptyBorder(4, 0, 0, 0),
                        new CompoundBorder(tb, new EmptyBorder(8, 6, 6, 6)))));
        return p;
    }

    private JPanel wrapCard(JPanel card) {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.add(card, BorderLayout.NORTH);
        return outer;
    }

    private JPanel row(String labelText, JComponent field) {
        JPanel r = new JPanel(new BorderLayout(0, 4));
        r.setOpaque(false);
        r.setAlignmentX(Component.LEFT_ALIGNMENT);
        r.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(71, 85, 105));

        if (field instanceof JTextField || field instanceof JPasswordField) {
            field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            field.setBorder(new CompoundBorder(
                    new LineBorder(new Color(203, 213, 225), 1, true),
                    new EmptyBorder(6, 8, 6, 8)));
        }

        r.add(lbl,   BorderLayout.NORTH);
        r.add(field, BorderLayout.CENTER);
        return r;
    }

    private Component gap() { return Box.createRigidArea(new Dimension(0, 10)); }

    private JTextField field(String val) {
        return new JTextField(val);
    }

    private JPasswordField passField() {
        return new JPasswordField();
    }

    private JButton btn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setBackground(bg);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10, 18, 10, 18));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        return b;
    }
}
