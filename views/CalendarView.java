package views;

import controller.AppController;

import javax.swing.*;
import java.awt.*;

public class CalendarView extends JPanel {

    private AppController controller;

    public CalendarView(AppController controller) {
        this.controller = controller;

        setLayout(new BorderLayout());
        add(new JLabel("Calendar Screen", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
