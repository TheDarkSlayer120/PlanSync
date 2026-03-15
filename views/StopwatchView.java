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
 * File purpose: This class supports the StopwatchView part of PlanSync and documents the main responsibilities of the file.
 */

import controller.AppController;
import components.ModernScrollBars;
import model.PlanSyncStopwatch;

import javax.swing.*;
import java.awt.*;

public class StopwatchView extends JPanel {

    private final PlanSyncStopwatch stopwatch;

    private final JLabel timeLabel;
    private final JButton startButton;
    private final JButton stopButton;
    private final JButton resetButton;
    private final JButton lapButton;

    private final DefaultListModel<String> lapModel;

    public StopwatchView(AppController controller) {

        this.stopwatch = new PlanSyncStopwatch();

        // Section: Update the state used to layout.
        setLayout(new BorderLayout());
        // Section: Update the state used to background.
        setBackground(Color.WHITE);

        // ================= TITLE =================
        JLabel title = new JLabel("S T O P W A T C H", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        title.putClientProperty("on_base", true);
        // Section: Add the data or behavior needed to add.
        add(title, BorderLayout.NORTH);

        // ================= CENTER =================
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        // Section: Add the data or behavior needed to add.
        add(center, BorderLayout.CENTER);

        // ================= DISPLAY PANEL =================
        JPanel displayPanel = new RoundedPanel(30);
        displayPanel.putClientProperty("themed", true);
        displayPanel.setLayout(new BorderLayout());
        displayPanel.setPreferredSize(new Dimension(400, 120));
        displayPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        displayPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        timeLabel = new JLabel("00:00.000", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Monospaced", Font.BOLD, 48));
        displayPanel.add(timeLabel, BorderLayout.CENTER);

        center.add(Box.createVerticalStrut(20));
        center.add(displayPanel);
        center.add(Box.createVerticalStrut(30));

        // ================= BUTTON ROW =================
        JPanel buttonRow = new JPanel(new GridLayout(1, 4, 20, 0));
        buttonRow.setOpaque(false);
        buttonRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

        resetButton = createButton("RESET");
        stopButton = createButton("STOP");
        lapButton = createButton("LAP");
        startButton = createButton("START");

        buttonRow.add(resetButton);
        buttonRow.add(stopButton);
        buttonRow.add(lapButton);
        buttonRow.add(startButton);

        center.add(buttonRow);
        center.add(Box.createVerticalStrut(30));

        // ================= LAP PANEL =================
        JPanel lapPanel = new RoundedPanel(40);
        lapPanel.putClientProperty("themed", true);
        lapPanel.setLayout(new BorderLayout());
        lapPanel.setPreferredSize(new Dimension(400, 250));
        lapPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        lapPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        lapModel = new DefaultListModel<>();

        JList<String> lapList = new JList<>(lapModel);
        lapList.setFont(new Font("SansSerif", Font.BOLD, 16));
        lapList.setOpaque(false);
        lapList.setBackground(new Color(0, 0, 0, 0));

        lapList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setOpaque(false);

                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(lapList);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        ModernScrollBars.apply(scrollPane, false, new Color(95, 125, 195));

        lapPanel.add(scrollPane, BorderLayout.CENTER);
        center.add(lapPanel);

        // ================= BUTTON ACTIONS =================
        startButton.addActionListener(e -> stopwatch.start());
        stopButton.addActionListener(e -> stopwatch.stop());
        resetButton.addActionListener(e -> stopwatch.reset());
        lapButton.addActionListener(e -> stopwatch.lap());

        // ================= LISTENER =================
        // Section: Handle the logic for refresh button state.
        refreshButtonState(resetButton);
        // Section: Handle the logic for refresh button state.
        refreshButtonState(stopButton);
        // Section: Handle the logic for refresh button state.
        refreshButtonState(lapButton);
        // Section: Handle the logic for refresh button state.
        refreshButtonState(startButton);

        stopwatch.setListener(new PlanSyncStopwatch.StopwatchListener() {
            @Override
            // Section: Handle the logic for on tick.
            public void onTick(long elapsedMillis) {
                SwingUtilities.invokeLater(() ->
                        timeLabel.setText(PlanSyncStopwatch.formatTime(elapsedMillis))
                );
            }

            @Override
            // Section: Handle the logic for on lap recorded.
            public void onLapRecorded(String lapText) {
                SwingUtilities.invokeLater(() -> lapModel.addElement(lapText));
            }

            @Override
            // Section: Handle the logic for on reset.
            public void onReset() {
                SwingUtilities.invokeLater(lapModel::clear);
            }

            @Override
            // Section: Handle the logic for on state changed.
            public void onStateChanged(boolean running) {
                SwingUtilities.invokeLater(() -> {
                    startButton.setEnabled(!running);
                    stopButton.setEnabled(running);
                    lapButton.setEnabled(running);
                });
            }
        });
    }

    // Section: Build and return the elements needed to button.
    private JButton createButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 18));
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false);
        b.putClientProperty("JButton.buttonType", "square");
        b.addPropertyChangeListener("enabled", e -> refreshButtonState(b));
        // Section: Handle the logic for refresh button state.
        refreshButtonState(b);
        return b;
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
}
