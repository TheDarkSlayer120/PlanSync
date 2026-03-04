package views;

import controller.AppController;
import model.PlanSyncSettings;
import model.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class SettingsView extends JPanel {

    private final PlanSyncSettings settings;
    private JTextField usernameField;
    private Theme selectedTheme;

    private final Map<Theme, JPanel> themePanels = new HashMap<>();

    public SettingsView(AppController controller, PlanSyncSettings settings) {

        this.settings = settings;
        this.selectedTheme = settings.getSelectedTheme();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ================= TITLE =================
        JLabel title = new JLabel("S E T T I N G S", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // ================= CENTER PANEL =================
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(Color.WHITE);
        add(center, BorderLayout.CENTER);

        // ================= USERNAME TITLE =================
        JLabel usernameTitle = new JLabel("USERNAME:");
        usernameTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameTitle.setFont(new Font("SansSerif", Font.BOLD, 14));

        center.add(Box.createVerticalStrut(20));
        center.add(usernameTitle);
        center.add(Box.createVerticalStrut(10));

        // ================= USERNAME FIELD =================
        usernameField = new JTextField(settings.getUsername());
        usernameField.setMaximumSize(new Dimension(400, 40));
        usernameField.setHorizontalAlignment(JTextField.CENTER);
        usernameField.setFont(new Font("SansSerif", Font.BOLD, 14));

        center.add(usernameField);
        center.add(Box.createVerticalStrut(20));

        // ================= SELECT THEME TITLE =================
        JLabel themeTitle = new JLabel("SELECT THEME:");
        themeTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        themeTitle.setFont(new Font("SansSerif", Font.BOLD, 14));

        center.add(themeTitle);
        center.add(Box.createVerticalStrut(20));

        // ================= THEME GRID =================
        JPanel grid = new JPanel(new GridLayout(3, 3, 20, 20));
        grid.setBackground(Color.WHITE);
        grid.setMaximumSize(new Dimension(600, 300));

        for (Theme theme : settings.getThemes()) {
            JPanel panel = createThemePanel(theme);
            themePanels.put(theme, panel);
            grid.add(panel);
        }

        center.add(grid);
        center.add(Box.createVerticalStrut(20));

        // ================= SAVE BUTTON =================
        JButton save = new JButton("SAVE");
        save.setAlignmentX(Component.CENTER_ALIGNMENT);
        save.setFocusPainted(false);
        save.setFont(new Font("SansSerif", Font.BOLD, 14));

        Dimension size = new Dimension(200, 45);
        save.setPreferredSize(size);
        save.setMaximumSize(size);
        save.setMinimumSize(size);

        center.add(save);

        updateHighlight();

        // ================= SAVE ACTION =================
        save.addActionListener(e -> {
            settings.setUsername(usernameField.getText());
            settings.setSelectedTheme(selectedTheme);
            settings.saveToFile();
            controller.applyTheme();
        });
    }

    // ================= THEME PREVIEW PANEL =================
    private JPanel createThemePanel(Theme theme) {

        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.setPreferredSize(new Dimension(120, 60));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        JPanel light = new JPanel();
        light.setBackground(theme.getLightColor());

        JPanel dark = new JPanel();
        dark.setBackground(theme.getDarkColor());

        panel.add(light);
        panel.add(dark);

        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectedTheme = theme;
                updateHighlight();
            }
        });

        return panel;
    }

    // ================= HIGHLIGHT SELECTED =================
    private void updateHighlight() {

        for (Map.Entry<Theme, JPanel> entry : themePanels.entrySet()) {

            if (entry.getKey().equals(selectedTheme)) {
                entry.getValue().setBorder(
                        BorderFactory.createLineBorder(Color.BLACK, 4));
            } else {
                entry.getValue().setBorder(
                        BorderFactory.createLineBorder(Color.GRAY, 2));
            }
        }

        repaint();
    }
}