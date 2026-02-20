package views;

import controller.AppController;
import model.PlanSyncTimer;
import model.TimerListener;

import javax.swing.*;
import java.awt.*;

public class TimerView extends JPanel {

    private final AppController controller;
    private final PlanSyncTimer timer;

    private final JLabel timeLabel;
    private final JButton startButton;
    private final JButton stopButton;
    private final JButton resetButton;

    // Custom input fields
    private final JTextField hoursField;
    private final JTextField minutesField;
    private final JTextField secondsField;

    public TimerView(AppController controller, PlanSyncTimer timer) {
        this.controller = controller;
        this.timer = timer;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ===== TITLE =====
        JLabel title = new JLabel("TIMER", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // ===== MAIN VERTICAL LAYOUT =====
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        add(center, BorderLayout.CENTER);

        // ===== BIG DISPLAY PANEL =====
        JPanel displayPanel = new JPanel();
        displayPanel.setLayout(new BorderLayout());
        displayPanel.setBackground(new Color(230, 230, 230));
        displayPanel.setPreferredSize(new Dimension(400, 120));
        displayPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        displayPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        timeLabel = new JLabel("00:05:00", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Monospaced", Font.BOLD, 36));
        displayPanel.add(timeLabel, BorderLayout.CENTER);
        center.add(displayPanel);
        center.add(Box.createVerticalStrut(20));

        // ===== CONTROL BUTTONS =====
        JPanel buttonRow = new JPanel(new GridLayout(1, 3, 20, 0));
        buttonRow.setOpaque(false);
        buttonRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        resetButton = new JButton("RESET");
        stopButton = new JButton("STOP");
        startButton = new JButton("START");
        styleMainButton(resetButton);
        styleMainButton(stopButton);
        styleMainButton(startButton);

        buttonRow.add(resetButton);
        buttonRow.add(stopButton);
        buttonRow.add(startButton);
        center.add(buttonRow);
        center.add(Box.createVerticalStrut(20));

        // ===== PRESET BUTTONS =====
        JPanel presetGrid = new JPanel(new GridLayout(3, 2, 5, 5));
        presetGrid.setOpaque(false);
        presetGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        presetGrid.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        addPresetButton(presetGrid, "00:01:00", 60);
        addPresetButton(presetGrid, "00:05:00", 5 * 60);
        addPresetButton(presetGrid, "00:10:00", 10 * 60);
        addPresetButton(presetGrid, "00:15:00", 15 * 60);
        addPresetButton(presetGrid, "00:30:00", 30 * 60);
        addPresetButton(presetGrid, "01:00:00", 60 * 60);

        center.add(presetGrid);
        center.add(Box.createVerticalStrut(20));

        // ===== CUSTOM INPUT BAR =====
        JPanel customBar = new JPanel();
        customBar.setBackground(new Color(230, 230, 230));
        customBar.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        customBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        customBar.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 5));

        hoursField = createTimeField("00");
        minutesField = createTimeField("00");
        secondsField = createTimeField("00");

        customBar.add(new JLabel("Hours:", SwingConstants.CENTER));
        customBar.add(hoursField);
        customBar.add(new JLabel("Minutes:", SwingConstants.CENTER));
        customBar.add(minutesField);
        customBar.add(new JLabel("Seconds:", SwingConstants.CENTER));
        customBar.add(secondsField);

        JButton setButton = new JButton("SET");
        styleMainButton(setButton);
        setButton.setPreferredSize(new Dimension(70, 28));
        setButton.setBackground(new Color(250, 250, 250));
        customBar.add(setButton);

        center.add(customBar);

        // ===== BUTTON ACTIONS =====
        startButton.addActionListener(e -> {
            if (timer.isPaused()) {
                timer.resume();
            } else {
                timer.start();
            }
        });
        stopButton.addActionListener(e -> timer.stop());
        resetButton.addActionListener(e -> timer.reset());
        setButton.addActionListener(e -> applyCustomTime());

        // ===== TIMER LISTENER =====
        timer.setListener(new TimerListener() {
            @Override
            public void onTick(int remainingSeconds) {
                SwingUtilities.invokeLater(() ->
                        timeLabel.setText(PlanSyncTimer.formatTime(remainingSeconds)));
            }

            @Override
            public void onFinished() {
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(TimerView.this, "Time's up!", "Timer", JOptionPane.INFORMATION_MESSAGE));
            }

            @Override
            public void onStateChanged(boolean running, boolean paused) {
                SwingUtilities.invokeLater(() -> {
                    startButton.setText(paused ? "RESUME" : "START");
                    startButton.setEnabled(!running || paused);
                    stopButton.setEnabled(running);
                });
            }
        });

        // Default to 5 minutes
        timer.setDuration(5 * 60);
    }

    private void styleMainButton(JButton b) {
        b.setFocusPainted(false);
        b.setBackground(new Color(230, 230, 230));
        b.setForeground(Color.BLACK);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    }

    private void addPresetButton(JPanel parent, String label, int seconds) {
        JButton b = new JButton(label);
        b.setFocusPainted(false);
        b.setBackground(new Color(240, 240, 240));
        b.setForeground(Color.BLACK);
        b.setFont(new Font("Monospaced", Font.PLAIN, 14));
        b.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        b.addActionListener(e -> timer.setDuration(seconds));
        parent.add(b);
    }

    private JTextField createTimeField(String text) {
        JTextField field = new JTextField(text, 2);
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setFont(new Font("Monospaced", Font.BOLD, 16));
        field.setPreferredSize(new Dimension(40, 28));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(2, 5, 2, 5)));
        return field;
    }

    private void applyCustomTime() {
        try {
            int h = Integer.parseInt(hoursField.getText().trim());
            int m = Integer.parseInt(minutesField.getText().trim());
            int s = Integer.parseInt(secondsField.getText().trim());
            
            if (h < 0 || m < 0 || s < 0 || m > 59 || s > 59) {
                JOptionPane.showMessageDialog(this, "Invalid time format.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int total = h * 3600 + m * 60 + s;
            if (total <= 0) {
                JOptionPane.showMessageDialog(this, "Time must be greater than 0.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            timer.setDuration(total);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter valid numbers (0-23 for H, 0-59 for M/S).", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }
}
