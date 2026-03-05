package views;

import controller.AppController;
import model.PlanSyncCompletedTasksModel;

import javax.swing.*;
import java.awt.*;

public class CompletedTasksView extends JPanel implements RefreshableView {

    private final AppController controller;
    private final PlanSyncCompletedTasksModel completedModel;
    private final JTextArea taskArea;

    public CompletedTasksView(AppController controller, PlanSyncCompletedTasksModel completedModel) {
        this.controller = controller;
        this.completedModel = completedModel;

        setLayout(new BorderLayout());

        JLabel title = new JLabel("C O M P L E T E D   T A S K S", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.putClientProperty("on_base", true);
        title.setBorder(BorderFactory.createEmptyBorder(25, 10, 10, 10));
        add(title, BorderLayout.NORTH);

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

        JPanel buttons = new JPanel(new GridLayout(1, 2, 35, 0));
        buttons.putClientProperty("themed_base", true);
        buttons.setBorder(BorderFactory.createEmptyBorder(18, 210, 25, 210));

        JButton deleteBtn = bigButton("DELETE COMPLETED TASK");
        JButton clearBtn = bigButton("CLEAR WHOLE LIST");

        deleteBtn.addActionListener(e -> controller.showView("DELETE_COMPLETED"));
        clearBtn.addActionListener(e -> onClear());

        buttons.add(deleteBtn);
        buttons.add(clearBtn);

        JPanel center = new JPanel(new BorderLayout());
        center.putClientProperty("themed_base", true);
        center.setBorder(BorderFactory.createEmptyBorder(20, 120, 0, 120));
        center.add(listPanel, BorderLayout.CENTER);
        center.add(buttons, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);

        refresh();
    }

    private void onClear() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Clear the entire completed tasks list?",
                "Confirm Clear",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        completedModel.clearAll();
        JOptionPane.showMessageDialog(this, "Completed tasks list cleared.", "Cleared", JOptionPane.INFORMATION_MESSAGE);
        refresh();
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
        taskArea.setText(completedModel.formatForDisplay());
        taskArea.setCaretPosition(0);
    }
}