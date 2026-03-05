package model;

import java.awt.Color;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PlanSyncSettings {

    private static final String FOLDER_NAME = "data";
    private static final String FILE_NAME = "data/settings.properties";

    private String username = "USERNAME";
    private Theme selectedTheme;

    // false = light mode, true = dark mode
    private boolean darkMode = false;

    private final List<Theme> themes = new ArrayList<>();

    public PlanSyncSettings() {
        initializeThemes();
        loadFromFile();
    }

    // ================= INITIALIZE THEMES =================
    private void initializeThemes() {

        themes.add(new Theme("Blue",
                new Color(200, 220, 255),
                new Color(100, 130, 200)));

        themes.add(new Theme("Red",
                new Color(255, 200, 200),
                new Color(200, 100, 100)));

        themes.add(new Theme("Yellow",
                new Color(255, 255, 200),
                new Color(200, 200, 100)));

        themes.add(new Theme("Green",
                new Color(210, 255, 210),
                new Color(120, 180, 120)));

        themes.add(new Theme("Grey",
                new Color(230, 230, 230),
                new Color(150, 150, 150)));

        themes.add(new Theme("Purple",
                new Color(230, 210, 255),
                new Color(150, 100, 200)));

        themes.add(new Theme("Indigo",
                new Color(210, 210, 255),
                new Color(120, 120, 200)));

        themes.add(new Theme("Teal",
                new Color(200, 255, 240),
                new Color(100, 170, 150)));

        themes.add(new Theme("Pink",
                new Color(255, 210, 240),
                new Color(200, 100, 170)));

        selectedTheme = themes.get(0);
    }

    // ================= SAVE SETTINGS =================
    public void saveToFile() {

        try {

            // Ensure folder exists
            File folder = new File(FOLDER_NAME);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            Properties props = new Properties();
            props.setProperty("username", username);
            props.setProperty("theme", selectedTheme.getName());
            props.setProperty("mode", darkMode ? "dark" : "light");

            FileOutputStream fos = new FileOutputStream(FILE_NAME);
            props.store(fos, "PlanSync Settings");
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ================= LOAD SETTINGS =================
    private void loadFromFile() {

        try {

            File file = new File(FILE_NAME);
            if (!file.exists()) return;

            Properties props = new Properties();
            FileInputStream fis = new FileInputStream(file);
            props.load(fis);
            fis.close();

            username = props.getProperty("username", username);
            String themeName = props.getProperty("theme");

            String mode = props.getProperty("mode", "light");
            darkMode = "dark".equalsIgnoreCase(mode);

            if (themeName != null) {
                for (Theme t : themes) {
                    if (t.getName().equals(themeName)) {
                        selectedTheme = t;
                        break;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ================= GETTERS / SETTERS =================
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Theme> getThemes() {
        return themes;
    }

    public Theme getSelectedTheme() {
        return selectedTheme;
    }

    public void setSelectedTheme(Theme theme) {
        this.selectedTheme = theme;
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }
}