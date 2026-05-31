package controller;


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
 * File purpose: This class supports the AppController part of PlanSync and documents the main responsibilities of the file.
 */

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
import util.ResourceUtils;

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
        // Section: Set up the pieces required to frame.
        initializeFrame();
        // Section: Set up the pieces required to models.
        initializeModels();
        // Section: Set up the pieces required to views.
        initializeViews();
        // Section: Handle the logic for apply theme.
        applyTheme();
        frame.setVisible(true);
    }

    // Section: Set up the pieces required to frame.
    private void initializeFrame() {
        frame = new JFrame("PlanSync");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1300, 800);
        frame.setLocationRelativeTo(null);

        ResourceUtils.applyFrameIcon(frame, "icons/PlanSync.png");

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        frame.setLayout(new BorderLayout());
        frame.add(mainPanel, BorderLayout.CENTER);
    }

    // Section: Set up the pieces required to models.
    private void initializeModels() {
        timer = new PlanSyncTimer();
        settings = new PlanSyncSettings();

        activeTasksModel = new PlanSyncActiveTasksModel();
        completedTasksModel = new PlanSyncCompletedTasksModel();
        recurringTasksModel = new PlanSyncRecurringTasksModel();

        calendarModel = new PlanSyncCalendarModel(activeTasksModel);
    }

    // Section: Set up the pieces required to views.
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

        SettingsView settingsView = new SettingsView(this, settings);

        // Section: Handle the logic for register view.
        registerView("HOME", homeView);
        // Section: Handle the logic for register view.
        registerView("TIMER", timerView);
        // Section: Handle the logic for register view.
        registerView("STOPWATCH", stopwatchView);
        // Section: Handle the logic for register view.
        registerView("TIME_CALCULATOR", timeCalculatorView);
        // Section: Handle the logic for register view.
        registerView("CALENDAR", calendarView);

        // Section: Handle the logic for register view.
        registerView("ACTIVE", activeTasksView);
        // Section: Handle the logic for register view.
        registerView("ADD_ACTIVE", addActiveTaskView);
        // Section: Handle the logic for register view.
        registerView("DELETE_ACTIVE", deleteActiveTasksView);
        // Section: Handle the logic for register view.
        registerView("EDIT_ACTIVE", editActiveTaskView);

        // Section: Handle the logic for register view.
        registerView("RECURRING", recurringTasksView);
        // Section: Handle the logic for register view.
        registerView("ADD_RECURRING", addRecurringView);
        // Section: Handle the logic for register view.
        registerView("DELETE_RECURRING", deleteRecurringView);
        // Section: Handle the logic for register view.
        registerView("EDIT_RECURRING", editRecurringView);

        // Section: Handle the logic for register view.
        registerView("MARK_COMPLETED", markCompletedTasksView);

        // Section: Handle the logic for register view.
        registerView("COMPLETED", completedTasksView);
        // Section: Handle the logic for register view.
        registerView("DELETE_COMPLETED", deleteCompletedView);

        // Section: Handle the logic for register view.
        registerView("SETTINGS", settingsView);

        navBar = new BottomNavBar(this);
        navBar.putClientProperty("themed", true);
        frame.add(navBar, BorderLayout.SOUTH);

        // Section: Handle UI flow to view.
        showView("HOME");
    }

    // Section: Handle the logic for register view.
    private void registerView(String key, JPanel view) {
        mainPanel.add(view, key);
        viewRegistry.put(key, view);
    }

    // Section: Handle the logic for apply theme.
    public void applyTheme() {
        ThemeApplier.apply(frame, settings);
    }

    // Section: Return the data used to settings.
    public PlanSyncSettings getSettings() {
        return settings;
    }

    // Section: Return the data used to active tasks model.
    public PlanSyncActiveTasksModel getActiveTasksModel() {
        return activeTasksModel;
    }

    // Section: Return the data used to recurring tasks model.
    public PlanSyncRecurringTasksModel getRecurringTasksModel() {
        return recurringTasksModel;
    }

    // Section: Return the data used to calendar model.
    public PlanSyncCalendarModel getCalendarModel() {
        return calendarModel;
    }

    // Section: Handle UI flow to view.
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

        // Section: Handle the logic for apply theme.
        applyTheme();

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    // Section: Handle the logic for nav key for view.
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
