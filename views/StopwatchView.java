package views;

import controller.AppController;
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

        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // ================= TITLE =================
        JLabel title = new JLabel("S T O P W A T C H", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // ================= CENTER =================
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        add(center, BorderLayout.CENTER);

        // ================= DISPLAY PANEL =================
        JPanel displayPanel = new RoundedPanel(40, new Color(210, 210, 210));
        displayPanel.setLayout(new BorderLayout());
        displayPanel.setMaximumSize(new Dimension(800, 120));

        timeLabel = new JLabel("00:00.000", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Monospaced", Font.BOLD, 48));
        displayPanel.add(timeLabel, BorderLayout.CENTER);

        center.add(Box.createVerticalStrut(20));
        center.add(displayPanel);
        center.add(Box.createVerticalStrut(30));

        // ================= BUTTON ROW =================
        JPanel buttonRow = new JPanel(new GridLayout(1, 4, 20, 0));
        buttonRow.setOpaque(false);
        buttonRow.setMaximumSize(new Dimension(800, 50));

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
        JPanel lapPanel = new RoundedPanel(40, new Color(200, 200, 200));
        lapPanel.setLayout(new BorderLayout());
        lapPanel.setMaximumSize(new Dimension(800, 250));

        lapModel = new DefaultListModel<>();
        JList<String> lapList = new JList<>(lapModel);
        lapList.setFont(new Font("SansSerif", Font.BOLD, 16));

        JScrollPane scrollPane = new JScrollPane(lapList);
        scrollPane.setBorder(null);

        lapPanel.add(scrollPane, BorderLayout.CENTER);
        center.add(lapPanel);

        // ================= BUTTON ACTIONS =================
        startButton.addActionListener(e -> stopwatch.start());
        stopButton.addActionListener(e -> stopwatch.stop());
        resetButton.addActionListener(e -> stopwatch.reset());
        lapButton.addActionListener(e -> stopwatch.lap());

        // ================= LISTENER =================
        stopwatch.setListener(new PlanSyncStopwatch.StopwatchListener() {

            @Override
            public void onTick(long elapsedMillis) {
                SwingUtilities.invokeLater(() ->
                        timeLabel.setText(
                                PlanSyncStopwatch.formatTime(elapsedMillis)
                        )
                );
            }

            @Override
            public void onLapRecorded(String lapText) {
                SwingUtilities.invokeLater(() ->
                        lapModel.addElement(lapText)
                );
            }

            @Override
            public void onReset() {
                SwingUtilities.invokeLater(lapModel::clear);
            }

            @Override
            public void onStateChanged(boolean running) {
                SwingUtilities.invokeLater(() -> {
                    startButton.setEnabled(!running);
                    stopButton.setEnabled(running);
                    lapButton.setEnabled(running);
                });
            }
        });
    }

    private JButton createButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBackground(new Color(180, 180, 180));
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        return b;
    }

    // ================= ROUNDED PANEL =================
    static class RoundedPanel extends JPanel {

        private final int radius;
        private final Color bgColor;

        public RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}