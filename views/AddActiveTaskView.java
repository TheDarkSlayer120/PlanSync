package views;

import controller.AppController;
import components.PlanSyncDialogs;
import components.ThemeApplier;
import model.PlanSyncActiveTasksModel;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.Locale;

public class AddActiveTaskView extends JPanel implements RefreshableView {

    private final AppController controller;
    private final PlanSyncActiveTasksModel activeModel;

    private final JTextField nameField;
    private final JTextArea descArea;
    private final JTextField dateField;

    private static final DateTimeFormatter DATE_FMT = PlanSyncActiveTasksModel.getDateFormatter();

    public AddActiveTaskView(AppController controller, PlanSyncActiveTasksModel activeModel) {
        this.controller = controller;
        this.activeModel = activeModel;

        setLayout(new BorderLayout());

        JLabel title = new JLabel("A D D   A C T I V E   T A S K", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.putClientProperty("on_base", true);
        title.setBorder(BorderFactory.createEmptyBorder(25, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        JPanel formWrap = new JPanel();
        formWrap.putClientProperty("themed_base", true);
        formWrap.setLayout(new GridBagLayout());
        formWrap.setBorder(BorderFactory.createEmptyBorder(10, 120, 0, 120));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.insets = new Insets(14, 0, 8, 0);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        formWrap.add(sectionLabel("TASK NAME:"), gc);

        gc.gridy++;
        RoundedPanel namePanel = roundedFieldPanel();
        nameField = new JTextField();
        nameField.setFont(new Font("SansSerif", Font.BOLD, 18));
        namePanel.add(nameField, BorderLayout.CENTER);
        formWrap.add(namePanel, gc);

        gc.gridy++;
        formWrap.add(sectionLabel("TASK DESCRIPTION:"), gc);

        gc.gridy++;
        RoundedPanel descPanel = new RoundedPanel(28);
        descPanel.putClientProperty("themed", true);
        descPanel.setLayout(new BorderLayout());
        descPanel.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        descArea = new JTextArea(5, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setFont(new Font("SansSerif", Font.BOLD, 16));

        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBorder(BorderFactory.createEmptyBorder());
        descPanel.add(descScroll, BorderLayout.CENTER);
        formWrap.add(descPanel, gc);

        gc.gridy++;
        JPanel dateRow = new JPanel(new BorderLayout(12, 0));
        dateRow.putClientProperty("themed_base", true);

        JLabel dateLabel = sectionLabel("DATE (DD/MM/YYYY):");
        dateRow.add(dateLabel, BorderLayout.WEST);

        RoundedPanel datePanel = roundedFieldPanel();
        dateField = new JTextField();
        dateField.setFont(new Font("SansSerif", Font.BOLD, 18));
        datePanel.add(dateField, BorderLayout.CENTER);
        dateRow.add(datePanel, BorderLayout.CENTER);

        JButton pickBtn = new JButton("🗓");
        pickBtn.setToolTipText("Pick date from calendar");
        pickBtn.setFocusPainted(false);
        pickBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        pickBtn.setPreferredSize(new Dimension(64, 48));
        pickBtn.addActionListener(e -> openDatePicker());
        dateRow.add(pickBtn, BorderLayout.EAST);

        formWrap.add(dateRow, gc);

        add(formWrap, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(1, 2, 35, 0));
        buttons.putClientProperty("themed_base", true);
        buttons.setBorder(BorderFactory.createEmptyBorder(25, 240, 30, 240));

        JButton cancel = bigButton("CANCEL");
        JButton save = bigButton("SAVE");
        cancel.addActionListener(e -> controller.showActiveTasks());
        save.addActionListener(e -> onSave());

        buttons.add(cancel);
        buttons.add(save);
        add(buttons, BorderLayout.SOUTH);
    }

    private void openDatePicker() {
        Window w = SwingUtilities.getWindowAncestor(this);
        DatePickerDialog dlg = new DatePickerDialog(w, controller, selected -> dateField.setText(selected.format(DATE_FMT)));
        dlg.setVisible(true);
    }

    private void onSave() {
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
            activeModel.addTask(name, desc, date);
            controller.showActiveTasks();
        } catch (DateTimeParseException ex) {
            PlanSyncDialogs.alert(this, controller, "Invalid Date", "Invalid date format. Please use DD/MM/YYYY.");
        }
    }

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(new Font("SansSerif", Font.BOLD, 18));
        l.putClientProperty("on_base", true);
        return l;
    }

    private RoundedPanel roundedFieldPanel() {
        RoundedPanel p = new RoundedPanel(28);
        p.putClientProperty("themed", true);
        p.setLayout(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        return p;
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
        nameField.setText("");
        descArea.setText("");
        dateField.setText("");
    }

    private static class DatePickerDialog extends JDialog {
        private final AppController controller;
        private final java.util.function.Consumer<LocalDate> onPick;

        private YearMonth current;
        private final JLabel monthLabel = new JLabel("", SwingConstants.CENTER);
        private final JButton[] dayButtons = new JButton[42];

        DatePickerDialog(Window owner, AppController controller, java.util.function.Consumer<LocalDate> onPick) {
            super(owner, "Pick a date", ModalityType.APPLICATION_MODAL);
            this.controller = controller;
            this.onPick = onPick;
            this.current = YearMonth.now();

            setUndecorated(true);
            setBackground(new Color(0, 0, 0, 0));

            RoundedPanel card = new RoundedPanel(28);
            card.putClientProperty("themed", true);
            card.setLayout(new BorderLayout(0, 14));
            card.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedStrokeBorder(28, 2, Color.BLACK),
                    BorderFactory.createEmptyBorder(18, 22, 18, 22)
            ));
            card.setPreferredSize(new Dimension(620, 470));

            JPanel top = new JPanel(new BorderLayout(14, 0));
            top.setOpaque(false);

            JButton prev = headerButton("←");
            JButton next = headerButton("→");

            monthLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
            monthLabel.setHorizontalAlignment(SwingConstants.CENTER);
            monthLabel.putClientProperty("ignore_theme", true);
            monthLabel.setForeground(Color.BLACK);

            top.add(prev, BorderLayout.WEST);
            top.add(monthLabel, BorderLayout.CENTER);
            top.add(next, BorderLayout.EAST);

            JPanel center = new JPanel(new BorderLayout(0, 12));
            center.setOpaque(false);

            JPanel dow = new JPanel(new GridLayout(1, 7, 8, 0));
            dow.setOpaque(false);
            for (DayOfWeek d : DayOfWeek.values()) {
                JLabel l = new JLabel(d.getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toUpperCase(), SwingConstants.CENTER);
                l.setFont(new Font("SansSerif", Font.BOLD, 16));
                l.putClientProperty("ignore_theme", true);
                l.setForeground(Color.BLACK);
                dow.add(l);
            }

            JPanel grid = new JPanel(new GridLayout(6, 7, 8, 8));
            grid.setOpaque(false);

            for (int i = 0; i < 42; i++) {
                JButton b = createDayButton();
                dayButtons[i] = b;
                grid.add(b);
            }

            center.add(dow, BorderLayout.NORTH);
            center.add(grid, BorderLayout.CENTER);

            JPanel bottom = new JPanel(new GridLayout(1, 2, 16, 0));
            bottom.setOpaque(false);

            JButton today = footerButton("TODAY");
            JButton cancel = footerButton("CANCEL");

            today.addActionListener(e -> {
                onPick.accept(LocalDate.now());
                dispose();
            });
            cancel.addActionListener(e -> dispose());

            bottom.add(today);
            bottom.add(cancel);

            card.add(top, BorderLayout.NORTH);
            card.add(center, BorderLayout.CENTER);
            card.add(bottom, BorderLayout.SOUTH);

            JPanel outer = new JPanel(new GridBagLayout());
            outer.setOpaque(true);
            outer.setBackground(new Color(0, 0, 0, 110));
            outer.add(card);
            setContentPane(outer);

            prev.addActionListener(e -> {
                current = current.minusMonths(1);
                render();
            });
            next.addActionListener(e -> {
                current = current.plusMonths(1);
                render();
            });

            render();
            ThemeApplier.apply(this, controller.getSettings());

            pack();
            setResizable(false);
            setLocationRelativeTo(owner);
        }

        private JButton headerButton(String text) {
            JButton b = new JButton(text);
            b.setFocusPainted(false);
            b.setFont(new Font("SansSerif", Font.BOLD, 22));
            b.setPreferredSize(new Dimension(88, 50));
            return b;
        }

        private JButton footerButton(String text) {
            JButton b = new JButton(text);
            b.setFocusPainted(false);
            b.setFont(new Font("SansSerif", Font.BOLD, 20));
            b.setPreferredSize(new Dimension(0, 52));
            return b;
        }

        private JButton createDayButton() {
            JButton b = new JButton("");
            b.setFocusPainted(false);
            b.setMargin(new Insets(0, 0, 0, 0));
            b.setFont(new Font("SansSerif", Font.BOLD, 20));
            b.setPreferredSize(new Dimension(64, 48));
            b.setOpaque(true);
            b.setContentAreaFilled(true);
            b.putClientProperty("ignore_theme", true);
            b.addActionListener(e -> {
                Object v = b.getClientProperty("date");
                if (v instanceof LocalDate ld) {
                    onPick.accept(ld);
                    dispose();
                }
            });
            return b;
        }

        private void render() {
            String monthName = current.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH).toUpperCase();
            monthLabel.setText(monthName + " " + current.getYear());

            LocalDate first = current.atDay(1);
            int firstIndex = (first.getDayOfWeek().getValue() + 6) % 7;
            LocalDate start = first.minusDays(firstIndex);
            LocalDate today = LocalDate.now();

            for (int i = 0; i < 42; i++) {
                LocalDate d = start.plusDays(i);
                JButton b = dayButtons[i];
                b.putClientProperty("date", d);
                b.setText(String.valueOf(d.getDayOfMonth()));

                boolean inMonth = d.getMonthValue() == current.getMonthValue();
                boolean isToday = d.equals(today);

                b.setEnabled(true);
                b.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
                b.setForeground(inMonth ? Color.BLACK : new Color(130, 130, 130));
                b.setBackground(isToday ? new Color(255, 236, 170) : Color.WHITE);
            }
        }

        private static final class RoundedStrokeBorder extends AbstractBorder {
            private final int radius;
            private final int stroke;
            private final Color color;

            private RoundedStrokeBorder(int radius, int stroke, Color color) {
                this.radius = radius;
                this.stroke = Math.max(1, stroke);
                this.color = color;
            }

            @Override
            public Insets getBorderInsets(Component c) {
                int s = stroke;
                return new Insets(s, s, s, s);
            }

            @Override
            public Insets getBorderInsets(Component c, Insets insets) {
                Insets i = getBorderInsets(c);
                insets.top = i.top;
                insets.left = i.left;
                insets.bottom = i.bottom;
                insets.right = i.right;
                return insets;
            }

            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(color);
                    g2.setStroke(new BasicStroke(stroke));

                    float inset = stroke / 2f;
                    float w = width - stroke;
                    float h = height - stroke;

                    Shape rr = new RoundRectangle2D.Float(
                            x + inset,
                            y + inset,
                            w,
                            h,
                            radius * 2f,
                            radius * 2f
                    );
                    g2.draw(rr);
                } finally {
                    g2.dispose();
                }
            }
        }
    }
}