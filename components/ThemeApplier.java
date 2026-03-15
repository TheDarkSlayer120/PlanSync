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
 * File purpose: This class supports the ThemeApplier part of PlanSync and documents the main responsibilities of the file.
 */

import model.PlanSyncSettings;
import model.Theme;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;

public final class ThemeApplier {

    private ThemeApplier() {}

    // Section: Handle the logic for apply.
    public static void apply(Component root, PlanSyncSettings settings) {
        Theme theme = settings.getSelectedTheme();
        Color light = theme.getLightColor();
        Color dark = theme.getDarkColor();

        boolean darkMode = settings.isDarkMode();
        Color baseBg = darkMode ? new Color(25, 25, 25) : Color.WHITE;

        if (root instanceof JFrame frame) {
            frame.getContentPane().setBackground(baseBg);
        // Section: Handle the logic for if.
        } else if (root instanceof JDialog dialog) {
            dialog.getContentPane().setBackground(baseBg);
        // Section: Handle the logic for if.
        } else if (root instanceof JPanel panel) {
            panel.setBackground(baseBg);
        }

        // Section: Handle the logic for apply theme to component.
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
        if (comp instanceof JComponent jc) {
            Object ignore = jc.getClientProperty("ignore_theme");
            if (Boolean.TRUE.equals(ignore)) return;
        }

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

        if (comp instanceof JButton button) {
            button.setBackground(dark);

            Object nav = button.getClientProperty("nav_button");
            boolean isNav = Boolean.TRUE.equals(nav);

            if (!isNav) {
                Font f = button.getFont();
                float size = Math.max(16f, f.getSize2D());
                button.setFont(f.deriveFont(Font.BOLD, size));
            }
        }

        if (comp instanceof JLabel label) {
            Object onBase = label.getClientProperty("on_base");
            if (Boolean.TRUE.equals(onBase)) {
                label.setForeground(darkMode ? Color.WHITE : Color.BLACK);
            }
        }

        if (comp instanceof JTextField field) {
            field.setBackground(Color.WHITE);
            field.setForeground(Color.BLACK);
            field.setCaretColor(Color.BLACK);
        }

        if (comp instanceof JTextArea area) {
            area.setBackground(Color.WHITE);
            area.setForeground(Color.BLACK);
            area.setCaretColor(Color.BLACK);
        }

        if (comp instanceof JComboBox<?> combo) {
            // Section: Handle the logic for style combo box.
            styleComboBox(combo, darkMode, dark);
        }

        if (comp instanceof JScrollPane sp) {
            if (sp.getViewport() != null) {
                sp.getViewport().setBackground(Color.WHITE);
            }
            ModernScrollBars.apply(sp, darkMode, dark);
        }

        if (comp instanceof Container container) {
            for (Component child : container.getComponents()) {
                // Section: Handle the logic for apply theme to component.
                applyThemeToComponent(child, light, dark, baseBg, darkMode);
            }
        }
    }

    // Section: Handle the logic for style combo box.
    private static void styleComboBox(JComboBox<?> combo, boolean darkMode, Color accent) {
        combo.setBackground(Color.WHITE);
        combo.setForeground(Color.BLACK);
        combo.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        if (!(combo.getUI() instanceof ModernComboBoxUI)) {
            combo.setUI(new ModernComboBoxUI(darkMode, accent));
        }

        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setFont(combo.getFont());
                label.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                label.setBackground(isSelected ? accent : Color.WHITE);
                label.setForeground(Color.BLACK);
                return label;
            }
        });

        if (combo.isEditable()) {
            Component ec = combo.getEditor().getEditorComponent();
            if (ec instanceof JTextField tf) {
                tf.setBackground(Color.WHITE);
                tf.setForeground(Color.BLACK);
                tf.setCaretColor(Color.BLACK);
                tf.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                tf.setFont(combo.getFont());
            }
        }
    }

    private static final class ModernComboBoxUI extends BasicComboBoxUI {
        private final boolean darkMode;
        private final Color accent;

        // Section: Handle the logic for modern combo box ui.
        private ModernComboBoxUI(boolean darkMode, Color accent) {
            this.darkMode = darkMode;
            this.accent = accent;
        }

        @Override
        // Section: Build and return the elements needed to arrow button.
        protected JButton createArrowButton() {
            JButton button = new BasicArrowButton(SwingConstants.SOUTH, accent, accent, Color.BLACK, accent);
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setFocusPainted(false);
            button.setContentAreaFilled(true);
            return button;
        }

        @Override
        // Section: Build and return the elements needed to popup.
        protected ComboPopup createPopup() {
            BasicComboPopup popup = new BasicComboPopup(comboBox) {
                @Override
                // Section: Build and return the elements needed to scroller.
                protected JScrollPane createScroller() {
                    JScrollPane sp = new JScrollPane(list,
                            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                    sp.setBorder(BorderFactory.createEmptyBorder());
                    sp.getViewport().setBackground(Color.WHITE);
                    ModernScrollBars.apply(sp, darkMode, accent);
                    return sp;
                }
            };
            Border border = BorderFactory.createLineBorder(new Color(0, 0, 0, 70), 1);
            popup.setBorder(border);
            popup.setBackground(Color.WHITE);
            return popup;
        }
    }
}
