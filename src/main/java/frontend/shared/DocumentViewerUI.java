package main.java.frontend.shared;

import java.awt.*;
import java.awt.print.*;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import main.java.backend.model.*;

/**
 * Premium, print-ready document viewer dialog for Invoices, Receipts, and Reports.
 */
public class DocumentViewerUI extends JFrame implements Printable {

    private final JPanel docPanel;

    private DocumentViewerUI(Window parent, String title) {
        super(title);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(650, 750);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(0, 0));

        // Create the scrollable printable document panel
        docPanel = new JPanel();
        docPanel.setLayout(new BoxLayout(docPanel, BoxLayout.Y_AXIS));
        docPanel.setBackground(Color.WHITE);
        docPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(40, 45, 40, 45)
        ));

        // Use a wrapper panel to keep it centered and looking like a page
        JPanel pageWrapper = new JPanel(new GridBagLayout());
        pageWrapper.setBackground(new Color(241, 245, 249)); // Slate background for contrast
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        pageWrapper.add(docPanel, gbc);

        JScrollPane scroll = new JScrollPane(pageWrapper);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        // Control Panel
        JPanel ctrlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        ctrlPanel.setBackground(new Color(248, 250, 252));
        ctrlPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        JButton btnPrint = createBtn("Print Document", new Color(34, 197, 94)); // Emerald green
        btnPrint.addActionListener(e -> printDocument());

        JButton btnClose = createBtn("Close", new Color(100, 116, 139)); // Slate gray
        btnClose.addActionListener(e -> dispose());

        ctrlPanel.add(btnPrint);
        ctrlPanel.add(btnClose);
        add(ctrlPanel, BorderLayout.SOUTH);
    }

    public static void forInvoice(Invoice inv, Window parent) {
        DocumentViewerUI viewer = new DocumentViewerUI(parent, "Invoice — " + inv.getInvoiceDocNo());
        viewer.buildInvoiceContent(inv);
        viewer.setVisible(true);
    }

    public static void forReceipt(Receipt rec, Window parent) {
        DocumentViewerUI viewer = new DocumentViewerUI(parent, "Receipt — " + rec.getReceiptDocNo());
        viewer.buildReceiptContent(rec);
        viewer.setVisible(true);
    }

    public static void forSalesReport(SalesReport r, Window parent) {
        DocumentViewerUI viewer = new DocumentViewerUI(parent, "Sales Report — " + r.getReportDocNo());
        viewer.buildSalesReportContent(r);
        viewer.setVisible(true);
    }

    public static void forDataReport(DataReport r, Window parent) {
        DocumentViewerUI viewer = new DocumentViewerUI(parent, "System Data Report — " + r.getReportDocNo());
        viewer.buildDataReportContent(r);
        viewer.setVisible(true);
    }

    private void buildHeader(String docType, String docNo, LocalDate date) {
        // Company Title Block
        JLabel compName = new JLabel("AUTTOMOV AUTOMOTIVE SERVICES");
        compName.setFont(new Font("Segoe UI", Font.BOLD, 22));
        compName.setForeground(new Color(15, 23, 42)); // Deep slate
        compName.setAlignmentX(Component.LEFT_ALIGNMENT);
        docPanel.add(compName);

        JLabel compAddr1 = new JLabel("123 Technology Park Drive, Bukit Jalil");
        compAddr1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        compAddr1.setForeground(new Color(100, 116, 139));
        compAddr1.setAlignmentX(Component.LEFT_ALIGNMENT);
        docPanel.add(compAddr1);

        JLabel compAddr2 = new JLabel("57000 Kuala Lumpur | Tel: +60 3-8996 1234");
        compAddr2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        compAddr2.setForeground(new Color(100, 116, 139));
        compAddr2.setAlignmentX(Component.LEFT_ALIGNMENT);
        docPanel.add(compAddr2);

        docPanel.add(Box.createVerticalStrut(15));

        // Horizontal Line Divider
        JPanel line = new JPanel();
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        line.setBackground(new Color(226, 232, 240));
        line.setAlignmentX(Component.LEFT_ALIGNMENT);
        docPanel.add(line);

        docPanel.add(Box.createVerticalStrut(20));

        // Document Details Area
        JPanel detailsPanel = new JPanel(new GridLayout(2, 2, 20, 8));
        detailsPanel.setOpaque(false);
        detailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblType = new JLabel(docType.toUpperCase());
        lblType.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblType.setForeground(new Color(29, 78, 216)); // Royal Blue

        JLabel lblDocNo = new JLabel("Doc No: " + docNo, SwingConstants.RIGHT);
        lblDocNo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblDocNo.setForeground(new Color(51, 65, 85));

        JLabel lblDate = new JLabel("Date: " + date.toString());
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDate.setForeground(new Color(100, 116, 139));

        detailsPanel.add(lblType);
        detailsPanel.add(lblDocNo);
        detailsPanel.add(lblDate);

        docPanel.add(detailsPanel);
        docPanel.add(Box.createVerticalStrut(25));
    }

    private void addDivider() {
        docPanel.add(Box.createVerticalStrut(15));
        JPanel divider = new JPanel();
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        divider.setBackground(new Color(241, 245, 249));
        divider.setAlignmentX(Component.LEFT_ALIGNMENT);
        docPanel.add(divider);
        docPanel.add(Box.createVerticalStrut(15));
    }

    private void addRow(String label, String value, boolean isBold, boolean isTotal) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", isBold ? Font.BOLD : Font.PLAIN, isTotal ? 15 : 13));
        lbl.setForeground(new Color(51, 65, 85));

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", isBold ? Font.BOLD : Font.PLAIN, isTotal ? 16 : 13));
        if (isTotal) {
            val.setForeground(new Color(22, 163, 74)); // Emerald Green for totals
        } else {
            val.setForeground(new Color(15, 23, 42));
        }

        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        docPanel.add(row);
    }

    private void addNotesSection(String notes) {
        if (notes == null || notes.isBlank()) return;
        docPanel.add(Box.createVerticalStrut(20));
        
        JLabel lbl = new JLabel("Notes & Remarks:");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(100, 116, 139));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        docPanel.add(lbl);

        docPanel.add(Box.createVerticalStrut(4));

        JTextArea ta = new JTextArea(notes);
        ta.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        ta.setForeground(new Color(51, 65, 85));
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setEditable(false);
        ta.setBackground(Color.WHITE);
        ta.setOpaque(true);
        ta.setAlignmentX(Component.LEFT_ALIGNMENT);
        docPanel.add(ta);
    }

    private void addFooter(String issuerName) {
        docPanel.add(Box.createVerticalGlue());
        docPanel.add(Box.createVerticalStrut(40));

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        JLabel lblPrepared = new JLabel("Prepared / Issued By:");
        lblPrepared.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblPrepared.setForeground(new Color(148, 163, 184));
        JLabel lblName = new JLabel(issuerName != null ? issuerName : "System Seed / Auto");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblName.setForeground(new Color(51, 65, 85));
        left.add(lblPrepared);
        left.add(Box.createVerticalStrut(4));
        left.add(lblName);

        footerPanel.add(left, BorderLayout.WEST);
        docPanel.add(footerPanel);
    }

    private void buildInvoiceContent(Invoice inv) {
        buildHeader("Invoice", inv.getInvoiceDocNo(), inv.getInvoiceCreationDate());

        String customerName = "N/A";
        String serviceTitle = "N/A";
        String packageName = "N/A";
        String duration = "N/A";

        if (inv.getAppointment() != null) {
            serviceTitle = inv.getAppointment().getAppointmentTitle();
            if (inv.getAppointment().getCustomerAccount() != null) {
                customerName = inv.getAppointment().getCustomerAccount().getFirstName() + " " +
                               inv.getAppointment().getCustomerAccount().getLastName();
            }
            if (inv.getAppointment().getServicePackage() != null) {
                packageName = inv.getAppointment().getServicePackage().getServicePackageName();
                duration = inv.getAppointment().getServicePackage().getServiceDuration() + " hours";
            }
        }

        addRow("Customer Name:", customerName, false, false);
        addRow("Service Details:", serviceTitle, false, false);
        addRow("Service Package:", packageName, false, false);
        addRow("Estimated Duration:", duration, false, false);
        
        addDivider();

        addRow("Subtotal Amount:", "RM " + String.format("%.2f", inv.getSubtotalAmount()), false, false);
        addRow("Service Tax (6%):", "RM " + String.format("%.2f", inv.getServiceTaxAmount()), false, false);
        addRow("Additional Fee:", "RM " + String.format("%.2f", inv.getAdditionalFeeAmount()), false, false);
        
        addDivider();

        addRow("TOTAL AMOUNT DUE:", "RM " + String.format("%.2f", inv.getTotalAmount()), true, true);
        addRow("Payment Status:", inv.getInvoiceStatus().getInvoiceStatus().toUpperCase(), true, false);

        addNotesSection(inv.getInvoiceNote());

        String issuerName = inv.getInvoiceIssuer() != null ? 
                inv.getInvoiceIssuer().getFirstName() + " " + inv.getInvoiceIssuer().getLastName() : "N/A";
        addFooter(issuerName);
    }

    private void buildReceiptContent(Receipt rec) {
        buildHeader("Official Receipt", rec.getReceiptDocNo(), rec.getReceiptCreationDate());

        String payerName = "N/A";
        String payMethod = "N/A";
        double amountPaid = 0.0;
        String payRecNo = "N/A";

        if (rec.getPaymentRecord() != null) {
            payRecNo = rec.getPaymentRecord().getPaymentRecordNo();
            amountPaid = rec.getPaymentRecord().getPaymentAmount();
            payMethod = rec.getPaymentRecord().getPaymentMethod().getPaymentMethod();
            if (rec.getPaymentRecord().getPayerAccount() != null) {
                payerName = rec.getPaymentRecord().getPayerAccount().getFirstName() + " " +
                            rec.getPaymentRecord().getPayerAccount().getLastName();
            }
        }

        addRow("Customer / Payer Name:", payerName, false, false);
        addRow("Linked Payment Record:", payRecNo, false, false);
        addRow("Payment Method:", payMethod, false, false);
        addRow("Linked Invoice No:", rec.getInvoice() != null ? rec.getInvoice().getInvoiceDocNo() : "N/A", false, false);

        String serviceTitle = "N/A";
        String packageName = "N/A";
        String technicians = "N/A";
        if (rec.getInvoice() != null && rec.getInvoice().getAppointment() != null) {
            main.java.backend.model.Appointment appt = rec.getInvoice().getAppointment();
            if (appt.getAppointmentTitle() != null) serviceTitle = appt.getAppointmentTitle();
            if (appt.getServicePackage() != null) packageName = appt.getServicePackage().getServicePackageName();
            if (appt.getTechnicianList() != null && !appt.getTechnicianList().isEmpty()) {
                StringBuilder techSb = new StringBuilder();
                for (main.java.backend.model.StaffAccount tech : appt.getTechnicianList()) {
                    if (!techSb.isEmpty()) techSb.append(", ");
                    techSb.append(tech.getFirstName()).append(" ").append(tech.getLastName());
                }
                technicians = techSb.toString();
            }
        }
        
        addDivider();
        addRow("Service Details:", serviceTitle, false, false);
        addRow("Service Package:", packageName, false, false);
        addRow("Assigned Technician(s):", technicians, false, false);

        addDivider();

        addRow("Subtotal Paid:", "RM " + String.format("%.2f", rec.getInvoice() != null ? rec.getInvoice().getSubtotalAmount() : 0.0), false, false);
        addRow("Service Tax Paid:", "RM " + String.format("%.2f", rec.getInvoice() != null ? rec.getInvoice().getServiceTaxAmount() : 0.0), false, false);
        addRow("Additional Fees:", "RM " + String.format("%.2f", rec.getInvoice() != null ? rec.getInvoice().getAdditionalFeeAmount() : 0.0), false, false);

        addDivider();

        addRow("TOTAL AMOUNT PAID:", "RM " + String.format("%.2f", amountPaid), true, true);

        addNotesSection(rec.getReceiptNote());

        String issuerName = rec.getReceiptIssuer() != null ? 
                rec.getReceiptIssuer().getFirstName() + " " + rec.getReceiptIssuer().getLastName() : "N/A";
        addFooter(issuerName);
    }

    private void buildSalesReportContent(SalesReport r) {
        buildHeader("Management Sales Report", r.getReportDocNo(), r.getReportCreationDate());

        addRow("Report Period / Type:", r.getReportType().getReportType(), false, false);
        addRow("Generated By:", r.getManagerAccount() != null ? r.getManagerAccount().getFirstName() + " " + r.getManagerAccount().getLastName() : "N/A", false, false);

        addDivider();

        addRow("Normal Services Completed:", String.valueOf(r.getTotalNormalServicesDone()), false, false);
        addRow("Major Services Completed:", String.valueOf(r.getTotalMajorServicesDone()), false, false);
        addRow("All Services Completed:", String.valueOf(r.getTotalAllServicesDone()), true, false);

        addDivider();

        addRow("Normal Service Sales:", "RM " + String.format("%.2f", r.getTotalNormalServiceSales()), false, false);
        addRow("Major Service Sales:", "RM " + String.format("%.2f", r.getTotalMajorServiceSales()), false, false);
        addRow("All Service Sales:", "RM " + String.format("%.2f", r.getTotalAllServiceSales()), true, false);

        addDivider();

        addRow("Sales This Month:", "RM " + String.format("%.2f", r.getTotalSalesByThisMonth()), false, false);
        addRow("Sales This Year:", "RM " + String.format("%.2f", r.getTotalSalesByThisYear()), false, false);
        
        addDivider();
        addRow("LIFETIME CUMULATIVE SALES:", "RM " + String.format("%.2f", r.getTotalSalesByLifetime()), true, true);

        String issuerName = r.getManagerAccount() != null ? 
                r.getManagerAccount().getFirstName() + " " + r.getManagerAccount().getLastName() : "N/A";
        addFooter(issuerName);
    }

    private void buildDataReportContent(DataReport r) {
        buildHeader("System Records Report", r.getReportDocNo(), r.getReportCreationDate());

        addRow("Report Period / Type:", r.getReportType().getReportType(), false, false);
        addRow("Generated By:", r.getManagerAccount() != null ? r.getManagerAccount().getFirstName() + " " + r.getManagerAccount().getLastName() : "N/A", false, false);

        addDivider();

        addRow("Total Registered Customers:", String.valueOf(r.getTotalCustomers()), false, false);
        addRow("Total Registered Technicians:", String.valueOf(r.getTotalTechnicians()), false, false);
        addRow("Total Registered Counter Staff:", String.valueOf(r.getTotalCounterStaffs()), false, false);
        addRow("Total Registered Managers:", String.valueOf(r.getTotalManagers()), false, false);

        addDivider();

        addRow("Total Service Activities:", String.valueOf(r.getTotalServiceActivities()), false, false);
        addRow("Total Vehicle Parts:", String.valueOf(r.getTotalVehicleParts()), false, false);
        addRow("Total Service Packages:", String.valueOf(r.getTotalServicePackages()), false, false);
        addRow("Total Logged Appointments:", String.valueOf(r.getTotalAppointments()), true, false);

        addDivider();

        addRow("New Customers (This Month):", String.valueOf(r.getTotalCustomersByThisMonth()), false, false);
        addRow("New Customers (This Year):", String.valueOf(r.getTotalCustomersByThisYear()), false, false);
        addRow("Customers Deactivated (This Month):", String.valueOf(r.getTotalCustomersLeftByThisMonth()), false, false);
        addRow("Customers Deactivated (This Year):", String.valueOf(r.getTotalCustomersLeftByThisYear()), false, false);

        String issuerName = r.getManagerAccount() != null ? 
                r.getManagerAccount().getFirstName() + " " + r.getManagerAccount().getLastName() : "N/A";
        addFooter(issuerName);
    }

    private void printDocument() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);
        boolean ok = job.printDialog();
        if (ok) {
            try {
                job.print();
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this, "Printing failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }

        Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        // Scale component to fit the page width
        double scaleX = pageFormat.getImageableWidth() / docPanel.getWidth();
        double scaleY = pageFormat.getImageableHeight() / docPanel.getHeight();
        double scale = Math.min(scaleX, scaleY);

        g2d.scale(scale, scale);
        docPanel.printAll(g2d);

        return PAGE_EXISTS;
    }

    private JButton createBtn(String text, Color color) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setBackground(color);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}
