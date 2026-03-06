package controller;

import model.PlanSyncTimer;
import model.PlanSyncSettings;
import model.PlanSyncActiveTasksModel;
import model.PlanSyncCompletedTasksModel;
import model.PlanSyncRecurringTasksModel;
import model.PlanSyncCalendarModel;

import views.*;
import components.BottomNavBar;
import components.ThemeApplier;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AppController {

    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    private PlanSyncTimer timer;
    private PlanSyncSettings settings;

    private PlanSyncActiveTasksModel activeTasksModel;
    private PlanSyncCompletedTasksModel completedTasksModel;
    private PlanSyncRecurringTasksModel recurringTasksModel;

    private PlanSyncCalendarModel calendarModel;

    private BottomNavBar navBar;

    private Map<String, JPanel> viewRegistry;

    public AppController() {
        initializeFrame();
        initializeModels();
        initializeViews();
        applyTheme();
        frame.setVisible(true);
    }

    private void initializeFrame() {
        frame = new JFrame("PlanSync");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1300, 800);
        frame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        frame.setLayout(new BorderLayout());
        frame.add(mainPanel, BorderLayout.CENTER);
    }

    private void initializeModels() {
        timer = new PlanSyncTimer();
        settings = new PlanSyncSettings();

        activeTasksModel = new PlanSyncActiveTasksModel();
        completedTasksModel = new PlanSyncCompletedTasksModel();
        recurringTasksModel = new PlanSyncRecurringTasksModel();

        calendarModel = new PlanSyncCalendarModel(activeTasksModel);
    }

    private void initializeViews() {

        viewRegistry = new HashMap<String, JPanel>();

        HomeView homeView = new HomeView(this);
        TimerView timerView = new TimerView(this, timer);
        StopwatchView stopwatchView = new StopwatchView(this);
        TimeCalculatorView timeCalculatorView = new TimeCalculatorView(this);
        CalendarView calendarView = new CalendarView(this, calendarModel);

        ActiveTasksView activeTasksView = new ActiveTasksView(this, activeTasksModel);
        AddActiveTaskView addActiveTaskView = new AddActiveTaskView(this, activeTasksModel);
        DeleteActiveTasksView deleteActiveTasksView = new DeleteActiveTasksView(this, activeTasksModel);
        EditActiveTaskView editActiveTaskView = new EditActiveTaskView(this, activeTasksModel);

        RecurringTasksView recurringTasksView = new RecurringTasksView(this, recurringTasksModel);
        AddRecurringView addRecurringView = new AddRecurringView(this, recurringTasksModel);
        DeleteRecurringView deleteRecurringView = new DeleteRecurringView(this, recurringTasksModel);
        EditRecurringView editRecurringView = new EditRecurringView(this, recurringTasksModel);

        CompletedTasksView completedTasksView = new CompletedTasksView(this, completedTasksModel);
        DeleteCompletedView deleteCompletedView = new DeleteCompletedView(this, completedTasksModel);

        MarkCompletedTasksView markCompletedTasksView =
                new MarkCompletedTasksView(this, activeTasksModel, completedTasksModel);

        // ✅ FIX: pass settings into SettingsView (constructor in your project requires it)
        SettingsView settingsView = new SettingsView(this, settings);

        registerView("HOME", homeView);
        registerView("TIMER", timerView);
        registerView("STOPWATCH", stopwatchView);
        registerView("TIME_CALCULATOR", timeCalculatorView);
        registerView("CALENDAR", calendarView);

        registerView("ACTIVE", activeTasksView);
        registerView("ADD_ACTIVE", addActiveTaskView);
        registerView("DELETE_ACTIVE", deleteActiveTasksView);
        registerView("EDIT_ACTIVE", editActiveTaskView);

        registerView("RECURRING", recurringTasksView);
        registerView("ADD_RECURRING", addRecurringView);
        registerView("DELETE_RECURRING", deleteRecurringView);
        registerView("EDIT_RECURRING", editRecurringView);

        registerView("MARK_COMPLETED", markCompletedTasksView);

        registerView("COMPLETED", completedTasksView);
        registerView("DELETE_COMPLETED", deleteCompletedView);

        registerView("SETTINGS", settingsView);

        navBar = new BottomNavBar(this);
        navBar.putClientProperty("themed", true);
        frame.add(navBar, BorderLayout.SOUTH);

        showView("HOME");
    }

    private void registerView(String key, JPanel view) {
        mainPanel.add(view, key);
        viewRegistry.put(key, view);
    }

    public void applyTheme() {
        ThemeApplier.apply(frame, settings);
    }

    public PlanSyncSettings getSettings() {
        return settings;
    }

    public PlanSyncActiveTasksModel getActiveTasksModel() {
        return activeTasksModel;
    }

    public PlanSyncRecurringTasksModel getRecurringTasksModel() {
    return recurringTasksModel;
    }

    public PlanSyncCalendarModel getCalendarModel() {
        return calendarModel;
    }

    public void showView(String name) {

        if (viewRegistry == null || !viewRegistry.containsKey(name)) {
            name = "HOME";
        }

        cardLayout.show(mainPanel, name);

        JPanel view = viewRegistry.get(name);
        if (view instanceof RefreshableView) {
            ((RefreshableView) view).refresh();
        }

        if (navBar != null) {
            navBar.setActive(navKeyForView(name));
        }

        applyTheme();

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private String navKeyForView(String name) {
        if (name == null) return "HOME";

        if (name.startsWith("ADD_ACTIVE") || name.startsWith("DELETE_ACTIVE") || name.startsWith("EDIT_ACTIVE")) {
            return "ACTIVE";
        }
        if (name.startsWith("ADD_RECURRING") || name.startsWith("DELETE_RECURRING") || name.startsWith("EDIT_RECURRING")) {
            return "RECURRING";
        }
        if (name.startsWith("DELETE_COMPLETED")) {
            return "COMPLETED";
        }
        if (name.startsWith("MARK_COMPLETED")) {
            return "ACTIVE";
        }

        return name;
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
}