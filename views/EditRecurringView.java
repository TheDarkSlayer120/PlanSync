package views;

import controller.AppController;
import model.PlanSyncRecurringTasksModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * EDIT RECURRING TASK page (GUI).
 * Workflow:
 * 1) Enter 1-based Task ID and press LOAD.
 * 2) Edit fields and press SAVE.
 */
public class EditRecurringView extends JPanel implements RefreshableView {

    private final AppController controller;
    private final PlanSyncRecurringTasksModel recurringModel;

    private final JTextArea taskArea;
    private final JScrollPane listScroll;

    private final JTextField idField;
    private Integer loadedId = null; // 1-based

    private final JTextField nameField;
    private final JTextArea descArea;

    // Frequency toggles
    private final JToggleButton dailyBtn;
    private final JToggleButton weeklyBtn;
    private final JToggleButton monthlyBtn;
    private final JToggleButton yearlyBtn;

    private final CardLayout frequencyCards;
    private final JPanel frequencyPanel;

    // DAILY
    private final JTextField dailyTimeField;

    // WEEKLY
    private final JTextField weeklyTimeField;
    private final JComboBox<String> weeklyDayCombo;

    // MONTHLY
    private final JTextField monthlyDayField;
    private final JTextField monthlyMonthField;

    // YEARLY
    private final JTextField yearlyDayMonthField;
    private final JTextField yearlyYearField;

    private String selectedFrequency = "WEEKLY";

    public EditRecurringView(AppController controller, PlanSyncRecurringTasksModel recurringModel) {
        this.controller = controller;
        this.recurringModel = recurringModel;

        setLayout(new BorderLayout());

        JLabel title = new JLabel("E D I T   R E C U R R I N G   T A S K", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.putClientProperty("on_base", true);
        title.setBorder(BorderFactory.createEmptyBorder(25, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        // ===== Scrollable page content (prevents squishing) =====
        JPanel content = new JPanel();
        content.putClientProperty("themed_base", true);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(20, 120, 30, 120));

        // ===== Task list =====
        RoundedPanel listPanel = new RoundedPanel(35);
        listPanel.putClientProperty("themed", true);
        listPanel.setLayout(new BorderLayout());
        listPanel.setBorder(BorderFactory.createEmptyBorder(22, 28, 22, 28));
        listPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        taskArea = new JTextArea();
        taskArea.setEditable(false);
        taskArea.setLineWrap(true);
        taskArea.setWrapStyleWord(false);
        taskArea.setFont(new Font("Monospaced", Font.BOLD, 14));
        taskArea.setOpaque(false);

        listScroll = new JScrollPane(taskArea);
        listScroll.setBorder(BorderFactory.createEmptyBorder());
        listScroll.setOpaque(false);
        listScroll.getViewport().setOpaque(false);

        listScroll.getViewport().addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                updateListText();
            }
        });

        listPanel.add(listScroll, BorderLayout.CENTER);
        content.add(listPanel);
        content.add(Box.createVerticalStrut(18));

        // ===== Form =====
        JPanel formWrap = new JPanel(new GridBagLayout());
        formWrap.putClientProperty("themed_base", true);
        formWrap.setAlignmentX(Component.CENTER_ALIGNMENT);

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.insets = new Insets(12, 0, 8, 0);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        // ID row + LOAD button
        RoundedPanel idRow = new RoundedPanel(35);
        idRow.putClientProperty("themed", true);
        idRow.setLayout(new BorderLayout(20, 0));
        idRow.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JLabel prompt = new JLabel("TASK ID TO EDIT:");
        prompt.setFont(new Font("SansSerif", Font.BOLD, 18));
        prompt.setForeground(Color.BLACK);

        RoundedPanel idFieldWrap = roundedFieldPanel();
        idField = new JTextField();
        idField.setFont(new Font("SansSerif", Font.BOLD, 18));
        idFieldWrap.add(idField, BorderLayout.CENTER);

        JButton loadBtn = bigButton("LOAD");
        loadBtn.setPreferredSize(new Dimension(170, 50));
        loadBtn.addActionListener(e -> onLoad());

