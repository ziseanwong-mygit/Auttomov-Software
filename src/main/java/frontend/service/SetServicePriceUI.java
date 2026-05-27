package main.java.frontend.service;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import main.java.backend.controller.ServiceActivityController;
import main.java.backend.controller.ServicePackageController;
import main.java.backend.controller.VehiclePartController;
import main.java.backend.model.ServiceActivity;
import main.java.backend.model.ServicePackage;
import main.java.backend.model.VehiclePart;
import main.java.dtos.ServiceActivityDTO;
import main.java.dtos.ServicePackageDTO;
import main.java.dtos.VehiclePartDTO;
import main.java.enums.data.ServicePackageType;
import main.java.records.OperationResult;

/**
 * SetServicePriceUI — Manager Service Configuration Hub
 * 
 * Includes 3 tabs to manage the complete Automotive service catalog:
 * 1. Service Packages (Normal/Major types linking to activities and parts)
 * 2. Service Activities (Individual labor/jobs)
 * 3. Vehicle Parts (Physical parts in inventory)
 */
public class SetServicePriceUI extends JFrame implements main.java.frontend.shared.Refreshable {

    private JTabbedPane tabbedPane;

    // --- Service Packages Tab Components ---
    private List<ServicePackage> packageList = new ArrayList<>();
    private DefaultTableModel packageModel;
    private JTable packageTable;

    // --- Service Activities Tab Components ---
    private List<ServiceActivity> activityList = new ArrayList<>();
    private DefaultTableModel activityModel;
    private JTable activityTable;

    // --- Vehicle Parts Tab Components ---
    private List<VehiclePart> partList = new ArrayList<>();
    private DefaultTableModel partModel;
    private JTable partTable;

