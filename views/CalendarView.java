package views;

import components.PlanSyncDialogs;
import controller.AppController;
import model.PlanSyncActiveTasksModel;
import model.PlanSyncCalendarModel;
import model.PlanSyncSettings;
import model.Theme;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class CalendarView extends JPanel implements RefreshableView {

    private final AppController controller;
    private final PlanSyncCalendarModel calendarModel;

    private final JLabel headerLabel;
    private final JButton prevBtn;
    private final JButton nextBtn;
    private final JButton[] dayButtons = new JButton[42];

    // Day-of-week header labels (so we can force their colour)
    private final JLabel[] dowLabels = new JLabel[7];

    // Colors (computed from theme on refresh)
    private Color inMonthBg;
    private Color outMonthBg;
    private Color taskBg;
    private Color todayBg;
    private Color textOnDark;
    private Color textOnLight;
    private Color taskPip;

    // Nav button colors
    private Color navBg;
    private Color navBgHover;
    private Color navBorder;

    private static final DateTimeFormatter POPUP_TITLE_FMT =
            DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy");

    public CalendarView(AppController controller, PlanSyncCalendarModel calendarModel) {
        this.controller = controller;
        this.calendarModel = calendarModel;

        setLayout(new BorderLayout());
        putClientProperty("themed_base", true);

        // ===== Top title bar =====
        JPanel top = new JPanel(new BorderLayout());
        top.putClientProperty("themed_base", true);
        top.setBorder(BorderFactory.createEmptyBorder(18, 22, 10, 22));

        JLabel title = new JLabel("C A L E N D A R", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.putClientProperty("on_base", true);

        top.add(title, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        // ===== Center area that stretches =====
        JPanel content = new JPanel(new BorderLayout());
        content.putClientProperty("themed_base", true);
        content.setBorder(BorderFactory.createEmptyBorder(8, 22, 22, 22));
        add(content, BorderLayout.CENTER);

        RoundedPanel card = new RoundedPanel(30);
        card.putClientProperty("themed", true);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        content.add(card, BorderLayout.CENTER);

        // ===== Month header row inside card =====
        JPanel monthRow = new JPanel(new BorderLayout(12, 0));
        monthRow.setOpaque(false);

        prevBtn = navButton("\u2190 PREVIOUS MONTH");
        nextBtn = navButton("NEXT MONTH \u2192");

        headerLabel = new JLabel("", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        headerLabel.setForeground(Color.BLACK); // ✅ ALWAYS BLACK

        monthRow.add(prevBtn, BorderLayout.WEST);
        monthRow.add(headerLabel, BorderLayout.CENTER);
        monthRow.add(nextBtn, BorderLayout.EAST);

        card.add(monthRow, BorderLayout.NORTH);

        // ===== Day-of-week header + grid =====
        JPanel gridWrap = new JPanel(new BorderLayout(0, 10));
        gridWrap.setOpaque(false);
        gridWrap.setBorder(BorderFactory.createEmptyBorder(12, 6, 0, 6));

        JPanel dow = new JPanel(new GridLayout(1, 7, 10, 0));
        dow.setOpaque(false);

        int idx = 0;
        for (DayOfWeek d : DayOfWeek.values()) {
            JLabel l = new JLabel(d.getDisplayName(TextStyle.FULL, Locale.ENGLISH).toUpperCase(), SwingConstants.CENTER);
            l.setFont(new Font("SansSerif", Font.BOLD, 12));
            l.setForeground(Color.BLACK); // ✅ ALWAYS BLACK
            dowLabels[idx++] = l;
            dow.add(l);
        }

        JPanel grid = new JPanel(new GridLayout(6, 7, 10, 10));
        grid.setOpaque(false);

        for (int i = 0; i < 42; i++) {
            JButton b = dayButton();
            dayButtons[i] = b;
            grid.add(b);
        }

        gridWrap.add(dow, BorderLayout.NORTH);
        gridWrap.add(grid, BorderLayout.CENTER);

        card.add(gridWrap, BorderLayout.CENTER);

        // ===== Wire navigation =====
        prevBtn.addActionListener(e -> {
            calendarModel.previousMonth();
            refresh();
        });

        nextBtn.addActionListener(e -> {
            calendarModel.nextMonth();
            refresh();
        });

        refresh();
    }

    @Override
    public void refresh() {
        computeColors();

        // ✅ enforce readable header colours regardless of theme
        headerLabel.setForeground(Color.BLACK);
        for (JLabel l : dowLabels) {
            if (l != null) l.setForeground(Color.BLACK);
        }

        renderMonth();
        applyNavButtonStyle(prevBtn);
        applyNavButtonStyle(nextBtn);
    }

    private void renderMonth() {
        YearMonth ym = calendarModel.getCurrentMonth();
        String monthName = ym.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH).toUpperCase();
        headerLabel.setText(monthName + " " + ym.getYear());

        List<PlanSyncCalendarModel.DayCell> cells = calendarModel.buildMonthGrid();
        LocalDate selected = calendarModel.getSelectedDate();

        for (int i = 0; i < 42; i++) {
            PlanSyncCalendarModel.DayCell cell = cells.get(i);
            JButton b = dayButtons[i];
            LocalDate date = cell.getDate();

            b.setText(String.valueOf(date.getDayOfMonth()));
            b.putClientProperty("date", date);

            b.setBorder(defaultRoundedBorder(2, new Color(0, 0, 0, 35)));

            boolean inMonth = cell.isInCurrentMonth();
            boolean hasTasks = cell.hasActiveTasks();
            boolean isToday = cell.isToday();
            boolean isSelected = selected != null && selected.equals(date);

            if (!inMonth) {
                b.setBackground(outMonthBg);
                b.setForeground(textOnDark);
            } else {
                b.setBackground(inMonthBg);
                b.setForeground(textOnLight);
            }

            if (hasTasks) {
                b.setBackground(taskBg);
                b.setForeground(textOnDark);
                b.putClientProperty("has_tasks", true);
            } else {
                b.putClientProperty("has_tasks", false);
            }

            if (isToday) {
                if (!hasTasks) {
                    b.setBackground(todayBg);
                    b.setForeground(textOnLight);
                }
                b.setBorder(defaultRoundedBorder(3, new Color(0, 0, 0, 180)));
            }

            if (isSelected) {
                b.setBorder(defaultRoundedBorder(4, new Color(0, 0, 0, 220)));
            }

            if (hasTasks) {
                String pip = String.format(
                        "<html><div style='text-align:center;'>%d<br/><span style='font-size:10px; color: rgb(%d,%d,%d);'>&#9679;</span></div></html>",
                        date.getDayOfMonth(), taskPip.getRed(), taskPip.getGreen(), taskPip.getBlue()
                );
                b.setText(pip);
            }
        }

        revalidate();
        repaint();
    }

    private JButton dayButton() {
        JButton b = new JButton();
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setPreferredSize(null);
        b.setMinimumSize(new Dimension(40, 40));
        b.setFont(new Font("SansSerif", Font.BOLD, 20));
        b.setBorderPainted(true);
        b.putClientProperty("ignore_theme", true);

        b.addActionListener(e -> {
            Object v = b.getClientProperty("date");
            if (!(v instanceof LocalDate)) return;

            LocalDate date = (LocalDate) v;
            calendarModel.setSelectedDate(date);
            refresh();
            showTasksPopup(date);
        });

        return b;
    }

    private JButton navButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setPreferredSize(null);
        b.putClientProperty("ignore_theme", true);
        return b;
    }

    private void applyNavButtonStyle(JButton b) {
        b.setBackground(navBg);
        b.setForeground(textOnDark);

        b.setBorder(BorderFactory.createCompoundBorder(
                defaultRoundedBorder(3, navBorder),
                BorderFactory.createEmptyBorder(10, 18, 10, 18)
        ));

        for (MouseListener ml : b.getMouseListeners()) {
            if (ml instanceof NavHoverListener) b.removeMouseListener(ml);
        }
        b.addMouseListener(new NavHoverListener(b));
    }

    private class NavHoverListener extends MouseAdapter {
        private final JButton btn;
        NavHoverListener(JButton btn) { this.btn = btn; }

        @Override public void mouseEntered(MouseEvent e) { btn.setBackground(navBgHover); }
        @Override public void mouseExited(MouseEvent e) { btn.setBackground(navBg); }
    }

    private void showTasksPopup(LocalDate date) {
        PlanSyncActiveTasksModel active = controller.getActiveTasksModel();
        List<PlanSyncActiveTasksModel.Task> tasks = active.getTasksForDate(date);

        String title = date.format(POPUP_TITLE_FMT);
        if (tasks.isEmpty()) {
            PlanSyncDialogs.alert(this, controller, title, "No active tasks due on this date.");
            return;
        }

        StringBuilder msg = new StringBuilder();
        msg.append("Active tasks due today:\n\n");
        int i = 1;
        for (PlanSyncActiveTasksModel.Task t : tasks) {
            msg.append(i++).append(") ").append(t.name);
            if (t.description != null && !t.description.isBlank()) {
                msg.append(" - ").append(t.description);
            }
            msg.append("\n");
        }
        msg.append("\nOpen Active Tasks to edit/mark completed.");

        boolean go = PlanSyncDialogs.confirm(this, controller, title,
                msg.toString() + "\n\nGo to Active Tasks?");
        if (go) controller.showActiveTasks();
    }

    private void computeColors() {
        PlanSyncSettings settings = controller.getSettings();
        Theme theme = settings.getSelectedTheme();
        boolean darkMode = settings.isDarkMode();

        inMonthBg = blend(theme.getLightColor(), theme.getDarkColor(), darkMode ? 0.70 : 0.45);
        outMonthBg = darken(inMonthBg, darkMode ? 0.22 : 0.30);
        taskBg = darken(theme.getDarkColor(), darkMode ? 0.10 : 0.00);
        todayBg = blend(theme.getLightColor(), new Color(255, 240, 190), darkMode ? 0.35 : 0.55);

        textOnDark = Color.WHITE;
        textOnLight = Color.BLACK;

        taskPip = blend(Color.WHITE, theme.getLightColor(), 0.55);

        navBg = darken(theme.getDarkColor(), darkMode ? 0.00 : 0.10);
        navBgHover = darken(navBg, 0.12);
        navBorder = new Color(0, 0, 0, darkMode ? 160 : 140);

        // ✅ Do NOT set headerLabel colour based on mode anymore.
        // We force it to black in refresh().
    }

    private static Color blend(Color a, Color b, double t) {
        t = Math.max(0, Math.min(1, t));
        int r = (int) Math.round(a.getRed() * (1 - t) + b.getRed() * t);
        int g = (int) Math.round(a.getGreen() * (1 - t) + b.getGreen() * t);
        int bl = (int) Math.round(a.getBlue() * (1 - t) + b.getBlue() * t);
        return new Color(clamp(r), clamp(g), clamp(bl));
    }

    private static Color darken(Color c, double amount) {
        amount = Math.max(0, Math.min(1, amount));
        int r = (int) Math.round(c.getRed() * (1 - amount));
        int g = (int) Math.round(c.getGreen() * (1 - amount));
        int b = (int) Math.round(c.getBlue() * (1 - amount));
        return new Color(clamp(r), clamp(g), clamp(b));
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }

    private static Border defaultRoundedBorder(int thickness, Color c) {
        return new LineBorder(c, thickness, true);
    }
}