        idRow.add(prompt, BorderLayout.WEST);
        idRow.add(idFieldWrap, BorderLayout.CENTER);
        idRow.add(loadBtn, BorderLayout.EAST);

        formWrap.add(idRow, gc);

        // Name
        gc.gridy++;
        formWrap.add(sectionLabel("TASK NAME:"), gc);

        gc.gridy++;
        RoundedPanel namePanel = roundedFieldPanel();
        nameField = new JTextField();
        nameField.setFont(new Font("SansSerif", Font.BOLD, 18));
        namePanel.add(nameField, BorderLayout.CENTER);
        formWrap.add(namePanel, gc);

        // Description
        gc.gridy++;
        formWrap.add(sectionLabel("TASK DESCRIPTION:"), gc);

        gc.gridy++;
        RoundedPanel descPanel = new RoundedPanel(28);
        descPanel.putClientProperty("themed", true);
        descPanel.setLayout(new BorderLayout());
        descPanel.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        descArea = new JTextArea(4, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setFont(new Font("SansSerif", Font.BOLD, 16));

        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBorder(BorderFactory.createEmptyBorder());
        descPanel.add(descScroll, BorderLayout.CENTER);
        formWrap.add(descPanel, gc);

        // Frequency buttons row
        gc.gridy++;
        JPanel freqButtons = new JPanel(new GridLayout(1, 4, 25, 0));
        freqButtons.putClientProperty("themed_base", true);
        freqButtons.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

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

        // Frequency dynamic panel
        gc.gridy++;
        RoundedPanel freqBar = new RoundedPanel(35);
        freqBar.putClientProperty("themed", true);
        freqBar.setLayout(new BorderLayout());
        freqBar.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        frequencyCards = new CardLayout();
        frequencyPanel = new JPanel(frequencyCards);
        frequencyPanel.putClientProperty("themed_base", true);
        frequencyPanel.setOpaque(false);

        // DAILY panel
        JPanel dailyPanel = new JPanel(new GridLayout(1, 2, 25, 0));
        dailyPanel.putClientProperty("themed_base", true);
        dailyPanel.setOpaque(false);

        dailyPanel.add(barLabel("TIME:"));
        RoundedPanel dailyTimeWrap = roundedFieldPanel();
        dailyTimeField = new JTextField();
        dailyTimeField.setFont(new Font("SansSerif", Font.BOLD, 18));
        dailyTimeWrap.add(dailyTimeField, BorderLayout.CENTER);
        dailyPanel.add(dailyTimeWrap);

        // WEEKLY panel
        JPanel weeklyPanel = new JPanel(new GridLayout(1, 4, 25, 0));
        weeklyPanel.putClientProperty("themed_base", true);
        weeklyPanel.setOpaque(false);

        weeklyPanel.add(barLabel("TIME:"));
        RoundedPanel weeklyTimeWrap = roundedFieldPanel();
        weeklyTimeField = new JTextField();
        weeklyTimeField.setFont(new Font("SansSerif", Font.BOLD, 18));
        weeklyTimeWrap.add(weeklyTimeField, BorderLayout.CENTER);
        weeklyPanel.add(weeklyTimeWrap);

        weeklyPanel.add(barLabel("DAY OF THE WEEK:"));
        RoundedPanel weeklyDayWrap = roundedFieldPanel();
        weeklyDayCombo = new JComboBox<>(new String[]{
                "MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY","SUNDAY"
        });
        weeklyDayCombo.setFont(new Font("SansSerif", Font.BOLD, 16));
        weeklyDayWrap.add(weeklyDayCombo, BorderLayout.CENTER);
        weeklyPanel.add(weeklyDayWrap);

        // MONTHLY panel
        JPanel monthlyPanel = new JPanel(new GridLayout(1, 4, 25, 0));
        monthlyPanel.putClientProperty("themed_base", true);
        monthlyPanel.setOpaque(false);

        monthlyPanel.add(barLabel("DATE (DD):"));
        RoundedPanel monthlyDayWrap = roundedFieldPanel();
        monthlyDayField = new JTextField();
        monthlyDayField.setFont(new Font("SansSerif", Font.BOLD, 18));
        monthlyDayWrap.add(monthlyDayField, BorderLayout.CENTER);
        monthlyPanel.add(monthlyDayWrap);

        monthlyPanel.add(barLabel("STARTING MONTH (MM):"));
        RoundedPanel monthlyMonthWrap = roundedFieldPanel();
        monthlyMonthField = new JTextField();
        monthlyMonthField.setFont(new Font("SansSerif", Font.BOLD, 18));
        monthlyMonthWrap.add(monthlyMonthField, BorderLayout.CENTER);
        monthlyPanel.add(monthlyMonthWrap);

        // YEARLY panel
        JPanel yearlyPanel = new JPanel(new GridLayout(1, 4, 25, 0));
        yearlyPanel.putClientProperty("themed_base", true);
        yearlyPanel.setOpaque(false);

        yearlyPanel.add(barLabel("DATE (DD/MM):"));
        RoundedPanel yearlyDMWrap = roundedFieldPanel();
        yearlyDayMonthField = new JTextField();
        yearlyDayMonthField.setFont(new Font("SansSerif", Font.BOLD, 18));
        yearlyDMWrap.add(yearlyDayMonthField, BorderLayout.CENTER);
        yearlyPanel.add(yearlyDMWrap);

        yearlyPanel.add(barLabel("STARTING YEAR (YYYY):"));
        RoundedPanel yearlyYearWrap = roundedFieldPanel();
        yearlyYearField = new JTextField();
        yearlyYearField.setFont(new Font("SansSerif", Font.BOLD, 18));
        yearlyYearWrap.add(yearlyYearField, BorderLayout.CENTER);
        yearlyPanel.add(yearlyYearWrap);

        frequencyPanel.add(dailyPanel, "DAILY");
        frequencyPanel.add(weeklyPanel, "WEEKLY");
        frequencyPanel.add(monthlyPanel, "MONTHLY");
        frequencyPanel.add(yearlyPanel, "YEARLY");

        freqBar.add(frequencyPanel, BorderLayout.CENTER);
        formWrap.add(freqBar, gc);

        content.add(formWrap);
        content.add(Box.createVerticalStrut(18));

        // Buttons (inside scrollable content so they don't overlap)
        JPanel buttons = new JPanel(new GridLayout(1, 2, 35, 0));
        buttons.putClientProperty("themed_base", true);
        buttons.setBorder(BorderFactory.createEmptyBorder(25, 240, 30, 240));
        buttons.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton cancel = bigButton("CANCEL");
        JButton save = bigButton("SAVE");

        cancel.addActionListener(e -> controller.showRecurringTasks());
        save.addActionListener(e -> onSave());

        buttons.add(cancel);
        buttons.add(save);
        content.add(buttons);

        JScrollPane contentScroll = new JScrollPane(content);
        contentScroll.setBorder(BorderFactory.createEmptyBorder());
        contentScroll.setOpaque(false);
        contentScroll.getViewport().setOpaque(false);
        contentScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(contentScroll, BorderLayout.CENTER);

        // Toggle behaviour
        dailyBtn.addActionListener(e -> setFrequency("DAILY"));
        weeklyBtn.addActionListener(e -> setFrequency("WEEKLY"));
        monthlyBtn.addActionListener(e -> setFrequency("MONTHLY"));
        yearlyBtn.addActionListener(e -> setFrequency("YEARLY"));

        // Default: WEEKLY
        weeklyBtn.setSelected(true);
        setFrequency("WEEKLY");

        setEditingEnabled(false);
    }

