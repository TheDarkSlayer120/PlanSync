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
        setBackground(Color.WHITE);

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
        JPanel displayPanel = new RoundedPanel(30);
        displayPanel.putClientProperty("themed", true);
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
        JPanel lapPanel = new RoundedPanel(40);
        lapPanel.putClientProperty("themed", true);
        lapPanel.setLayout(new BorderLayout());
        lapPanel.setMaximumSize(new Dimension(800, 250));
        lapPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        lapModel = new DefaultListModel<>();

        JList<String> lapList = new JList<>(lapModel);
        lapList.setFont(new Font("SansSerif", Font.BOLD, 16));
        lapList.setOpaque(false);
        lapList.setBackground(new Color(0, 0, 0, 0));

        // ✅ CENTER LAP TEXT PROPERLY
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
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        return b;
    }

}