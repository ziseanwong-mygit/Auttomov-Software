package main.java.frontend.shared;

import main.java.backend.controller.AccountController;
import main.java.backend.model.UserAccount;
import main.java.frontend.menu.mainMenuUI;
import main.java.records.OperationResult;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * BaseDashboardUI — Master sidebar layout template for all four roles.
 *
 * Layout:
 *   NORTH  — Header bar (branding left, user info right)
 *   WEST   — Navigation sidebar (dark slate, grouped nav buttons)
 *   CENTER — CardLayout workspace (panels swapped on nav click)
 *
 * Subclasses call:
 *   addNavGroup(label)         — inserts a section divider + label
 *   addNavItem(icon, label, cardName) — inserts a sidebar nav button
 *   registerCard(name, panel) — registers a workspace panel
 *   showCard(name)            — switches the workspace to a named card
 */
public abstract class BaseDashboardUI extends JFrame {

    // ── Design tokens ────────────────────────────────────────────────────────
    protected static final Color SIDEBAR_BG       = new Color(15,  23,  42);   // #0F172A
    protected static final Color SIDEBAR_HOVER    = new Color(30,  41,  59);   // #1E293B
    protected static final Color SIDEBAR_ACTIVE   = new Color(37,  99, 235);   // #2563EB (blue accent)
    protected static final Color SIDEBAR_TEXT     = new Color(203, 213, 225);  // #CBD5E1
    protected static final Color SIDEBAR_GROUP    = new Color(100, 116, 139);  // #64748B
    protected static final Color HEADER_BG        = new Color(15,  23,  42);   // same as sidebar
    protected static final Color WORKSPACE_BG     = new Color(241, 245, 249);  // #F1F5F9
    protected static final Font  FONT_NAV         = new Font("Segoe UI", Font.PLAIN,  14);
    protected static final Font  FONT_NAV_GROUP   = new Font("Segoe UI", Font.BOLD,   10);
    protected static final Font  FONT_HEADER_NAME = new Font("Segoe UI", Font.BOLD,   16);
    protected static final Font  FONT_HEADER_SUB  = new Font("Segoe UI", Font.PLAIN,  12);

    // ── Layout components ────────────────────────────────────────────────────
    private final JPanel     sidebarPanel  = new JPanel();
    private final JPanel     workspaceHost = new JPanel();
    private final CardLayout cardLayout    = new CardLayout();

    private JButton activeNavButton = null;
    private final java.util.Map<String, JPanel> registeredCards = new java.util.HashMap<>();

