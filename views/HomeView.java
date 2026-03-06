package views;

import controller.AppController;
import model.PlanSyncActiveTasksModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class HomeView extends JPanel implements RefreshableView {

    private final AppController controller;

    private final JLabel welcomeLabel;
    private final JLabel timeLabel;
    private final JLabel dateLabel;

    private final JTextArea taskArea;
    private final JScrollPane taskScroll;

    private Timer liveClockTimer;

    // CHANGED: now includes seconds
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.ENGLISH);

    public HomeView(AppController controller) {
        this.controller = controller;

        setLayout(new BorderLayout());
        setOpaque(true);

        JLabel title = new JLabel("P L A N S Y N C", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.putClientProperty("on_base", true);
        title.setBorder(BorderFactory.createEmptyBorder(18, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(10, 70, 18, 70));
        add(content, BorderLayout.CENTER);

        RoundedPanel welcomePanel = new RoundedPanel(35);
        welcomePanel.putClientProperty("themed", true);
        welcomePanel.setLayout(new BorderLayout());
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(14, 22, 14, 22));
        welcomePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        welcomeLabel = new JLabel("WELCOME to PlanSync, USERNAME!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);

        content.add(welcomePanel);
        content.add(Box.createVerticalStrut(16));

        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 170));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;

        // TODAY panel
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
        // slight reduction so HH:mm:ss fits cleanly
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
        gbc.weightx = 0.55;
        gbc.insets = new Insets(0, 0, 0, 14);
        row.add(todayPanel, gbc);

        // TOOLS panel
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

        toolsGrid.add(toolButton("⌛", "Timer", () -> controller.showView("TIMER")));
        toolsGrid.add(toolButton("⏱", "Stopwatch", () -> controller.showView("STOPWATCH")));
        toolsGrid.add(toolButton("Σ", "Time Calculator", () -> controller.showView("TIME_CALCULATOR")));
        toolsGrid.add(toolButton("🗓", "Calendar", () -> controller.showView("CALENDAR")));

        toolsGrid.add(toolButton("+", "Add Active Task", () -> controller.showView("ADD_ACTIVE")));
        toolsGrid.add(toolButton("✅", "Mark Task as Complete", () -> controller.showView("MARK_COMPLETED")));
        toolsGrid.add(toolButton("🔁", "Add Recurring Task", () -> controller.showView("ADD_RECURRING")));
        toolsGrid.add(toolButton("⚙", "Settings", () -> controller.showView("SETTINGS")));

        toolsPanel.add(toolsGrid, BorderLayout.CENTER);

        gbc.gridx = 1;
        gbc.weightx = 0.45;
        gbc.insets = new Insets(0, 0, 0, 0);
        row.add(toolsPanel, gbc);

        content.add(row);
        content.add(Box.createVerticalStrut(16));

        // ACTIVE TASKS panel
        RoundedPanel tasksPanel = new RoundedPanel(30);
        tasksPanel.putClientProperty("themed", true);
        tasksPanel.setLayout(new BorderLayout());
        tasksPanel.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        tasksPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 330));

        JLabel tasksTag = new JLabel("ACTIVE TASKS:");
        tasksTag.setFont(new Font("SansSerif", Font.BOLD, 12));
        tasksTag.setBorder(BorderFactory.createEmptyBorder(0, 6, 6, 0));
        tasksPanel.add(tasksTag, BorderLayout.NORTH);

        taskArea = new JTextArea();
        taskArea.setEditable(false);
        taskArea.setLineWrap(true);
        taskArea.setWrapStyleWord(false);
        taskArea.setFont(new Font("Monospaced", Font.BOLD, 14));
        taskArea.setOpaque(false);

        taskScroll = new JScrollPane(taskArea);
        taskScroll.setBorder(BorderFactory.createEmptyBorder());
        taskScroll.setOpaque(false);
        taskScroll.getViewport().setOpaque(false);

        taskScroll.getViewport().addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                updateTasksText();
            }
        });

        tasksPanel.add(taskScroll, BorderLayout.CENTER);
        content.add(tasksPanel);

        startLiveClock();
        refresh();
    }

    private JButton toolButton(String symbol, String tooltip, Runnable action) {
        JButton b = new JButton(symbol);
        b.setToolTipText(tooltip);
        b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 28));
        b.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        b.setPreferredSize(new Dimension(74, 64));
        b.addActionListener(e -> action.run());
        return b;
    }

    private void startLiveClock() {
        if (liveClockTimer != null) return;
        liveClockTimer = new Timer(1000, e -> updateClock());
        liveClockTimer.setInitialDelay(0);
        liveClockTimer.start();
    }

    private void stopLiveClock() {
        if (liveClockTimer != null) {
            liveClockTimer.stop();
            liveClockTimer = null;
        }
    }

    private void updateClock() {
        LocalDateTime now = LocalDateTime.now();
        timeLabel.setText(now.format(TIME_FMT));
        dateLabel.setText(now.toLocalDate().format(DATE_FMT));
    }

    private int getWidthChars() {
        int px = taskScroll.getViewport().getExtentSize().width;
        Insets in = taskArea.getInsets();
        px -= (in.left + in.right);
        if (px <= 0) return 93;

        FontMetrics fm = taskArea.getFontMetrics(taskArea.getFont());
        int charW = fm.charWidth('=');
        if (charW <= 0) charW = Math.max(1, fm.charWidth('W'));

        int chars = px / charW;
        return Math.max(40, chars);
    }

    private String stripActiveTasksHeader(String formatted) {
        if (formatted == null || formatted.isBlank()) return "";

        String[] lines = formatted.split("\\R", -1);
        StringBuilder out = new StringBuilder();

        boolean skipping = true;
        for (String line : lines) {
            String trimmed = line.trim();

            if (skipping) {
                if (trimmed.contains("<<ACTIVE TASKS:>>")) continue;

                if (!trimmed.isEmpty()) {
                    boolean allEq = true;
                    for (int i = 0; i < trimmed.length(); i++) {
                        char c = trimmed.charAt(i);
                        if (c != '=') { allEq = false; break; }
                    }
                    if (allEq) continue;
                }

                if (trimmed.isEmpty()) continue;
                skipping = false;
            }

            out.append(line).append('\n');
        }

        return out.toString().stripTrailing() + "\n";
    }

    private void updateTasksText() {
        PlanSyncActiveTasksModel model = controller.getActiveTasksModel();
        if (model == null) {
            taskArea.setText("No active tasks found.\n");
            taskArea.setCaretPosition(0);
            return;
        }

        String formatted = model.formatForDisplay(getWidthChars());
        taskArea.setText(stripActiveTasksHeader(formatted));
        taskArea.setCaretPosition(0);
    }

    @Override
    public void refresh() {
        String name = controller.getSettings() != null ? controller.getSettings().getUsername() : "USERNAME";
        welcomeLabel.setText("WELCOME to PlanSync, " + name + "!");

        updateClock();
        updateTasksText();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        startLiveClock();
        updateClock();
    }

    @Override
    public void removeNotify() {
        stopLiveClock();
        super.removeNotify();
    }
}