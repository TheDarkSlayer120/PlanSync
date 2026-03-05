package views;

import controller.AppController;
import model.PlanSyncRecurringTasksModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class RecurringTasksView extends JPanel implements RefreshableView {

    private final AppController controller;
    private final PlanSyncRecurringTasksModel recurringModel;

    private final JTextArea taskArea;
    private final JScrollPane scroll;

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
        taskArea.setWrapStyleWord(false);
        taskArea.setFont(new Font("Monospaced", Font.BOLD, 14));
        taskArea.setOpaque(false);

        scroll = new JScrollPane(taskArea);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        scroll.getViewport().addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                updateListText();
            }
        });

        listPanel.add(scroll, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(1, 3, 25, 0));
        buttons.putClientProperty("themed_base", true);
        buttons.setBorder(BorderFactory.createEmptyBorder(18, 160, 25, 160));

        JButton deleteBtn = bigButton("DELETE RECURRING TASK");
        JButton editBtn = bigButton("EDIT RECURRING TASK");
        JButton addBtn = bigButton("NEW RECURRING TASK");

        deleteBtn.addActionListener(e -> controller.showView("DELETE_RECURRING"));
        editBtn.addActionListener(e -> controller.showView("EDIT_RECURRING"));
        addBtn.addActionListener(e -> controller.showView("ADD_RECURRING"));

        buttons.add(deleteBtn);
        buttons.add(editBtn);
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

    private void updateListText() {
        taskArea.setText(recurringModel.formatForDisplay(getWidthChars()));
        taskArea.setCaretPosition(0);
    }

    @Override
    public void refresh() {
        updateListText();
    }
}