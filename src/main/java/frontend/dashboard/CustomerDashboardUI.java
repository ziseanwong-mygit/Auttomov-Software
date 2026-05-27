package main.java.frontend.dashboard;

import main.java.frontend.comment.ManageCommentUI;
import main.java.frontend.forum.AppointmentForumUI;
import main.java.frontend.history.CustomerHistoryUI;
import main.java.frontend.menu.mainMenuUI;
import main.java.frontend.shared.BaseDashboardUI;
import main.java.frontend.shared.EditProfilePanel;
import main.java.frontend.shared.SessionManager;
import main.java.frontend.payment.ManageInvoiceUI;
import main.java.frontend.payment.ManagePaymentReceiptUI;
import main.java.frontend.shared.ViewProfilePanel;

import javax.swing.*;
import java.awt.*;

public class CustomerDashboardUI extends BaseDashboardUI {

    public CustomerDashboardUI() {
        super("Customer Dashboard — Auttomov Software");

        buildSidebarItems();
        buildWorkspaceCards();
        setVisible(true);
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────

    private void buildSidebarItems() {
        addNavGroup("My History");
        JButton apptBtn = addNavItem("", "My Appointments", "appointments");
        addNavItem("", "My Invoices", "myInvoices");
        addNavItem("", "My Payments & Receipts", "myPaymentReceipt");

        addNavGroup("Communication");
        addNavItem("", "Service Review Hub", "forum");

        addSidebarSpacer();
        addSidebarDivider();
        addNavGroup("Account");
        addNavItem("", "View Profile", "viewProfile");
        addNavItem("", "Edit Profile", "editProfile");
        addNavAction("", "Logout", new Color(239, 68, 68), e -> {
            SessionManager.clearSession();
            new mainMenuUI().setVisible(true);
            dispose();
        });

        // Default card on load
        SwingUtilities.invokeLater(() -> showDefaultCard("appointments", apptBtn));
    }

    // ── Workspace cards ───────────────────────────────────────────────────────

    private void buildWorkspaceCards() {
        registerCard("appointments", buildCard("My Appointments", new main.java.frontend.appointment.ViewAppointmentUI()));
        registerCard("myInvoices",   buildCard("My Invoices", new ManageInvoiceUI()));
        registerCard("myPaymentReceipt", buildCard("My Payments & Receipts", new ManagePaymentReceiptUI()));
        registerCard("forum",        buildForumCard());
        
        JPanel viewProfileCard = makeCardPanel();
        viewProfileCard.add(makeWorkspaceTitle("View Profile"), BorderLayout.NORTH);
        viewProfileCard.add(new ViewProfilePanel(false), BorderLayout.CENTER);
        registerCard("viewProfile",  viewProfileCard);
        
        JPanel profilePanel = makeCardPanel();
        profilePanel.add(makeWorkspaceTitle("Edit Profile"), BorderLayout.NORTH);
        profilePanel.add(new EditProfilePanel(this::handleDeleteAccount, false), BorderLayout.CENTER);
        registerCard("editProfile",  profilePanel);
    }

    // ── Card builders ─────────────────────────────────────────────────────────

    private JPanel buildCard(String title, JFrame ui) {
        JPanel panel = makeCardPanel();
        panel.add(makeWorkspaceTitle(title), BorderLayout.NORTH);
        panel.add(extractContent(ui), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildForumCard() {
        JPanel panel = makeCardPanel();
        panel.add(makeWorkspaceTitle("Service Review Hub"), BorderLayout.NORTH);
        AppointmentForumUI forum = new AppointmentForumUI();
        Container cp = forum.getContentPane();
        cp.setBackground(new Color(241, 245, 249));
        panel.add(cp, BorderLayout.CENTER);
        return panel;
    }

    // ── Helper: extract content pane from a JFrame ────────────────────────────
    
    private Container wrapExisting(JPanel panel) {
        panel.setBackground(new Color(241, 245, 249));
        return panel;
    }

    private Container extractContent(JFrame ui) {
        Container cp = ui.getContentPane();
        cp.setBackground(new Color(241, 245, 249));
        return cp;
    }

    @Override
    protected String getRoleBadgeText() {
        return "CUSTOMER";
    }
}
