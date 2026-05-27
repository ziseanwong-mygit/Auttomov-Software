package main.java.frontend.report;
 
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*; 
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import main.java.backend.controller.RecordsReportController;
import main.java.backend.controller.SalesReportController;
import main.java.backend.model.DataReport;
import main.java.backend.model.SalesReport;
import main.java.backend.model.UserAccount;
import main.java.frontend.shared.SessionManager;
import main.java.records.OperationResult;
 
/**
 * AnalyzedReportsUI — Manager
 * Shows business intelligence analytics, service summaries, customer counts, and revenues.
 */
public class AnalyzedReportsUI extends JFrame {
 
    private static final String[] SALES_COLUMNS = {
            "Report ID", "Report No", "Date", "Normal Services", "Major Services", "Total Revenue (RM)", "Author"
    };
 
    private static final String[] RECORDS_COLUMNS = {
            "Report ID", "Report No", "Date", "Customers", "Technicians", "Service Packages", "Appointments", "Author"
    };
 
    private List<SalesReport> salesReportsList = new ArrayList<>();
    private List<DataReport> recordsReportsList = new ArrayList<>();
 
    private DefaultTableModel modelSales;
    private DefaultTableModel modelRecords;
    private JTable            tableSales;
    private JTable            tableRecords;
 
    private JLabel lblRev;
    private JLabel lblAppt;
    private JLabel lblCust;
    private JLabel lblStaff;
 
