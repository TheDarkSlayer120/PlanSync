package views;

import controller.AppController;

import javax.swing.*;
import java.awt.*;

public class RecurringTasksView extends JPanel {

    private AppController controller;

    public RecurringTasksView(AppController controller) {
        this.controller = controller;

        setLayout(new BorderLayout());
        add(new JLabel("Recurring Tasks Screen", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
