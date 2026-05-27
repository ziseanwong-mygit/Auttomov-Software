package main.java.frontend.account;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.*;
import main.java.backend.controller.AccountController;
import main.java.backend.model.StaffAccount;
import main.java.backend.model.UserAccount;
import main.java.dtos.StaffAccountDTO;
import main.java.dtos.UserAccountDTO;
import main.java.enums.data.EmploymentStatus;
import main.java.enums.data.JobPosition;
import main.java.frontend.shared.SessionManager;
import main.java.records.OperationResult;

public class UpdateAccountUI extends JFrame {

    private JTextField userIdField, firstNameField, lastNameField, emailField, phoneField;
    private JComboBox<JobPosition> jobPositionComboBox;
    private JComboBox<EmploymentStatus> empStatusComboBox;
    private JTextField employmentDateField, specializationField;
    private JPanel specializationPanel;

    public UpdateAccountUI() {
        UserAccount user = SessionManager.getCurrentUser();
        boolean isStaff = user instanceof StaffAccount;

        setTitle("Update Account - Auttomov Software");
        setSize(500, isStaff ? 680 : 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 244, 248));

        // ── Header ──────────────────────────────────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        JLabel title = new JLabel("Update My Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        JLabel subtitle = new JLabel("accountID, role and status cannot be changed here");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitle.setForeground(new Color(200, 230, 255));
        headerPanel.add(title, BorderLayout.CENTER);
        headerPanel.add(subtitle, BorderLayout.SOUTH);

        // ── Form panels ──────────────────────────────────────────────────────
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        mainPanel.setOpaque(false);

