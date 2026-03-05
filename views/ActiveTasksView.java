package views;

import controller.AppController;
import model.PlanSyncActiveTasksModel;

import javax.swing.*;
import java.awt.*;

/**
 * ACTIVE TASKS page (GUI).
 *
 * Uses PlanSyncActiveTasksModel to render the terminal-style task list.
 */

public class ActiveTasksView extends JPanel implements RefreshableView {

    private final AppController controller;
    private final PlanSyncActiveTasksModel activeModel;

    private final JTextArea taskArea;

    public ActiveTasksView(AppController controller, PlanSyncActiveTasksModel activeModel) {
        this.controller = controller;
        this.activeModel = activeModel;

        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("A C T I V E   T A S K S", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.putClientProperty("on_base", true);
        title.setBorder(BorderFactory.createEmptyBorder(25, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        // Center list panel
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

        // Buttons row
        JPanel buttons = new JPanel(new GridLayout(1, 3, 25, 0));
        buttons.putClientProperty("themed_base", true);
        buttons.setBorder(BorderFactory.createEmptyBorder(18, 120, 25, 120));

        JButton deleteBtn = bigButton("DELETE ACTIVE TASK");
        JButton completeBtn = bigButton("MARK COMPLETED");
        JButton addBtn = bigButton("NEW ACTIVE TASK");

        deleteBtn.addActionListener(e -> controller.showView("DELETE_ACTIVE"));
        completeBtn.addActionListener(e -> controller.showView("MARK_COMPLETED"));
        addBtn.addActionListener(e -> controller.showView("ADD_ACTIVE"));

        buttons.add(deleteBtn);
        buttons.add(completeBtn);
        buttons.add(addBtn);

        JPanel center = new JPanel();
        center.putClientProperty("themed_base", true);
        center.setLayout(new BorderLayout());
        center.setBorder(BorderFactory.createEmptyBorder(20, 120, 0, 120));
        center.add(listPanel, BorderLayout.CENTER);
        center.add(buttons, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);

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
        taskArea.setText(activeModel.formatForDisplay());
        taskArea.setCaretPosition(0);
    }
}