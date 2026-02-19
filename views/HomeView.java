package views;

import controller.AppController;

import javax.swing.*;
import java.awt.*;

public class HomeView extends JPanel {

    private AppController controller;

    public HomeView(AppController controller) {
        this.controller = controller;

        setLayout(new BorderLayout());
        add(new JLabel("Home Screen", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
