package views;

import controller.AppController;

import javax.swing.*;
import java.awt.*;

public class ActiveTasksView extends JPanel {

    private AppController controller;

    public ActiveTasksView(AppController controller) {
        this.controller = controller;

        setLayout(new BorderLayout());
        add(new JLabel("Active Tasks Screen", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
