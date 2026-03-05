package views;

import controller.AppController;
import components.PlanSyncDialogs;
import model.PlanSyncCompletedTasksModel;

import javax.swing.*;
import java.awt.*;

public class CompletedTasksView extends JPanel implements RefreshableView {

    private final AppController controller;
    private final PlanSyncCompletedTasksModel completedModel;

    private final JTextArea taskArea;
    private final JScrollPane scroll;

    public CompletedTasksView(AppController controller, PlanSyncCompletedTasksModel completedModel) {

        this.controller = controller;
        this.completedModel = completedModel;

        setLayout(new BorderLayout());

        JLabel title = new JLabel("C O M P L E T E D   T A S K S", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.putClientProperty("on_base", true);
        title.setBorder(BorderFactory.createEmptyBorder(25, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        RoundedPanel listPanel = new RoundedPanel(28);
        listPanel.putClientProperty("themed", true);
        listPanel.setLayout(new BorderLayout());
        listPanel.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        taskArea = new JTextArea();
        taskArea.setEditable(false);
        taskArea.setFont(new Font("Monospaced", Font.BOLD, 16));
        taskArea.setOpaque(false);

        scroll = new JScrollPane(taskArea);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        listPanel.add(scroll, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(1, 2, 35, 0));
        buttons.putClientProperty("themed_base", true);
        buttons.setBorder(BorderFactory.createEmptyBorder(25, 120, 30, 120));

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
        boolean confirm = PlanSyncDialogs.confirm(
                this,
                controller,
                "Confirm Clear",
                "Clear the entire completed tasks list?"
        );
        if (!confirm) return;

        completedModel.clearAll();
        // ✅ Removed unnecessary “list cleared” pop-up
        updateListText();
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
        taskArea.setText(completedModel.formatForDisplay(getWidthChars()));
        taskArea.setCaretPosition(0);
    }

    @Override
    public void refresh() {
        updateListText();
    }
}