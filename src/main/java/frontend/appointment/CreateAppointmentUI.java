package main.java.frontend.appointment;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.swing.*;
import main.java.backend.controller.AppointmentController;
import main.java.backend.controller.ServicePackageController;
import main.java.backend.controller.AccountController;
import main.java.backend.model.ServicePackage;
import main.java.backend.model.UserAccount;
import main.java.enums.data.Role;
import main.java.dtos.AppointmentDTO;
import main.java.records.OperationResult;

/**
 * CreateAppointmentUI — Counter Staff / Manager
 *
 * Dynamically fetches all available Service Packages and Customers from the database.
 * One appointment can only have one service package, and duration is
 * automatically loaded according to the package duration.
 * Now features a robust drop-down date and time selection interface.
 */
public class CreateAppointmentUI extends JFrame {

    private JTextField titleField;
    private JComboBox<String> servicePackageCombo;
    private JComboBox<String> customerCombo;
    private JLabel durationValueLabel;
    private JTextArea descriptionArea;

    // Premium Date Selection Dropdowns
    private JComboBox<Integer> dayCombo;
    private JComboBox<String> monthCombo;
    private JComboBox<Integer> yearCombo;

    // Premium Time Selection Dropdowns
    private JComboBox<String> hourCombo;
    private JComboBox<String> minuteCombo;

    private List<ServicePackage> availablePackages = new ArrayList<>();
    private List<UserAccount> availableCustomers = new ArrayList<>();

    public CreateAppointmentUI() {
        setTitle("Create Appointment — Auttomov Software");
        setSize(520, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 244, 248));

        // ── Header ────────────────────────────────────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 5, 25));

