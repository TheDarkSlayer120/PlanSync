package views;

import controller.AppController;
import model.PlanSyncRecurringTasksModel;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class AddRecurringView extends JPanel implements RefreshableView {

    private final AppController controller;
    private final PlanSyncRecurringTasksModel recurringModel;

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

    private String selectedFrequency = "WEEKLY"; // match your wireframe example

    public AddRecurringView(AppController controller, PlanSyncRecurringTasksModel recurringModel) {

        this.controller = controller;
        this.recurringModel = recurringModel;

        setLayout(new BorderLayout());

        JLabel title = new JLabel("A D D   R E C U R R I N G   T A S K", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.putClientProperty("on_base", true);
        title.setBorder(BorderFactory.createEmptyBorder(25, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        JPanel formWrap = new JPanel();
        formWrap.putClientProperty("themed_base", true);
        formWrap.setLayout(new GridBagLayout());
        formWrap.setBorder(BorderFactory.createEmptyBorder(10, 120, 0, 120));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.insets = new Insets(14, 0, 8, 0);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        // TASK NAME
        formWrap.add(sectionLabel("TASK NAME:"), gc);

        gc.gridy++;
        RoundedPanel namePanel = roundedFieldPanel();
        nameField = new JTextField();
        nameField.setFont(new Font("SansSerif", Font.BOLD, 18));
        namePanel.add(nameField, BorderLayout.CENTER);
        formWrap.add(namePanel, gc);

        // DESCRIPTION
        gc.gridy++;
        formWrap.add(sectionLabel("TASK DESCRIPTION:"), gc);

        gc.gridy++;
        RoundedPanel descPanel = new RoundedPanel(28);
        descPanel.putClientProperty("themed", true);
        descPanel.setLayout(new BorderLayout());
        descPanel.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        descArea = new JTextArea(5, 20);
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

        // Frequency dynamic panel (wireframe grey bar)
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

        add(formWrap, BorderLayout.CENTER);

        // Buttons
        JPanel buttons = new JPanel(new GridLayout(1, 2, 35, 0));
        buttons.putClientProperty("themed_base", true);
        buttons.setBorder(BorderFactory.createEmptyBorder(25, 240, 30, 240));

        JButton cancel = bigButton("CANCEL");
        JButton save = bigButton("SAVE");

        cancel.addActionListener(e -> controller.showRecurringTasks());
        save.addActionListener(e -> onSave());

        buttons.add(cancel);
        buttons.add(save);
        add(buttons, BorderLayout.SOUTH);

        // Toggle behaviour
        dailyBtn.addActionListener(e -> setFrequency("DAILY"));
        weeklyBtn.addActionListener(e -> setFrequency("WEEKLY"));
        monthlyBtn.addActionListener(e -> setFrequency("MONTHLY"));
        yearlyBtn.addActionListener(e -> setFrequency("YEARLY"));

        // Default: WEEKLY (wireframe example)
        weeklyBtn.setSelected(true);
        setFrequency("WEEKLY");
    }

    private void setFrequency(String freq) {
        selectedFrequency = freq;
        frequencyCards.show(frequencyPanel, freq);
    }

    private void onSave() {

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

            recurringModel.addTask(name, desc, timeDate, selectedFrequency);
            JOptionPane.showMessageDialog(this, "New recurring task added!", "Saved", JOptionPane.INFORMATION_MESSAGE);
            controller.showRecurringTasks();

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid time/date format. Check your inputs.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number input. Check DD/MM/YYYY values.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(new Font("SansSerif", Font.BOLD, 18));
        l.putClientProperty("on_base", true);
        return l;
    }

    /**
     * ✅ These labels (TIME:, DAY OF THE WEEK:, etc.) should ALWAYS be black.
     * So we do NOT use on_base, and we force foreground to black.
     */
    private JLabel barLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 18));

        // Don't let theme toggle this one
        // l.putClientProperty("on_base", true);

        l.setForeground(Color.BLACK);

        // Extra safety: if your theme logic changes later, keep it untouched
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

    @Override
    public void refresh() {
        nameField.setText("");
        descArea.setText("");

        dailyTimeField.setText("");
        weeklyTimeField.setText("");
        weeklyDayCombo.setSelectedIndex(0);

        monthlyDayField.setText("");
        monthlyMonthField.setText("");

        yearlyDayMonthField.setText("");
        yearlyYearField.setText("");

        // keep the same default as wireframe
        weeklyBtn.setSelected(true);
        setFrequency("WEEKLY");
    }
}