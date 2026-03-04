package components;

import controller.AppController;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class BottomNavBar extends JPanel {

    private final AppController controller;
    private final Map<String, JButton> buttons = new HashMap<>();
    private JButton activeButton;

    private final Color defaultColor = new Color(240, 240, 240);
    private final Color hoverColor = new Color(220, 220, 220);

    public BottomNavBar(AppController controller) {

        this.controller = controller;

        setLayout(new GridLayout(1, 9, 8, 0));
        setPreferredSize(new Dimension(0, 60));
        setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        // This panel should be theme controlled
        putClientProperty("themed", true);

        String[] labels = {
                "Home",
                "Timer",
                "Stopwatch",
                "Time Calculator",
                "Calendar",
                "Active",
                "Recurring",
                "Completed",
                "Settings"
        };

        String[] actions = {
                "HOME",
                "TIMER",
                "STOPWATCH",
                "TIME_CALCULATOR",
                "CALENDAR",
                "ACTIVE",
                "RECURRING",
                "COMPLETED",
                "SETTINGS"
        };

        for (int i = 0; i < labels.length; i++) {

            JButton btn = createNavButton(labels[i], actions[i]);
            buttons.put(actions[i], btn);
            add(btn);
        }
    }

    // ================= BUTTON CREATION =================
    private JButton createNavButton(String text, String action) {

        JButton btn = new JButton(text);

        btn.setFocusPainted(false);
        btn.setBackground(defaultColor);
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        btn.putClientProperty("active", false);

        // Hover effect (does NOT override active)
        btn.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!(Boolean) btn.getClientProperty("active")) {
                    btn.setBackground(hoverColor);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!(Boolean) btn.getClientProperty("active")) {
                    btn.setBackground(defaultColor);
                }
            }
        });

        btn.addActionListener(e -> controller.showView(action));

        return btn;
    }

    // ================= ACTIVE PAGE HANDLING =================
    public void setActive(String pageName) {

        // Remove highlight from previous
        if (activeButton != null) {
            activeButton.putClientProperty("active", false);
            activeButton.setBorder(
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
            );
            activeButton.setBackground(defaultColor);
        }

        // Set new active
        JButton newActive = buttons.get(pageName);

        if (newActive != null) {
            newActive.putClientProperty("active", true);

            // Modern bottom underline indicator
            newActive.setBorder(
                    BorderFactory.createMatteBorder(
                            0, 0, 4, 0,
                            new Color(70, 110, 200)
                    )
            );

            activeButton = newActive;
        }
    }
}