    public AnalyzedReportsUI() {
        setTitle("Analyzed Reports Dashboard — Auttomov Software");
        setSize(950, 680);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 244, 248));
 
        // ── Header ──────────────────────────────────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JLabel title = new JLabel("Manager Analytics & Reports Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
 
        JLabel subtitle = new JLabel("Real-time summary statistics, appointment counts, and revenue calculations");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(210, 230, 255));
 
        headerPanel.add(title,    BorderLayout.CENTER);
        headerPanel.add(subtitle, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);
 
        // ── Metrics Cards Row ─────────────────────────────────────────────────
        JPanel metricsPanel = new JPanel(new GridLayout(1, 4, 12, 0));
        metricsPanel.setOpaque(false);
        metricsPanel.setBorder(new EmptyBorder(15, 15, 10, 15));
 
        JPanel card1 = buildMetricCard("Total Revenue", new Color(46, 204, 113));
        JPanel card2 = buildMetricCard("Appointments Completed", new Color(52, 152, 219));
        JPanel card3 = buildMetricCard("Total Customers", new Color(241, 196, 15));
        JPanel card4 = buildMetricCard("Total Staff members", new Color(155, 89, 182));
 
        lblRev = (JLabel) card1.getClientProperty("valLabel");
        lblAppt = (JLabel) card2.getClientProperty("valLabel");
        lblCust = (JLabel) card3.getClientProperty("valLabel");
        lblStaff = (JLabel) card4.getClientProperty("valLabel");
 
        metricsPanel.add(card1);
        metricsPanel.add(card2);
        metricsPanel.add(card3);
        metricsPanel.add(card4);
 
        // ── Tabbed Pane for Tables ──────────────────────────────────────────
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
 
        tabbedPane.addTab("Sales Reports", buildSalesReportsPanel());
        tabbedPane.addTab("Records Reports", buildRecordsReportsPanel());
 
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(metricsPanel, BorderLayout.NORTH);
        centerWrapper.add(tabbedPane,  BorderLayout.CENTER);
        add(centerWrapper, BorderLayout.CENTER);
 
        // ── Button Row ────────────────────────────────────────────────────────
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 12));
        buttonPanel.setOpaque(false);
 
        JButton btnGenSales   = createBtn("Generate Sales Report", new Color(46, 204, 113));
        JButton btnGenRecords = createBtn("Generate Records Report", new Color(52, 152, 219));
        JButton btnView       = createBtn("View Details", new Color(41, 128, 185));
        JButton btnDelete     = createBtn("Delete Report", new Color(192, 57, 43));
 
        btnGenSales.addActionListener(e -> {
            UserAccount cur = SessionManager.getCurrentUser();
            if (cur == null) return;
            SalesReportController ctrl = new SalesReportController();
            OperationResult result = ctrl.createSalesReport(cur.getAccountID());
            if (result.isValid()) {
                JOptionPane.showMessageDialog(this, "Sales Report generated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadReports();
                refreshTables();
            } else {
                JOptionPane.showMessageDialog(this, result.generalMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
 
        btnGenRecords.addActionListener(e -> {
            UserAccount cur = SessionManager.getCurrentUser();
            if (cur == null) return;
            RecordsReportController ctrl = new RecordsReportController();
            OperationResult result = ctrl.createRecordsReport(cur.getAccountID());
            if (result.isValid()) {
                JOptionPane.showMessageDialog(this, "Records Report generated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadReports();
                refreshTables();
            } else {
                JOptionPane.showMessageDialog(this, result.generalMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
 
        btnView.addActionListener(e -> {
            int selectedTab = tabbedPane.getSelectedIndex();
            if (selectedTab == 0) {
                int row = tableSales.getSelectedRow();
                if (row < 0) { showNoSelectionWarning(); return; }
                showSalesReportDetails(salesReportsList.get(row));
            } else {
                int row = tableRecords.getSelectedRow();
                if (row < 0) { showNoSelectionWarning(); return; }
                showRecordsReportDetails(recordsReportsList.get(row));
            }
        });
 
        btnDelete.addActionListener(e -> {
            int selectedTab = tabbedPane.getSelectedIndex();
            if (selectedTab == 0) {
                int row = tableSales.getSelectedRow();
                if (row < 0) { showNoSelectionWarning(); return; }
                deleteSalesReport(salesReportsList.get(row));
            } else {
                int row = tableRecords.getSelectedRow();
                if (row < 0) { showNoSelectionWarning(); return; }
                deleteRecordsReport(recordsReportsList.get(row));
            }
        });
 
        buttonPanel.add(btnGenSales);
        buttonPanel.add(btnGenRecords);
        buttonPanel.add(btnView);
        buttonPanel.add(btnDelete);
        add(buttonPanel, BorderLayout.SOUTH);
 
        loadReports();
        refreshTables();
    }
 
    private JPanel buildSalesReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
 
        modelSales = new DefaultTableModel(SALES_COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableSales = new JTable(modelSales);
        tableSales.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableSales.setRowHeight(26);
        tableSales.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableSales.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableSales.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int r = tableSales.getSelectedRow();
                    if (r >= 0) {
                        showSalesReportDetails(salesReportsList.get(r));
                    }
                }
            }
        });
 
        panel.add(new JScrollPane(tableSales), BorderLayout.CENTER);
        return panel;
    }
 
    private JPanel buildRecordsReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
 
        modelRecords = new DefaultTableModel(RECORDS_COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableRecords = new JTable(modelRecords);
        tableRecords.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableRecords.setRowHeight(26);
        tableRecords.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableRecords.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableRecords.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int r = tableRecords.getSelectedRow();
                    if (r >= 0) {
                        showRecordsReportDetails(recordsReportsList.get(r));
                    }
                }
            }
        });
 
        panel.add(new JScrollPane(tableRecords), BorderLayout.CENTER);
        return panel;
    }
 
    private JPanel buildMetricCard(String title, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                new EmptyBorder(12, 16, 12, 16)
        ));
 
        JLabel titleL = new JLabel(title.toUpperCase());
        titleL.setFont(new Font("Segoe UI", Font.BOLD, 10));
        titleL.setForeground(new Color(160, 165, 175));
 
        JLabel valueL = new JLabel("0");
        valueL.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueL.setForeground(accentColor);
 
        JPanel strip = new JPanel();
        strip.setBackground(accentColor);
        strip.setPreferredSize(new Dimension(4, 0));
 
        card.add(titleL, BorderLayout.NORTH);
        card.add(valueL, BorderLayout.CENTER);
        card.add(strip,  BorderLayout.WEST);
 
        card.putClientProperty("valLabel", valueL);
        return card;
    }
 
    private void showSalesReportDetails(SalesReport r) {
        main.java.frontend.shared.DocumentViewerUI.forSalesReport(r, this);
    }
 
    private void showRecordsReportDetails(DataReport r) {
        main.java.frontend.shared.DocumentViewerUI.forDataReport(r, this);
    }
 
    private void deleteSalesReport(SalesReport r) {
        int confirm = JOptionPane.showConfirmDialog(this, "Delete Sales Report \"" + r.getReportDocNo() + "\"?\nThis cannot be undone.", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            SalesReportController ctrl = new SalesReportController();
            OperationResult result = ctrl.deleteSalesReport(r.getReportID());
            if (result.isValid()) {
                JOptionPane.showMessageDialog(this, "Report deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadReports();
                refreshTables();
            } else {
                JOptionPane.showMessageDialog(this, result.generalMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
 
    private void deleteRecordsReport(DataReport r) {
        int confirm = JOptionPane.showConfirmDialog(this, "Delete Records Report \"" + r.getReportDocNo() + "\"?\nThis cannot be undone.", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            RecordsReportController ctrl = new RecordsReportController();
            OperationResult result = ctrl.deleteRecordsReport(r.getReportID());
            if (result.isValid()) {
                JOptionPane.showMessageDialog(this, "Report deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadReports();
                refreshTables();
            } else {
                JOptionPane.showMessageDialog(this, result.generalMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
 
    private void loadReports() {
        salesReportsList.clear();
        SalesReportController sCtrl = new SalesReportController();
        java.util.Optional<List<SalesReport>> sOpt = sCtrl.retrieveAllSalesReports();
        if (sOpt.isPresent()) {
            salesReportsList.addAll(sOpt.get());
        }
 
        recordsReportsList.clear();
        RecordsReportController rCtrl = new RecordsReportController();
        java.util.Optional<List<DataReport>> rOpt = rCtrl.retrieveAllRecordsReports();
        if (rOpt.isPresent()) {
            recordsReportsList.addAll(rOpt.get());
        }
 
        // Update summary metrics cards
        double rev = 0.0;
        int appts = 0;
        int custs = 0;
        int staffs = 0;
 
        if (!salesReportsList.isEmpty()) {
            SalesReport latestS = salesReportsList.get(salesReportsList.size() - 1);
            rev = latestS.getTotalAllServiceSales();
        }
        if (!recordsReportsList.isEmpty()) {
            DataReport latestR = recordsReportsList.get(recordsReportsList.size() - 1);
            appts = latestR.getTotalAppointments();
            custs = latestR.getTotalCustomers();
            staffs = latestR.getTotalTechnicians() + latestR.getTotalCounterStaffs() + latestR.getTotalManagers();
        }
 
        if (lblRev != null) lblRev.setText("RM " + String.format("%.2f", rev));
        if (lblAppt != null) lblAppt.setText(appts + " Orders");
        if (lblCust != null) lblCust.setText(custs + " Customers");
        if (lblStaff != null) lblStaff.setText(staffs + " Staff");
    }
 
    private void refreshTables() {
        modelSales.setRowCount(0);
        for (SalesReport r : salesReportsList) {
            modelSales.addRow(new Object[]{
                r.getReportID(),
                r.getReportDocNo(),
                r.getReportCreationDate().toString(),
                r.getTotalNormalServicesDone(),
                r.getTotalMajorServicesDone(),
                String.format("%.2f", r.getTotalAllServiceSales()),
                r.getManagerAccount() != null ? r.getManagerAccount().getFirstName() + " " + r.getManagerAccount().getLastName() : "N/A"
            });
        }
 
        modelRecords.setRowCount(0);
        for (DataReport r : recordsReportsList) {
            modelRecords.addRow(new Object[]{
                r.getReportID(),
                r.getReportDocNo(),
                r.getReportCreationDate().toString(),
                r.getTotalCustomers(),
                r.getTotalTechnicians(),
                r.getTotalServicePackages(),
                r.getTotalAppointments(),
                r.getManagerAccount() != null ? r.getManagerAccount().getFirstName() + " " + r.getManagerAccount().getLastName() : "N/A"
            });
        }
    }
 
    private void showNoSelectionWarning() {
        JOptionPane.showMessageDialog(this, "Please select a report row first.", "No Selection", JOptionPane.WARNING_MESSAGE);
    }
 
    private JButton createBtn(String text, Color color) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setBackground(color);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}