    // ── Constructor ──────────────────────────────────────────────────────────
    public BaseDashboardUI(String frameTitle) {
        super(frameTitle);
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 580));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        buildHeader();
        buildSidebar();
        buildWorkspace();
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private void buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(new EmptyBorder(0, 20, 0, 20));
        header.setPreferredSize(new Dimension(0, 60));

        // Left: app branding
        JLabel brand = new JLabel("Auttomov");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 18));
        brand.setForeground(Color.WHITE);

        // Right: user info chip
        UserAccount user = SessionManager.getCurrentUser();
        String nameText = user != null
                ? user.getFirstName() + " " + user.getLastName()
                : "Unknown";
        String roleText = getRoleBadgeText();

        JPanel userChip = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userChip.setOpaque(false);

        JLabel roleLabel = new JLabel(roleText);
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        roleLabel.setForeground(new Color(148, 163, 184));

        JLabel nameLabel = new JLabel(nameText);
        nameLabel.setFont(FONT_HEADER_NAME);
        nameLabel.setForeground(Color.WHITE);

        userChip.add(roleLabel);
        userChip.add(nameLabel);

        header.add(brand,    BorderLayout.WEST);
        header.add(userChip, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────
    private void buildSidebar() {
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(SIDEBAR_BG);
        sidebarPanel.setBorder(new EmptyBorder(16, 0, 16, 0));
        // Scroll wrapper (in case many items)
        JScrollPane sidebarScroll = new JScrollPane(sidebarPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sidebarScroll.setBorder(null);
        sidebarScroll.getViewport().setBackground(SIDEBAR_BG);
        sidebarScroll.setPreferredSize(new Dimension(220, 0));

        add(sidebarScroll, BorderLayout.WEST);
    }

    // ── Workspace ─────────────────────────────────────────────────────────────
    private void buildWorkspace() {
        workspaceHost.setLayout(cardLayout);
        workspaceHost.setBackground(WORKSPACE_BG);

        // Default blank card shown before any nav item is selected
        JPanel blank = new JPanel(new BorderLayout());
        blank.setBackground(WORKSPACE_BG);
        JLabel welcome = new JLabel("Select an option from the sidebar.", SwingConstants.CENTER);
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        welcome.setForeground(new Color(148, 163, 184));
        blank.add(welcome, BorderLayout.CENTER);
        workspaceHost.add(blank, "__blank__");

        add(workspaceHost, BorderLayout.CENTER);
    }

    // ── Protected API ─────────────────────────────────────────────────────────

    /**
     * Add a section divider label to the sidebar (e.g. "ACCOUNT MANAGEMENT").
     */
    protected void addNavGroup(String label) {
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 18)));

        JLabel groupLabel = new JLabel("  " + label.toUpperCase());
        groupLabel.setFont(FONT_NAV_GROUP);
        groupLabel.setForeground(SIDEBAR_GROUP);
        groupLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        groupLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        sidebarPanel.add(groupLabel);

        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 4)));
    }

    /**
     * Add a clickable navigation item to the sidebar.
     * Clicking it switches the workspace CardLayout to the named card.
     *
     * @param icon     emoji or short icon text
     * @param label    display text
     * @param cardName the key used in registerCard()
     */
    protected JButton addNavItem(String icon, String label, String cardName) {
        String text = (icon != null && !icon.isEmpty()) ? icon + "  " + label : label;
        JButton btn = new JButton(text);
        btn.setFont(FONT_NAV);
        btn.setForeground(SIDEBAR_TEXT);
        btn.setBackground(SIDEBAR_BG);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn != activeNavButton) btn.setBackground(SIDEBAR_HOVER);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn != activeNavButton) btn.setBackground(SIDEBAR_BG);
            }
        });

        btn.addActionListener(e -> {
            setActiveNavButton(btn);
            showCard(cardName);
        });

        sidebarPanel.add(btn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        return btn;
    }

    /**
     * Add a bottom-pinned sidebar button that does not correspond to a card
     * (e.g. Logout). The caller supplies the full ActionListener.
     */
    protected JButton addNavAction(String icon, String label, Color textColor, ActionListener action) {
        String text = (icon != null && !icon.isEmpty()) ? icon + "  " + label : label;
        JButton btn = new JButton(text);
        btn.setFont(FONT_NAV);
        btn.setForeground(textColor);
        btn.setBackground(SIDEBAR_BG);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(SIDEBAR_HOVER); }
            @Override public void mouseExited (java.awt.event.MouseEvent e) { btn.setBackground(SIDEBAR_BG); }
        });

        btn.addActionListener(action);
        sidebarPanel.add(btn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        return btn;
    }

    /** Push all subsequent items to the bottom of the sidebar. */
    protected void addSidebarSpacer() {
        sidebarPanel.add(Box.createVerticalGlue());
    }

    /** Add a thin horizontal separator line in the sidebar. */
    protected void addSidebarDivider() {
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(new Color(30, 41, 59));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebarPanel.add(sep);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 8)));
    }

    /**
     * Register a JPanel as a named workspace card.
     * Must be called during subclass initialisation, before the frame is shown.
     */
    protected void registerCard(String name, JPanel panel) {
        panel.setBackground(WORKSPACE_BG);
        workspaceHost.add(panel, name);
        registeredCards.put(name, panel);
    }

    /** Switch the workspace to a registered card by name. */
    protected void showCard(String name) {
        cardLayout.show(workspaceHost, name);
        JPanel card = registeredCards.get(name);
        if (card != null) {
            triggerRefresh(card);
        }
    }

    private void triggerRefresh(Component comp) {
        if (comp instanceof Refreshable) {
            ((Refreshable) comp).refreshData();
        }
        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                triggerRefresh(child);
            }
        }
    }

    /** Show the first card and mark its nav button as active. */
    protected void showDefaultCard(String cardName, JButton navButton) {
        setActiveNavButton(navButton);
        showCard(cardName);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void setActiveNavButton(JButton btn) {
        if (activeNavButton != null) {
            activeNavButton.setBackground(SIDEBAR_BG);
            activeNavButton.setForeground(SIDEBAR_TEXT);
        }
        activeNavButton = btn;
        btn.setBackground(SIDEBAR_ACTIVE);
        btn.setForeground(Color.WHITE);
    }

    /** Subclass must return the text to display in the role badge (e.g. "MANAGER"). */
    protected abstract String getRoleBadgeText();

    // ── Shared Delete-Account logic (used by Edit Profile card in all roles) ──
    protected void handleDeleteAccount() {
        int first = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete your account?\nThis action cannot be undone.",
                "Confirm Deletion — Step 1/2",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (first != JOptionPane.YES_OPTION) return;

        int second = JOptionPane.showConfirmDialog(this,
                "This is your FINAL confirmation.\nYour account and all data will be permanently removed.",
                "Confirm Deletion — Step 2/2",
                JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
        if (second != JOptionPane.YES_OPTION) return;

        UserAccount user = SessionManager.getCurrentUser();
        AccountController ctrl = new AccountController();
        OperationResult result = ctrl.deleteAccount(user.getAccountID(), SessionManager.getCurrentRole());

        if (result.isValid()) {
            JOptionPane.showMessageDialog(this,
                    "Your account has been deleted. Goodbye!",
                    "Account Deleted", JOptionPane.INFORMATION_MESSAGE);
            SessionManager.clearSession();
            new mainMenuUI().setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, result.generalMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Utility: styled workspace title panel mimicking service screen ─────────
    protected JComponent makeWorkspaceTitle(String text) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(15, 23, 42)); // slate 900 dark bg
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel titleLbl = new JLabel(text);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLbl.setForeground(Color.WHITE);

        String subtitleText;
        switch (text) {
            case "Manage Accounts":
                subtitleText = "Configure and manage active credentials for counter staff, managers, and customers.";
                break;
            case "Appointments":
                subtitleText = "Create and schedule customer appointments, configure service times, and manage rosters.";
                break;
            case "My Assigned Appointments":
                subtitleText = "Track and execute vehicle service jobs assigned to your active shift.";
                break;
            case "Comments & Feedbacks":
                subtitleText = "Browse customer ratings, reviews, and detailed service inquiries.";
                break;
            case "Service Review Hub":
                subtitleText = "Appointment-oriented center for customer comments and technician feedback threads.";
                break;
            case "Customer Comments":
                subtitleText = "View comment threads and messages submitted by vehicle owners.";
                break;
            case "Edit Profile":
                subtitleText = "Manage your personal account information, credentials, and password security.";
                break;
            case "View Profile":
                subtitleText = "Review your registered account credentials and details.";
                break;
            case "Payment Records":
                subtitleText = "View complete historic financial logs, transactions, and invoice receipts.";
                break;
            case "Billing & Payments":
                subtitleText = "Generate invoices, record customer payments, and print system receipts.";
                break;
            case "Invoices":
            case "My Invoices":
                subtitleText = "Track and review invoices issued for appointments, and submit customer payments.";
                break;
            case "Payment & Receipts":
            case "My Payments & Receipts":
                subtitleText = "View complete payment transaction records and manage official customer receipts.";
                break;
            case "My Feedbacks":
                subtitleText = "Browse reviews and feedback scores left by customers for your completed appointments.";
                break;
            case "Mark Completed":
            case "Update Appointment Status":
                subtitleText = "Update in-progress appointment statuses and mark them as successfully completed.";
                break;
            case "Manage Packages":
                subtitleText = "Manage package structures, configure prices, activities, and vehicle parts.";
                break;
            default:
                subtitleText = "Manage and track system operations for the Auttomov software suite.";
                break;
        }

        JLabel subLbl = new JLabel(subtitleText);
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLbl.setForeground(new Color(200, 220, 255));

        headerPanel.add(titleLbl, BorderLayout.WEST);
        headerPanel.add(subLbl, BorderLayout.SOUTH);
        
        // Add subtle bottom margin below the header (set to 0 to eliminate any gap, matching the service UI)
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.add(headerPanel, BorderLayout.CENTER);
        container.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        return container;
    }

    // ── Utility: card wrapper panel with padding ──────────────────────────────
    protected JPanel makeCardPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(WORKSPACE_BG);
        p.setBorder(null);
        return p;
    }

    // ── Helper: extract content pane from JFrame and strip internal header banners ──
    protected Container extractAndStripUI(JFrame ui) {
        Container cp = ui.getContentPane();
        cp.setBackground(WORKSPACE_BG);
        hideInternalBanners(cp);
        return cp;
    }

    private void hideInternalBanners(Container container) {
        if (container == null) return;
        for (Component child : container.getComponents()) {
            if (child instanceof JPanel p) {
                if (p.getBorder() instanceof EmptyBorder border) {
                    Insets insets = border.getBorderInsets();
                    if (insets.left == 25 && insets.right == 25) {
                        p.setVisible(false);
                        continue;
                    }
                }
                hideInternalBanners(p);
            }
        }
    }
}
