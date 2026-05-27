package main.java.frontend.account;

import java.awt.*;
import javax.swing.*;
import main.java.backend.controller.AccountController;
import main.java.backend.model.UserAccount;
import main.java.backend.util.PswdHasher;
import main.java.dtos.UserAccountDTO;
import main.java.frontend.shared.SessionManager;
import main.java.records.OperationResult;

public class UpdatePasswordUI extends JFrame {

    private JPasswordField currentPassField, newPassField, confirmPassField;

    public UpdatePasswordUI() {
        setTitle("Update Password - Auttomov Software");
        setSize(420, 380);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 244, 248));

        // ── Header ──────────────────────────────────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        JLabel title = new JLabel("Update Password");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        headerPanel.add(title);

        // ── Form ─────────────────────────────────────────────────────────────
        JPanel formPanel = new JPanel(new GridLayout(6, 1, 10, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 10, 40));
        formPanel.setOpaque(false);

        currentPassField = createPassField();
        newPassField     = createPassField();
        confirmPassField = createPassField();

        formPanel.add(createLabel("Current Password:"));
        formPanel.add(currentPassField);
        formPanel.add(createLabel("New Password:"));
        formPanel.add(newPassField);
        formPanel.add(createLabel("Confirm New Password:"));
        formPanel.add(confirmPassField);

        // ── Buttons ──────────────────────────────────────────────────────────
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setOpaque(false);
        JButton saveBtn = createBtn("Update Password", new Color(142, 68, 173));

        saveBtn.addActionListener(e -> handleUpdate());
        buttonPanel.add(saveBtn);

        // add(headerPanel, BorderLayout.NORTH);
        add(formPanel,   BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleUpdate() {
        UserAccount user      = SessionManager.getCurrentUser();
        String currentPass    = new String(currentPassField.getPassword());
        String newPass        = new String(newPassField.getPassword());
        String confirmPass    = new String(confirmPassField.getPassword());

        // 1. Verify current password
        if (!PswdHasher.verifyPassword(currentPass, user)) {
            JOptionPane.showMessageDialog(this, "Current password is incorrect.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
 
        // 2. Delegate format checking and updating to AccountController
        AccountController accountController = new AccountController();
        UserAccountDTO userAccountDTO = new UserAccountDTO(user.getAccountID(), SessionManager.getCurrentRole(), newPass, confirmPass);
        OperationResult result = accountController.updateAccountPassword(userAccountDTO);
 
        if (result.isValid()) {
            JOptionPane.showMessageDialog(this, "Password updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            // Refresh Session User Info from database
            java.util.Optional<UserAccount> updatedUserOpt = accountController.retrieveAccountById(user.getEmailAddress(), SessionManager.getCurrentRole());
            if (updatedUserOpt.isPresent()) {
                SessionManager.setCurrentUser(updatedUserOpt.get());
            }
            // Password updated; panel stays in place (embedded in dashboard)
        } else {
            StringBuilder sb = new StringBuilder();
            if (!result.fieldErrors().isEmpty())
                result.fieldErrors().forEach((f, msg) -> sb.append("- ").append(msg).append("\n"));
            else
                sb.append(result.generalMessage());
            JOptionPane.showMessageDialog(this, sb.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text); l.setFont(new Font("Segoe UI", Font.BOLD, 13)); l.setForeground(new Color(44, 62, 80)); return l;
    }

    private JPasswordField createPassField() {
        JPasswordField f = new JPasswordField(); f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200,200,200)), BorderFactory.createEmptyBorder(5,5,5,5)));
        return f;
    }

    private JButton createBtn(String text, Color color) {
        JButton b = new JButton(text); b.setFont(new Font("Segoe UI", Font.BOLD, 13)); b.setForeground(Color.WHITE);
        b.setBackground(color); b.setFocusPainted(false); b.setBorder(BorderFactory.createEmptyBorder(8,18,8,18)); b.setCursor(new Cursor(Cursor.HAND_CURSOR)); return b;
    }
}
