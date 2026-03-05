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

    public BottomNavBar(AppController controller) {

        this.controller = controller;

        setLayout(new GridLayout(1, 9, 8, 0));
        setPreferredSize(new Dimension(0, 60));
        setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

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

    private JButton createNavButton(String text, String action) {

        JButton btn = new JButton(text);

        btn.setFocusPainted(false);
        btn.setBackground(defaultColor);
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        btn.setRolloverEnabled(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        btn.putClientProperty("active", false);
        btn.putClientProperty("nav_button", true); // ✅ prevents AppController from enlarging nav font

        btn.addActionListener(e -> controller.showView(action));

        return btn;
    }

    public void setActive(String pageName) {

        if (activeButton != null) {
            activeButton.putClientProperty("active", false);
            activeButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            activeButton.setBackground(defaultColor);
        }

        JButton newActive = buttons.get(pageName);

        if (newActive != null) {
            newActive.putClientProperty("active", true);

            newActive.setBorder(
                    BorderFactory.createMatteBorder(0, 0, 4, 0, new Color(70, 110, 200))
            );

            activeButton = newActive;
        }
    }
}