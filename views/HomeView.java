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
 * File purpose: This class supports the HomeView part of PlanSync and documents the main responsibilities of the file.
 */

import controller.AppController;
import model.PlanSyncActiveTasksModel;
import model.PlanSyncRecurringTasksModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import util.SymbolIcon;
import util.SymbolIcon.Kind;

public class HomeView extends JPanel implements RefreshableView {

    private final AppController controller;

    private final JLabel welcomeLabel;
    private final JLabel timeLabel;
    private final JLabel dateLabel;

    private final JToggleButton activeAscBtn;
    private final JToggleButton activeDescBtn;
    private final JTextArea activeArea;
    private final JScrollPane activeScroll;

    private final JComboBox<String> recurringFilter;
    private final JTextArea recurringArea;
    private final JScrollPane recurringScroll;

    private final JScrollPane pageScroll;

    private Timer liveClockTimer;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter ACTIVE_DATE_FMT = PlanSyncActiveTasksModel.getDateFormatter();

    public HomeView(AppController controller) {
        this.controller = controller;

        // Section: Update the state used to layout.
        setLayout(new BorderLayout());
        // Section: Update the state used to opaque.
        setOpaque(true);

        JLabel title = new JLabel("P L A N S Y N C", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.putClientProperty("on_base", true);
        title.setBorder(BorderFactory.createEmptyBorder(18, 0, 10, 0));
        // Section: Add the data or behavior needed to add.
        add(title, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(10, 40, 18, 40));

        pageScroll = new JScrollPane(content);
        pageScroll.setBorder(BorderFactory.createEmptyBorder());
        pageScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        pageScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        pageScroll.setOpaque(false);
        pageScroll.getViewport().setOpaque(false);
        pageScroll.getVerticalScrollBar().setUnitIncrement(16);

        // Section: Add the data or behavior needed to add.
        add(pageScroll, BorderLayout.CENTER);

        RoundedPanel welcomePanel = new RoundedPanel(35);
        welcomePanel.putClientProperty("themed", true);
        welcomePanel.setLayout(new BorderLayout());
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(14, 22, 14, 22));
        welcomePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        welcomePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        welcomeLabel = new JLabel("WELCOME to PlanSync, USERNAME!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);

        content.add(welcomePanel);
        content.add(Box.createVerticalStrut(16));

        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 170));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;

        RoundedPanel todayPanel = new RoundedPanel(30);
        todayPanel.putClientProperty("themed", true);
        todayPanel.setLayout(new BorderLayout());
        todayPanel.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JLabel todayTag = new JLabel("TODAY:");
        todayTag.setFont(new Font("SansSerif", Font.BOLD, 12));
        todayTag.setBorder(BorderFactory.createEmptyBorder(0, 6, 6, 0));
        todayPanel.add(todayTag, BorderLayout.NORTH);

        JPanel todayCenter = new JPanel();
        todayCenter.setOpaque(false);
        todayCenter.setLayout(new BoxLayout(todayCenter, BoxLayout.Y_AXIS));

        timeLabel = new JLabel("00:00:00", SwingConstants.CENTER);
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timeLabel.setFont(new Font("Monospaced", Font.BOLD, 44));

        dateLabel = new JLabel("Friday, 1 January 2026", SwingConstants.CENTER);
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dateLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        todayCenter.add(Box.createVerticalStrut(8));
        todayCenter.add(timeLabel);
        todayCenter.add(Box.createVerticalStrut(6));
        todayCenter.add(dateLabel);
        todayCenter.add(Box.createVerticalStrut(4));

