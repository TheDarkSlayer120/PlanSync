package views;

import controller.AppController;
import components.PlanSyncDialogs;
import model.PlanSyncRecurringTasksModel;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class AddRecurringView extends JPanel implements RefreshableView {

    private static final int MAX_CONTENT_WIDTH = 1100;

    private final AppController controller;
    private final PlanSyncRecurringTasksModel recurringModel;

    private final JTextField nameField;
    private final JTextArea descArea;

    private final JToggleButton dailyBtn;
    private final JToggleButton weeklyBtn;
    private final JToggleButton monthlyBtn;
    private final JToggleButton yearlyBtn;

    private final CardLayout frequencyCards;
    private final JPanel frequencyPanel;

    private JComboBox<String> dailyTimeCombo;
    private JComboBox<String> weeklyTimeCombo;
    private JComboBox<String> weeklyDayCombo;
    private JComboBox<String> monthlyMonthCombo;
    private JComboBox<String> monthlyDayCombo;
    private JComboBox<String> yearlyMonthCombo;
    private JComboBox<String> yearlyDayCombo;
    private JComboBox<Integer> yearlyStartYearCombo;

    private String selectedFrequency = "WEEKLY";

    public AddRecurringView(AppController controller, PlanSyncRecurringTasksModel recurringModel) {
        this.controller = controller;
        this.recurringModel = recurringModel;

        setLayout(new BorderLayout());
        setOpaque(true);

        JLabel title = new JLabel("A D D   R E C U R R I N G   T A S K", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.putClientProperty("on_base", true);
        title.setBorder(BorderFactory.createEmptyBorder(25, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.putClientProperty("themed_base", true);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(18, 40, 28, 40));
        content.setOpaque(false);

        JScrollPane pageScroll = new JScrollPane(content);
        pageScroll.setBorder(BorderFactory.createEmptyBorder());
        pageScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        pageScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        pageScroll.setOpaque(false);
        pageScroll.getViewport().setOpaque(false);
        pageScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(pageScroll, BorderLayout.CENTER);

        JPanel formWrap = new JPanel(new GridBagLayout());
        formWrap.putClientProperty("themed_base", true);
        formWrap.setOpaque(false);
        formWrap.setAlignmentX(Component.CENTER_ALIGNMENT);
        formWrap.setMaximumSize(new Dimension(MAX_CONTENT_WIDTH, Integer.MAX_VALUE));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.insets = new Insets(12, 0, 8, 0);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;

        formWrap.add(sectionLabel("TASK NAME:"), gc);

        gc.gridy++;
        RoundedPanel namePanel = roundedFieldPanel();
        nameField = new JTextField();
        nameField.setFont(new Font("SansSerif", Font.BOLD, 18));
        namePanel.add(nameField, BorderLayout.CENTER);
        formWrap.add(namePanel, gc);

        gc.gridy++;
        formWrap.add(sectionLabel("TASK DESCRIPTION:"), gc);

        gc.gridy++;
        RoundedPanel descPanel = new RoundedPanel(28);
        descPanel.putClientProperty("themed", true);
        descPanel.setLayout(new BorderLayout());
        descPanel.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        descPanel.setPreferredSize(new Dimension(MAX_CONTENT_WIDTH, 140));

        descArea = new JTextArea(4, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setFont(new Font("SansSerif", Font.BOLD, 16));

        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBorder(BorderFactory.createEmptyBorder());
        descScroll.setOpaque(false);
        descScroll.getViewport().setOpaque(false);
        descPanel.add(descScroll, BorderLayout.CENTER);
        formWrap.add(descPanel, gc);

        gc.gridy++;
        JPanel freqButtons = new JPanel(new GridLayout(1, 4, 20, 0));
        freqButtons.putClientProperty("themed_base", true);
        freqButtons.setOpaque(false);
        freqButtons.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        freqButtons.setMaximumSize(new Dimension(MAX_CONTENT_WIDTH, 52));

        dailyBtn = pillToggle("DAILY");
        weeklyBtn = pillToggle("WEEKLY");
        monthlyBtn = pillToggle("MONTHLY");
        yearlyBtn = pillToggle("YEARLY");

        ButtonGroup group = new ButtonGroup();
        group.add(dailyBtn);
        group.add(weeklyBtn);
        group.add(monthlyBtn);
        group.add(yearlyBtn);

        freqButtons.add(dailyBtn);
        freqButtons.add(weeklyBtn);
        freqButtons.add(monthlyBtn);
        freqButtons.add(yearlyBtn);

        formWrap.add(freqButtons, gc);

        gc.gridy++;
        RoundedPanel freqBar = new RoundedPanel(35);
        freqBar.putClientProperty("themed", true);
        freqBar.setLayout(new BorderLayout());
        freqBar.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        frequencyCards = new CardLayout();
        frequencyPanel = new JPanel(frequencyCards);
        frequencyPanel.putClientProperty("themed_base", true);
        frequencyPanel.setOpaque(false);

        String[] times = buildTimeOptions();

        JPanel dailyPanel = createDailyPanel(times);
        JPanel weeklyPanel = createWeeklyPanel(times);
        JPanel monthlyPanel = createMonthlyPanel();
        JPanel yearlyPanel = createYearlyPanel();

        monthlyMonthCombo.addActionListener(e -> updateMonthlyDays());
        yearlyMonthCombo.addActionListener(e -> updateYearlyDays());
        yearlyStartYearCombo.addActionListener(e -> updateYearlyDays());

        frequencyPanel.add(dailyPanel, "DAILY");
        frequencyPanel.add(weeklyPanel, "WEEKLY");
        frequencyPanel.add(monthlyPanel, "MONTHLY");
        frequencyPanel.add(yearlyPanel, "YEARLY");

        freqBar.add(frequencyPanel, BorderLayout.CENTER);
        formWrap.add(freqBar, gc);

        content.add(formWrap);
        content.add(Box.createVerticalStrut(18));

        JPanel buttons = new JPanel(new GridLayout(1, 2, 35, 0));
        buttons.putClientProperty("themed_base", true);
        buttons.setOpaque(false);
        buttons.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttons.setMaximumSize(new Dimension(760, 60));
        buttons.setPreferredSize(new Dimension(760, 60));

        JButton cancel = bigButton("CANCEL");
        JButton save = bigButton("SAVE");

        cancel.addActionListener(e -> controller.showRecurringTasks());
        save.addActionListener(e -> onSave());

        buttons.add(cancel);
        buttons.add(save);
        content.add(buttons);

        dailyBtn.addActionListener(e -> setFrequency("DAILY"));
        weeklyBtn.addActionListener(e -> setFrequency("WEEKLY"));
        monthlyBtn.addActionListener(e -> setFrequency("MONTHLY"));
        yearlyBtn.addActionListener(e -> setFrequency("YEARLY"));

        weeklyBtn.setSelected(true);
        setFrequency("WEEKLY");
        updateMonthlyDays();
        updateYearlyDays();
    }

    private JPanel createDailyPanel(String[] times) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.putClientProperty("themed_base", true);
        panel.setOpaque(false);

        GridBagConstraints gc = barConstraints();

        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 0;
        panel.add(barLabel("TIME:"), gc);

        gc.gridx = 1;
        gc.weightx = 1;
        gc.insets = new Insets(0, 0, 0, 0);

        RoundedPanel dailyTimeWrap = roundedFieldPanel(new Dimension(320, 50));
        dailyTimeCombo = editableTimeCombo(times);
        dailyTimeCombo.setSelectedItem("09:00");
        dailyTimeWrap.add(dailyTimeCombo, BorderLayout.CENTER);
        panel.add(dailyTimeWrap, gc);

        return panel;
    }

    private JPanel createWeeklyPanel(String[] times) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.putClientProperty("themed_base", true);
        panel.setOpaque(false);

        GridBagConstraints gc = barConstraints();

        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 0;
        panel.add(barLabel("TIME:"), gc);

        gc.gridx = 1;
        gc.weightx = 0.45;

        RoundedPanel weeklyTimeWrap = roundedFieldPanel(new Dimension(280, 50));
        weeklyTimeCombo = editableTimeCombo(times);
        weeklyTimeCombo.setSelectedItem("09:00");
        weeklyTimeWrap.add(weeklyTimeCombo, BorderLayout.CENTER);
        panel.add(weeklyTimeWrap, gc);

        gc.gridx = 2;
        gc.weightx = 0;
        gc.insets = new Insets(0, 22, 0, 18);
        panel.add(barLabel("DAY OF THE WEEK:"), gc);

        gc.gridx = 3;
        gc.weightx = 0.55;
        gc.insets = new Insets(0, 0, 0, 0);

        RoundedPanel weeklyDayWrap = roundedFieldPanel(new Dimension(320, 50));
        weeklyDayCombo = new JComboBox<>(new String[]{
                "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY",
                "FRIDAY", "SATURDAY", "SUNDAY"
        });
        weeklyDayCombo.setFont(new Font("SansSerif", Font.BOLD, 16));
        weeklyDayCombo.setFocusable(false);
        weeklyDayCombo.setEditable(false);
        weeklyDayWrap.add(weeklyDayCombo, BorderLayout.CENTER);
        panel.add(weeklyDayWrap, gc);

        return panel;
    }

    private JPanel createMonthlyPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.putClientProperty("themed_base", true);
        panel.setOpaque(false);

        GridBagConstraints gc = barConstraints();

        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 0;
        panel.add(barLabel("MONTH:"), gc);

        gc.gridx = 1;
        gc.weightx = 0.5;

        RoundedPanel monthlyMonthWrap = roundedFieldPanel(new Dimension(280, 50));
        monthlyMonthCombo = new JComboBox<>(buildMonthNames());
        monthlyMonthCombo.setFont(new Font("SansSerif", Font.BOLD, 16));
        monthlyMonthCombo.setFocusable(false);
        monthlyMonthCombo.setEditable(false);
        monthlyMonthWrap.add(monthlyMonthCombo, BorderLayout.CENTER);
        panel.add(monthlyMonthWrap, gc);

        gc.gridx = 2;
        gc.weightx = 0;
        gc.insets = new Insets(0, 22, 0, 18);
        panel.add(barLabel("DAY:"), gc);

        gc.gridx = 3;
        gc.weightx = 0.5;
        gc.insets = new Insets(0, 0, 0, 0);

        RoundedPanel monthlyDayWrap = roundedFieldPanel(new Dimension(280, 50));
        monthlyDayCombo = new JComboBox<>(buildDayNumbersForMonth(
                parseMonth(String.valueOf(monthlyMonthCombo.getSelectedItem())),
                LocalDate.now().getYear()
        ));
        monthlyDayCombo.setFont(new Font("SansSerif", Font.BOLD, 16));
        monthlyDayCombo.setFocusable(false);
        monthlyDayCombo.setEditable(true);
        monthlyDayWrap.add(monthlyDayCombo, BorderLayout.CENTER);
        panel.add(monthlyDayWrap, gc);

        return panel;
    }

    private JPanel createYearlyPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.putClientProperty("themed_base", true);
        panel.setOpaque(false);

        GridBagConstraints gc = barConstraints();

        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 0;
        panel.add(barLabel("MONTH:"), gc);

        gc.gridx = 1;
        gc.weightx = 1;

        RoundedPanel yearlyMonthWrap = roundedFieldPanel(new Dimension(240, 50));
        yearlyMonthCombo = new JComboBox<>(buildMonthNames());
        yearlyMonthCombo.setFont(new Font("SansSerif", Font.BOLD, 16));
        yearlyMonthCombo.setFocusable(false);
        yearlyMonthCombo.setEditable(false);
        yearlyMonthWrap.add(yearlyMonthCombo, BorderLayout.CENTER);
        panel.add(yearlyMonthWrap, gc);

        gc.gridx = 2;
        gc.weightx = 0;
        gc.insets = new Insets(0, 22, 0, 18);
        panel.add(barLabel("DAY:"), gc);

        gc.gridx = 3;
        gc.weightx = 1;
        gc.insets = new Insets(0, 0, 0, 0);

        RoundedPanel yearlyDayWrap = roundedFieldPanel(new Dimension(200, 50));
        yearlyDayCombo = new JComboBox<>(
                buildDayNumbersForMonth(
                        parseMonth((String) yearlyMonthCombo.getSelectedItem()),
                        LocalDate.now().getYear()
                )
        );
        yearlyDayCombo.setFont(new Font("SansSerif", Font.BOLD, 16));
        yearlyDayCombo.setFocusable(false);
        yearlyDayCombo.setEditable(true);
        yearlyDayWrap.add(yearlyDayCombo, BorderLayout.CENTER);
        panel.add(yearlyDayWrap, gc);

        gc.gridx = 0;
        gc.gridy = 1;
        gc.gridwidth = 1;
        gc.weightx = 0;
        gc.insets = new Insets(16, 0, 0, 18);
        panel.add(barLabel("START YEAR:"), gc);

        gc.gridx = 1;
        gc.gridwidth = 3;
        gc.weightx = 1;
        gc.insets = new Insets(16, 0, 0, 0);

        RoundedPanel yearlyYearWrap = roundedFieldPanel(new Dimension(240, 50));
        yearlyStartYearCombo = new JComboBox<>(buildYearOptions());
        yearlyStartYearCombo.setFont(new Font("SansSerif", Font.BOLD, 16));
        yearlyStartYearCombo.setFocusable(false);
        yearlyStartYearCombo.setEditable(true);
        yearlyYearWrap.add(yearlyStartYearCombo, BorderLayout.CENTER);
        panel.add(yearlyYearWrap, gc);

        return panel;
    }

    private GridBagConstraints barConstraints() {
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(0, 0, 0, 18);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.WEST;
        return gc;
    }

    private JComboBox<String> editableTimeCombo(String[] times) {
        JComboBox<String> combo = new JComboBox<>(times);
        combo.setFont(new Font("SansSerif", Font.BOLD, 16));
        combo.setEditable(true);
        combo.setMaximumRowCount(10);

        Component editorComp = combo.getEditor().getEditorComponent();
        if (editorComp instanceof JTextField tf) {
            tf.setFont(new Font("SansSerif", Font.BOLD, 16));
            tf.setEditable(true);
            tf.setColumns(8);
            tf.setBorder(BorderFactory.createEmptyBorder());
            tf.setToolTipText("Enter time as HH:mm or pick from the dropdown.");
        }

        return combo;
    }

    private void setFrequency(String freq) {
        selectedFrequency = freq;
        frequencyCards.show(frequencyPanel, freq);
    }

    private void updateMonthlyDays() {
        int month = parseMonth(String.valueOf(monthlyMonthCombo.getSelectedItem()));
        String currentDay = getEditableComboValue(monthlyDayCombo);

        monthlyDayCombo.setModel(new DefaultComboBoxModel<>(
                buildDayNumbersForMonth(month, LocalDate.now().getYear())
        ));

        int max = Month.of(month).length(true);
        int day = parseDay(currentDay, max);
        monthlyDayCombo.setSelectedItem(String.valueOf(day));
    }

    private void updateYearlyDays() {
        int year = parseYear(getEditableComboValue(yearlyStartYearCombo), LocalDate.now().getYear());
        int month = parseMonth(String.valueOf(yearlyMonthCombo.getSelectedItem()));
        String currentDay = getEditableComboValue(yearlyDayCombo);

        yearlyDayCombo.setModel(new DefaultComboBoxModel<>(buildDayNumbersForMonth(month, year)));

        int max = Month.of(month).length(Year.isLeap(year));
        int day = parseDay(currentDay, max);
        yearlyDayCombo.setSelectedItem(String.valueOf(day));
    }

    private void onSave() {
        String name = nameField.getText().trim();
        String desc = descArea.getText().trim();

        if (name.isEmpty()) {
            PlanSyncDialogs.alert(this, controller, "Missing Name", "Please enter a task name.");
            return;
        }
        if (desc.isEmpty()) {
            PlanSyncDialogs.alert(this, controller, "Missing Description", "Please enter a task description.");
            return;
        }

        try {
            String timeDate;

            switch (selectedFrequency) {
                case "DAILY" -> {
                    String time = getCommittedEditableComboValue(dailyTimeCombo);
                    if (time.isBlank()) {
                        PlanSyncDialogs.alert(this, controller, "Missing Time", "Please enter or select a time.");
                        return;
                    }

                    LocalTime t = PlanSyncRecurringTasksModel.parseTimeHHmm(time);
                    timeDate = t.format(DateTimeFormatter.ofPattern("HH:mm"));
                }

                case "WEEKLY" -> {
                    String time = getCommittedEditableComboValue(weeklyTimeCombo);
                    if (time.isBlank()) {
                        PlanSyncDialogs.alert(this, controller, "Missing Time", "Please enter or select a time.");
                        return;
                    }

                    LocalTime t = PlanSyncRecurringTasksModel.parseTimeHHmm(time);
                    String day = String.valueOf(weeklyDayCombo.getSelectedItem()).trim().toUpperCase();

                    if (!isValidWeekday(day)) {
                        PlanSyncDialogs.alert(this, controller, "Invalid Day", "Please choose a valid day of the week.");
                        return;
                    }

                    timeDate = t.format(DateTimeFormatter.ofPattern("HH:mm")) + " " + day;
                }

                case "MONTHLY" -> {
                    int month = parseMonth(String.valueOf(monthlyMonthCombo.getSelectedItem()));
                    int max = Month.of(month).length(true);
                    String dayText = getEditableComboValue(monthlyDayCombo);

                    if (dayText.isBlank()) {
                        PlanSyncDialogs.alert(this, controller, "Missing Day", "Please enter or select a day for the selected month.");
                        return;
                    }

                    int day = Integer.parseInt(dayText);
                    if (day < 1 || day > max) {
                        PlanSyncDialogs.alert(
                                this,
                                controller,
                                "Invalid Day",
                                "For " + Month.of(month).name() + ", the day must be between 1 and " + max + "."
                        );
                        return;
                    }

                    LocalDate.of(2024, month, day);
                    timeDate = String.format("%02d/%02d (Start Month: %s)", day, month, Month.of(month).name());
                }

                case "YEARLY" -> {
                    int month = parseMonth(String.valueOf(yearlyMonthCombo.getSelectedItem()));
                    String dayText = getEditableComboValue(yearlyDayCombo);
                    String yearText = getEditableComboValue(yearlyStartYearCombo);

                    if (dayText.isBlank() || yearText.isBlank()) {
                        PlanSyncDialogs.alert(this, controller, "Missing Data", "Please choose month, day, and starting year.");
                        return;
                    }

                    int year = Integer.parseInt(yearText);
                    int max = Month.of(month).length(Year.isLeap(year));
                    int day = Integer.parseInt(dayText);

                    if (day < 1 || day > max) {
                        PlanSyncDialogs.alert(
                                this,
                                controller,
                                "Invalid Day",
                                "For " + Month.of(month).name() + " " + year + ", the day must be between 1 and " + max + "."
                        );
                        return;
                    }

                    LocalDate.of(Math.max(1900, year), month, day);
                    timeDate = String.format("%02d/%02d (Start: %d)", day, month, year);
                }

                default -> throw new IllegalStateException("Unknown frequency: " + selectedFrequency);
            }

            recurringModel.addTask(name, desc, timeDate, selectedFrequency);
            controller.showRecurringTasks();

        } catch (DateTimeParseException ex) {
            PlanSyncDialogs.alert(this, controller, "Invalid Input", "Invalid time format. Please use HH:mm.");
        } catch (NumberFormatException ex) {
            PlanSyncDialogs.alert(this, controller, "Invalid Input", "Please enter valid numeric values.");
        } catch (Exception ex) {
            PlanSyncDialogs.alert(this, controller, "Invalid Input", "Invalid date selection.");
        }
    }

    private String getCommittedEditableComboValue(JComboBox<String> combo) {
        if (combo.isEditable()) {
            Object editorValue = combo.getEditor().getItem();
            combo.setSelectedItem(editorValue);
        }

        Object value = combo.isEditable() ? combo.getEditor().getItem() : combo.getSelectedItem();
        return value == null ? "" : value.toString().trim();
    }

    private String getEditableComboValue(JComboBox<?> combo) {
        Object value = combo.isEditable() ? combo.getEditor().getItem() : combo.getSelectedItem();
        return value == null ? "" : value.toString().trim();
    }

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(new Font("SansSerif", Font.BOLD, 18));
        l.putClientProperty("on_base", true);
        return l;
    }

    private JLabel barLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 18));
        l.setForeground(Color.BLACK);
        l.putClientProperty("ignore_theme", true);
        return l;
    }

    private RoundedPanel roundedFieldPanel() {
        return roundedFieldPanel(null);
    }

    private RoundedPanel roundedFieldPanel(Dimension preferredSize) {
        RoundedPanel p = new RoundedPanel(28);
        p.putClientProperty("themed", true);
        p.setLayout(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        if (preferredSize != null) {
            p.setPreferredSize(preferredSize);
        }
        return p;
    }

    private JToggleButton pillToggle(String text) {
        JToggleButton b = new JToggleButton(text);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(0, 48));
        b.setFont(new Font("SansSerif", Font.BOLD, 18));
        b.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        return b;
    }

    private JButton bigButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(0, 55));
        b.setFont(new Font("SansSerif", Font.BOLD, 18));
        b.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        return b;
    }

    private static String[] buildTimeOptions() {
        List<String> out = new ArrayList<>();
        for (int h = 0; h < 24; h++) {
            out.add(String.format("%02d:00", h));
            out.add(String.format("%02d:30", h));
        }
        return out.toArray(new String[0]);
    }

    private static String[] buildMonthNames() {
        String[] out = new String[12];
        Month[] months = Month.values();
        for (int i = 0; i < 12; i++) {
            out[i] = months[i].name();
        }
        return out;
    }

    private static String[] buildDayNumbersForMonth(int month, int year) {
        int m = Math.max(1, Math.min(12, month));
        int y = Math.max(1900, year);
        int max = Month.of(m).length(Year.isLeap(y));

        String[] days = new String[max];
        for (int i = 0; i < max; i++) {
            days[i] = String.valueOf(i + 1);
        }
        return days;
    }

    private static int parseMonth(String raw) {
        String s = raw == null ? "" : raw.trim();
        if (s.isEmpty()) return 1;

        try {
            int n = Integer.parseInt(s);
            if (n >= 1 && n <= 12) return n;
        } catch (NumberFormatException ignored) {
        }

        String up = s.toUpperCase();
        for (Month m : Month.values()) {
            if (m.name().equals(up)) return m.getValue();
            if (up.length() >= 3 && m.name().startsWith(up.substring(0, 3))) return m.getValue();
        }

        return 1;
    }

    private static int parseDay(String raw, int max) {
        try {
            int d = Integer.parseInt(raw == null ? "" : raw.trim());
            if (d < 1) return 1;
            return Math.min(d, Math.max(1, max));
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private static int parseYear(String raw, int fallback) {
        try {
            return Integer.parseInt(raw == null ? "" : raw.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static boolean isValidWeekday(String up) {
        if (up == null) return false;
        return switch (up) {
            case "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY" -> true;
            default -> false;
        };
    }

    private static Integer[] buildYearOptions() {
        int start = LocalDate.now().getYear();
        Integer[] years = new Integer[21];
        for (int i = 0; i < years.length; i++) {
            years[i] = start + i;
        }
        return years;
    }

    @Override
    public void refresh() {
        nameField.setText("");
        descArea.setText("");

        dailyTimeCombo.setSelectedItem("09:00");

        weeklyTimeCombo.setSelectedItem("09:00");
        weeklyDayCombo.setSelectedIndex(0);

        monthlyMonthCombo.setSelectedIndex(0);
        updateMonthlyDays();
        monthlyDayCombo.setSelectedItem("1");

        yearlyMonthCombo.setSelectedIndex(0);
        yearlyStartYearCombo.setSelectedItem(LocalDate.now().getYear());
        updateYearlyDays();
        yearlyDayCombo.setSelectedItem("1");

        weeklyBtn.setSelected(true);
        setFrequency("WEEKLY");
    }
}