        // Read-only info
        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 10, 8));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Account Info (read-only)"));
        infoPanel.setOpaque(false);
        addReadOnly(infoPanel, "Account ID:", user.getAccountID());
        addReadOnly(infoPanel, "Role:", SessionManager.getCurrentRole().getUserRole());

        // Editable common fields
        JPanel commonPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        commonPanel.setBorder(BorderFactory.createTitledBorder("Personal Details"));
        commonPanel.setOpaque(false);

        userIdField    = createField(user.getUserID());
        firstNameField = createField(user.getFirstName());
        lastNameField  = createField(user.getLastName());
        emailField     = createField(user.getEmailAddress());
        phoneField     = createField(user.getPhoneNumber());

        addPair(commonPanel, "User ID (IC/Passport):", userIdField);
        addPair(commonPanel, "First Name:", firstNameField);
        addPair(commonPanel, "Last Name:", lastNameField);
        addPair(commonPanel, "Email Address:", emailField);
        addPair(commonPanel, "Phone Number:", phoneField);

        mainPanel.add(infoPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(commonPanel);

        // Staff-only fields
        if (isStaff) {
            StaffAccount staff = (StaffAccount) user;

            JPanel staffOuter = new JPanel(new BorderLayout());
            staffOuter.setBorder(BorderFactory.createTitledBorder("Staff Details"));
            staffOuter.setOpaque(false);

            JPanel staffContent = new JPanel();
            staffContent.setLayout(new BoxLayout(staffContent, BoxLayout.Y_AXIS));
            staffContent.setOpaque(false);

            JPanel staffBase = new JPanel(new GridLayout(3, 2, 10, 10));
            staffBase.setOpaque(false);

            jobPositionComboBox = new JComboBox<>(JobPosition.values());
            jobPositionComboBox.setSelectedItem(staff.getJobPosition());
            empStatusComboBox = new JComboBox<>(EmploymentStatus.values());
            empStatusComboBox.setSelectedItem(staff.getEmploymentStatus());
            employmentDateField = createField(staff.getEmploymentDate().toString());

            addPair(staffBase, "Job Position:", jobPositionComboBox);
            addPair(staffBase, "Employment Status:", empStatusComboBox);
            addPair(staffBase, "Employment Date (YYYY-MM-DD):", employmentDateField);

            specializationPanel = new JPanel(new GridLayout(1, 2, 10, 10));
            specializationPanel.setOpaque(false);
            specializationField = createField(staff.getSpecialization() != null ? staff.getSpecialization() : "");
            addPair(specializationPanel, "Specialization:", specializationField);
            specializationPanel.setVisible(staff.getJobPosition() == JobPosition.TECHNICIAN);

            jobPositionComboBox.addActionListener(e -> {
                boolean isTech = jobPositionComboBox.getSelectedItem() == JobPosition.TECHNICIAN;
                specializationPanel.setVisible(isTech);
                if (!isTech) specializationField.setText("");
                pack();
            });

            staffContent.add(staffBase);
            staffContent.add(Box.createRigidArea(new Dimension(0, 6)));
            staffContent.add(specializationPanel);

            staffOuter.add(staffContent, BorderLayout.NORTH);

            mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            mainPanel.add(staffOuter);
        }

        // ── Buttons ──────────────────────────────────────────────────────────
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setOpaque(false);
        JButton saveBtn = createBtn("Save Changes", new Color(39, 174, 96));

        saveBtn.addActionListener(e -> handleUpdate(isStaff, user));
        buttonPanel.add(saveBtn);

        JScrollPane scroll = new JScrollPane(mainPanel);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);

        // add(headerPanel, BorderLayout.NORTH);
        add(scroll,      BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleUpdate(boolean isStaff, UserAccount user) {
        OperationResult result;
        AccountController accountController = new AccountController();
        if (isStaff) {
            LocalDate empDate;
            try {
                empDate = LocalDate.parse(employmentDateField.getText().trim());
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JobPosition jobPos = (JobPosition) jobPositionComboBox.getSelectedItem();
            String spec = (jobPos == JobPosition.TECHNICIAN) ? specializationField.getText() : null;
            StaffAccountDTO staffDto = new StaffAccountDTO(
                user.getAccountID(), SessionManager.getCurrentRole(), userIdField.getText(),
                firstNameField.getText(), lastNameField.getText(),
                emailField.getText(), phoneField.getText(), user.getAccountStatus(),
                jobPos, empDate, (EmploymentStatus) empStatusComboBox.getSelectedItem(), spec
            );
            result = accountController.updateAccount(staffDto);
        } else {
            UserAccountDTO userDto = new UserAccountDTO(
                user.getAccountID(), SessionManager.getCurrentRole(), userIdField.getText(),
                firstNameField.getText(), lastNameField.getText(),
                emailField.getText(), phoneField.getText(), user.getAccountStatus()
            );
            result = accountController.updateAccount(userDto);
        }

        if (result.isValid()) {
            JOptionPane.showMessageDialog(this, "Account updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            // Refresh Session User Info from database
            java.util.Optional<UserAccount> updatedUserOpt = accountController.retrieveAccountById(emailField.getText().trim(), SessionManager.getCurrentRole());
            if (updatedUserOpt.isPresent()) {
                SessionManager.setCurrentUser(updatedUserOpt.get());
            }
            // Profile updated; panel stays in place (embedded in dashboard)
        } else {
            StringBuilder sb = new StringBuilder();
            if (!result.fieldErrors().isEmpty())
                result.fieldErrors().forEach((f, msg) -> sb.append("- ").append(msg).append("\n"));
            else
                sb.append(result.generalMessage());
            JOptionPane.showMessageDialog(this, sb.toString(), "Update Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addReadOnly(JPanel p, String label, String val) {
        JLabel l = new JLabel(label); l.setFont(new Font("Segoe UI", Font.BOLD, 12)); l.setForeground(new Color(120,120,120)); p.add(l);
        JLabel v = new JLabel(val);   v.setFont(new Font("Segoe UI", Font.PLAIN, 12)); v.setForeground(new Color(120,120,120)); p.add(v);
    }

    private void addPair(JPanel p, String label, JComponent field) {
        JLabel l = new JLabel(label); l.setFont(new Font("Segoe UI", Font.BOLD, 13)); l.setForeground(new Color(44,62,80)); p.add(l); p.add(field);
    }

    private JTextField createField(String val) {
        JTextField f = new JTextField(val); f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200,200,200)), BorderFactory.createEmptyBorder(5,5,5,5)));
        return f;
    }

    private JButton createBtn(String text, Color color) {
        JButton b = new JButton(text); b.setFont(new Font("Segoe UI", Font.BOLD, 13)); b.setForeground(Color.WHITE);
        b.setBackground(color); b.setFocusPainted(false); b.setBorder(BorderFactory.createEmptyBorder(8,18,8,18)); b.setCursor(new Cursor(Cursor.HAND_CURSOR)); return b;
    }
}
