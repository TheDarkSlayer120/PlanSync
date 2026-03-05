package views;

import controller.AppController;
import model.PlanSyncTimeCalculator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TimeCalculatorView extends JPanel {

    private final AppController controller;
    private final PlanSyncTimeCalculator calculator;

    // Section 1
    private JTextField durationFromNowField;
    private JLabel durationFromNowResult;

    // Section 2
    private JTextField startTimeField;
    private JTextField endTimeField;
    private JLabel betweenTimesResult;

    // Section 3
    private JTextField startDateField;
    private JTextField endDateField;
    private JRadioButton fullBreakdownBtn;
    private JRadioButton daysOnlyBtn;
    private JRadioButton weeksOnlyBtn;
    private JRadioButton monthsOnlyBtn;
    private JRadioButton yearsOnlyBtn;
    private JTextArea betweenDatesResultArea;

    // Fixed colors that should NEVER change with theme
    private static final Color FIXED_WHITE = Color.WHITE;
    private static final Color FIXED_BLACK = Color.BLACK;

    public TimeCalculatorView(AppController controller) {
        this.controller = controller;
        this.calculator = new PlanSyncTimeCalculator();

        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(Color.WHITE);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildMain(), BorderLayout.CENTER);
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(25, 20, 10, 20));

        JLabel title = new JLabel("T I M E   C A L C U L A T O R", SwingConstants.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.putClientProperty("on_base", true);

        header.add(title);
        header.add(Box.createVerticalStrut(10));
        return header;
    }

    private JComponent buildMain() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBorder(new EmptyBorder(10, 70, 20, 70));
        outer.setOpaque(true);
        outer.setBackground(Color.WHITE);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        outer.add(center, BorderLayout.CENTER);

        center.add(buildDurationFromNowPanel());
        center.add(Box.createVerticalStrut(18));
        center.add(buildDurationBetweenTimesPanel());
        center.add(Box.createVerticalStrut(18));
        center.add(buildDurationBetweenDatesPanel());
        center.add(Box.createVerticalStrut(18));
        center.add(buildBottomDecorBar());

        return outer;
    }

    /* ========================= COMMON STYLING ========================= */

    private void styleSectionTitle(JLabel label) {
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        label.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void styleSmallLabel(JLabel label) {
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
    }

    // ✅ UPDATED: input typing box has a visible border
    private JTextField createPillField(String text, int columns) {
        JTextField field = new JTextField(text, columns);
        field.setHorizontalAlignment(SwingConstants.CENTER);
        field.setFont(new Font("SansSerif", Font.BOLD, 16));
        field.setMaximumSize(new Dimension(9999, 40));

        // fixed colors regardless of theme
        field.setOpaque(true);
        field.setBackground(FIXED_WHITE);
        field.setForeground(FIXED_BLACK);
        field.setCaretColor(FIXED_BLACK);

        // visible border around the typing box + inner padding
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(170, 170, 170), 2, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        return field;
    }

    private JLabel createPillResultLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        label.setForeground(FIXED_BLACK);
        return label;
    }

    private JButton createActionButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /* ========================= SECTION 1 ========================= */

    private JComponent buildDurationFromNowPanel() {
        RoundedPanel panel = new RoundedPanel(35);
        panel.putClientProperty("themed", true);
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(16, 22, 16, 22));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JLabel sectionTitle = new JLabel("D U R A T I O N   F R O M   N O W", SwingConstants.CENTER);
        styleSectionTitle(sectionTitle);

        JPanel titleWrap = new JPanel(new BorderLayout());
        titleWrap.setOpaque(false);
        titleWrap.add(sectionTitle, BorderLayout.CENTER);
        titleWrap.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(titleWrap, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        JLabel hint = new JLabel("DURATION (e.g. 1 DAY 2 HOUR 30 MINUTES):");
        styleSmallLabel(hint);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);

        body.add(hint);
        body.add(Box.createVerticalStrut(10));

        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));

        durationFromNowField = createPillField("1 DAY", 18);

        JLabel arrow = new JLabel(" \u2192 ");
        arrow.setFont(new Font("SansSerif", Font.BOLD, 22));
        arrow.setBorder(new EmptyBorder(0, 14, 0, 14));

        durationFromNowResult = createPillResultLabel("—");

        JButton calcBtn = createActionButton("CALC");
        calcBtn.addActionListener(e -> onCalculateFromNow());

        row.add(wrapAsFixedWhitePill(durationFromNowField));
        row.add(arrow);
        row.add(wrapAsFixedWhitePill(durationFromNowResult));
        row.add(Box.createHorizontalStrut(12));
        row.add(calcBtn);

        body.add(row);
        panel.add(body, BorderLayout.CENTER);

        setFromNowResultPreview();
        return panel;
    }

    private void setFromNowResultPreview() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime result = calculator.addDurationFrom(now, durationFromNowField.getText());
            durationFromNowResult.setText(calculator.formatDateTime(result));
        } catch (Exception ignored) {
            durationFromNowResult.setText("—");
        }
    }

    private void onCalculateFromNow() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime result = calculator.addDurationFrom(now, durationFromNowField.getText());
            durationFromNowResult.setText(calculator.formatDateTime(result));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid Duration", JOptionPane.WARNING_MESSAGE);
        }
    }

    /* ========================= SECTION 2 ========================= */

    private JComponent buildDurationBetweenTimesPanel() {
        RoundedPanel panel = new RoundedPanel(35);
        panel.putClientProperty("themed", true);
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(16, 22, 16, 22));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JLabel sectionTitle = new JLabel("D U R A T I O N   B E T W E E N   T I M E S", SwingConstants.CENTER);
        styleSectionTitle(sectionTitle);

        JPanel titleWrap = new JPanel(new BorderLayout());
        titleWrap.setOpaque(false);
        titleWrap.add(sectionTitle, BorderLayout.CENTER);
        titleWrap.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(titleWrap, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        JLabel hint = new JLabel("START AND END TIME (HH:MM):");
        styleSmallLabel(hint);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);

        body.add(hint);
        body.add(Box.createVerticalStrut(10));

        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));

        startTimeField = createPillField("00:00", 6);

        JLabel bothWays = new JLabel(" \u2194 ");
        bothWays.setFont(new Font("SansSerif", Font.BOLD, 22));
        bothWays.setBorder(new EmptyBorder(0, 10, 0, 10));

        endTimeField = createPillField("00:00", 6);

        JLabel arrow = new JLabel(" \u2192 ");
        arrow.setFont(new Font("SansSerif", Font.BOLD, 22));
        arrow.setBorder(new EmptyBorder(0, 14, 0, 14));

        betweenTimesResult = createPillResultLabel("—");

        JButton calcBtn = createActionButton("CALC");
        calcBtn.addActionListener(e -> onCalculateBetweenTimes());

        row.add(wrapAsFixedWhitePill(startTimeField));
        row.add(bothWays);
        row.add(wrapAsFixedWhitePill(endTimeField));
        row.add(arrow);
        row.add(wrapAsFixedWhitePill(betweenTimesResult));
        row.add(Box.createHorizontalStrut(12));
        row.add(calcBtn);

        body.add(row);
        panel.add(body, BorderLayout.CENTER);

        tryPreviewBetweenTimes();
        return panel;
    }

    private void tryPreviewBetweenTimes() {
        try {
            var res = calculator.durationBetweenTimes(startTimeField.getText(), endTimeField.getText());
            betweenTimesResult.setText(res.toDisplayString());
        } catch (Exception ignored) {
            betweenTimesResult.setText("—");
        }
    }

    private void onCalculateBetweenTimes() {
        try {
            var res = calculator.durationBetweenTimes(startTimeField.getText(), endTimeField.getText());
            betweenTimesResult.setText(res.toDisplayString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid Time", JOptionPane.WARNING_MESSAGE);
        }
    }

    /* ========================= SECTION 3 ========================= */

    private JComponent buildDurationBetweenDatesPanel() {
        RoundedPanel panel = new RoundedPanel(35);
        panel.putClientProperty("themed", true);
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(16, 22, 16, 22));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        JLabel sectionTitle = new JLabel("D U R A T I O N   B E T W E E N   D A T E S", SwingConstants.CENTER);
        styleSectionTitle(sectionTitle);

        JPanel titleWrap = new JPanel(new BorderLayout());
        titleWrap.setOpaque(false);
        titleWrap.add(sectionTitle, BorderLayout.CENTER);
        titleWrap.setBorder(new EmptyBorder(0, 0, 12, 0));
        panel.add(titleWrap, BorderLayout.NORTH);

        JPanel contentRow = new JPanel();
        contentRow.setOpaque(false);
        contentRow.setLayout(new BoxLayout(contentRow, BoxLayout.X_AXIS));

        FixedWhiteRoundedPanel dateBox = new FixedWhiteRoundedPanel(25);
        dateBox.setLayout(new BoxLayout(dateBox, BoxLayout.Y_AXIS));
        dateBox.setBorder(new EmptyBorder(14, 16, 14, 16));
        dateBox.setPreferredSize(new Dimension(320, 170));
        dateBox.setMaximumSize(new Dimension(340, 9999));

        JLabel startLabel = new JLabel("START DATE (DD/MM/YYYY):");
        styleSmallLabel(startLabel);
        startLabel.setForeground(FIXED_BLACK);

        startDateField = createPillField("01/01/2026", 10);

        JLabel endLabel = new JLabel("END DATE (DD/MM/YYYY):");
        styleSmallLabel(endLabel);
        endLabel.setForeground(FIXED_BLACK);

        endDateField = createPillField("01/01/2027", 10);

        dateBox.add(startLabel);
        dateBox.add(Box.createVerticalStrut(6));
        dateBox.add(wrapAsFixedWhitePill(startDateField));
        dateBox.add(Box.createVerticalStrut(12));
        dateBox.add(endLabel);
        dateBox.add(Box.createVerticalStrut(6));
        dateBox.add(wrapAsFixedWhitePill(endDateField));

        FixedWhiteRoundedPanel chooseBox = new FixedWhiteRoundedPanel(25);
        chooseBox.setLayout(new BoxLayout(chooseBox, BoxLayout.Y_AXIS));
        chooseBox.setBorder(new EmptyBorder(14, 16, 14, 16));
        chooseBox.setPreferredSize(new Dimension(240, 170));
        chooseBox.setMaximumSize(new Dimension(260, 9999));

        JLabel chooseTitle = new JLabel("CHOOSE:");
        chooseTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        chooseTitle.setForeground(FIXED_BLACK);

        fullBreakdownBtn = new JRadioButton("FULL BREAKDOWN");
        daysOnlyBtn = new JRadioButton("DAYS ONLY");
        weeksOnlyBtn = new JRadioButton("WEEKS ONLY");
        monthsOnlyBtn = new JRadioButton("MONTHS ONLY");
        yearsOnlyBtn = new JRadioButton("YEARS ONLY");

        ButtonGroup group = new ButtonGroup();
        group.add(fullBreakdownBtn);
        group.add(daysOnlyBtn);
        group.add(weeksOnlyBtn);
        group.add(monthsOnlyBtn);
        group.add(yearsOnlyBtn);

        for (JRadioButton rb : new JRadioButton[]{fullBreakdownBtn, daysOnlyBtn, weeksOnlyBtn, monthsOnlyBtn, yearsOnlyBtn}) {
            rb.setOpaque(false);
            rb.setForeground(FIXED_BLACK);
        }
        fullBreakdownBtn.setSelected(true);

        JButton calcBtn = createActionButton("CALC");
        calcBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        calcBtn.addActionListener(e -> onCalculateBetweenDates());

        chooseBox.add(chooseTitle);
        chooseBox.add(Box.createVerticalStrut(8));
        chooseBox.add(fullBreakdownBtn);
        chooseBox.add(daysOnlyBtn);
        chooseBox.add(weeksOnlyBtn);
        chooseBox.add(monthsOnlyBtn);
        chooseBox.add(yearsOnlyBtn);
        chooseBox.add(Box.createVerticalStrut(10));
        chooseBox.add(calcBtn);

        FixedWhiteRoundedPanel outputBox = new FixedWhiteRoundedPanel(25);
        outputBox.setLayout(new BorderLayout());
        outputBox.setBorder(new EmptyBorder(12, 12, 12, 12));
        outputBox.setPreferredSize(new Dimension(340, 170));
        outputBox.setMaximumSize(new Dimension(420, 9999));

        betweenDatesResultArea = new JTextArea();
        betweenDatesResultArea.setEditable(false);
        betweenDatesResultArea.setLineWrap(true);
        betweenDatesResultArea.setWrapStyleWord(true);
        betweenDatesResultArea.setFont(new Font("SansSerif", Font.BOLD, 14));
        betweenDatesResultArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        betweenDatesResultArea.setOpaque(false);
        betweenDatesResultArea.setForeground(FIXED_BLACK);

        outputBox.add(betweenDatesResultArea, BorderLayout.CENTER);

        JLabel arrow1 = new JLabel(" \u2192 ");
        arrow1.setFont(new Font("SansSerif", Font.BOLD, 22));
        arrow1.setBorder(new EmptyBorder(0, 14, 0, 14));

        JLabel arrow2 = new JLabel(" \u2192 ");
        arrow2.setFont(new Font("SansSerif", Font.BOLD, 22));
        arrow2.setBorder(new EmptyBorder(0, 14, 0, 14));

        contentRow.add(dateBox);
        contentRow.add(arrow1);
        contentRow.add(chooseBox);
        contentRow.add(arrow2);
        contentRow.add(outputBox);

        panel.add(contentRow, BorderLayout.CENTER);

        tryPreviewBetweenDates();
        return panel;
    }

    private void tryPreviewBetweenDates() {
        try {
            LocalDate start = calculator.parseDate(startDateField.getText());
            LocalDate end = calculator.parseDate(endDateField.getText());
            var type = getSelectedDateOutputType();
            betweenDatesResultArea.setText(calculator.durationBetweenDatesDisplay(start, end, type));
        } catch (Exception ignored) {
            betweenDatesResultArea.setText("—");
        }
    }

    private PlanSyncTimeCalculator.DateOutputType getSelectedDateOutputType() {
        if (daysOnlyBtn.isSelected()) return PlanSyncTimeCalculator.DateOutputType.DAYS_ONLY;
        if (weeksOnlyBtn.isSelected()) return PlanSyncTimeCalculator.DateOutputType.WEEKS_ONLY;
        if (monthsOnlyBtn.isSelected()) return PlanSyncTimeCalculator.DateOutputType.MONTHS_ONLY;
        if (yearsOnlyBtn.isSelected()) return PlanSyncTimeCalculator.DateOutputType.YEARS_ONLY;
        return PlanSyncTimeCalculator.DateOutputType.FULL_BREAKDOWN;
    }

    private void onCalculateBetweenDates() {
        try {
            LocalDate start = calculator.parseDate(startDateField.getText());
            LocalDate end = calculator.parseDate(endDateField.getText());
            var type = getSelectedDateOutputType();
            betweenDatesResultArea.setText(calculator.durationBetweenDatesDisplay(start, end, type));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid Date", JOptionPane.WARNING_MESSAGE);
        }
    }

    /* ========================= BOTTOM DECOR ========================= */

    private JComponent buildBottomDecorBar() {
        RoundedPanel bar = new RoundedPanel(35);
        bar.putClientProperty("themed", true);
        bar.setLayout(new BorderLayout());
        bar.setBorder(new EmptyBorder(14, 18, 14, 18));
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));

        JPanel pills = new JPanel(new GridLayout(1, 10, 12, 0));
        pills.setOpaque(false);

        for (int i = 0; i < 10; i++) {
            RoundedPanel p = new RoundedPanel(18);
            p.putClientProperty("themed_base", true);
            p.setPreferredSize(new Dimension(40, 40));
            pills.add(p);
        }

        bar.add(pills, BorderLayout.CENTER);
        return bar;
    }

    /* ========================= ALWAYS-WHITE PILL WRAPPER ========================= */

    private JComponent wrapAsFixedWhitePill(JComponent inner) {
        FixedWhiteRoundedPanel pill = new FixedWhiteRoundedPanel(18);
        pill.setLayout(new BorderLayout());
        pill.add(inner, BorderLayout.CENTER);
        return pill;
    }

    /* ========================= ALWAYS-WHITE ROUNDED PANEL WITH BORDER ========================= */

    private static class FixedWhiteRoundedPanel extends JPanel {
        private final int radius;

        private final int strokeWidth = 2;
        private final Color strokeColor = new Color(190, 190, 190);

        FixedWhiteRoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false); // we paint ourselves
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // background
            g2.setColor(FIXED_WHITE);
            g2.fillRoundRect(0, 0, w, h, radius, radius);

            // border
            g2.setStroke(new BasicStroke(strokeWidth));
            g2.setColor(strokeColor);
            int inset = strokeWidth / 2;
            g2.drawRoundRect(inset, inset, w - strokeWidth, h - strokeWidth, radius, radius);

            g2.dispose();
        }
    }
}