    private void setEditingEnabled(boolean enabled) {
        nameField.setEnabled(enabled);
        descArea.setEnabled(enabled);

        dailyBtn.setEnabled(enabled);
        weeklyBtn.setEnabled(enabled);
        monthlyBtn.setEnabled(enabled);
        yearlyBtn.setEnabled(enabled);

        dailyTimeField.setEnabled(enabled);
        weeklyTimeField.setEnabled(enabled);
        weeklyDayCombo.setEnabled(enabled);
        monthlyDayField.setEnabled(enabled);
        monthlyMonthField.setEnabled(enabled);
        yearlyDayMonthField.setEnabled(enabled);
        yearlyYearField.setEnabled(enabled);
    }

    private void setFrequency(String freq) {
        selectedFrequency = freq;
        frequencyCards.show(frequencyPanel, freq);
    }

    private void onLoad() {
        String raw = idField.getText().trim();
        int id;
        try {
            id = Integer.parseInt(raw);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric task ID.", "Invalid ID", JOptionPane.WARNING_MESSAGE);
            return;
        }

        PlanSyncRecurringTasksModel.RecurringTask t = recurringModel.getTaskById(id);
        if (t == null) {
            JOptionPane.showMessageDialog(this, "No recurring task found with ID " + id + ".", "Not Found", JOptionPane.WARNING_MESSAGE);
            return;
        }

        loadedId = id;

        nameField.setText(t.name);
        descArea.setText(t.description);

        String freq = (t.frequency == null) ? "WEEKLY" : t.frequency.toUpperCase();
        setFrequency(freq);

        // Parse stored timeDate formats
        try {
            switch (freq) {
                case "DAILY" -> {
                    dailyBtn.setSelected(true);
                    dailyTimeField.setText(extractHHmm(t.timeDate));
                }
                case "WEEKLY" -> {
                    weeklyBtn.setSelected(true);
                    weeklyTimeField.setText(extractHHmm(t.timeDate));
                    String day = extractWeeklyDay(t.timeDate);
                    if (day != null) weeklyDayCombo.setSelectedItem(day);
                }
                case "MONTHLY" -> {
                    monthlyBtn.setSelected(true);
                    String[] dm = extractDM(t.timeDate);
                    if (dm != null) {
                        monthlyDayField.setText(dm[0]);
                        monthlyMonthField.setText(dm[1]);
                    } else {
                        monthlyDayField.setText("");
                        monthlyMonthField.setText("");
                    }
                }
                case "YEARLY" -> {
                    yearlyBtn.setSelected(true);
                    String dm = extractDayMonthOnly(t.timeDate);
                    String year = extractStartYear(t.timeDate);
                    yearlyDayMonthField.setText(dm != null ? dm : "");
                    yearlyYearField.setText(year != null ? year : "");
                }
                default -> {
                    weeklyBtn.setSelected(true);
                    setFrequency("WEEKLY");
                }
            }
        } catch (Exception ignored) {
            // If malformed, leave fields as-is so the user can correct them.
        }

        setEditingEnabled(true);

        // ✅ Removed success pop-up on load
    }

