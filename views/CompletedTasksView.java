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
 * File purpose: This class supports the CompletedTasksView part of PlanSync and documents the main responsibilities of the file.
 */

import controller.AppController;
import components.PlanSyncDialogs;
import model.PlanSyncCompletedTasksModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class CompletedTasksView extends JPanel implements RefreshableView {

    private final AppController controller;
    private final PlanSyncCompletedTasksModel completedModel;

    private final JTextArea taskArea;
    private final JScrollPane scroll;

    public CompletedTasksView(AppController controller, PlanSyncCompletedTasksModel completedModel) {

        this.controller = controller;
        this.completedModel = completedModel;

        // Section: Update the state used to layout.
        setLayout(new BorderLayout());

        JLabel title = new JLabel("C O M P L E T E D   T A S K S", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.putClientProperty("on_base", true);
        title.setBorder(BorderFactory.createEmptyBorder(25, 10, 10, 10));
        // Section: Add the data or behavior needed to add.
        add(title, BorderLayout.NORTH);

        // ✅ Same as Active/Recurring
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
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        // ✅ Same dynamic resize behavior as Active/Recurring
        scroll.getViewport().addComponentListener(new ComponentAdapter() {
            // Section: Handle the logic for component resized.
            @Override public void componentResized(ComponentEvent e) {
                // Section: Refresh or recompute the state used to list text.
                updateListText();
            }
        });

        listPanel.add(scroll, BorderLayout.CENTER);

        // ✅ Button row sizing aligned with Recurring (2 buttons, so use 60 padding)
        JPanel buttons = new JPanel(new GridLayout(1, 2, 25, 0));
        buttons.putClientProperty("themed_base", true);
        buttons.setBorder(BorderFactory.createEmptyBorder(18, 60, 25, 60));

        JButton deleteBtn = bigButton("DELETE COMPLETED TASK");
        JButton clearBtn = bigButton("CLEAR WHOLE LIST");

        deleteBtn.addActionListener(e -> controller.showView("DELETE_COMPLETED"));
        clearBtn.addActionListener(e -> onClear());

        buttons.add(deleteBtn);
        buttons.add(clearBtn);

        // ✅ Center padding EXACTLY like Active/Recurring (this controls list box size)
        JPanel center = new JPanel(new BorderLayout());
        center.putClientProperty("themed_base", true);
        center.setBorder(BorderFactory.createEmptyBorder(20, 60, 0, 60));

        center.add(listPanel, BorderLayout.CENTER);
        center.add(buttons, BorderLayout.SOUTH);

        // Section: Add the data or behavior needed to add.
        add(center, BorderLayout.CENTER);

        // Section: Handle the logic for refresh.
        refresh();
    }

    // Section: Handle the logic for on clear.
    private void onClear() {
        boolean confirm = PlanSyncDialogs.confirm(
                this,
                controller,
                "Confirm Clear",
                "Clear the entire completed tasks list?"
        );
        if (!confirm) return;

        completedModel.clearAll();
        // Section: Refresh or recompute the state used to list text.
        updateListText();
    }

    // Section: Handle the logic for big button.
    private JButton bigButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);

        // match the newer button sizing we used elsewhere
        b.setFont(new Font("SansSerif", Font.BOLD, 16));
        b.setPreferredSize(new Dimension(0, 55));
        b.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
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
        taskArea.setText(completedModel.formatForDisplay(getWidthChars()));
        taskArea.setCaretPosition(0);
    }

    @Override
    // Section: Handle the logic for refresh.
    public void refresh() {
        // Section: Refresh or recompute the state used to list text.
        updateListText();
    }
}
