package views;

import controller.AppController;
import components.PlanSyncDialogs;
import model.PlanSyncActiveTasksModel;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeParseException;

/**
 * ADD ACTIVE TASK page (GUI).
 */
public class AddActiveTaskView extends JPanel implements RefreshableView {

    private final AppController controller;
    private final PlanSyncActiveTasksModel activeModel;

    private final JTextField nameField;
    private final JTextArea descArea;
    private final JTextField dateField;

    public AddActiveTaskView(AppController controller, PlanSyncActiveTasksModel activeModel) {

        this.controller = controller;
        this.activeModel = activeModel;

        setLayout(new BorderLayout());

        JLabel title = new JLabel("A D D   A C T I V E   T A S K", SwingConstants.CENTER);
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

        // Task name
        JLabel nameLabel = sectionLabel("TASK NAME:");
        formWrap.add(nameLabel, gc);

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

        descArea = new JTextArea(5, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setFont(new Font("SansSerif", Font.BOLD, 16));

        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBorder(BorderFactory.createEmptyBorder());
        descPanel.add(descScroll, BorderLayout.CENTER);
        formWrap.add(descPanel, gc);

        // Date
        gc.gridy++;
        JPanel dateRow = new JPanel(new BorderLayout(20, 0));
        dateRow.putClientProperty("themed_base", true);
        JLabel dateLabel = sectionLabel("DATE (DD/MM/YYYY):");
        dateRow.add(dateLabel, BorderLayout.WEST);
        RoundedPanel datePanel = roundedFieldPanel();
        dateField = new JTextField();
        dateField.setFont(new Font("SansSerif", Font.BOLD, 18));
        datePanel.add(dateField, BorderLayout.CENTER);
        dateRow.add(datePanel, BorderLayout.CENTER);
        formWrap.add(dateRow, gc);

        add(formWrap, BorderLayout.CENTER);

        // Buttons
        JPanel buttons = new JPanel(new GridLayout(1, 2, 35, 0));
        buttons.putClientProperty("themed_base", true);
        buttons.setBorder(BorderFactory.createEmptyBorder(25, 240, 30, 240));

        JButton cancel = bigButton("CANCEL");
        JButton save = bigButton("SAVE");
        cancel.addActionListener(e -> controller.showActiveTasks());
        save.addActionListener(e -> onSave());

        buttons.add(cancel);
        buttons.add(save);

        add(buttons, BorderLayout.SOUTH);
    }

    private void onSave() {

        String name = nameField.getText().trim();
        String desc = descArea.getText().trim();
        String date = dateField.getText().trim();

        if (name.isEmpty()) {
            PlanSyncDialogs.alert(this, controller, "Missing Name", "Please enter a task name.");
            return;
        }
        if (desc.isEmpty()) {
            PlanSyncDialogs.alert(this, controller, "Missing Description", "Please enter a task description.");
            return;
        }
        if (date.isEmpty()) {
            PlanSyncDialogs.alert(this, controller, "Missing Date", "Please enter a deadline date (DD/MM/YYYY).");
            return;
        }

        try {
            activeModel.addTask(name, desc, date);
            // ✅ Removed unnecessary “task added” pop-up
            controller.showActiveTasks();
        } catch (DateTimeParseException ex) {
            PlanSyncDialogs.alert(this, controller, "Invalid Date", "Invalid date format. Please use DD/MM/YYYY.");
        }
    }

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(new Font("SansSerif", Font.BOLD, 18));
        l.putClientProperty("on_base", true);
        return l;
    }

    private RoundedPanel roundedFieldPanel() {
        RoundedPanel p = new RoundedPanel(28);
        p.putClientProperty("themed", true);
        p.setLayout(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        return p;
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
        dateField.setText("");
    }
}