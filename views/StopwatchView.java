package views;

import controller.AppController;

import javax.swing.*;
import java.awt.*;

public class StopwatchView extends JPanel {

    private AppController controller;

    public StopwatchView(AppController controller) {
        this.controller = controller;

        setLayout(new BorderLayout());
        add(new JLabel("Stopwatch Screen", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
