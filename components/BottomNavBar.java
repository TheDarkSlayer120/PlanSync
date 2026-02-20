package components;

import controller.AppController;
import javax.swing.*;
import java.awt.*;

public class BottomNavBar extends JPanel {

    private AppController controller;

    public BottomNavBar(AppController controller) {
        this.controller = controller;

        setLayout(new GridLayout(1, 8, 8, 0));
        setBackground(new Color(230, 230, 230));  // Same light gray as TimerView
        setPreferredSize(new Dimension(0, 60));
        setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        // EXACTLY same labels/actions as before
        String[] labels = {"Timer", "Stopwatch", "Time Calculator", "Calendar", "Active", "Recurring", "Completed", "Settings"};
        String[] actions = {"TIMER", "STOPWATCH", "TIME_CALCULATOR", "CALENDAR", 
                           "ACTIVE", "RECURRING", "COMPLETED", "SETTINGS"};

        for (int i = 0; i < labels.length; i++) {
            final String label = labels[i];
            final String action = actions[i];
            
            JButton btn = new JButton(label);
            
            // SAME EXACT STYLING as TimerView buttons
            btn.setFocusPainted(false);
            btn.setBackground(new Color(240, 240, 240));  // Same as TimerView presets
            btn.setForeground(Color.BLACK);
            btn.setFont(new Font("SansSerif", Font.BOLD, 11));  // Smaller than TimerView
            btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            
            // Hover effect matching TimerView style
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btn.setBackground(new Color(220, 220, 220));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btn.setBackground(new Color(240, 240, 240));
                }
            });
            
            btn.addActionListener(e -> controller.showView(action));
            add(btn);
        }
    }
}
