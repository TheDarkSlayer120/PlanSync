package views;

import controller.AppController;

import javax.swing.*;
import java.awt.*;

public class TimeCalculatorView extends JPanel {

    private AppController controller;

    public TimeCalculatorView(AppController controller) {
        this.controller = controller;

        setLayout(new BorderLayout());
        add(new JLabel("Time Calculator Screen", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
