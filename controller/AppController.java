package controller;

import model.PlanSyncTimer;
import model.PlanSyncSettings;
import model.Theme;

import views.*;
import components.BottomNavBar;

import javax.swing.*;
import java.awt.*;

public class AppController {

    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    private PlanSyncTimer timer;
    private PlanSyncSettings settings;

    private BottomNavBar navBar;

    public AppController() {
        initializeFrame();
        initializeModels();
        initializeViews();
        applyTheme();
        frame.setVisible(true);
    }

    // ================= FRAME =================
    private void initializeFrame() {

        frame = new JFrame("PlanSync");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 800);
        frame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        frame.setLayout(new BorderLayout());
        frame.add(mainPanel, BorderLayout.CENTER);
    }

    // ================= MODELS =================
    private void initializeModels() {
        timer = new PlanSyncTimer();
        settings = new PlanSyncSettings();
    }

    // ================= VIEWS =================
    private void initializeViews() {

        HomeView homeView = new HomeView(this);
        TimerView timerView = new TimerView(this, timer);
        StopwatchView stopwatchView = new StopwatchView(this);
        TimeCalculatorView timeCalculatorView = new TimeCalculatorView(this);
        CalendarView calendarView = new CalendarView(this);
        ActiveTasksView activeTasksView = new ActiveTasksView(this);
        RecurringTasksView recurringTasksView = new RecurringTasksView(this);
        CompletedTasksView completedTasksView = new CompletedTasksView(this);
        SettingsView settingsView = new SettingsView(this, settings);

        mainPanel.add(homeView, "HOME");
        mainPanel.add(timerView, "TIMER");
        mainPanel.add(stopwatchView, "STOPWATCH");
        mainPanel.add(timeCalculatorView, "TIME_CALCULATOR");
        mainPanel.add(calendarView, "CALENDAR");
        mainPanel.add(activeTasksView, "ACTIVE");
        mainPanel.add(recurringTasksView, "RECURRING");
        mainPanel.add(completedTasksView, "COMPLETED");
        mainPanel.add(settingsView, "SETTINGS");

        navBar = new BottomNavBar(this);
        navBar.putClientProperty("themed", true);
        frame.add(navBar, BorderLayout.SOUTH);

        showView("HOME");
    }

    // ================= THEME SYSTEM =================
    public void applyTheme() {

        Theme theme = settings.getSelectedTheme();
        Color light = theme.getLightColor();
        Color dark = theme.getDarkColor();

        boolean darkMode = settings.isDarkMode();
        Color baseBg = darkMode ? new Color(25, 25, 25) : Color.WHITE;

        frame.getContentPane().setBackground(baseBg);
        mainPanel.setBackground(baseBg);

        applyThemeToComponent(frame, light, dark, baseBg, darkMode);

        frame.repaint();
    }

    private void applyThemeToComponent(
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
            if (button instanceof JComponent jc) {
                Object nav = jc.getClientProperty("nav_button");
                isNav = Boolean.TRUE.equals(nav);
            }

            if (!isNav) {
                Font f = button.getFont();
                float size = Math.max(16f, f.getSize2D());
                button.setFont(f.deriveFont(Font.BOLD, size));
            }
        }

        // ✅ FIX: Titles/headers should switch color BOTH ways
        if (comp instanceof JLabel label) {
            if (label instanceof JComponent jc) {
                Object onBase = jc.getClientProperty("on_base");
                if (Boolean.TRUE.equals(onBase)) {
                    label.setForeground(darkMode ? Color.WHITE : Color.BLACK);
                }
            }
        }

        // Text fields: always readable (white bg, black text)
        if (comp instanceof JTextField field) {
            field.setBackground(Color.WHITE);
            field.setForeground(Color.BLACK);
            field.setCaretColor(Color.BLACK);
        }

        // Recursively apply
        if (comp instanceof Container container) {
            for (Component child : container.getComponents()) {
                applyThemeToComponent(child, light, dark, baseBg, darkMode);
            }
        }
    }

    // ================= VIEW SWITCHING =================
    public void showView(String name) {

        cardLayout.show(mainPanel, name);

        if (navBar != null) {
            navBar.setActive(name);
        }

        applyTheme();

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public void showHome() { showView("HOME"); }
    public void showTimer() { showView("TIMER"); }
    public void showStopwatch() { showView("STOPWATCH"); }
    public void showTimeCalculator() { showView("TIME_CALCULATOR"); }
    public void showCalendar() { showView("CALENDAR"); }
    public void showActiveTasks() { showView("ACTIVE"); }
    public void showRecurringTasks() { showView("RECURRING"); }
    public void showCompletedTasks() { showView("COMPLETED"); }
    public void showSettings() { showView("SETTINGS"); }

    public PlanSyncSettings getSettings() {
        return settings;
    }
}