    public SetServicePriceUI() {
        setTitle("Set Service Prices & Catalog — Auttomov Software");
        setSize(960, 560);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header Panel removed to avoid duplication when embedded in dashboard

        // Tabbed Pane
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabbedPane.addTab("Service Packages", buildPackagesTabPanel());
        tabbedPane.addTab("Service Activities", buildActivitiesTabPanel());
        tabbedPane.addTab("Vehicle Parts", buildPartsTabPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // Load all initial data
        reloadData();
    }

    private JPanel buildHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(15, 23, 42)); // Slate dark theme matching mainMenu
        panel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel title = new JLabel("Service & Parts Configuration Hub");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Configure service packages, individual labor activities, and spare vehicle parts.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(200, 220, 255));

        panel.add(title, BorderLayout.WEST);
        panel.add(subtitle, BorderLayout.SOUTH);
        return panel;
    }

    private void reloadData() {
        loadServicePackages();
        loadServiceActivities();
        loadVehicleParts();
    }

    @Override
    public void refreshData() {
        reloadData();
    }

    // ── TAB 1: SERVICE PACKAGES ──────────────────────────────────────────────
    private JPanel buildPackagesTabPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(new EmptyBorder(10, 12, 10, 12));
        panel.setOpaque(false);

        String[] cols = {"Package ID", "Package Name", "Type", "Duration (Hrs)", "Base Price (RM)", "Activities", "Vehicle Parts"};
        packageModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        packageTable = new JTable(packageModel);
        styleTable(packageTable);
        packageTable.getColumnModel().getColumn(0).setMinWidth(0);
        packageTable.getColumnModel().getColumn(0).setMaxWidth(0);
        packageTable.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scroll = new JScrollPane(packageTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        panel.add(scroll, BorderLayout.CENTER);

        // Buttons Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setOpaque(false);

        JButton btnCreate = createBtn("Create Package", new Color(16, 185, 129));
        JButton btnEdit = createBtn("Edit Package", new Color(37, 99, 235));
        JButton btnDelete = createBtn("Delete Package", new Color(239, 68, 68));

        btnCreate.addActionListener(e -> showPackageDialog(null));
        btnEdit.addActionListener(e -> {
            int row = packageTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a package to edit first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            showPackageDialog(packageList.get(row));
        });
        btnDelete.addActionListener(e -> {
            int row = packageTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a package to delete first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            handleDeletePackage(packageList.get(row));
        });

        btnPanel.add(btnCreate);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadServicePackages() {
        packageList.clear();
        packageModel.setRowCount(0);
        ServicePackageController ctrl = new ServicePackageController();
        java.util.Optional<List<ServicePackage>> listOpt = ctrl.retrieveAllServicePackages();
        if (listOpt.isPresent()) {
            packageList.addAll(listOpt.get());
            for (ServicePackage pkg : packageList) {
                String acts = pkg.getServiceActivityList().stream().map(ServiceActivity::getServiceActivityName).collect(Collectors.joining(", "));
                String parts = pkg.getVehiclePartList().stream().map(VehiclePart::getVehiclePartName).collect(Collectors.joining(", "));
                packageModel.addRow(new Object[]{
                    pkg.getServicePackageID(),
                    pkg.getServicePackageName(),
                    pkg.getServicePackageType().getServicePkgType(),
                    pkg.getServiceDuration(),
                    String.format("%.2f", pkg.getPackageBasePrice()),
                    acts.isBlank() ? "None" : acts,
                    parts.isBlank() ? "None" : parts
                });
            }
        }
    }

    private void showPackageDialog(ServicePackage existing) {
        JDialog dialog = new JDialog(this, (existing == null ? "Create" : "Edit") + " Service Package", true);
        dialog.setSize(580, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        JTextField nameField = new JTextField(existing == null ? "" : existing.getServicePackageName());
        JTextField descField = new JTextField(existing == null ? "" : existing.getServicePackageDescription());
        JComboBox<ServicePackageType> typeCombo = new JComboBox<>(ServicePackageType.values());
        if (existing != null) typeCombo.setSelectedItem(existing.getServicePackageType());
        
        JTextField priceField = new JTextField(existing == null ? "0.00" : String.format("%.2f", existing.getPackageBasePrice()));
        priceField.setEditable(false);
        priceField.setBackground(new Color(233, 236, 239)); // premium light-slate gray indicating read-only
        priceField.setFocusable(false);
        priceField.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Service Activities List selector
        DefaultListModel<String> actListModel = new DefaultListModel<>();
        activityList.forEach(a -> actListModel.addElement(a.getServiceActivityName() + " (RM " + String.format("%.2f", a.getServiceActivityPrice()) + ")"));
        JList<String> actList = new JList<>(actListModel);
        actList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        if (existing != null) {
            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < activityList.size(); i++) {
                String actId = activityList.get(i).getServiceActivityID();
                if (existing.getServiceActivityList().stream().anyMatch(a -> a.getServiceActivityID().equals(actId))) {
                    indices.add(i);
                }
            }
            actList.setSelectedIndices(indices.stream().mapToInt(Integer::intValue).toArray());
        }

        // Vehicle Parts List selector
        DefaultListModel<String> partListModel = new DefaultListModel<>();
        partList.forEach(p -> partListModel.addElement(p.getVehiclePartBrand() + " " + p.getVehiclePartName() + " (RM " + String.format("%.2f", p.getVehiclePartPrice()) + ")"));
        JList<String> partListSelector = new JList<>(partListModel);
        partListSelector.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        if (existing != null) {
            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < partList.size(); i++) {
                String partId = partList.get(i).getVehiclePartID();
                if (existing.getVehiclePartList().stream().anyMatch(p -> p.getVehiclePartID().equals(partId))) {
                    indices.add(i);
                }
            }
            partListSelector.setSelectedIndices(indices.stream().mapToInt(Integer::intValue).toArray());
        }

        // Helper to update base price dynamically in real-time
        Runnable updatePriceField = () -> {
            double sumActivities = 0.0;
            int[] selectedActIdx = actList.getSelectedIndices();
            for (int idx : selectedActIdx) {
                sumActivities += activityList.get(idx).getServiceActivityPrice();
            }

            double sumParts = 0.0;
            int[] selectedPartIdx = partListSelector.getSelectedIndices();
            for (int idx : selectedPartIdx) {
                sumParts += partList.get(idx).getVehiclePartPrice();
            }

            double totalCost = sumActivities + sumParts;
            priceField.setText(String.format("%.2f", totalCost));
        };

        // Attach real-time list selection listeners to auto-calculate base price
        actList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updatePriceField.run();
            }
        });
        partListSelector.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updatePriceField.run();
            }
        });

        // Trigger initial calculation
        updatePriceField.run();

        int row = 0;
        addGridRow(form, gbc, row++, "Package Name *:", nameField);
        addGridRow(form, gbc, row++, "Description:", descField);
        addGridRow(form, gbc, row++, "Package Type *:", typeCombo);
        addGridRow(form, gbc, row++, "Base Price (RM, Auto-calculated):", priceField);

        // Wrap lists in scrolls
        JScrollPane actScroll = new JScrollPane(actList);
        actScroll.setPreferredSize(new Dimension(280, 80));
        addGridRow(form, gbc, row++, "Select Activities * (Ctrl+Click):", actScroll);

        JScrollPane partScroll = new JScrollPane(partListSelector);
        partScroll.setPreferredSize(new Dimension(280, 80));
        addGridRow(form, gbc, row++, "Select Parts (Ctrl+Click):", partScroll);

        dialog.add(new JScrollPane(form), BorderLayout.CENTER);

        // Buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 12));
        JButton btnSave = createBtn("Save Package", new Color(16, 185, 129));
        JButton btnCancel = createBtn("Cancel", new Color(100, 116, 139));
        btnCancel.addActionListener(e -> dialog.dispose());

        btnSave.addActionListener(e -> {
            String name = nameField.getText().trim();
            String desc = descField.getText().trim();
            ServicePackageType type = (ServicePackageType) typeCombo.getSelectedItem();
            double price;
            try {
                price = Double.parseDouble(priceField.getText().trim());
                if (price < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid positive number for package price.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int[] selectedActIdx = actList.getSelectedIndices();
            if (selectedActIdx.length == 0) {
                JOptionPane.showMessageDialog(dialog, "A service package must contain at least one service activity.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            List<String> actIds = new ArrayList<>();
            for (int idx : selectedActIdx) {
                actIds.add(activityList.get(idx).getServiceActivityID());
            }

            int[] selectedPartIdx = partListSelector.getSelectedIndices();
            List<String> partIds = new ArrayList<>();
            for (int idx : selectedPartIdx) {
                partIds.add(partList.get(idx).getVehiclePartID());
            }

            // Calculate total cost of selected service activities and vehicle parts
            double sumActivities = 0.0;
            for (int idx : selectedActIdx) {
                sumActivities += activityList.get(idx).getServiceActivityPrice();
            }
            double sumParts = 0.0;
            for (int idx : selectedPartIdx) {
                sumParts += partList.get(idx).getVehiclePartPrice();
            }
            double totalCost = sumActivities + sumParts;

            // Integrity Check: base price cannot be less than the sum of activities & parts
            if (price < totalCost) {
                JOptionPane.showMessageDialog(dialog, 
                    String.format("Pricing Error: Package base price (RM %.2f) cannot be less than the combined cost of selected activities and parts (RM %.2f).", price, totalCost),
                    "Pricing Integrity Violation", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            ServicePackageController ctrl = new ServicePackageController();
            OperationResult res;
            if (existing == null) {
                ServicePackageDTO dto = new ServicePackageDTO(name, desc, type, actIds, partIds, price);
                res = ctrl.createServicePackage(dto);
            } else {
                ServicePackageDTO dto = new ServicePackageDTO(existing.getServicePackageID(), name, desc, type, actIds, partIds, price);
                res = ctrl.updateServicePackage(dto);
            }

            if (res.isValid()) {
                JOptionPane.showMessageDialog(this, "Package successfully " + (existing == null ? "created!" : "updated!"), "Success", JOptionPane.INFORMATION_MESSAGE);
                loadServicePackages();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, res.generalMessage(), "Save Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        bottom.add(btnSave);
        bottom.add(btnCancel);
        dialog.add(bottom, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void handleDeletePackage(ServicePackage pkg) {
        if (JOptionPane.showConfirmDialog(this, "Delete Service Package \"" + pkg.getServicePackageName() + "\"?\nThis cannot be undone.", "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            ServicePackageController ctrl = new ServicePackageController();
            OperationResult res = ctrl.deleteServicePackage(pkg.getServicePackageID());
            if (res.isValid()) {
                JOptionPane.showMessageDialog(this, "Package deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadServicePackages();
            } else {
                JOptionPane.showMessageDialog(this, res.generalMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // ── TAB 2: SERVICE ACTIVITIES ────────────────────────────────────────────
    private JPanel buildActivitiesTabPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(new EmptyBorder(10, 12, 10, 12));
        panel.setOpaque(false);

        String[] cols = {"Activity ID", "Activity Name", "Description", "Price (RM)"};
        activityModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        activityTable = new JTable(activityModel);
        styleTable(activityTable);
        activityTable.getColumnModel().getColumn(0).setMinWidth(0);
        activityTable.getColumnModel().getColumn(0).setMaxWidth(0);
        activityTable.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scroll = new JScrollPane(activityTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        panel.add(scroll, BorderLayout.CENTER);

        // Buttons Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setOpaque(false);

        JButton btnCreate = createBtn("Create Activity", new Color(16, 185, 129));
        JButton btnEdit = createBtn("Edit Activity", new Color(37, 99, 235));
        JButton btnDelete = createBtn("Delete Activity", new Color(239, 68, 68));

        btnCreate.addActionListener(e -> showActivityDialog(null));
        btnEdit.addActionListener(e -> {
            int row = activityTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select an activity to edit first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            showActivityDialog(activityList.get(row));
        });
        btnDelete.addActionListener(e -> {
            int row = activityTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select an activity to delete first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            handleDeleteActivity(activityList.get(row));
        });

        btnPanel.add(btnCreate);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadServiceActivities() {
        activityList.clear();
        activityModel.setRowCount(0);
        ServiceActivityController ctrl = new ServiceActivityController();
        java.util.Optional<List<ServiceActivity>> listOpt = ctrl.retrieveAllServiceActivities();
        if (listOpt.isPresent()) {
            activityList.addAll(listOpt.get());
            for (ServiceActivity a : activityList) {
                activityModel.addRow(new Object[]{
                    a.getServiceActivityID(),
                    a.getServiceActivityName(),
                    a.getServiceActivityDescription() != null ? a.getServiceActivityDescription() : "(no description)",
                    String.format("%.2f", a.getServiceActivityPrice())
                });
            }
        }
    }

    private void showActivityDialog(ServiceActivity existing) {
        JDialog dialog = new JDialog(this, (existing == null ? "Create" : "Edit") + " Service Activity", true);
        dialog.setSize(420, 280);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        JTextField nameField = new JTextField(existing == null ? "" : existing.getServiceActivityName());
        JTextField descField = new JTextField(existing == null ? "" : existing.getServiceActivityDescription());
        JTextField priceField = new JTextField(existing == null ? "0.00" : String.format("%.2f", existing.getServiceActivityPrice()));

        int row = 0;
        addGridRow(form, gbc, row++, "Activity Name *:", nameField);
        addGridRow(form, gbc, row++, "Description:", descField);
        addGridRow(form, gbc, row++, "Price (RM) *:", priceField);

        dialog.add(form, BorderLayout.CENTER);

        // Buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 12));
        JButton btnSave = createBtn("Save Activity", new Color(16, 185, 129));
        JButton btnCancel = createBtn("Cancel", new Color(100, 116, 139));
        btnCancel.addActionListener(e -> dialog.dispose());

        btnSave.addActionListener(e -> {
            String name = nameField.getText().trim();
            String desc = descField.getText().trim();
            double price;
            try {
                price = Double.parseDouble(priceField.getText().trim());
                if (price < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid positive price.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ServiceActivityController ctrl = new ServiceActivityController();
            OperationResult res;
            if (existing == null) {
                ServiceActivityDTO dto = new ServiceActivityDTO(name, desc, price);
                res = ctrl.createServiceActivity(dto);
            } else {
                ServiceActivityDTO dto = new ServiceActivityDTO(existing.getServiceActivityID(), name, desc, price);
                res = ctrl.updateServiceActivity(dto);
            }

            if (res.isValid()) {
                JOptionPane.showMessageDialog(this, "Activity successfully " + (existing == null ? "created!" : "updated!"), "Success", JOptionPane.INFORMATION_MESSAGE);
                reloadData(); // Package lists also need updating
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, res.generalMessage(), "Save Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        bottom.add(btnSave);
        bottom.add(btnCancel);
        dialog.add(bottom, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void handleDeleteActivity(ServiceActivity a) {
        if (JOptionPane.showConfirmDialog(this, "Delete Service Activity \"" + a.getServiceActivityName() + "\"?\nThis cannot be undone.", "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            ServiceActivityController ctrl = new ServiceActivityController();
            OperationResult res = ctrl.deleteServiceActivity(a.getServiceActivityID());
            if (res.isValid()) {
                JOptionPane.showMessageDialog(this, "Activity deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                reloadData();
            } else {
                JOptionPane.showMessageDialog(this, res.generalMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // ── TAB 3: VEHICLE PARTS ─────────────────────────────────────────────────
    private JPanel buildPartsTabPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(new EmptyBorder(10, 12, 10, 12));
        panel.setOpaque(false);

        String[] cols = {"Part ID", "Brand", "Name", "Type", "Spec", "Price (RM)", "Quantity"};
        partModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        partTable = new JTable(partModel);
        styleTable(partTable);
        partTable.getColumnModel().getColumn(0).setMinWidth(0);
        partTable.getColumnModel().getColumn(0).setMaxWidth(0);
        partTable.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scroll = new JScrollPane(partTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        panel.add(scroll, BorderLayout.CENTER);

        // Buttons Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setOpaque(false);

        JButton btnCreate = createBtn("Create Part", new Color(16, 185, 129));
        JButton btnEdit = createBtn("Edit Part", new Color(37, 99, 235));
        JButton btnDelete = createBtn("Delete Part", new Color(239, 68, 68));

        btnCreate.addActionListener(e -> showPartDialog(null));
        btnEdit.addActionListener(e -> {
            int row = partTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a part to edit first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            showPartDialog(partList.get(row));
        });
        btnDelete.addActionListener(e -> {
            int row = partTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a part to delete first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            handleDeletePart(partList.get(row));
        });

        btnPanel.add(btnCreate);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadVehicleParts() {
        partList.clear();
        partModel.setRowCount(0);
        VehiclePartController ctrl = new VehiclePartController();
        java.util.Optional<List<VehiclePart>> listOpt = ctrl.retrieveAllVehicleParts();
        if (listOpt.isPresent()) {
            partList.addAll(listOpt.get());
            for (VehiclePart p : partList) {
                partModel.addRow(new Object[]{
                    p.getVehiclePartID(),
                    p.getVehiclePartBrand(),
                    p.getVehiclePartName(),
                    p.getVehiclePartType(),
                    p.getVehiclePartSpec(),
                    String.format("%.2f", p.getVehiclePartPrice()),
                    p.getVehiclePartQuantity()
                });
            }
        }
    }

    private void showPartDialog(VehiclePart existing) {
        JDialog dialog = new JDialog(this, (existing == null ? "Create" : "Edit") + " Vehicle Part", true);
        dialog.setSize(440, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        JTextField nameField = new JTextField(existing == null ? "" : existing.getVehiclePartName());
        JTextField descField = new JTextField(existing == null ? "" : existing.getVehiclePartDescription());
        JTextField typeField = new JTextField(existing == null ? "" : existing.getVehiclePartType());
        JTextField brandField = new JTextField(existing == null ? "" : existing.getVehiclePartBrand());
        JTextField specField = new JTextField(existing == null ? "" : existing.getVehiclePartSpec());
        JTextField priceField = new JTextField(existing == null ? "0.00" : String.format("%.2f", existing.getVehiclePartPrice()));
        JTextField qtyField = new JTextField(existing == null ? "0" : String.valueOf(existing.getVehiclePartQuantity()));

        int row = 0;
        addGridRow(form, gbc, row++, "Part Name *:", nameField);
        addGridRow(form, gbc, row++, "Description:", descField);
        addGridRow(form, gbc, row++, "Part Type *:", typeField);
        addGridRow(form, gbc, row++, "Brand *:", brandField);
        addGridRow(form, gbc, row++, "Specification *:", specField);
        addGridRow(form, gbc, row++, "Price (RM) *:", priceField);
        addGridRow(form, gbc, row++, "Quantity *:", qtyField);

        dialog.add(form, BorderLayout.CENTER);

        // Buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 12));
        JButton btnSave = createBtn("Save Part", new Color(16, 185, 129));
        JButton btnCancel = createBtn("Cancel", new Color(100, 116, 139));
        btnCancel.addActionListener(e -> dialog.dispose());

        btnSave.addActionListener(e -> {
            String name = nameField.getText().trim();
            String desc = descField.getText().trim();
            String type = typeField.getText().trim();
            String brand = brandField.getText().trim();
            String spec = specField.getText().trim();
            double price;
            int qty;
            try {
                price = Double.parseDouble(priceField.getText().trim());
                qty = Integer.parseInt(qtyField.getText().trim());
                if (price < 0 || qty < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid positive numbers for price and quantity.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            VehiclePartController ctrl = new VehiclePartController();
            OperationResult res;
            if (existing == null) {
                VehiclePartDTO dto = new VehiclePartDTO(name, desc, type, brand, spec, price, qty);
                res = ctrl.createVehiclePart(dto);
            } else {
                VehiclePartDTO dto = new VehiclePartDTO(existing.getVehiclePartID(), name, desc, type, brand, spec, price, qty);
                res = ctrl.updateVehiclePart(dto);
            }

            if (res.isValid()) {
                JOptionPane.showMessageDialog(this, "Part successfully " + (existing == null ? "created!" : "updated!"), "Success", JOptionPane.INFORMATION_MESSAGE);
                reloadData(); // Package lists also need updating
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, res.generalMessage(), "Save Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        bottom.add(btnSave);
        bottom.add(btnCancel);
        dialog.add(bottom, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void handleDeletePart(VehiclePart p) {
        if (JOptionPane.showConfirmDialog(this, "Delete Vehicle Part \"" + p.getVehiclePartName() + "\"?\nThis cannot be undone.", "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            VehiclePartController ctrl = new VehiclePartController();
            OperationResult res = ctrl.deleteVehiclePart(p.getVehiclePartID());
            if (res.isValid()) {
                JOptionPane.showMessageDialog(this, "Part deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                reloadData();
            } else {
                JOptionPane.showMessageDialog(this, res.generalMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // ── GENERAL STYLING AND HELPERS ──────────────────────────────────────────
    private void addGridRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        JLabel l = new JLabel(labelText);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(new Color(71, 85, 105));
        panel.add(l, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(field, gbc);
    }

    private void styleTable(JTable t) {
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setRowHeight(26);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private JButton createBtn(String text, Color color) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setBackground(color);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}
