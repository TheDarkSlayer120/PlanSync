package views;

import controller.AppController;

import javax.swing.*;
import java.awt.*;

public class TimerView extends JPanel {

    private AppController controller;

    public TimerView(AppController controller) {
        this.controller = controller;

        setLayout(new BorderLayout());
        add(new JLabel("Timer Screen", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
