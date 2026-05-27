package main.java.frontend.menu;

import main.java.backend.controller.AccountController;
import main.java.backend.model.UserAccount;
import main.java.dtos.UserAccountDTO;
import main.java.dtos.StaffAccountDTO;
import main.java.frontend.dashboard.CustomerDashboardUI;
import main.java.frontend.dashboard.StaffDashboardUI;
import main.java.frontend.shared.SessionManager;
import main.java.enums.data.Role;
import main.java.enums.data.JobPosition;
import main.java.enums.data.AccountStatus;
import main.java.records.OperationResult;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class mainMenuUI extends JFrame {

    private JComboBox<Role> roleComboBox;
    private JTextField emailField;
    private JPasswordField passwordField;

    // Customer Registration Fields
    private JTextField regUserIdField;
    private JTextField regFirstNameField;
    private JTextField regLastNameField;
    private JTextField regEmailField;
    private JTextField regPhoneField;
    private JPasswordField regPasswordField;
    private JPasswordField regConfirmPasswordField;

    // Staff Registration Fields
    private JTextField regStaffUserIdField;
    private JTextField regStaffFirstNameField;
    private JTextField regStaffLastNameField;
    private JTextField regStaffEmailField;
    private JTextField regStaffPhoneField;
    private JPasswordField regStaffPasswordField;
    private JPasswordField regStaffConfirmPasswordField;
    private JComboBox<JobPosition> regStaffJobPosCombo;
    private JTextField regStaffSpecField;

    private CardLayout cardLayout;
    private JPanel rightContainer;

    private boolean isCustomerActive = true;

    public mainMenuUI() {
        setTitle("Auttomov Software Suite");
        setSize(820, 560); // Spacious comfortable size for forms
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new GridLayout(1, 2, 0, 0));

        // ── 1. LEFT SIDE: BRANDING & FEATURES (GRADIENT PANEL) ─────────────────
        GradientPanel leftPanel = new GradientPanel();
        leftPanel.setLayout(new GridBagLayout());
        leftPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbcL = new GridBagConstraints();
        gbcL.fill = GridBagConstraints.BOTH;
        gbcL.gridx = 0;
        gbcL.weightx = 1.0;

        // --- UPPER SECTION: LOGO IMAGE ---
        JPanel upperPanel = new JPanel(new GridBagLayout());
        upperPanel.setOpaque(false);
        GridBagConstraints gbcU = new GridBagConstraints();
        gbcU.fill = GridBagConstraints.CENTER;

        ImageIcon rawIcon = new ImageIcon("../APUASC-Auttomov/src/main/java/frontend/logo/Auttomov_logo.png");
        Image rawImg = rawIcon.getImage();
        Image scaledImg = rawImg.getScaledInstance(320, -1, Image.SCALE_SMOOTH);
        ImageIcon logoIcon = new ImageIcon(scaledImg);
        JLabel logoLabel = new JLabel(logoIcon);
        upperPanel.add(logoLabel, gbcU);

        gbcL.gridy = 0;
        gbcL.weighty = 0.4;
        leftPanel.add(upperPanel, gbcL);

        // --- LOWER SECTION: TITLE, SUBTITLE & BULLETS ---
        JPanel lowerPanel = new JPanel(new GridBagLayout());
        lowerPanel.setOpaque(false);
        GridBagConstraints gbcD = new GridBagConstraints();
        gbcD.fill = GridBagConstraints.HORIZONTAL;
        gbcD.gridx = 0;
        gbcD.weightx = 1.0;

        // App Title
        JLabel appTitle = new JLabel("AUTTOMOV", SwingConstants.CENTER);
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        appTitle.setForeground(Color.WHITE);
        gbcD.gridy = 0; gbcD.insets = new Insets(0, 0, 4, 0);
        lowerPanel.add(appTitle, gbcD);

        // Subtitle
        JLabel subtitle = new JLabel("Automotive & Service Suite", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(new Color(200, 220, 255));
        gbcD.gridy = 1; gbcD.insets = new Insets(0, 0, 20, 0);
        lowerPanel.add(subtitle, gbcD);

        // Features list
        String[] features = {
            "•  Unified Service Scheduling",
            "•  Instant Technician Assignment",
            "•  Real-time Invoice & Payments",
            "•  Customer Feedback Analytics"
        };
        int fRow = 2;
        for (String feat : features) {
            JLabel featLbl = new JLabel(feat, SwingConstants.CENTER);
            featLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            featLbl.setForeground(Color.WHITE);
            gbcD.gridy = fRow++; gbcD.insets = new Insets(0, 0, 10, 0);
            lowerPanel.add(featLbl, gbcD);
        }

        gbcL.gridy = 1;
        gbcL.weighty = 0.6;
        gbcL.insets = new Insets(20, 0, 0, 0);
        leftPanel.add(lowerPanel, gbcL);

        // ── 2. RIGHT SIDE: CARD CONTAINER (LOGIN & REGISTER) ─────────────────────
        cardLayout = new CardLayout();
        rightContainer = new JPanel(cardLayout);
        rightContainer.setBackground(Color.WHITE);

        // Build Login and Register panels
        JPanel loginPanel = createLoginPanel();
        JPanel registerPanel = createRegisterPanel();

        rightContainer.add(loginPanel, "login");
        rightContainer.add(registerPanel, "register");

        add(leftPanel);
        add(rightContainer);
    }

    // ── CREATE LOGIN PANEL ───────────────────────────────────────────────────
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new EmptyBorder(30, 45, 30, 45));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        // Login Title
        JLabel loginTitle = new JLabel("Welcome Back");
        loginTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        loginTitle.setForeground(new Color(30, 41, 59));
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 4, 0);
        panel.add(loginTitle, gbc);

        // Subtext
        JLabel loginSubtext = new JLabel("Please enter your account details below.");
        loginSubtext.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        loginSubtext.setForeground(new Color(100, 116, 139));
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 24, 0);
        panel.add(loginSubtext, gbc);

        // Role Dropdown
        JLabel roleLabel = new JLabel("Select Role");
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        roleLabel.setForeground(new Color(71, 85, 105));
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 6, 0);
        panel.add(roleLabel, gbc);

        roleComboBox = new JComboBox<>(Role.values());
        roleComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roleComboBox.setPreferredSize(new Dimension(220, 34));
        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 16, 0);
        panel.add(roleComboBox, gbc);

        // Email Address
        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        emailLabel.setForeground(new Color(71, 85, 105));
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 6, 0);
        panel.add(emailLabel, gbc);

        emailField = new JTextField();
        styleTextField(emailField);
        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 16, 0);
        panel.add(emailField, gbc);

        // Password
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passLabel.setForeground(new Color(71, 85, 105));
        gbc.gridy = 6; gbc.insets = new Insets(0, 0, 6, 0);
        panel.add(passLabel, gbc);

        passwordField = new JPasswordField();
        styleTextField(passwordField);
        gbc.gridy = 7; gbc.insets = new Insets(0, 0, 8, 0);
        panel.add(passwordField, gbc);

        // Show Password check
        JCheckBox showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPasswordCheckBox.setOpaque(false);
        showPasswordCheckBox.setFocusPainted(false);
        showPasswordCheckBox.setForeground(new Color(100, 116, 139));
        char defaultEchoChar = passwordField.getEchoChar();
        showPasswordCheckBox.addActionListener(e -> {
            if (showPasswordCheckBox.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar(defaultEchoChar);
            }
        });
        gbc.gridy = 8; gbc.insets = new Insets(0, 0, 20, 0);
        panel.add(showPasswordCheckBox, gbc);

        // Sign In Button
        JButton loginButton = new JButton("Sign In");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(new Color(59, 130, 246));
        loginButton.setOpaque(true);
        loginButton.setContentAreaFilled(true);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(59, 130, 246)),
            BorderFactory.createEmptyBorder(10, 0, 10, 0)
        ));
        loginButton.addActionListener(e -> handleLogin());
        gbc.gridy = 9; gbc.insets = new Insets(0, 0, 20, 0);
        panel.add(loginButton, gbc);

        // Register label link
        JPanel registerRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        registerRow.setOpaque(false);
        JLabel noAccount = new JLabel("Don't have an account?");
        noAccount.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        noAccount.setForeground(new Color(100, 116, 139));
        
        JLabel registerLink = new JLabel("Register");
        registerLink.setFont(new Font("Segoe UI", Font.BOLD, 12));
        registerLink.setForeground(new Color(59, 130, 246));
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(rightContainer, "register");
            }
        });

        registerRow.add(noAccount);
        registerRow.add(registerLink);
        gbc.gridy = 10; gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(registerRow, gbc);

        return panel;
    }

    // ── CREATE REGISTER PANEL WITH ROLE SELECTOR & INTERNAL CARDS ────────────
    private JPanel createRegisterPanel() {
        JPanel mainRegPanel = new JPanel(new BorderLayout());
        mainRegPanel.setBackground(Color.WHITE);

        // 1. North Section: Title, Subtitle, and Role Selector
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.setOpaque(false);
        northPanel.setBorder(new EmptyBorder(15, 30, 5, 30));

        JLabel regTitle = new JLabel("Create an Account");
        regTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        regTitle.setForeground(new Color(30, 41, 59));
        regTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        northPanel.add(regTitle);

        JLabel regSubtext = new JLabel("Join Auttomov Software today.");
        regSubtext.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        regSubtext.setForeground(new Color(100, 116, 139));
        regSubtext.setAlignmentX(Component.CENTER_ALIGNMENT);
        northPanel.add(regSubtext);
        northPanel.add(Box.createVerticalStrut(10));

        // Styled Role Toggle Selector
        JPanel togglePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        togglePanel.setOpaque(false);

        JButton customerToggle = new JButton("Customer");
        JButton staffToggle = new JButton("Staff / Manager");
        Dimension toggleSize = new Dimension(160, 32);
        customerToggle.setPreferredSize(toggleSize);
        staffToggle.setPreferredSize(toggleSize);
        customerToggle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        staffToggle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        customerToggle.setFocusPainted(false);
        staffToggle.setFocusPainted(false);
        customerToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        staffToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Color activeColor = new Color(59, 130, 246);
        Color activeText = Color.WHITE;
        Color inactiveColor = new Color(241, 245, 249);
        Color inactiveText = new Color(71, 85, 105);

        CardLayout formCardLayout = new CardLayout();
        JPanel formCardPanel = new JPanel(formCardLayout);
        formCardPanel.setOpaque(false);

        Runnable updateToggles = () -> {
            if (isCustomerActive) {
                customerToggle.setBackground(activeColor);
                customerToggle.setForeground(activeText);
                customerToggle.setBorder(BorderFactory.createLineBorder(activeColor, 1));
                
                staffToggle.setBackground(inactiveColor);
                staffToggle.setForeground(inactiveText);
                staffToggle.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
            } else {
                staffToggle.setBackground(activeColor);
                staffToggle.setForeground(activeText);
                staffToggle.setBorder(BorderFactory.createLineBorder(activeColor, 1));
                
                customerToggle.setBackground(inactiveColor);
                customerToggle.setForeground(inactiveText);
                customerToggle.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
            }
        };

        customerToggle.addActionListener(e -> {
            isCustomerActive = true;
            updateToggles.run();
            formCardLayout.show(formCardPanel, "customer");
        });
        staffToggle.addActionListener(e -> {
            isCustomerActive = false;
            updateToggles.run();
            formCardLayout.show(formCardPanel, "staff");
        });

        updateToggles.run(); // Initial paint
        togglePanel.add(customerToggle);
        togglePanel.add(staffToggle);
        northPanel.add(togglePanel);

        // 2. Center Section: Form Card Panels (Customer and Staff)
        JPanel customerForm = createCustomerFormPanel();
        JPanel staffForm = createStaffFormPanel();

        formCardPanel.add(customerForm, "customer");
        formCardPanel.add(staffForm, "staff");

        JScrollPane scrollPane = new JScrollPane(formCardPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        mainRegPanel.add(northPanel, BorderLayout.NORTH);
        mainRegPanel.add(scrollPane, BorderLayout.CENTER);

        return mainRegPanel;
    }

    // ── CUSTOMER SIGNUP CARD ────────────────────────────────────────────────
    private JPanel createCustomerFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 5, 4, 5);

        // First Name & Last Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5; gbc.gridwidth = 1;
        JLabel fNameLbl = new JLabel("First Name");
        fNameLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        fNameLbl.setForeground(new Color(71, 85, 105));
        panel.add(fNameLbl, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        JLabel lNameLbl = new JLabel("Last Name");
        lNameLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lNameLbl.setForeground(new Color(71, 85, 105));
        panel.add(lNameLbl, gbc);

        regFirstNameField = new JTextField();
        styleTextField(regFirstNameField);
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(regFirstNameField, gbc);

        regLastNameField = new JTextField();
        styleTextField(regLastNameField);
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(regLastNameField, gbc);

        // Email & Phone Number
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel emailLbl = new JLabel("Email Address");
        emailLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        emailLbl.setForeground(new Color(71, 85, 105));
        panel.add(emailLbl, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        JLabel phoneLbl = new JLabel("Phone Number");
        phoneLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        phoneLbl.setForeground(new Color(71, 85, 105));
        panel.add(phoneLbl, gbc);

        regEmailField = new JTextField();
        styleTextField(regEmailField);
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(regEmailField, gbc);

        regPhoneField = new JTextField();
        styleTextField(regPhoneField);
        gbc.gridx = 1; gbc.gridy = 3;
        panel.add(regPhoneField, gbc);

        // User ID
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.weightx = 1.0;
        JLabel idLbl = new JLabel("User ID (IC/Passport)");
        idLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        idLbl.setForeground(new Color(71, 85, 105));
        panel.add(idLbl, gbc);

        regUserIdField = new JTextField();
        styleTextField(regUserIdField);
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(regUserIdField, gbc);

        gbc.gridwidth = 1;

        // Password & Confirm Password
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0.5;
        JLabel passLbl = new JLabel("Password");
        passLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passLbl.setForeground(new Color(71, 85, 105));
        panel.add(passLbl, gbc);

        gbc.gridx = 1; gbc.gridy = 6;
        JLabel confPassLbl = new JLabel("Confirm Password");
        confPassLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        confPassLbl.setForeground(new Color(71, 85, 105));
        panel.add(confPassLbl, gbc);

        regPasswordField = new JPasswordField();
        styleTextField(regPasswordField);
        gbc.gridx = 0; gbc.gridy = 7;
        panel.add(regPasswordField, gbc);

        regConfirmPasswordField = new JPasswordField();
        styleTextField(regConfirmPasswordField);
        gbc.gridx = 1; gbc.gridy = 7;
        panel.add(regConfirmPasswordField, gbc);

        // Show Passwords check
        JCheckBox showPassCheck = new JCheckBox("Show Passwords");
        showPassCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPassCheck.setOpaque(false);
        showPassCheck.setFocusPainted(false);
        showPassCheck.setForeground(new Color(100, 116, 139));
        char defaultEcho = regPasswordField.getEchoChar();
        showPassCheck.addActionListener(e -> {
            char echo = showPassCheck.isSelected() ? (char) 0 : defaultEcho;
            regPasswordField.setEchoChar(echo);
            regConfirmPasswordField.setEchoChar(echo);
        });
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.insets = new Insets(2, 5, 8, 5);
        panel.add(showPassCheck, gbc);

        // Register Button
        JButton registerButton = new JButton("COMPLETE SIGN UP");
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        registerButton.setForeground(Color.WHITE);
        registerButton.setBackground(new Color(34, 197, 94)); // Positive green
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(34, 197, 94)),
            BorderFactory.createEmptyBorder(8, 0, 8, 0)
        ));
        registerButton.addActionListener(e -> handleRegisterSubmit());
        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 2; gbc.insets = new Insets(0, 5, 8, 5);
        panel.add(registerButton, gbc);

        // Already have account?
        JPanel backToLoginRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        backToLoginRow.setOpaque(false);
        JLabel hasAccount = new JLabel("Already have an account?");
        hasAccount.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hasAccount.setForeground(new Color(100, 116, 139));

        JLabel signInLink = new JLabel("Sign In");
        signInLink.setFont(new Font("Segoe UI", Font.BOLD, 12));
        signInLink.setForeground(new Color(59, 130, 246));
        signInLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signInLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(rightContainer, "login");
            }
        });
        backToLoginRow.add(hasAccount);
        backToLoginRow.add(signInLink);
        gbc.gridx = 0; gbc.gridy = 10; gbc.gridwidth = 2; gbc.insets = new Insets(0, 5, 10, 5);
        panel.add(backToLoginRow, gbc);

        return panel;
    }

    // ── STAFF SIGNUP CARD ────────────────────────────────────────────────────
    private JPanel createStaffFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 5, 4, 5);

        // First Name & Last Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5; gbc.gridwidth = 1;
        JLabel fNameLbl = new JLabel("First Name");
        fNameLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        fNameLbl.setForeground(new Color(71, 85, 105));
        panel.add(fNameLbl, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        JLabel lNameLbl = new JLabel("Last Name");
        lNameLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lNameLbl.setForeground(new Color(71, 85, 105));
        panel.add(lNameLbl, gbc);

        regStaffFirstNameField = new JTextField();
        styleTextField(regStaffFirstNameField);
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(regStaffFirstNameField, gbc);

        regStaffLastNameField = new JTextField();
        styleTextField(regStaffLastNameField);
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(regStaffLastNameField, gbc);

        // Email & Phone Number
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel emailLbl = new JLabel("Email Address");
        emailLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        emailLbl.setForeground(new Color(71, 85, 105));
        panel.add(emailLbl, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        JLabel phoneLbl = new JLabel("Phone Number");
        phoneLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        phoneLbl.setForeground(new Color(71, 85, 105));
        panel.add(phoneLbl, gbc);

        regStaffEmailField = new JTextField();
        styleTextField(regStaffEmailField);
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(regStaffEmailField, gbc);

        regStaffPhoneField = new JTextField();
        styleTextField(regStaffPhoneField);
        gbc.gridx = 1; gbc.gridy = 3;
        panel.add(regStaffPhoneField, gbc);

        // User ID
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.weightx = 1.0;
        JLabel idLbl = new JLabel("User ID (IC/Passport)");
        idLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        idLbl.setForeground(new Color(71, 85, 105));
        panel.add(idLbl, gbc);

        regStaffUserIdField = new JTextField();
        styleTextField(regStaffUserIdField);
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(regStaffUserIdField, gbc);

        gbc.gridwidth = 1;

        // Password & Confirm Password
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0.5;
        JLabel passLbl = new JLabel("Password");
        passLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passLbl.setForeground(new Color(71, 85, 105));
        panel.add(passLbl, gbc);

        gbc.gridx = 1; gbc.gridy = 6;
        JLabel confPassLbl = new JLabel("Confirm Password");
        confPassLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        confPassLbl.setForeground(new Color(71, 85, 105));
        panel.add(confPassLbl, gbc);

        regStaffPasswordField = new JPasswordField();
        styleTextField(regStaffPasswordField);
        gbc.gridx = 0; gbc.gridy = 7;
        panel.add(regStaffPasswordField, gbc);

        regStaffConfirmPasswordField = new JPasswordField();
        styleTextField(regStaffConfirmPasswordField);
        gbc.gridx = 1; gbc.gridy = 7;
        panel.add(regStaffConfirmPasswordField, gbc);

        // Job Position & Specialization (dynamic grey out)
        gbc.gridx = 0; gbc.gridy = 8;
        JLabel jobLbl = new JLabel("Job Position");
        jobLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        jobLbl.setForeground(new Color(71, 85, 105));
        panel.add(jobLbl, gbc);

        gbc.gridx = 1; gbc.gridy = 8;
        JLabel specLbl = new JLabel("Specialization (Technician)");
        specLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        specLbl.setForeground(new Color(71, 85, 105));
        panel.add(specLbl, gbc);

        regStaffJobPosCombo = new JComboBox<>(JobPosition.values());
        regStaffJobPosCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        regStaffJobPosCombo.setPreferredSize(new Dimension(170, 34));
        gbc.gridx = 0; gbc.gridy = 9;
        panel.add(regStaffJobPosCombo, gbc);

        regStaffSpecField = new JTextField();
        styleTextField(regStaffSpecField);
        // Initially greyed out because manager is selected by default
        regStaffSpecField.setEnabled(false);
        regStaffSpecField.setBackground(new Color(241, 245, 249));
        gbc.gridx = 1; gbc.gridy = 9;
        panel.add(regStaffSpecField, gbc);

        regStaffJobPosCombo.addActionListener(e -> {
            boolean isTech = regStaffJobPosCombo.getSelectedItem() == JobPosition.TECHNICIAN;
            regStaffSpecField.setEnabled(isTech);
            regStaffSpecField.setBackground(isTech ? Color.WHITE : new Color(241, 245, 249));
            if (!isTech) regStaffSpecField.setText("");
        });

        // Show Passwords check
        JCheckBox showPassCheck = new JCheckBox("Show Passwords");
        showPassCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPassCheck.setOpaque(false);
        showPassCheck.setFocusPainted(false);
        showPassCheck.setForeground(new Color(100, 116, 139));
        char defaultEcho = regStaffPasswordField.getEchoChar();
        showPassCheck.addActionListener(e -> {
            char echo = showPassCheck.isSelected() ? (char) 0 : defaultEcho;
            regStaffPasswordField.setEchoChar(echo);
            regStaffConfirmPasswordField.setEchoChar(echo);
        });
        gbc.gridx = 0; gbc.gridy = 10; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.insets = new Insets(2, 5, 8, 5);
        panel.add(showPassCheck, gbc);

        // Register Button
        JButton registerButton = new JButton("COMPLETE STAFF SIGN UP");
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        registerButton.setForeground(Color.WHITE);
        registerButton.setBackground(new Color(59, 130, 246)); // Theme blue
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(59, 130, 246)),
            BorderFactory.createEmptyBorder(8, 0, 8, 0)
        ));
        registerButton.addActionListener(e -> handleStaffRegisterSubmit());
        gbc.gridx = 0; gbc.gridy = 11; gbc.gridwidth = 2; gbc.insets = new Insets(0, 5, 8, 5);
        panel.add(registerButton, gbc);

        // Already have account?
        JPanel backToLoginRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        backToLoginRow.setOpaque(false);
        JLabel hasAccount = new JLabel("Already have an account?");
        hasAccount.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hasAccount.setForeground(new Color(100, 116, 139));

        JLabel signInLink = new JLabel("Sign In");
        signInLink.setFont(new Font("Segoe UI", Font.BOLD, 12));
        signInLink.setForeground(new Color(59, 130, 246));
        signInLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signInLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(rightContainer, "login");
            }
        });
        backToLoginRow.add(hasAccount);
        backToLoginRow.add(signInLink);
        gbc.gridx = 0; gbc.gridy = 12; gbc.gridwidth = 2; gbc.insets = new Insets(0, 5, 10, 5);
        panel.add(backToLoginRow, gbc);

        return panel;
    }

    private void styleTextField(JTextField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setPreferredSize(new Dimension(170, 34));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225)), 
            BorderFactory.createEmptyBorder(2, 8, 4, 8)));
    }

    private void handleLogin() {
        Role role = (Role) roleComboBox.getSelectedItem();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        AccountController accountController = new AccountController();
        UserAccountDTO loginDto = new UserAccountDTO(role, email, password);
        OperationResult result = accountController.loginAccount(loginDto);

        if (!result.isValid()) {
            StringBuilder sb = new StringBuilder();
            if (!result.fieldErrors().isEmpty()) {
                result.fieldErrors().forEach((f, msg) -> sb.append("- ").append(msg).append("\n"));
            } else if (result.generalMessage() != null) {
                sb.append(result.generalMessage());
            } else {
                sb.append("Validation failed.");
            }
            JOptionPane.showMessageDialog(this, sb.toString(),
                "Login Failed", JOptionPane.ERROR_MESSAGE);
        } else {
            java.util.Optional<UserAccount> loggedInUserOpt = accountController.retrieveAccountById(email, role);
            if (loggedInUserOpt.isPresent()) {
                UserAccount user = loggedInUserOpt.get();
                if (user.getAccountStatus() == AccountStatus.PENDING) {
                    JOptionPane.showMessageDialog(this,
                        "Your account is pending approval. Please wait for a manager to activate your account.",
                        "Pending Approval", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                SessionManager.setCurrentUser(user);

                if (role == Role.CUSTOMER) {
                    new CustomerDashboardUI().setVisible(true);
                } else {
                    new StaffDashboardUI().setVisible(true);
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Could not fetch user profile details.",
                    "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleRegisterSubmit() {
        String userId = regUserIdField.getText().trim();
        String fName = regFirstNameField.getText().trim();
        String lName = regLastNameField.getText().trim();
        String email = regEmailField.getText().trim();
        String phone = regPhoneField.getText().trim();
        String pass = new String(regPasswordField.getPassword());
        String confPass = new String(regConfirmPasswordField.getPassword());

        UserAccountDTO userDto = new UserAccountDTO(userId, Role.CUSTOMER, fName, lName, email, phone, pass, confPass);
        AccountController accountController = new AccountController();
        OperationResult result = accountController.createAccount(userDto);

        if (!result.isValid()) {
            StringBuilder sb = new StringBuilder("There are problems in signing up account:\n\n");
            result.fieldErrors().forEach((field, msg) -> sb.append("- ").append(msg).append("\n"));
            if (result.fieldErrors().isEmpty()) {
                sb.append(result.generalMessage());
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "Validation Failed", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Registration Successful! Your can now log in your account.", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Clear fields
            regUserIdField.setText("");
            regFirstNameField.setText("");
            regLastNameField.setText("");
            regEmailField.setText("");
            regPhoneField.setText("");
            regPasswordField.setText("");
            regConfirmPasswordField.setText("");

            // Switch back to Login card
            cardLayout.show(rightContainer, "login");
        }
    }

    private void handleStaffRegisterSubmit() {
        String userId = regStaffUserIdField.getText().trim();
        String fName = regStaffFirstNameField.getText().trim();
        String lName = regStaffLastNameField.getText().trim();
        String email = regStaffEmailField.getText().trim();
        String phone = regStaffPhoneField.getText().trim();
        String pass = new String(regStaffPasswordField.getPassword());
        String confPass = new String(regStaffConfirmPasswordField.getPassword());
        JobPosition jobPos = (JobPosition) regStaffJobPosCombo.getSelectedItem();
        String spec = (jobPos == JobPosition.TECHNICIAN) ? regStaffSpecField.getText().trim() : null;

        StaffAccountDTO staffDto = new StaffAccountDTO(
            userId, Role.STAFF, fName, lName, email, phone, pass, confPass,
            jobPos, main.java.enums.data.EmploymentStatus.FULL_TIME, spec
        );

        AccountController accountController = new AccountController();
        OperationResult result = accountController.createAccount(staffDto);

        if (!result.isValid()) {
            StringBuilder sb = new StringBuilder("There are problems in signing up account:\n\n");
            result.fieldErrors().forEach((field, msg) -> sb.append("- ").append(msg).append("\n"));
            if (result.fieldErrors().isEmpty()) {
                sb.append(result.generalMessage());
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "Validation Failed", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Registration Successful! Your account is pending manager approval.\n" +
                "You cannot log in until your account is approved and activated.", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Clear fields
            regStaffUserIdField.setText("");
            regStaffFirstNameField.setText("");
            regStaffLastNameField.setText("");
            regStaffEmailField.setText("");
            regStaffPhoneField.setText("");
            regStaffPasswordField.setText("");
            regStaffConfirmPasswordField.setText("");
            regStaffSpecField.setText("");
            regStaffJobPosCombo.setSelectedIndex(0);

            // Switch back to Login card
            cardLayout.show(rightContainer, "login");
        }
    }

    // Custom gradient drawing panel
    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            int w = getWidth();
            int h = getHeight();
            Color color1 = new Color(15, 23, 42); // slate 900
            Color color2 = new Color(37, 99, 235); // blue 600
            GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, w, h);
        }
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
//            new mainMenuUI().setVisible(true);
//        });
//    }
}
