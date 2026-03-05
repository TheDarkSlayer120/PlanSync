package views;

import controller.AppController;
import model.PlanSyncActiveTasksModel;
import model.PlanSyncCompletedTasksModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

public class MarkCompletedTasksView extends JPanel implements RefreshableView {

    private final AppController controller;
    private final PlanSyncActiveTasksModel activeModel;
    private final PlanSyncCompletedTasksModel completedModel;

    private final JTextArea taskArea;
    private final JTextField selectionField;
    private final JScrollPane scroll;

    public MarkCompletedTasksView(
            AppController controller,
            PlanSyncActiveTasksModel activeModel,
            PlanSyncCompletedTasksModel completedModel
    ) {
        this.controller = controller;
        this.activeModel = activeModel;
        this.completedModel = completedModel;

        setLayout(new BorderLayout());

        JLabel title = new JLabel("M A R K   C O M P L E T E D   T A S K", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.putClientProperty("on_base", true);
        title.setBorder(BorderFactory.createEmptyBorder(25, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 18));
        center.putClientProperty("themed_base", true);
        center.setBorder(BorderFactory.createEmptyBorder(20, 140, 0, 140));

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
            @Override public void componentResized(ComponentEvent e) {
                updateListTextPreserveInput();
            }
        });

        listPanel.add(scroll, BorderLayout.CENTER);
        center.add(listPanel, BorderLayout.CENTER);

        RoundedPanel inputRow = new RoundedPanel(35);
        inputRow.putClientProperty("themed", true);
        inputRow.setLayout(new BorderLayout(20, 0));
        inputRow.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        JLabel prompt = new JLabel("MARK COMPLETED TASK(s) (e.g. 1 3 4):");
        prompt.setFont(new Font("SansSerif", Font.BOLD, 18));
        prompt.setForeground(Color.BLACK);

        RoundedPanel fieldWrap = new RoundedPanel(28);
        fieldWrap.putClientProperty("themed", true);
        fieldWrap.setLayout(new BorderLayout());
        fieldWrap.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));

        selectionField = new JTextField();
        selectionField.setFont(new Font("SansSerif", Font.BOLD, 18));
        fieldWrap.add(selectionField, BorderLayout.CENTER);

        inputRow.add(prompt, BorderLayout.WEST);
        inputRow.add(fieldWrap, BorderLayout.CENTER);

        center.add(inputRow, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(1, 2, 35, 0));
        buttons.putClientProperty("themed_base", true);
        buttons.setBorder(BorderFactory.createEmptyBorder(25, 230, 30, 230));

        JButton cancel = bigButton("CANCEL");
        JButton complete = bigButton("MARK AS COMPLETE");

        cancel.addActionListener(e -> controller.showActiveTasks());
        complete.addActionListener(e -> onMarkComplete());

        buttons.add(cancel);
        buttons.add(complete);
        add(buttons, BorderLayout.SOUTH);
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

    private void updateListTextPreserveInput() {
        String keep = selectionField.getText();
        taskArea.setText(activeModel.formatForDisplay(getWidthChars()));
        taskArea.setCaretPosition(0);
        selectionField.setText(keep);
    }

    private void onMarkComplete() {
        List<Integer> indexes = parseSelections(selectionField.getText());
        if (indexes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No valid tasks selected.", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Mark the selected task(s) as complete? (They will move to Completed Tasks.)",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        activeModel.markCompletedByIndexes(indexes, completedModel);
        JOptionPane.showMessageDialog(this, "Task(s) marked complete!", "Completed", JOptionPane.INFORMATION_MESSAGE);
        controller.showActiveTasks();
    }

    private List<Integer> parseSelections(String input) {
        input = input == null ? "" : input.trim();
        if (input.isEmpty()) return List.of();

        String[] parts = input.split("\\s+");
        List<Integer> idx = new ArrayList<>();
        int size = activeModel.getTasks().size();

        for (String p : parts) {
            try {
                int oneBased = Integer.parseInt(p);
                int zero = oneBased - 1;
                if (zero >= 0 && zero < size && !idx.contains(zero)) idx.add(zero);
            } catch (NumberFormatException ignored) {}
        }
        return idx;
    }

    @Override
    public void refresh() {
        taskArea.setText(activeModel.formatForDisplay(getWidthChars()));
        taskArea.setCaretPosition(0);
        selectionField.setText("");
    }
}