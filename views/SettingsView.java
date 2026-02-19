package views;

import controller.AppController;

import javax.swing.*;
import java.awt.*;

public class SettingsView extends JPanel {

    private AppController controller;

    public SettingsView(AppController controller) {
        this.controller = controller;

        setLayout(new BorderLayout());
        add(new JLabel("Settings Screen", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
