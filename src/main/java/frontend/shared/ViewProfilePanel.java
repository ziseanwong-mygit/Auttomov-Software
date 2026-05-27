package main.java.frontend.shared;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import main.java.backend.model.StaffAccount;
import main.java.backend.model.UserAccount;
import main.java.enums.data.JobPosition;

/**
 * ViewProfilePanel — Reusable read-only profile viewer panel for all dashboards.
 */
public class ViewProfilePanel extends JPanel {

    public ViewProfilePanel() {
        this(true);
    }

    public ViewProfilePanel(boolean showTitle) {
        setBackground(new Color(241, 245, 249));
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(28, 28, 28, 28));

        // Title
        if (showTitle) {
            JLabel title = new JLabel("View Profile");
            title.setFont(new Font("Segoe UI", Font.BOLD, 22));
            title.setForeground(new Color(15, 23, 42));
            title.setBorder(new EmptyBorder(0, 0, 20, 0));
            add(title, BorderLayout.NORTH);
        }

        UserAccount user = SessionManager.getCurrentUser();
        if (user == null) {
            add(new JLabel("No active session found."), BorderLayout.CENTER);
            return;
        }

        JPanel cols = new JPanel(new GridLayout(1, 2, 30, 0));
        cols.setOpaque(false);

        cols.add(buildPersonalSection(user));

        if (user instanceof StaffAccount staff) {
            cols.add(buildStaffSection(staff));
        } else {
            // Fill right side for visual symmetry
            JPanel emptyPlaceholder = new JPanel();
            emptyPlaceholder.setOpaque(false);
            cols.add(emptyPlaceholder);
        }

        JScrollPane scroll = new JScrollPane(cols,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildPersonalSection(UserAccount user) {
        JPanel card = makeCard("Personal Details");
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        card.add(detailRow("User ID (IC / Passport):", user.getUserID()));
        card.add(gap());
        card.add(detailRow("First Name:", user.getFirstName()));
        card.add(gap());
        card.add(detailRow("Last Name:", user.getLastName()));
        card.add(gap());
        card.add(detailRow("Email Address:", user.getEmailAddress()));
        card.add(gap());
        card.add(detailRow("Phone Number:", user.getPhoneNumber()));
        card.add(gap());
        card.add(detailRow("User Role:", user.getUserRole().getUserRole()));
        card.add(gap());
        card.add(detailRow("Account Status:", user.getAccountStatus().getAccStatus()));

        card.add(Box.createVerticalGlue());
        return wrapCard(card);
    }

    private JPanel buildStaffSection(StaffAccount staff) {
        JPanel card = makeCard("Employment & Work Details");
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        card.add(detailRow("Job Position:", staff.getJobPosition().getJobPosition()));
        card.add(gap());
        card.add(detailRow("Employment Status:", staff.getEmploymentStatus().getEmpStatus()));
        card.add(gap());
        card.add(detailRow("Employment Date:", staff.getEmploymentDate().toString()));

        if (staff.getJobPosition() == JobPosition.TECHNICIAN) {
            card.add(gap());
            String spec = staff.getSpecialization() != null && !staff.getSpecialization().isBlank() ? 
                    staff.getSpecialization() : "General Automotive Care";
            card.add(detailRow("Specialization Area:", spec));
        }

        card.add(Box.createVerticalGlue());
        return wrapCard(card);
    }

    private JPanel detailRow(String labelText, String valueText) {
        JPanel r = new JPanel(new BorderLayout(0, 4));
        r.setOpaque(false);
        r.setAlignmentX(Component.LEFT_ALIGNMENT);
        r.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(100, 116, 139));

        JLabel val = new JLabel(valueText);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        val.setForeground(new Color(15, 23, 42));
        val.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(8, 10, 8, 10)));
        val.setBackground(Color.WHITE);
        val.setOpaque(true);

        r.add(lbl, BorderLayout.NORTH);
        r.add(val, BorderLayout.CENTER);
        return r;
    }

    private JPanel makeCard(String sectionTitle) {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));
        TitledBorder tb = BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(), sectionTitle,
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), new Color(30, 41, 59));
        p.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new CompoundBorder(new EmptyBorder(4, 0, 0, 0),
                        new CompoundBorder(tb, new EmptyBorder(8, 6, 6, 6)))));
        return p;
    }

    private JPanel wrapCard(JPanel card) {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.add(card, BorderLayout.NORTH);
        return outer;
    }

    private Component gap() {
        return Box.createRigidArea(new Dimension(0, 12));
    }
}
