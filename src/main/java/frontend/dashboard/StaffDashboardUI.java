package main.java.frontend.dashboard;

import main.java.backend.model.StaffAccount;
import main.java.backend.model.UserAccount;
import main.java.enums.data.JobPosition;
import main.java.frontend.account.ManageCustomerUI;
import main.java.frontend.account.ManageStaffUI;
import main.java.frontend.account.UpdateAccStatusUI;
import main.java.frontend.appointment.AssignTechnicianUI;
import main.java.frontend.appointment.CreateAppointmentUI;
import main.java.frontend.appointment.ManageAppointmentUI;
import main.java.frontend.appointment.UpdateApptStatusUI;
import main.java.frontend.appointment.ViewAppointmentUI;
import main.java.frontend.comment.ViewCommentUI;
import main.java.frontend.feedback.ManageFeedbackUI;
import main.java.frontend.forum.AppointmentForumUI;
import main.java.frontend.menu.mainMenuUI;
import main.java.frontend.payment.ManageInvoiceUI;
import main.java.frontend.payment.ManagePaymentReceiptUI;
import main.java.frontend.shared.ViewProfilePanel;
import main.java.frontend.report.AnalyzedReportsUI;
import main.java.frontend.service.SetServicePriceUI;
import main.java.frontend.shared.BaseDashboardUI;
import main.java.frontend.shared.EditProfilePanel;
import main.java.frontend.shared.SessionManager;

import javax.swing.*;
import java.awt.*;

/**
 * StaffDashboardUI — Sidebar dashboard for Manager, Counter Staff, and Technician.
 *
 * Extends BaseDashboardUI (BorderLayout: header NORTH, sidebar WEST, workspace CENTER).
 * Role-gated sidebar items are built at runtime based on the logged-in user's JobPosition.
 * Each workspace card embeds the content of the corresponding feature UI directly —
 * no separate JFrame windows are opened for navigation.
 */
public class StaffDashboardUI extends BaseDashboardUI {

    private final JobPosition jobPos;

    public StaffDashboardUI() {
        super("Staff Dashboard — Auttomov Software");

        UserAccount user = SessionManager.getCurrentUser();
        StaffAccount staff = (StaffAccount) user;
        this.jobPos = staff.getJobPosition();

        buildSidebarItems();
        buildWorkspaceCards();
        setVisible(true);
    }

    // ── Sidebar construction (role-gated) ────────────────────────────────────

    private void buildSidebarItems() {
        JButton firstBtn = null;
        String  firstCard = null;

        // ── MANAGER ONLY ─────────────────────────────────────────────────────
        if (jobPos == JobPosition.MANAGER) {
            addNavGroup("Account Management");
            JButton b1 = addNavItem("", "Manage Accounts",     "manageAccounts");
            firstBtn = b1; firstCard = "manageAccounts";

            addNavGroup("Service Packages, Activities & Parts");
            addNavItem("", "Manage Packages", "managePackages");

            addNavGroup("Appointments");
            addNavItem("", "Appointments", "appointments");

            addNavGroup("Reviews & Insights");
            addNavItem("", "Service Review Hub", "reviewsManager");
            addNavItem("", "Reports",          "reports");

            addNavGroup("Finance");
            addNavItem("", "Invoices", "managerInvoices");
            addNavItem("", "Payment & Receipts", "managerPaymentReceipt");
        }

        // ── COUNTER STAFF ONLY ───────────────────────────────────────────────
        if (jobPos == JobPosition.COUNTER_STAFF) {
            addNavGroup("Accounts");
            JButton b1 = addNavItem("", "Manage Customers", "manageCustomers");
            firstBtn = b1; firstCard = "manageCustomers";

            addNavGroup("Appointments");
            addNavItem("", "Appointments", "appointments");

            addNavGroup("Billing");
            addNavItem("", "Invoices", "staffInvoices");
            addNavItem("", "Payment & Receipts", "staffPaymentReceipt");

            addNavGroup("Reviews");
            addNavItem("", "Service Review Hub", "reviewsStaff");
        }

        // ── TECHNICIAN ONLY ──────────────────────────────────────────────────
        if (jobPos == JobPosition.TECHNICIAN) {
            addNavGroup("My Work");
            JButton b1 = addNavItem("", "My Appointments", "myAppointments");
            firstBtn = b1; firstCard = "myAppointments";
            addNavItem("", "Update Appointment Status", "markCompleted");
            addNavItem("", "Service Review Hub",  "myFeedbacks");
        }

        // ── ALL STAFF — bottom-pinned ────────────────────────────────────────
        addSidebarSpacer();
        addSidebarDivider();
        addNavGroup("Account");
        addNavItem("", "View Profile", "viewProfile");
        JButton profileBtn = addNavItem("", "Edit Profile", "editProfile");
        addNavAction("", "Logout", new Color(239, 68, 68), e -> {
            SessionManager.clearSession();
            new mainMenuUI().setVisible(true);
            dispose();
        });

        // Auto-select the first meaningful card
        if (firstBtn != null && firstCard != null) {
            final JButton fb = firstBtn;
            final String  fc = firstCard;
            SwingUtilities.invokeLater(() -> showDefaultCard(fc, fb));
        }
    }

    // ── Workspace cards ──────────────────────────────────────────────────────