        JLabel title = new JLabel("Create New Appointment");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.BLACK);

        headerPanel.add(title, BorderLayout.CENTER);

        // ── Form ──────────────────────────────────────────────────────────────
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 35, 10, 35));
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Service Packages combo
        servicePackageCombo = new JComboBox<>();
        servicePackageCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        servicePackageCombo.setName("comboServicePackage");

        // Duration auto-fill label
        durationValueLabel = new JLabel("Select a package");
        durationValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        durationValueLabel.setForeground(new Color(22, 160, 133));

        // Description Area
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));

        // Load Service Packages from Database
        loadServicePackages();

        // Update duration label & description when selection changes
        servicePackageCombo.addActionListener(e -> {
            int idx = servicePackageCombo.getSelectedIndex();
            if (idx >= 0 && idx < availablePackages.size()) {
                ServicePackage pkg = availablePackages.get(idx);
                durationValueLabel.setText(pkg.getServiceDuration() + " hour(s)");
                descriptionArea.setText(pkg.getServicePackageDescription() != null ? pkg.getServicePackageDescription() : "");
            }
        });

        // Trigger initial select to populate label
        if (servicePackageCombo.getItemCount() > 0) {
            servicePackageCombo.setSelectedIndex(0);
        }

        // Customer Combo Box
        customerCombo = new JComboBox<>();
        customerCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        customerCombo.setName("comboCustomer");
        loadCustomers();

        titleField = createField("e.g. Routine Service");

        // ── Date Selector Panel ───────────────────────────────────────────────
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        datePanel.setOpaque(false);

        dayCombo = new JComboBox<>();
        monthCombo = new JComboBox<>(new String[]{
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", 
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        });
        yearCombo = new JComboBox<>();

        dayCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        monthCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        yearCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Populate years: current year + next 2 years
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        yearCombo.addItem(currentYear);
        yearCombo.addItem(currentYear + 1);
        yearCombo.addItem(currentYear + 2);

        // Add action listeners to dynamically update the number of days in dayCombo
        monthCombo.addActionListener(e -> updateDays());
        yearCombo.addActionListener(e -> updateDays());
        updateDays(); // Initial run

        datePanel.add(dayCombo);
        datePanel.add(monthCombo);
        datePanel.add(yearCombo);

        // Set default selection to today's date
        LocalDate today = LocalDate.now();
        yearCombo.setSelectedItem(today.getYear());
        monthCombo.setSelectedIndex(today.getMonthValue() - 1);
        dayCombo.setSelectedItem(today.getDayOfMonth());

        // ── Time Selector Panel ───────────────────────────────────────────────
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        timePanel.setOpaque(false);

        hourCombo = new JComboBox<>(new String[]{
            "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18"
        });
        minuteCombo = new JComboBox<>(new String[]{"00", "15", "30", "45"});

        hourCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        minuteCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        hourCombo.setSelectedItem("09"); // Default to 09:00 AM

        timePanel.add(hourCombo);
        timePanel.add(new JLabel(":"));
        timePanel.add(minuteCombo);

        int row = 0;
        addFormRow(formPanel, gbc, row++, "Appointment Title *:", titleField);
        addFormRow(formPanel, gbc, row++, "Customer *:", customerCombo);
        addFormRow(formPanel, gbc, row++, "Service Package *:", servicePackageCombo);
        addFormRow(formPanel, gbc, row++, "Duration:", durationValueLabel);
        addFormRow(formPanel, gbc, row++, "Description:", descScroll);
        addFormRow(formPanel, gbc, row++, "Scheduled Date *:", datePanel);
        addFormRow(formPanel, gbc, row++, "Scheduled Time *:", timePanel);

        // ── Buttons ───────────────────────────────────────────────────────────
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setOpaque(false);

        JButton createBtn = createBtn("Create Appointment", new Color(22, 160, 133));
        createBtn.addActionListener(e -> handleCreate());

        buttonPanel.add(createBtn);

        // add(headerPanel, BorderLayout.NORTH);
        add(formPanel,   BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updateDays() {
        if (yearCombo.getSelectedItem() == null || monthCombo.getSelectedItem() == null) return;
        
        int year = (Integer) yearCombo.getSelectedItem();
        int monthIdx = monthCombo.getSelectedIndex() + 1;
        
        // Save current selected day if possible
        Object prevSelected = dayCombo.getSelectedItem();
        
        dayCombo.removeAllItems();
        int daysInMonth = Month.of(monthIdx).length(java.time.Year.isLeap(year));
        
        for (int i = 1; i <= daysInMonth; i++) {
            dayCombo.addItem(i);
        }
        
        // Restore selected day if it is still valid in new month
        if (prevSelected != null && (Integer) prevSelected <= daysInMonth) {
            dayCombo.setSelectedItem(prevSelected);
        } else {
            dayCombo.setSelectedIndex(0);
        }
    }

    private void loadServicePackages() {
        availablePackages.clear();
        servicePackageCombo.removeAllItems();

        ServicePackageController pkgController = new ServicePackageController();
        java.util.Optional<List<ServicePackage>> pkgsOpt = pkgController.retrieveAllServicePackages();
        
        if (pkgsOpt.isPresent()) {
            availablePackages.addAll(pkgsOpt.get());
            for (ServicePackage pkg : availablePackages) {
                String typeStr = pkg.getServicePackageType() != null ? pkg.getServicePackageType().getServicePkgType() : "PACKAGE";
                servicePackageCombo.addItem("[" + typeStr.toUpperCase() + "] " + pkg.getServicePackageName() + " (RM " + String.format("%.2f", pkg.getPackageBasePrice()) + ")");
            }
        }
    }

    private void loadCustomers() {
        availableCustomers.clear();
        customerCombo.removeAllItems();

        AccountController accController = new AccountController();
        java.util.Optional<List<UserAccount>> custsOpt = accController.retrieveAllAccounts(Role.CUSTOMER);

        if (custsOpt.isPresent()) {
            availableCustomers.addAll(custsOpt.get());
            for (UserAccount cust : availableCustomers) {
                customerCombo.addItem(cust.getFirstName() + " " + cust.getLastName());
            }
        }
    }

    // ── Create handler ────────────────────────────────────────────────────────
    private void handleCreate() {
        String apptTitle = titleField.getText().trim();
        int custIndex = customerCombo.getSelectedIndex();
        int pkgIndex = servicePackageCombo.getSelectedIndex();

        if (apptTitle.isEmpty() || custIndex < 0 || pkgIndex < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all required fields (*) and choose a customer and service package", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String customerId = availableCustomers.get(custIndex).getAccountID();

        // Retrieve structured Date and Time Selection
        int selectedDay = (Integer) dayCombo.getSelectedItem();
        int selectedMonthIdx = monthCombo.getSelectedIndex() + 1;
        int selectedYear = (Integer) yearCombo.getSelectedItem();

        String selectedHour = (String) hourCombo.getSelectedItem();
        String selectedMinute = (String) minuteCombo.getSelectedItem();

        LocalDate apptDate = LocalDate.of(selectedYear, selectedMonthIdx, selectedDay);
        LocalTime apptTime = LocalTime.of(Integer.parseInt(selectedHour), Integer.parseInt(selectedMinute));

        ServicePackage selectedPkg = availablePackages.get(pkgIndex);

        String desc = descriptionArea.getText().trim();
        if (desc.isEmpty()) {
            desc = selectedPkg.getServicePackageDescription() != null ? selectedPkg.getServicePackageDescription() : "Appointment for " + selectedPkg.getServicePackageName();
        }

        AppointmentDTO apptDto = new AppointmentDTO(
            apptTitle,
            desc,
            customerId,
            selectedPkg.getServicePackageID(),
            apptDate,
            apptTime
        );

        AppointmentController apptController = new AppointmentController();
        OperationResult result = apptController.createAppointment(apptDto);

        if (result.isValid()) {
            // Reset form for next appointment
            titleField.setText("");
            descriptionArea.setText("");
            
            // Set date selection back to today's date
            LocalDate today = LocalDate.now();
            yearCombo.setSelectedItem(today.getYear());
            monthCombo.setSelectedIndex(today.getMonthValue() - 1);
            dayCombo.setSelectedItem(today.getDayOfMonth());

            hourCombo.setSelectedItem("09");
            minuteCombo.setSelectedIndex(0);

            if (servicePackageCombo.getItemCount() > 0) {
                servicePackageCombo.setSelectedIndex(0);
            }
            if (customerCombo.getItemCount() > 0) {
                customerCombo.setSelectedIndex(0);
            }
            JOptionPane.showMessageDialog(this,
                    result.generalMessage() + "\n\nPlease use 'Assign Technician' tab to assign.",
                    "Appointment Created", JOptionPane.INFORMATION_MESSAGE);

            this.dispose();
        } else {
            StringBuilder sb = new StringBuilder();
            if (!result.fieldErrors().isEmpty())
                result.fieldErrors().forEach((f, msg) -> sb.append("- ").append(msg).append("\n"));
            else
                sb.append(result.generalMessage());
            JOptionPane.showMessageDialog(this, sb.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row,
                             String labelText, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        JLabel l = new JLabel(labelText);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(new Color(44, 62, 80));
        panel.add(l, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        panel.add(field, gbc);
    }

    private JTextField createField(String placeholder) {
        JTextField f = new JTextField(placeholder);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setForeground(new Color(150, 160, 170));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (f.getForeground().equals(new Color(150, 160, 170))) {
                    f.setText("");
                    f.setForeground(new Color(44, 62, 80));
                }
            }
        });
        return f;
    }

    private JButton createBtn(String text, Color color) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setBackground(color);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(9, 20, 9, 20));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}
