package model;


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
 * File purpose: This class supports the PlanSyncSettings part of PlanSync and documents the main responsibilities of the file.
 */

import java.awt.Color;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import util.AppPaths;
import java.util.Properties;

public class PlanSyncSettings {

    private static final File FOLDER_NAME = AppPaths.getDataDir().toFile();
    private static final File FILE_NAME = AppPaths.getDataFile("settings.properties");

    private String username = "USERNAME";
    private Theme selectedTheme;

    // false = light mode, true = dark mode
    private boolean darkMode = false;

    private final List<Theme> themes = new ArrayList<>();

    public PlanSyncSettings() {
        // Section: Set up the pieces required to themes.
        initializeThemes();
        // Section: Read and prepare the data used to from file.
        loadFromFile();
    }

    // ================= INITIALIZE THEMES =================
    // Section: Set up the pieces required to themes.
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
    // Section: Persist the data used to to file.
    public void saveToFile() {

        try {

            // Ensure folder exists
            File folder = FOLDER_NAME;
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

        // Section: Handle the logic for catch.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ================= LOAD SETTINGS =================
    // Section: Read and prepare the data used to from file.
    private void loadFromFile() {

        try {

            File file = FILE_NAME;
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

        // Section: Handle the logic for catch.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ================= GETTERS / SETTERS =================
    // Section: Return the data used to username.
    public String getUsername() {
        return username;
    }

    // Section: Update the state used to username.
    public void setUsername(String username) {
        this.username = username;
    }

    // Section: Return the data used to themes.
    public List<Theme> getThemes() {
        return themes;
    }

    // Section: Return the data used to selected theme.
    public Theme getSelectedTheme() {
        return selectedTheme;
    }

    // Section: Update the state used to selected theme.
    public void setSelectedTheme(Theme theme) {
        this.selectedTheme = theme;
    }

    // Section: Report whether dark mode.
    public boolean isDarkMode() {
        return darkMode;
    }

    // Section: Update the state used to dark mode.
    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }
}
