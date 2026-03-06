package views;

import controller.AppController;
import model.PlanSyncActiveTasksModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ActiveTasksView extends JPanel implements RefreshableView {

    private final AppController controller;
    private final PlanSyncActiveTasksModel activeModel;

    private final JTextArea taskArea;
    private final JScrollPane scroll;

    private final JToggleButton ascBtn;
    private final JToggleButton descBtn;

    public ActiveTasksView(AppController controller, PlanSyncActiveTasksModel activeModel) {
        this.controller = controller;
        this.activeModel = activeModel;

        setLayout(new BorderLayout());

        JLabel title = new JLabel("A C T I V E   T A S K S", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.putClientProperty("on_base", true);
        title.setBorder(BorderFactory.createEmptyBorder(25, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        RoundedPanel listPanel = new RoundedPanel(35);
        listPanel.putClientProperty("themed", true);
        listPanel.setLayout(new BorderLayout());
        listPanel.setBorder(BorderFactory.createEmptyBorder(22, 28, 22, 28));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel listTag = new JLabel("ACTIVE TASKS:");
        listTag.setFont(new Font("SansSerif", Font.BOLD, 12));
        listTag.setBorder(BorderFactory.createEmptyBorder(0, 6, 10, 0));
        header.add(listTag, BorderLayout.WEST);

        JPanel orderWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        orderWrap.setOpaque(false);

        JLabel orderLabel = new JLabel("ORDER:");
        orderLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        orderWrap.add(orderLabel);

        ascBtn = smallArrowToggle("▲", "Ascending");
        descBtn = smallArrowToggle("▼", "Descending");

        ButtonGroup orderGroup = new ButtonGroup();
        orderGroup.add(ascBtn);
        orderGroup.add(descBtn);

        ascBtn.setSelected(true);

        ascBtn.addActionListener(e -> updateListText());
        descBtn.addActionListener(e -> updateListText());

        orderWrap.add(ascBtn);
        orderWrap.add(descBtn);

        header.add(orderWrap, BorderLayout.EAST);
        listPanel.add(header, BorderLayout.NORTH);

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
            @Override
            public void componentResized(ComponentEvent e) {
                updateListText();
            }
        });

        listPanel.add(scroll, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(1, 4, 20, 0));
        buttons.putClientProperty("themed_base", true);
        buttons.setBorder(BorderFactory.createEmptyBorder(18, 40, 25, 40));

        JButton deleteBtn = bigButton("DELETE ACTIVE TASK");
        JButton completeBtn = bigButton("MARK COMPLETED");
        JButton editBtn = bigButton("EDIT ACTIVE TASK");
        JButton addBtn = bigButton("NEW ACTIVE TASK");

        deleteBtn.addActionListener(e -> controller.showView("DELETE_ACTIVE"));
        completeBtn.addActionListener(e -> controller.showView("MARK_COMPLETED"));
        editBtn.addActionListener(e -> controller.showView("EDIT_ACTIVE"));
        addBtn.addActionListener(e -> controller.showView("ADD_ACTIVE"));

        buttons.add(deleteBtn);
        buttons.add(completeBtn);
        buttons.add(editBtn);
        buttons.add(addBtn);

        JPanel center = new JPanel(new BorderLayout());
        center.putClientProperty("themed_base", true);
        center.setBorder(BorderFactory.createEmptyBorder(20, 60, 0, 60));

        center.add(listPanel, BorderLayout.CENTER);
        center.add(buttons, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);

        refresh();
    }

    private JButton bigButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 16));
        b.setPreferredSize(new Dimension(0, 55));
        b.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        return b;
    }

    private JToggleButton smallArrowToggle(String symbol, String tooltip) {
        JToggleButton b = new JToggleButton(symbol);
        b.setToolTipText(tooltip);
        b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setPreferredSize(new Dimension(34, 28));
        b.setMargin(new Insets(0, 0, 0, 0));
        b.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
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
        if (activeModel == null) {
            taskArea.setText("No active tasks found.\n");
            taskArea.setCaretPosition(0);
            return;
        }

        List<PlanSyncActiveTasksModel.Task> tasks = activeModel.getTasks();
        LocalDate today = LocalDate.now();

        List<PlanSyncActiveTasksModel.Task> upcoming = new ArrayList<>();
        List<PlanSyncActiveTasksModel.Task> overdue = new ArrayList<>();

        for (PlanSyncActiveTasksModel.Task t : tasks) {
            if (t == null || t.deadline == null) continue;

            if (!t.deadline.isBefore(today)) {
                upcoming.add(t);
            } else {
                overdue.add(t);
            }
        }

        Comparator<PlanSyncActiveTasksModel.Task> byDate = Comparator.comparing(t -> t.deadline);
        boolean desc = descBtn.isSelected();

        upcoming.sort(desc ? byDate.reversed() : byDate);
        overdue.sort(desc ? byDate.reversed() : byDate);

        StringBuilder sb = new StringBuilder();
        int id = 1;

        for (PlanSyncActiveTasksModel.Task t : upcoming) {
            sb.append(formatTaskRow(id++, t, today)).append("\n\n");
        }

        for (PlanSyncActiveTasksModel.Task t : overdue) {
            sb.append(formatTaskRow(id++, t, today)).append("\n\n");
        }

        if (sb.length() == 0) {
            sb.append("No active tasks found.\n");
        }

        taskArea.setText(sb.toString());
        taskArea.setCaretPosition(0);
    }

    private String formatTaskRow(int id, PlanSyncActiveTasksModel.Task t, LocalDate today) {
        long days = ChronoUnit.DAYS.between(today, t.deadline);
        String status = (days == 0)
                ? "TODAY"
                : (days > 0 ? days + " DAYS REMAINING" : Math.abs(days) + " DAYS OVERDUE");

        return "[" + id + "] "
                + safe(t.name)
                + " -> "
                + safe(t.description)
                + " -> {"
                + t.deadline.format(PlanSyncActiveTasksModel.getDateFormatter())
                + "} -> ["
                + status
                + "]";
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    @Override
    public void refresh() {
        updateListText();
    }
}