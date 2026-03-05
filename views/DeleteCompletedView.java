package views;

import controller.AppController;
import model.PlanSyncCompletedTasksModel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DeleteCompletedView extends JPanel implements RefreshableView {

    private final AppController controller;
    private final PlanSyncCompletedTasksModel completedModel;

    private final JTextArea taskArea;
    private final JTextField selectionField;

    public DeleteCompletedView(AppController controller, PlanSyncCompletedTasksModel completedModel) {
        this.controller = controller;
        this.completedModel = completedModel;

        setLayout(new BorderLayout());

        JLabel title = new JLabel("D E L E T E   C O M P L E T E D   T A S K", SwingConstants.CENTER);
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
        taskArea.setWrapStyleWord(true);
        taskArea.setFont(new Font("Monospaced", Font.BOLD, 14));
        taskArea.setOpaque(false);

        JScrollPane scroll = new JScrollPane(taskArea);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        listPanel.add(scroll, BorderLayout.CENTER);

        center.add(listPanel, BorderLayout.CENTER);

        RoundedPanel inputRow = new RoundedPanel(35);
        inputRow.putClientProperty("themed", true);
        inputRow.setLayout(new BorderLayout(20, 0));
        inputRow.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        JLabel prompt = new JLabel("TASK(s) TO DELETE (e.g. 1 3 4):");
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
        buttons.setBorder(BorderFactory.createEmptyBorder(25, 260, 30, 260));

        JButton cancel = bigButton("CANCEL");
        JButton delete = bigButton("DELETE");

        cancel.addActionListener(e -> controller.showCompletedTasks());
        delete.addActionListener(e -> onDelete());

        buttons.add(cancel);
        buttons.add(delete);

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

    private void onDelete() {
        List<Integer> indexes = parseSelections(selectionField.getText());
        if (indexes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No valid tasks selected.", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete the selected task(s)?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        completedModel.deleteByIndexes(indexes);
        JOptionPane.showMessageDialog(this, "Task(s) deleted!", "Deleted", JOptionPane.INFORMATION_MESSAGE);
        controller.showCompletedTasks();
    }

    private List<Integer> parseSelections(String input) {
        input = input == null ? "" : input.trim();
        if (input.isEmpty()) return List.of();

        String[] parts = input.split("\\s+");
        List<Integer> idx = new ArrayList<>();
        int size = completedModel.getCompletedTasks().size();

        for (String p : parts) {
            try {
                int oneBased = Integer.parseInt(p);
                int zero = oneBased - 1;
                if (zero >= 0 && zero < size && !idx.contains(zero)) {
                    idx.add(zero);
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return idx;
    }

    @Override
    public void refresh() {
        taskArea.setText(completedModel.formatForDisplay());
        taskArea.setCaretPosition(0);
        selectionField.setText("");
    }
}