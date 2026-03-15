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
 * File purpose: This class supports the EditActiveTaskView part of PlanSync and documents the main responsibilities of the file.
 */

import controller.AppController;
import components.PlanSyncDialogs;
import model.PlanSyncActiveTasksModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.format.DateTimeParseException;

/**
 * EDIT ACTIVE TASK page (GUI).
 * Workflow:
 * 1) User enters Task ID (as displayed in the list) and presses LOAD.
 * 2) User edits name/description/date and presses SAVE.
 */
public class EditActiveTaskView extends JPanel implements RefreshableView {

    private final AppController controller;
    private final PlanSyncActiveTasksModel activeModel;

    private final JTextArea taskArea;
    private final JScrollPane scroll;

    private final JTextField idField;
    private final JTextField nameField;
    private final JTextArea descArea;
    private final JTextField dateField;

    private Integer loadedId = null; // 1-based

    public EditActiveTaskView(AppController controller, PlanSyncActiveTasksModel activeModel) {

        this.controller = controller;
        this.activeModel = activeModel;

        // Section: Update the state used to layout.
        setLayout(new BorderLayout());

        JLabel title = new JLabel("E D I T   A C T I V E   T A S K", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.putClientProperty("on_base", true);
        title.setBorder(BorderFactory.createEmptyBorder(25, 10, 10, 10));
        // Section: Add the data or behavior needed to add.
        add(title, BorderLayout.NORTH);

        // Make the edit screen scrollable so smaller windows don't squash the UI.
        // The bottom navigation bar (outside this view) remains visible.
        JPanel content = new JPanel();
        content.putClientProperty("themed_base", true);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(20, 120, 20, 120));

        // ===== Task list =====
        RoundedPanel listPanel = new RoundedPanel(35);
        listPanel.putClientProperty("themed", true);
        listPanel.setLayout(new BorderLayout());
        listPanel.setBorder(BorderFactory.createEmptyBorder(22, 28, 22, 28));

        taskArea = new JTextArea();
        taskArea.setEditable(false);
        taskArea.setLineWrap(true);
        taskArea.setWrapStyleWord(false);
        taskArea.setFont(new Font("Monospaced", Font.BOLD, 14));
        taskArea.setOpaque(false);

        scroll = new JScrollPane(taskArea);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        scroll.getViewport().addComponentListener(new ComponentAdapter() {
            // Section: Handle the logic for component resized.
            @Override public void componentResized(ComponentEvent e) {
                // Section: Refresh or recompute the state used to list text.
                updateListText();
            }
        });

        listPanel.add(scroll, BorderLayout.CENTER);
        listPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(listPanel);
        content.add(Box.createVerticalStrut(18));

        // ===== Edit form =====
        JPanel formWrap = new JPanel(new GridBagLayout());
        formWrap.putClientProperty("themed_base", true);
        formWrap.setAlignmentX(Component.CENTER_ALIGNMENT);

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.insets = new Insets(10, 0, 8, 0);
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

        content.add(formWrap);
        content.add(Box.createVerticalStrut(18));

        // Buttons (kept inside the scrollable content so they don't overlap/squash)
        JPanel buttons = new JPanel(new GridLayout(1, 2, 35, 0));
        buttons.putClientProperty("themed_base", true);
        buttons.setBorder(BorderFactory.createEmptyBorder(25, 240, 30, 240));
        buttons.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton cancel = bigButton("CANCEL");
        JButton save = bigButton("SAVE");

        cancel.addActionListener(e -> controller.showActiveTasks());
        save.addActionListener(e -> onSave());

        buttons.add(cancel);
        buttons.add(save);
        content.add(buttons);

        JScrollPane contentScroll = new JScrollPane(content);
        contentScroll.setBorder(BorderFactory.createEmptyBorder());
        contentScroll.setOpaque(false);
        contentScroll.getViewport().setOpaque(false);
        contentScroll.getVerticalScrollBar().setUnitIncrement(16);
        // Section: Add the data or behavior needed to add.
        add(contentScroll, BorderLayout.CENTER);

        // Section: Update the state used to editing enabled.
        setEditingEnabled(false);
    }

    // Section: Update the state used to editing enabled.
    private void setEditingEnabled(boolean enabled) {
        nameField.setEnabled(enabled);
        descArea.setEnabled(enabled);
        dateField.setEnabled(enabled);
    }

    // Section: Handle the logic for on load.
    private void onLoad() {
        String raw = idField.getText().trim();
        int id;
        try {
            id = Integer.parseInt(raw);
        // Section: Handle the logic for catch.
        } catch (NumberFormatException ex) {
            PlanSyncDialogs.alert(this, controller, "Invalid ID", "Please enter a valid numeric task ID.");
            return;
        }

        PlanSyncActiveTasksModel.Task t = activeModel.getTaskById(id);
        if (t == null) {
            PlanSyncDialogs.alert(this, controller, "Not Found", "No active task found with ID " + id + ".");
            return;
        }

        loadedId = id;
        nameField.setText(t.name);
        descArea.setText(t.description);
        dateField.setText(t.deadline.format(PlanSyncActiveTasksModel.getDateFormatter()));
        // Section: Update the state used to editing enabled.
        setEditingEnabled(true);

        // ✅ Removed success pop-up on load
    }

    // Section: Handle the logic for on save.
    private void onSave() {
        if (loadedId == null) {
            PlanSyncDialogs.alert(this, controller, "Nothing Loaded", "Load a task first (enter ID and press LOAD).");
            return;
        }

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
            activeModel.updateTaskById(loadedId, name, desc, date);
            // ✅ Removed unnecessary “task updated” pop-up
            controller.showActiveTasks();
        // Section: Handle the logic for catch.
        } catch (DateTimeParseException ex) {
            PlanSyncDialogs.alert(this, controller, "Invalid Date", "Invalid date format. Please use DD/MM/YYYY.");
        // Section: Handle the logic for catch.
        } catch (IllegalArgumentException ex) {
            PlanSyncDialogs.alert(this, controller, "Invalid ID", ex.getMessage());
        }
    }

    // Section: Handle the logic for section label.
    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(new Font("SansSerif", Font.BOLD, 18));
        l.putClientProperty("on_base", true);
        return l;
    }

    // Section: Handle the logic for rounded field panel.
    private RoundedPanel roundedFieldPanel() {
        RoundedPanel p = new RoundedPanel(28);
        p.putClientProperty("themed", true);
        p.setLayout(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        return p;
    }

    // Section: Handle the logic for big button.
    private JButton bigButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 18));
        b.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        return b;
    }

    // Section: Return the data used to width chars.
    private int getWidthChars() {
        int px = scroll.getViewport().getExtentSize().width;
        Insets in = taskArea.getInsets();
        px -= (in.left + in.right);
        if (px <= 0) return 93;

        FontMetrics fm = taskArea.getFontMetrics(taskArea.getFont());
        int charW = fm.charWidth('=');
        if (charW <= 0) charW = Math.max(1, fm.charWidth('W'));

        int chars = px / charW;
        return Math.max(40, chars);
    }

    // Section: Refresh or recompute the state used to list text.
    private void updateListText() {
        taskArea.setText(activeModel.formatForDisplay(getWidthChars()));
        taskArea.setCaretPosition(0);
    }

    @Override
    // Section: Handle the logic for refresh.
    public void refresh() {
        // Section: Refresh or recompute the state used to list text.
        updateListText();
        idField.setText("");
        nameField.setText("");
        descArea.setText("");
        dateField.setText("");
        loadedId = null;
        // Section: Update the state used to editing enabled.
        setEditingEnabled(false);
    }
}
