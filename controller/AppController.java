package controller;

import views.*;
import components.BottomNavBar;

import javax.swing.*;
import java.awt.*;
import java.sql.Time;

public class AppController {

    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    public AppController() {
        initializeFrame();
        initializeViews();
        frame.setVisible(true);
    }

    private void initializeFrame() {
        frame = new JFrame("PlanSync");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        frame.setLayout(new BorderLayout());
        frame.add(mainPanel, BorderLayout.CENTER);
    }

    private void initializeViews() {

        // Create views
        HomeView homeView = new HomeView(this);
        TimerView timerView = new TimerView(this);
        StopwatchView stopwatchView = new StopwatchView(this);
        TimeCalculatorView timeCalculatorView = new TimeCalculatorView(this);
        CalendarView calendarView = new CalendarView(this);
        ActiveTasksView activeTasksView = new ActiveTasksView(this);
        RecurringTasksView recurringTasksView = new RecurringTasksView(this);
        CompletedTasksView completedTasksView = new CompletedTasksView(this);
        SettingsView settingsView = new SettingsView(this);

        // Add them to CardLayout
        mainPanel.add(homeView, "HOME");
        mainPanel.add(timerView, "TIMER");
        mainPanel.add(stopwatchView, "STOPWATCH");
        mainPanel.add(timeCalculatorView, "TIME_CALCULATOR");
        mainPanel.add(calendarView, "CALENDAR");
        mainPanel.add(activeTasksView, "ACTIVE");
        mainPanel.add(recurringTasksView, "RECURRING");
        mainPanel.add(completedTasksView, "COMPLETED");
        mainPanel.add(settingsView, "SETTINGS");

        // Add navigation bar
        BottomNavBar navBar = new BottomNavBar(this);
        frame.add(navBar, BorderLayout.SOUTH);

        // Default screen
        showView("HOME");
    }

    public void showView(String name) {
        cardLayout.show(mainPanel, name);
    }

    // Convenience navigation methods
    public void showHome() { showView("HOME"); }
    public void showTimer() { showView("TIMER"); }
    public void showStopwatch() { showView("STOPWATCH"); }
    public void showTimeCalculator() { showView("TIME_CALCULATOR"); }
    public void showCalendar() { showView("CALENDAR"); }
    public void showActiveTasks() { showView("ACTIVE"); }
    public void showRecurringTasks() { showView("RECURRING"); }
    public void showCompletedTasks() { showView("COMPLETED"); }
    public void showSettings() { showView("SETTINGS"); }
}
