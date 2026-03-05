package views;

import controller.AppController;
import model.PlanSyncRecurringTasksModel;

import javax.swing.*;
import java.awt.*;

public class RecurringTasksView extends JPanel implements RefreshableView {

    private final AppController controller;
    private final PlanSyncRecurringTasksModel recurringModel;
    private final JTextArea taskArea;

    public RecurringTasksView(AppController controller, PlanSyncRecurringTasksModel recurringModel) {
        this.controller = controller;
        this.recurringModel = recurringModel;

        setLayout(new BorderLayout());

        JLabel title = new JLabel("R E C U R R I N G   T A S K S", SwingConstants.CENTER);
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

        JButton deleteBtn = bigButton("DELETE RECURRING TASK");
        JButton addBtn = bigButton("NEW RECURRING TASK");

        deleteBtn.addActionListener(e -> controller.showView("DELETE_RECURRING"));
        addBtn.addActionListener(e -> controller.showView("ADD_RECURRING"));

        buttons.add(deleteBtn);
        buttons.add(addBtn);

        JPanel center = new JPanel(new BorderLayout());
        center.putClientProperty("themed_base", true);
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
        taskArea.setText(recurringModel.formatForDisplay());
        taskArea.setCaretPosition(0);
    }
}