package views;

import controller.AppController;

import javax.swing.*;
import java.awt.*;

public class CompletedTasksView extends JPanel {

    private AppController controller;

    public CompletedTasksView(AppController controller) {
        this.controller = controller;

        setLayout(new BorderLayout());
        add(new JLabel("Completed Tasks Screen", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
