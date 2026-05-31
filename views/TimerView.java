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
 * File purpose: This class supports the TimerView part of PlanSync and documents the main responsibilities of the file.
 */

import controller.AppController;
import components.PlanSyncDialogs;
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

    private final JTextField hoursField;
    private final JTextField minutesField;
    private final JTextField secondsField;

    public TimerView(AppController controller, PlanSyncTimer timer) {
        this.controller = controller;
        this.timer = timer;

        // Section: Update the state used to layout.
        setLayout(new BorderLayout());
        // Section: Update the state used to background.
        setBackground(Color.WHITE);

        // ===== TITLE =====
        JLabel title = new JLabel("T I M E R", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        title.putClientProperty("on_base", true);
        // Section: Add the data or behavior needed to add.
        add(title, BorderLayout.NORTH);

        // ===== MAIN VERTICAL LAYOUT =====
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        // Section: Add the data or behavior needed to add.
        add(center, BorderLayout.CENTER);

        // ===== BIG DISPLAY PANEL =====
        JPanel displayPanel = new RoundedPanel(30);
        displayPanel.setLayout(new BorderLayout());
        displayPanel.putClientProperty("themed", true);
        displayPanel.setPreferredSize(new Dimension(400, 120));
        displayPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        displayPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        timeLabel = new JLabel("00:01:00", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Monospaced", Font.BOLD, 48));
        displayPanel.add(timeLabel, BorderLayout.CENTER);

        center.add(displayPanel);
        center.add(Box.createVerticalStrut(20));

        // ===== CONTROL BUTTONS =====
        JPanel buttonRow = new JPanel(new GridLayout(1, 3, 20, 0));
        buttonRow.setOpaque(false);
        buttonRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

        resetButton = new JButton("RESET");
        stopButton = new JButton("STOP");
        startButton = new JButton("START");
        // Section: Handle the logic for style main button.
        styleMainButton(resetButton);
        // Section: Handle the logic for style main button.
        styleMainButton(stopButton);
        // Section: Handle the logic for style main button.
        styleMainButton(startButton);
        // Section: Handle the logic for refresh button state.
        refreshButtonState(resetButton);
        // Section: Handle the logic for refresh button state.
        refreshButtonState(stopButton);
        // Section: Handle the logic for refresh button state.
        refreshButtonState(startButton);

        buttonRow.add(resetButton);
        buttonRow.add(stopButton);
        buttonRow.add(startButton);

        center.add(buttonRow);
        center.add(Box.createVerticalStrut(25));

        // ===== PRESET BUTTONS =====
        JPanel presetGrid = new JPanel(new GridLayout(3, 2, 14, 12));
        presetGrid.setOpaque(false);
        presetGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 170));
        presetGrid.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        // Section: Add the data or behavior needed to preset button.
        addPresetButton(presetGrid, "00:01:00", 60);
        // Section: Add the data or behavior needed to preset button.
        addPresetButton(presetGrid, "00:05:00", 5 * 60);
        // Section: Add the data or behavior needed to preset button.
        addPresetButton(presetGrid, "00:10:00", 10 * 60);
        // Section: Add the data or behavior needed to preset button.
        addPresetButton(presetGrid, "00:15:00", 15 * 60);
        // Section: Add the data or behavior needed to preset button.
        addPresetButton(presetGrid, "00:30:00", 30 * 60);
        // Section: Add the data or behavior needed to preset button.
        addPresetButton(presetGrid, "01:00:00", 60 * 60);

        center.add(presetGrid);
        center.add(Box.createVerticalStrut(30));

        // ===== CUSTOM INPUT BAR (CENTERED) =====
        JPanel customBar = new RoundedPanel(30);
        customBar.putClientProperty("themed", true);
        customBar.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        customBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        customBar.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 10, 0, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        hoursField = createTimeField("00");
        minutesField = createTimeField("00");
        secondsField = createTimeField("00");

        JLabel hLabel = new JLabel("Hours:");
        JLabel mLabel = new JLabel("Minutes:");
        JLabel sLabel = new JLabel("Seconds:");
        Font labelFont = new Font("SansSerif", Font.BOLD, 18);
        hLabel.setFont(labelFont);
        mLabel.setFont(labelFont);
        sLabel.setFont(labelFont);

        gbc.gridx = 0; customBar.add(hLabel, gbc);
        gbc.gridx = 1; customBar.add(hoursField, gbc);

        gbc.gridx = 2; customBar.add(mLabel, gbc);
        gbc.gridx = 3; customBar.add(minutesField, gbc);

        gbc.gridx = 4; customBar.add(sLabel, gbc);
        gbc.gridx = 5; customBar.add(secondsField, gbc);

        JButton setButton = new JButton("SET");
        // Section: Handle the logic for style main button.
        styleMainButton(setButton);
        // Section: Handle the logic for refresh button state.
        refreshButtonState(setButton);
        setButton.setPreferredSize(new Dimension(90, 32));
        gbc.gridx = 6; customBar.add(setButton, gbc);

        center.add(customBar);

        // ===== BUTTON ACTIONS =====
        startButton.addActionListener(e -> {
            if (timer.isPaused()) timer.resume();
            else timer.start();
        });

        stopButton.addActionListener(e -> timer.stop());
        resetButton.addActionListener(e -> timer.reset());
        setButton.addActionListener(e -> applyCustomTime());

        // ===== TIMER LISTENER =====
        timer.setListener(new TimerListener() {
            @Override
            // Section: Handle the logic for on tick.
            public void onTick(int remainingSeconds) {
                SwingUtilities.invokeLater(() ->
                        timeLabel.setText(PlanSyncTimer.formatTime(remainingSeconds)));
            }

            @Override
            // Section: Handle the logic for on finished.
            public void onFinished() {
                SwingUtilities.invokeLater(() ->
                        PlanSyncDialogs.alert(
                                TimerView.this,
                                controller,
                                "Timer",
                                "Time's up!"
                        )
                );
            }

            @Override
            // Section: Handle the logic for on state changed.
            public void onStateChanged(boolean running, boolean paused) {
                SwingUtilities.invokeLater(() -> {
                    startButton.setText(paused ? "RESUME" : "START");
                    startButton.setEnabled(!running || paused);
                    stopButton.setEnabled(running);
                });
            }
        });

        timer.setDuration(1 * 60);
    }

    // Section: Handle the logic for style main button.
    private void styleMainButton(JButton b) {
        b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 18));
        b.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false);
        b.putClientProperty("JButton.buttonType", "square");
        b.addPropertyChangeListener("enabled", e -> refreshButtonState(b));
        // Section: Handle the logic for refresh button state.
        refreshButtonState(b);
    }

    // Section: Handle the logic for refresh button state.
    private void refreshButtonState(JButton b) {
        if (b.isEnabled()) {
            b.setBackground(new Color(95, 125, 195));
            b.setForeground(Color.BLACK);
        } else {
            b.setBackground(new Color(160, 176, 220));
            b.setForeground(new Color(72, 84, 120));
        }
        b.repaint();
    }

    // Section: Add the data or behavior needed to preset button.
    private void addPresetButton(JPanel parent, String label, int seconds) {
        JButton b = new JButton(label);
        b.setFocusPainted(false);
        b.setBackground(new Color(95, 125, 195));
        b.setForeground(Color.BLACK);
        b.setFont(new Font("Monospaced", Font.BOLD, 18));
        b.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        b.addActionListener(e -> timer.setDuration(seconds));
        parent.add(b);
    }

    // Section: Build and return the elements needed to time field.
    private JTextField createTimeField(String text) {
        JTextField field = new JTextField(text, 2);
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setFont(new Font("Monospaced", Font.BOLD, 18));
        field.setPreferredSize(new Dimension(50, 32));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        return field;
    }

    // Section: Handle the logic for apply custom time.
    private void applyCustomTime() {
        try {
            int h = Integer.parseInt(hoursField.getText().trim());
            int m = Integer.parseInt(minutesField.getText().trim());
            int s = Integer.parseInt(secondsField.getText().trim());

            if (h < 0 || m < 0 || s < 0 || m > 59 || s > 59) {
                PlanSyncDialogs.alert(this, controller, "Error", "Invalid time format.");
                return;
            }

            int total = h * 3600 + m * 60 + s;
            if (total <= 0) {
                PlanSyncDialogs.alert(this, controller, "Error", "Time must be greater than 0.");
                return;
            }

            timer.setDuration(total);

        // Section: Handle the logic for catch.
        } catch (NumberFormatException ex) {
            PlanSyncDialogs.alert(this, controller, "Error", "Enter valid numbers (00-59 for Minutes/Seconds).");
        }
    }
}
