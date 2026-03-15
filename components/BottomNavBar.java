package components;


/*
 *  ██████╗ ██╗      █████╗ ███╗   ██╗███████╗██╗   ██╗███╗   ██╗ ██████╗
 *  ██╔══██╗██║     ██╔══██╗████╗  ██║██╔════╝╚██╗ ██╔╝████╗  ██║██╔════╝
 *  ██████╔╝██║     ███████║██╔██╗ ██║███████╗ ╚████╔╝ ██╔██╗ ██║██║     
 *  ██╔═══╝ ██║     ██╔══██║██║╚██╗██║╚════██║  ╚██╔╝  ██║╚██╗██║██║     
 *  ██║     ███████╗██║  ██║██║ ╚████║███████║   ██║   ██║ ╚████║╚██████╗
 *  ╚═╝     ╚══════╝╚═╝  ╚═╝╚═╝  ╚═══╝╚══════╝   ╚═╝   ╚═╝  ╚═══╝ ╚═════╝
 *
 *  PlanSync source guide
 *  - This file includes a short header describing the class or interface purpose.
 *  - Method comments mark the responsibility of each section so the flow is easier to follow.
 */
/**
 * File purpose: This class supports the BottomNavBar part of PlanSync and documents the main responsibilities of the file.
 */

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

        // Section: Update the state used to layout.
        setLayout(new GridLayout(1, 9, 8, 0));
        // Section: Update the state used to preferred size.
        setPreferredSize(new Dimension(0, 70));
        // Section: Update the state used to border.
        setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        // Section: Handle the logic for put client property.
        putClientProperty("themed", true);

        String[] labels = {
                "HOME",
                "TIMER",
                "STOPWATCH",
                "<html><div style='text-align:center;'>TIME<br>CALCULATOR</div></html>",
                "CALENDAR",
                "ACTIVE",
                "RECURRING",
                "COMPLETED",
                "SETTINGS"
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
            // Section: Add the data or behavior needed to add.
            add(btn);
        }
    }

    // Section: Build and return the elements needed to nav button.
    private JButton createNavButton(String text, String action) {

        JButton btn = new JButton(text);

        btn.setFocusPainted(false);
        btn.setBackground(defaultColor);
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setRolloverEnabled(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        btn.putClientProperty("active", false);
        btn.putClientProperty("nav_button", true);

        btn.addActionListener(e -> controller.showView(action));

        return btn;
    }

    // Section: Update the state used to active.
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
