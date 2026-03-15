package views;


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
 * File purpose: This class supports the SettingsView part of PlanSync and documents the main responsibilities of the file.
 */

import controller.AppController;
import model.PlanSyncSettings;
import model.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class SettingsView extends JPanel {

    private final AppController controller;
    private final PlanSyncSettings settings;

    private JTextField usernameField;
    private Theme selectedTheme;
    private boolean selectedDarkMode;

    private final Map<Theme, JPanel> themePanels = new HashMap<>();
    private final Map<String, JPanel> modePanels = new HashMap<>();

    public SettingsView(AppController controller, PlanSyncSettings settings) {

        this.controller = controller;
        this.settings = settings;

        this.selectedTheme = settings.getSelectedTheme();
        this.selectedDarkMode = settings.isDarkMode();

        // Section: Update the state used to layout.
        setLayout(new BorderLayout());

        // ================= TITLE =================
        JLabel title = new JLabel("S E T T I N G S", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        title.putClientProperty("on_base", true);
        // Section: Add the data or behavior needed to add.
        add(title, BorderLayout.NORTH);

        // ================= OUTER CENTER =================
        JPanel outer = new JPanel();
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setOpaque(false);
        outer.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        // Section: Add the data or behavior needed to add.
        add(outer, BorderLayout.CENTER);

        // ================= ROUNDED CONTENT PANEL =================
        JPanel content = new RoundedPanel(30);
        content.putClientProperty("themed", true);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(20, 40, 25, 40));
        content.setMaximumSize(new Dimension(Integer.MAX_VALUE, 700));
        content.setAlignmentX(Component.CENTER_ALIGNMENT);

        outer.add(Box.createVerticalStrut(10));
        outer.add(content);
        outer.add(Box.createVerticalGlue());

        // ================= USERNAME =================
        JLabel usernameTitle = new JLabel("USERNAME:");
        usernameTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameTitle.setFont(new Font("SansSerif", Font.BOLD, 14));

        content.add(Box.createVerticalStrut(10));
        content.add(usernameTitle);
        content.add(Box.createVerticalStrut(10));

        usernameField = new JTextField(settings.getUsername());
        usernameField.setMaximumSize(new Dimension(400, 40));
        usernameField.setHorizontalAlignment(JTextField.CENTER);
        usernameField.setFont(new Font("SansSerif", Font.BOLD, 14));
        content.add(usernameField);

        content.add(Box.createVerticalStrut(30));

        // ================= SELECT THEME =================
        JLabel themeTitle = new JLabel("SELECT THEME:");
        themeTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        themeTitle.setFont(new Font("SansSerif", Font.BOLD, 14));

        content.add(themeTitle);
        content.add(Box.createVerticalStrut(20));

        JPanel grid = new JPanel(new GridLayout(3, 3, 20, 20));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(600, 200));

        for (Theme theme : settings.getThemes()) {
            JPanel panel = createThemePanel(theme);
            themePanels.put(theme, panel);
            grid.add(panel);
        }

        content.add(grid);
        content.add(Box.createVerticalStrut(30));

        // ================= SELECT MODE =================
        JLabel modeTitle = new JLabel("SELECT MODE:");
        modeTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        modeTitle.setFont(new Font("SansSerif", Font.BOLD, 14));

        content.add(modeTitle);
        content.add(Box.createVerticalStrut(20));

        JPanel modeRow = new JPanel(new GridLayout(1, 2, 20, 0));
        modeRow.setOpaque(false);
        modeRow.setMaximumSize(new Dimension(520, 85));

        JPanel lightPanel = createModePanel("LIGHT");
        JPanel darkPanel  = createModePanel("DARK");

        modePanels.put("LIGHT", lightPanel);
        modePanels.put("DARK", darkPanel);

        modeRow.add(lightPanel);
        modeRow.add(darkPanel);

        content.add(modeRow);
        content.add(Box.createVerticalStrut(30));

        // ================= SAVE =================
        JButton save = new JButton("SAVE");
        save.setAlignmentX(Component.CENTER_ALIGNMENT);
        save.setFocusPainted(false);
        save.setFont(new Font("SansSerif", Font.BOLD, 18));

        Dimension size = new Dimension(300, 45);
        save.setPreferredSize(size);
        save.setMaximumSize(size);
        save.setMinimumSize(size);

        content.add(save);

        // Section: Refresh or recompute the state used to highlight.
        updateHighlight();

        save.addActionListener(e -> {
            settings.setUsername(usernameField.getText());
            settings.setSelectedTheme(selectedTheme);
            settings.setDarkMode(selectedDarkMode);
            settings.saveToFile();
            controller.applyTheme();
        });
    }

    // ================= THEME PREVIEW PANEL =================
    // Section: Build and return the elements needed to theme panel.
    private JPanel createThemePanel(Theme theme) {

        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.setPreferredSize(new Dimension(120, 60));
        panel.setOpaque(false);

        JPanel light = new JPanel();
        light.setBackground(theme.getLightColor());
        light.putClientProperty("ignore_theme", true);

        JPanel dark = new JPanel();
        dark.setBackground(theme.getDarkColor());
        dark.putClientProperty("ignore_theme", true);

        panel.add(light);
        panel.add(dark);

        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            // Section: Handle the logic for mouse clicked.
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectedTheme = theme;
                // Section: Refresh or recompute the state used to highlight.
                updateHighlight();
            }
        });

        return panel;
    }

    // ================= MODE PANEL (FIXED LABEL BAR COLORS) =================
    // Section: Build and return the elements needed to mode panel.
    private JPanel createModePanel(String modeName) {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(220, 65));
        panel.setOpaque(false);

        // preview area (top)
        JPanel preview = new JPanel();
        if ("LIGHT".equalsIgnoreCase(modeName)) {
            preview.setBackground(Color.WHITE);
        } else {
            preview.setBackground(new Color(35, 35, 35));
        }
        preview.putClientProperty("ignore_theme", true);
        preview.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(preview, BorderLayout.CENTER);

        JLabel label = new JLabel(modeName, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setOpaque(true);
        label.putClientProperty("ignore_theme", true);
        label.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        if ("LIGHT".equalsIgnoreCase(modeName)) {
            label.setBackground(Color.WHITE);
            label.setForeground(Color.BLACK);
        } else {
            label.setBackground(new Color(35, 35, 35));
            label.setForeground(Color.WHITE);
        }

        panel.add(label, BorderLayout.SOUTH);

        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            // Section: Handle the logic for mouse clicked.
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectedDarkMode = "DARK".equalsIgnoreCase(modeName);
                // Section: Refresh or recompute the state used to highlight.
                updateHighlight();
            }
        });

        return panel;
    }

    // ================= HIGHLIGHT SELECTED =================
    // Section: Refresh or recompute the state used to highlight.
    private void updateHighlight() {

        Color selectedBorder = selectedDarkMode ? Color.WHITE : Color.BLACK;
        Color unselectedBorder = selectedDarkMode ? new Color(140, 140, 140) : Color.GRAY;

        for (Map.Entry<Theme, JPanel> entry : themePanels.entrySet()) {
            if (entry.getKey().equals(selectedTheme)) {
                entry.getValue().setBorder(BorderFactory.createLineBorder(selectedBorder, 4));
            } else {
                entry.getValue().setBorder(BorderFactory.createLineBorder(unselectedBorder, 2));
            }
        }

        JPanel lightPanel = modePanels.get("LIGHT");
        JPanel darkPanel = modePanels.get("DARK");

        if (lightPanel != null) {
            boolean active = !selectedDarkMode;
            lightPanel.setBorder(BorderFactory.createLineBorder(active ? selectedBorder : unselectedBorder, active ? 4 : 2));
        }

        if (darkPanel != null) {
            boolean active = selectedDarkMode;
            darkPanel.setBorder(BorderFactory.createLineBorder(active ? selectedBorder : unselectedBorder, active ? 4 : 2));
        }

        // Section: Handle the logic for repaint.
        repaint();
    }
}