    private void onSave() {
        if (loadedId == null) {
            JOptionPane.showMessageDialog(this, "Load a task first (enter ID and press LOAD).", "Nothing Loaded", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = nameField.getText().trim();
        String desc = descArea.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a task name.", "Missing Name", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (desc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a task description.", "Missing Description", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String timeDate;

            switch (selectedFrequency) {

                case "DAILY" -> {
                    String time = dailyTimeField.getText().trim();
                    if (time.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Please enter time (HH:MM).", "Missing Time", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    LocalTime t = PlanSyncRecurringTasksModel.parseTimeHHmm(time);
                    timeDate = t.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
                }

                case "WEEKLY" -> {
                    String time = weeklyTimeField.getText().trim();
                    if (time.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Please enter time (HH:MM).", "Missing Time", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    LocalTime t = PlanSyncRecurringTasksModel.parseTimeHHmm(time);
                    String day = ((String) weeklyDayCombo.getSelectedItem()).toUpperCase();
                    timeDate = t.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")) + " " + day;
                }

                case "MONTHLY" -> {
                    String day = monthlyDayField.getText().trim();
                    String month = monthlyMonthField.getText().trim();
                    if (day.isEmpty() || month.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Please enter DD and MM.", "Missing Data", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    PlanSyncRecurringTasksModel.validateDayMonth(day, month);
                    int d = Integer.parseInt(day);
                    int m = Integer.parseInt(month);
                    timeDate = String.format("%02d/%02d (Start Month: %02d)", d, m, m);
                }

                case "YEARLY" -> {
                    String dm = yearlyDayMonthField.getText().trim();
                    String year = yearlyYearField.getText().trim();
                    if (dm.isEmpty() || year.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Please enter DD/MM and YYYY.", "Missing Data", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    PlanSyncRecurringTasksModel.validateDayMonthYear(dm, year);
                    int y = Integer.parseInt(year);
                    timeDate = dm + " (Start: " + y + ")";
                }

                default -> throw new IllegalStateException("Unknown frequency: " + selectedFrequency);
            }

            recurringModel.updateTaskById(loadedId, name, desc, timeDate, selectedFrequency);
            JOptionPane.showMessageDialog(this, "Recurring task updated!", "Saved", JOptionPane.INFORMATION_MESSAGE);
            controller.showRecurringTasks();

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid time/date format. Check your inputs.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number input. Check DD/MM/YYYY values.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --------- Parsing helpers for existing saved strings ---------

    private String extractHHmm(String s) {
        if (s == null) return "";
        Matcher m = Pattern.compile("(\\d{2}:\\d{2})").matcher(s);
        return m.find() ? m.group(1) : "";
    }

    private String extractWeeklyDay(String s) {
        if (s == null) return null;
        // expected: "HH:mm DAY"
        String[] parts = s.trim().split("\\s+");
        if (parts.length >= 2) return parts[1].toUpperCase();
        return null;
    }

    private String[] extractDM(String s) {
        if (s == null) return null;
        // expected start: "DD/MM ..."
        Matcher m = Pattern.compile("^(\\d{2})/(\\d{2})").matcher(s.trim());
        if (!m.find()) return null;
        return new String[]{m.group(1), m.group(2)};
    }

    private String extractDayMonthOnly(String s) {
        if (s == null) return null;
        Matcher m = Pattern.compile("^(\\d{2}/\\d{2})").matcher(s.trim());
        return m.find() ? m.group(1) : null;
    }

    private String extractStartYear(String s) {
        if (s == null) return null;
        Matcher m = Pattern.compile("\\(Start:\\s*(\\d{4})\\)").matcher(s);
        return m.find() ? m.group(1) : null;
    }

    // --------- UI helper methods ---------

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(new Font("SansSerif", Font.BOLD, 18));
        l.putClientProperty("on_base", true);
        return l;
    }

    /**
     * These labels should ALWAYS be black.
     */
    private JLabel barLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 18));
        l.setForeground(Color.BLACK);
        l.putClientProperty("ignore_theme", true);
        return l;
    }

    private RoundedPanel roundedFieldPanel() {
        RoundedPanel p = new RoundedPanel(28);
        p.putClientProperty("themed", true);
        p.setLayout(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
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

    private int getWidthChars() {
        int px = listScroll.getViewport().getExtentSize().width;
        Insets in = taskArea.getInsets();
        px -= (in.left + in.right);
        if (px <= 0) return 93;

        FontMetrics fm = taskArea.getFontMetrics(taskArea.getFont());
        int charW = fm.charWidth('=');
        if (charW <= 0) charW = Math.max(1, fm.charWidth('W'));

        int chars = px / charW;
        return Math.max(40, chars);
    }

    private void updateListText() {
        taskArea.setText(recurringModel.formatForDisplay(getWidthChars()));
        taskArea.setCaretPosition(0);
    }

    @Override
    public void refresh() {
        updateListText();

        idField.setText("");
        nameField.setText("");
        descArea.setText("");

        dailyTimeField.setText("");
        weeklyTimeField.setText("");
        weeklyDayCombo.setSelectedIndex(0);
        monthlyDayField.setText("");
        monthlyMonthField.setText("");
        yearlyDayMonthField.setText("");
        yearlyYearField.setText("");

        loadedId = null;
        setEditingEnabled(false);

        weeklyBtn.setSelected(true);
        setFrequency("WEEKLY");
    }
}