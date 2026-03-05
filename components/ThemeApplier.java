package components;

import model.PlanSyncSettings;
import model.Theme;

import javax.swing.*;
import java.awt.*;

/**
 * Centralized theme application so both the main app UI and custom dialogs
 * (confirm/alert popups) share the exact same styling rules.
 */
public final class ThemeApplier {

    private ThemeApplier() {}

    public static void apply(Component root, PlanSyncSettings settings) {

        Theme theme = settings.getSelectedTheme();
        Color light = theme.getLightColor();
        Color dark = theme.getDarkColor();

        boolean darkMode = settings.isDarkMode();
        Color baseBg = darkMode ? new Color(25, 25, 25) : Color.WHITE;

        // Give top-level containers a consistent base background
        if (root instanceof JFrame frame) {
            frame.getContentPane().setBackground(baseBg);
        } else if (root instanceof JDialog dialog) {
            dialog.getContentPane().setBackground(baseBg);
        } else if (root instanceof JPanel panel) {
            panel.setBackground(baseBg);
        }

        applyThemeToComponent(root, light, dark, baseBg, darkMode);

        if (root instanceof Window w) {
            w.repaint();
        } else {
            root.repaint();
        }
    }

    private static void applyThemeToComponent(
            Component comp,
            Color light,
            Color dark,
            Color baseBg,
            boolean darkMode
    ) {

        // Allow components to opt-out (used for Settings preview blocks)
        if (comp instanceof JComponent jc) {
            Object ignore = jc.getClientProperty("ignore_theme");
            if (Boolean.TRUE.equals(ignore)) return;
        }

        // Panels
        if (comp instanceof JPanel panel) {
            Object themed = panel.getClientProperty("themed");
            if (Boolean.TRUE.equals(themed)) {
                panel.setBackground(light);
            } else {
                panel.setBackground(baseBg);
            }

            Object themedBase = panel.getClientProperty("themed_base");
            if (Boolean.TRUE.equals(themedBase)) {
                panel.setBackground(baseBg);
            }
        }

        // Buttons (background + readable font; nav buttons excluded)
        if (comp instanceof JButton button) {
            button.setBackground(dark);

            boolean isNav = false;
            Object nav = button.getClientProperty("nav_button");
            isNav = Boolean.TRUE.equals(nav);

            if (!isNav) {
                Font f = button.getFont();
                float size = Math.max(16f, f.getSize2D());
                button.setFont(f.deriveFont(Font.BOLD, size));
            }
        }

        // Titles/headers should switch color BOTH ways
        if (comp instanceof JLabel label) {
            Object onBase = label.getClientProperty("on_base");
            if (Boolean.TRUE.equals(onBase)) {
                label.setForeground(darkMode ? Color.WHITE : Color.BLACK);
            }
        }

        // Text fields: always readable (white bg, black text)
        if (comp instanceof JTextField field) {
            field.setBackground(Color.WHITE);
            field.setForeground(Color.BLACK);
            field.setCaretColor(Color.BLACK);
        }

        // Text areas: match fields for readability
        if (comp instanceof JTextArea area) {
            area.setBackground(Color.WHITE);
            area.setForeground(Color.BLACK);
            area.setCaretColor(Color.BLACK);
        }

        // Scroll panes: keep their viewport consistent
        if (comp instanceof JScrollPane sp) {
            if (sp.getViewport() != null) {
                sp.getViewport().setBackground(Color.WHITE);
            }
        }

        // Recursively apply
        if (comp instanceof Container container) {
            for (Component child : container.getComponents()) {
                applyThemeToComponent(child, light, dark, baseBg, darkMode);
            }
        }
    }
}