    private void buildWorkspaceCards() {

        // ── MANAGER cards ────────────────────────────────────────────────────
        if (jobPos == JobPosition.MANAGER) {
            registerCard("manageAccounts",   buildAccountsCard());
            registerCard("managePackages",   wrapExisting("Manage Packages", new SetServicePriceUI()));
            registerCard("appointments",     buildManagerAppointmentsCard());
            registerCard("reviewsManager",   wrapExisting("Service Review Hub", new AppointmentForumUI()));
            registerCard("reports",          wrapExisting("Reports", new AnalyzedReportsUI()));
            registerCard("managerInvoices",  wrapExisting("Invoices", new ManageInvoiceUI()));
            registerCard("managerPaymentReceipt", wrapExisting("Payment & Receipts", new ManagePaymentReceiptUI()));
        }

        // ── COUNTER STAFF cards ──────────────────────────────────────────────
        if (jobPos == JobPosition.COUNTER_STAFF) {
            registerCard("manageCustomers", wrapExisting("Manage Customers", new ManageCustomerUI()));
            registerCard("appointments",    buildCounterStaffAppointmentsCard());
            registerCard("staffInvoices",   wrapExisting("Invoices", new ManageInvoiceUI()));
            registerCard("staffPaymentReceipt", wrapExisting("Payment & Receipts", new ManagePaymentReceiptUI()));
            registerCard("reviewsStaff",    wrapExisting("Service Review Hub", new AppointmentForumUI()));
        }

        // ── TECHNICIAN cards ─────────────────────────────────────────────────
        if (jobPos == JobPosition.TECHNICIAN) {
            registerCard("myAppointments", buildTechnicianAppointmentsCard());
            registerCard("markCompleted",  wrapExisting("Update Appointment Status", new UpdateApptStatusUI()));
            registerCard("myFeedbacks",    wrapExisting("Service Review Hub", new AppointmentForumUI()));
        }

        // ── ALL STAFF — shared Edit Profile & View Profile cards ─────────────
        JPanel viewProfileCard = makeCardPanel();
        viewProfileCard.add(makeWorkspaceTitle("View Profile"), BorderLayout.NORTH);
        viewProfileCard.add(new ViewProfilePanel(false), BorderLayout.CENTER);
        registerCard("viewProfile", viewProfileCard);

        JPanel profilePanel = makeCardPanel();
        profilePanel.add(makeWorkspaceTitle("Edit Profile"), BorderLayout.NORTH);
        profilePanel.add(new EditProfilePanel(this::handleDeleteAccount, false), BorderLayout.CENTER);
        registerCard("editProfile", profilePanel);
    }

    // ── Tabbed appointment cards ──────────────────────────────────────────────

    /**
     * Manager Appointments card: 5 tabs covering all appointment operations.
     * Tabs: View All | Create | Update & Delete | Assign Technician | Mark Completed
     */
    private JPanel buildAccountsCard() {
        JPanel panel = makeCardPanel();
        panel.add(makeWorkspaceTitle("Manage Accounts"), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.addTab("Staff Accounts",     stripFrame(new ManageStaffUI()));
        tabs.addTab("Customer Accounts",  stripFrame(new ManageCustomerUI()));
        tabs.addTab("Account Status",     stripFrame(new UpdateAccStatusUI()));

        panel.add(tabs, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildManagerAppointmentsCard() {
        JPanel panel = makeCardPanel();
        panel.add(makeWorkspaceTitle("Appointments"), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.addTab("Manage Appointments", stripFrame(new ManageAppointmentUI()));
        tabs.addTab("Assign Technician",   stripFrame(new AssignTechnicianUI()));
        tabs.addTab("Update Appointment Status",      stripFrame(new UpdateApptStatusUI()));

        panel.add(tabs, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Counter Staff Appointments card: 2 tabs (no Mark Completed — that's Technician/Manager only).
     * Tabs: Manage Appointments | Assign Technician
     */
    private JPanel buildCounterStaffAppointmentsCard() {
        JPanel panel = makeCardPanel();
        panel.add(makeWorkspaceTitle("Appointments"), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.addTab("Manage Appointments", stripFrame(new ManageAppointmentUI()));
        tabs.addTab("Assign Technician",   stripFrame(new AssignTechnicianUI()));

        panel.add(tabs, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Technician My Appointments card: shows all their assigned appointments.
     * Comments & feedbacks are accessed via the separate Service Review Hub nav item.
     */
    private JPanel buildTechnicianAppointmentsCard() {
        JPanel panel = makeCardPanel();
        panel.add(makeWorkspaceTitle("My Assigned Appointments"), BorderLayout.NORTH);
        panel.add(stripFrame(new ViewAppointmentUI()), BorderLayout.CENTER);
        return panel;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JPanel wrapExisting(String title, JFrame ui) {
        JPanel wrapper = makeCardPanel();
        wrapper.add(makeWorkspaceTitle(title), BorderLayout.NORTH);
        wrapper.add(extractAndStripUI(ui), BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel wrapExisting(JFrame ui) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(241, 245, 249));
        wrapper.add(extractAndStripUI(ui), BorderLayout.CENTER);
        return wrapper;
    }

    /**
     * Variant of wrapExisting for JFrames that need their JFrame reference
     * discarded — same extraction but more descriptively named.
     */
    private JPanel stripFrame(JFrame ui) {
        return wrapExisting(ui);
    }

    @Override
    protected String getRoleBadgeText() {
        UserAccount user = SessionManager.getCurrentUser();
        if (user instanceof StaffAccount) {
            return ((StaffAccount) user).getJobPosition().getJobPosition().toUpperCase();
        }
        return "STAFF";
    }
}
