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
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(Color.WHITE);

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

        frame.getContentPane().setBackground(Color.WHITE);
        mainPanel.setBackground(Color.WHITE);

        applyThemeToComponent(frame, light, dark);

        frame.repaint();
    }

    private void applyThemeToComponent(Component comp, Color light, Color dark) {

        // Rounded panels / themed panels
        if (comp instanceof JPanel panel) {
            Object themed = panel.getClientProperty("themed");
            if (Boolean.TRUE.equals(themed)) {
                panel.setBackground(light);
            }
        }

        // Buttons
        if (comp instanceof JButton button) {
            button.setBackground(dark);
            button.setForeground(Color.BLACK);
        }

        // Recursively apply
        if (comp instanceof Container container) {
            for (Component child : container.getComponents()) {
                applyThemeToComponent(child, light, dark);
            }
        }
    }

    // ================= VIEW SWITCHING =================
    public void showView(String name) {

        cardLayout.show(mainPanel, name);

        if (navBar != null) {
            navBar.setActive(name);   // tells navbar which page is active
        }

        applyTheme(); // re-apply theme after switch

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

    // ================= GETTERS =================
    public PlanSyncSettings getSettings() {
        return settings;
    }
}