        todayPanel.add(todayCenter, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.weightx = 0.50;
        gbc.insets = new Insets(0, 0, 0, 14);
        row.add(todayPanel, gbc);

        RoundedPanel toolsPanel = new RoundedPanel(30);
        toolsPanel.putClientProperty("themed", true);
        toolsPanel.setLayout(new BorderLayout());
        toolsPanel.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JLabel toolsTag = new JLabel("PLANSYNC TOOLS:");
        toolsTag.setFont(new Font("SansSerif", Font.BOLD, 12));
        toolsTag.setBorder(BorderFactory.createEmptyBorder(0, 6, 6, 0));
        toolsPanel.add(toolsTag, BorderLayout.NORTH);

        JPanel toolsGrid = new JPanel(new GridLayout(2, 4, 12, 12));
        toolsGrid.setOpaque(false);

        toolsGrid.add(toolButton(Kind.TIMER, "Timer", () -> controller.showView("TIMER")));
        toolsGrid.add(toolButton(Kind.STOPWATCH, "Stopwatch", () -> controller.showView("STOPWATCH")));
        toolsGrid.add(toolButton(Kind.CALCULATOR, "Time Calculator", () -> controller.showView("TIME_CALCULATOR")));
        toolsGrid.add(toolButton(Kind.CALENDAR, "Calendar", () -> controller.showView("CALENDAR")));

        toolsGrid.add(toolButton(Kind.ADD, "Add Active Task", () -> controller.showView("ADD_ACTIVE")));
        toolsGrid.add(toolButton(Kind.CHECK, "Mark Task as Complete", () -> controller.showView("MARK_COMPLETED")));
        toolsGrid.add(toolButton(Kind.RECURRING, "Add Recurring Task", () -> controller.showView("ADD_RECURRING")));
        toolsGrid.add(toolButton(Kind.SETTINGS, "Settings", () -> controller.showView("SETTINGS")));

        toolsPanel.add(toolsGrid, BorderLayout.CENTER);

        gbc.gridx = 1;
        gbc.weightx = 0.50;
        gbc.insets = new Insets(0, 0, 0, 0);
        row.add(toolsPanel, gbc);

        content.add(row);
        content.add(Box.createVerticalStrut(16));

        RoundedPanel activePanel = new RoundedPanel(30);
        activePanel.putClientProperty("themed", true);
        activePanel.setLayout(new BorderLayout());
        activePanel.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        activePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 210));
        activePanel.setPreferredSize(new Dimension(1200, 210));
        activePanel.setMinimumSize(new Dimension(0, 210));
        activePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel activeHeader = new JPanel(new BorderLayout());
        activeHeader.setOpaque(false);

        JLabel activeTag = new JLabel("ACTIVE TASKS:");
        activeTag.setFont(new Font("SansSerif", Font.BOLD, 12));
        activeTag.setBorder(BorderFactory.createEmptyBorder(0, 6, 6, 0));
        activeHeader.add(activeTag, BorderLayout.WEST);

        JPanel orderWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        orderWrap.setOpaque(false);

        JLabel orderLabel = new JLabel("ORDER:");
        orderLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        orderWrap.add(orderLabel);

        activeAscBtn = smallArrowToggle("▲", "Ascending");
        activeDescBtn = smallArrowToggle("▼", "Descending");

        ButtonGroup orderGroup = new ButtonGroup();
        orderGroup.add(activeAscBtn);
        orderGroup.add(activeDescBtn);
        activeAscBtn.setSelected(true);

        activeAscBtn.addActionListener(e -> updateActiveText());
        activeDescBtn.addActionListener(e -> updateActiveText());

        orderWrap.add(activeAscBtn);
        orderWrap.add(activeDescBtn);

        activeHeader.add(orderWrap, BorderLayout.EAST);
        activePanel.add(activeHeader, BorderLayout.NORTH);

        activeArea = new JTextArea();
        activeArea.setEditable(false);
        activeArea.setLineWrap(true);
        activeArea.setWrapStyleWord(false);
        activeArea.setFont(new Font("Monospaced", Font.BOLD, 14));
        activeArea.setOpaque(false);

        activeScroll = new JScrollPane(activeArea);
        activeScroll.setBorder(BorderFactory.createEmptyBorder());
        activeScroll.setOpaque(false);
        activeScroll.getViewport().setOpaque(false);

        activeScroll.getViewport().addComponentListener(new ComponentAdapter() {
            @Override
            // Section: Handle the logic for component resized.
            public void componentResized(ComponentEvent e) {
                // Section: Refresh or recompute the state used to active text.
                updateActiveText();
            }
        });

        activePanel.add(activeScroll, BorderLayout.CENTER);
        content.add(activePanel);
        content.add(Box.createVerticalStrut(16));

        RoundedPanel recurringPanel = new RoundedPanel(30);
        recurringPanel.putClientProperty("themed", true);
        recurringPanel.setLayout(new BorderLayout());
        recurringPanel.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        recurringPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 210));
        recurringPanel.setPreferredSize(new Dimension(1200, 210));
        recurringPanel.setMinimumSize(new Dimension(0, 210));
        recurringPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel recurringHeader = new JPanel(new BorderLayout());
        recurringHeader.setOpaque(false);

        JLabel recurringTag = new JLabel("RECURRING TASKS:");
        recurringTag.setFont(new Font("SansSerif", Font.BOLD, 12));
        recurringTag.setBorder(BorderFactory.createEmptyBorder(0, 6, 6, 0));
        recurringHeader.add(recurringTag, BorderLayout.WEST);

        JPanel filterWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        filterWrap.setOpaque(false);

        JLabel filterLabel = new JLabel("SHOW:");
        filterLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        filterWrap.add(filterLabel);

        recurringFilter = new JComboBox<>(new String[]{"DAILY", "WEEKLY", "MONTHLY", "YEARLY"});
        recurringFilter.setFont(new Font("SansSerif", Font.BOLD, 12));
        recurringFilter.setFocusable(false);
        recurringFilter.addActionListener(e -> updateRecurringText());
        filterWrap.add(recurringFilter);

        recurringHeader.add(filterWrap, BorderLayout.EAST);
        recurringPanel.add(recurringHeader, BorderLayout.NORTH);

        recurringArea = new JTextArea();
        recurringArea.setEditable(false);
        recurringArea.setLineWrap(true);
        recurringArea.setWrapStyleWord(false);
        recurringArea.setFont(new Font("Monospaced", Font.BOLD, 14));
        recurringArea.setOpaque(false);

        recurringScroll = new JScrollPane(recurringArea);
        recurringScroll.setBorder(BorderFactory.createEmptyBorder());
        recurringScroll.setOpaque(false);
        recurringScroll.getViewport().setOpaque(false);

        recurringScroll.getViewport().addComponentListener(new ComponentAdapter() {
            @Override
            // Section: Handle the logic for component resized.
            public void componentResized(ComponentEvent e) {
                // Section: Refresh or recompute the state used to recurring text.
                updateRecurringText();
            }
        });

        recurringPanel.add(recurringScroll, BorderLayout.CENTER);
        content.add(recurringPanel);
    }

    // Section: Handle the logic for tool button.
    private JButton toolButton(Kind iconKind, String tooltip, Runnable action) {
        JButton b = new JButton();
        b.setToolTipText(tooltip);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        b.setPreferredSize(new Dimension(74, 64));
        b.setMargin(new Insets(2, 2, 2, 2));
        b.setIcon(new SymbolIcon(iconKind, 28));
        b.addActionListener(e -> action.run());
        return b;
    }

    // Section: Handle the logic for small arrow toggle.
    private JToggleButton smallArrowToggle(String symbol, String tooltip) {
        JToggleButton b = new JToggleButton(symbol);
        b.setToolTipText(tooltip);
        b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setPreferredSize(new Dimension(34, 28));
        b.setMargin(new Insets(0, 0, 0, 0));
        b.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        return b;
    }

    // Section: Handle the logic for start live clock.
    private void startLiveClock() {
        if (liveClockTimer != null) return;
        liveClockTimer = new Timer(1000, e -> updateClock());
        liveClockTimer.setInitialDelay(0);
        liveClockTimer.start();
    }

    // Section: Handle the logic for stop live clock.
    private void stopLiveClock() {
        if (liveClockTimer != null) {
            liveClockTimer.stop();
            liveClockTimer = null;
        }
    }

    // Section: Refresh or recompute the state used to clock.
    private void updateClock() {
        LocalDateTime now = LocalDateTime.now();
        timeLabel.setText(now.format(TIME_FMT));
        dateLabel.setText(now.toLocalDate().format(DATE_FMT));
    }

    // Section: Refresh or recompute the state used to active text.
    private void updateActiveText() {
        PlanSyncActiveTasksModel model = controller.getActiveTasksModel();
        if (model == null) {
            activeArea.setText("No active tasks found.\n");
            activeArea.setCaretPosition(0);
            return;
        }

        List<PlanSyncActiveTasksModel.Task> tasks = model.getTasks();
        LocalDate today = LocalDate.now();

        List<PlanSyncActiveTasksModel.Task> upcoming = new ArrayList<>();
        List<PlanSyncActiveTasksModel.Task> overdue = new ArrayList<>();

        for (PlanSyncActiveTasksModel.Task t : tasks) {
            if (t == null || t.deadline == null) continue;
            if (!t.deadline.isBefore(today)) {
                upcoming.add(t);
            } else {
                overdue.add(t);
            }
        }

        Comparator<PlanSyncActiveTasksModel.Task> byDate = Comparator.comparing(t -> t.deadline);
        boolean desc = activeDescBtn.isSelected();

        upcoming.sort(desc ? byDate.reversed() : byDate);
        overdue.sort(desc ? byDate.reversed() : byDate);

        StringBuilder sb = new StringBuilder();
        int id = 1;

        for (PlanSyncActiveTasksModel.Task t : upcoming) {
            sb.append(formatActiveRow(id++, t, today)).append("\n\n");
        }
        for (PlanSyncActiveTasksModel.Task t : overdue) {
            sb.append(formatActiveRow(id++, t, today)).append("\n\n");
        }

        if (sb.length() == 0) {
            sb.append("No active tasks found.\n");
        }

        activeArea.setText(sb.toString());
        activeArea.setCaretPosition(0);
    }

    // Section: Handle the logic for format active row.
    private String formatActiveRow(int id, PlanSyncActiveTasksModel.Task t, LocalDate today) {
        long days = ChronoUnit.DAYS.between(today, t.deadline);
        String status = (days == 0)
                ? "TODAY"
                : (days > 0 ? days + " DAYS REMAINING" : Math.abs(days) + " DAYS OVERDUE");

        return "[" + id + "] "
                + safe(t.name)
                + " -> "
                + safe(t.description)
                + " -> {"
                + t.deadline.format(ACTIVE_DATE_FMT)
                + "} -> ["
                + status
                + "]";
    }

    // Section: Refresh or recompute the state used to recurring text.
    private void updateRecurringText() {
        PlanSyncRecurringTasksModel model = controller.getRecurringTasksModel();
        if (model == null) {
            recurringArea.setText("No recurring tasks found.\n");
            recurringArea.setCaretPosition(0);
            return;
        }

        String selected = (String) recurringFilter.getSelectedItem();
        if (selected == null) selected = "DAILY";

        List<PlanSyncRecurringTasksModel.RecurringTask> tasks = model.getTasks();
        StringBuilder sb = new StringBuilder();
        int id = 1;

        for (PlanSyncRecurringTasksModel.RecurringTask t : tasks) {
            if (t == null) continue;

            String freq = t.frequency == null ? "" : t.frequency.trim().toUpperCase();
            if (!freq.equals(selected)) continue;

            sb.append("[")
                    .append(id++)
                    .append("] ")
                    .append(safe(t.name))
                    .append(" -> ")
                    .append(safe(t.description))
                    .append(" -> {")
                    .append(safe(t.timeDate))
                    .append("} -> [")
                    .append(freq)
                    .append("]\n\n");
        }

        if (sb.length() == 0) {
            sb.append("No recurring tasks found for ").append(selected).append(".\n");
        }

        recurringArea.setText(sb.toString());
        recurringArea.setCaretPosition(0);
    }

    // Section: Handle the logic for safe.
    private String safe(String s) {
        return s == null ? "" : s;
    }

    @Override
    // Section: Handle the logic for refresh.
    public void refresh() {
        String name = controller.getSettings() != null ? controller.getSettings().getUsername() : "USERNAME";
        welcomeLabel.setText("WELCOME to PlanSync, " + name + "!");

        // Section: Refresh or recompute the state used to clock.
        updateClock();
        // Section: Refresh or recompute the state used to active text.
        updateActiveText();
        // Section: Refresh or recompute the state used to recurring text.
        updateRecurringText();
    }

    @Override
    // Section: Add the data or behavior needed to notify.
    public void addNotify() {
        super.addNotify();
        // Section: Handle the logic for start live clock.
        startLiveClock();
        // Section: Refresh or recompute the state used to clock.
        updateClock();
    }

    @Override
    // Section: Handle the logic for remove notify.
    public void removeNotify() {
        // Section: Handle the logic for stop live clock.
        stopLiveClock();
        super.removeNotify();
    }
}
