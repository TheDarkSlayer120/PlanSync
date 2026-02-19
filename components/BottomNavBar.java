package components;

import controller.AppController;

import javax.swing.*;
import java.awt.*;

public class BottomNavBar extends JPanel {

    private AppController controller;

    public BottomNavBar(AppController controller) {
        this.controller = controller;

        setLayout(new GridLayout(1, 6));

        JButton homeBtn = new JButton("Home");
        JButton timerBtn = new JButton("Timer");
        JButton stopwatchBtn = new JButton("Stopwatch");
        JButton timeCalculatorBtn = new JButton("Time Calculator");
        JButton calendarBtn = new JButton("Calendar");
        JButton activeBtn = new JButton("Active");
        JButton recurringBtn = new JButton("Recurring");
        JButton completedBtn = new JButton("Completed");
        JButton settingsBtn = new JButton("Settings");

        homeBtn.addActionListener(e -> controller.showHome());
        timerBtn.addActionListener(e -> controller.showTimer());
        stopwatchBtn.addActionListener(e -> controller.showStopwatch());
        timeCalculatorBtn.addActionListener(e -> controller.showTimeCalculator());
        calendarBtn.addActionListener(e -> controller.showCalendar());
        activeBtn.addActionListener(e -> controller.showActiveTasks());
        recurringBtn.addActionListener(e -> controller.showRecurringTasks());
        completedBtn.addActionListener(e -> controller.showCompletedTasks());
        settingsBtn.addActionListener(e -> controller.showSettings());

        add(homeBtn);
        add(timerBtn);
        add(stopwatchBtn);
        add(timeCalculatorBtn);
        add(calendarBtn);
        add(activeBtn);
        add(completedBtn);
        add(recurringBtn);
        add(settingsBtn);
    }
}
