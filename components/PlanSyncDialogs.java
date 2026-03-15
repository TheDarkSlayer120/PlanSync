package components;


/*
 *  ██████╗ ██╗      █████╗ ███╗   ██╗███████╗██╗   ██╗███╗   ██╗ ██████╗
 *  ██╔══██╗██║     ██╔══██╗████╗  ██║██╔════╝╚██╗ ██╔╝████╗  ██║██╔════╝
 *  ██████╔╝██║     ███████║██╔██╗ ██║███████╗ ╚████╔╝ ██╔██╗ ██║██║     
 *  ██╔═══╝ ██║     ██╔══██║██║╚██╗██║╚════██║  ╚██╔╝  ██║╚██╗██║██║     
 *  ██║     ███████╗██║  ██║██║ ╚████║███████║   ██║   ██║ ╚████║╚██████╗
 *  ╚═╝     ╚══════╝╚═╝  ╚═╝╚═╝  ╚═══╝╚══════╝   ╚═╝   ╚═╝  ╚═══╝ ╚═════╝
 *
 *  PlanSync source guide
 *  - This file includes a short header describing the class or interface purpose.
 *  - Method comments mark the responsibility of each section so the flow is easier to follow.
 */
/**
 * File purpose: This class supports the PlanSyncDialogs part of PlanSync and documents the main responsibilities of the file.
 */

import controller.AppController;
import views.RoundedPanel;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Modern, theme-aware dialogs used instead of JOptionPane.
 * Now includes a black rounded border so the popup doesn't blend into the background.
 */
public final class PlanSyncDialogs {

    private PlanSyncDialogs() {}

    // Section: Handle the logic for confirm.
    public static boolean confirm(Component parent, AppController controller, String title, String message) {
        return showDialog(parent, controller, title, message, true);
    }

    // Section: Handle the logic for alert.
    public static void alert(Component parent, AppController controller, String title, String message) {
        // Section: Handle UI flow to dialog.
        showDialog(parent, controller, title, message, false);
    }

    private static boolean showDialog(
            Component parent,
            AppController controller,
            String title,
            String message,
            boolean confirm
    ) {

        Window owner = parent != null ? SwingUtilities.getWindowAncestor(parent) : null;
        final JDialog dialog = new JDialog(owner);
        dialog.setModal(true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));

        final int radius = 28;

        // Card
        RoundedPanel card = new RoundedPanel(radius);

        // important: make sure background paints
        card.setOpaque(true);

        // Use your existing theme system to color the card
        card.putClientProperty("themed", true);

        // ✅ Rounded black border so it doesn't blend into background
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedStrokeBorder(radius, 2, Color.BLACK),
                BorderFactory.createEmptyBorder(18, 22, 18, 22)
        ));

        card.setLayout(new BorderLayout(0, 14));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.BLACK);

        JLabel msgLabel = new JLabel(wrap(message), SwingConstants.CENTER);
        msgLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        msgLabel.setForeground(Color.BLACK);

        JPanel center = new JPanel(new BorderLayout());
        center.putClientProperty("themed", true);
        center.setOpaque(false);
        center.add(msgLabel, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(1, confirm ? 2 : 1, 14, 0));
        buttons.putClientProperty("themed", true);
        buttons.setOpaque(false);

        final boolean[] result = new boolean[]{false};

        if (confirm) {
            JButton noBtn = button("NO");
            JButton yesBtn = button("YES");

            noBtn.addActionListener(e -> {
                result[0] = false;
                dialog.dispose();
            });
            yesBtn.addActionListener(e -> {
                result[0] = true;
                dialog.dispose();
            });

            buttons.add(noBtn);
            buttons.add(yesBtn);
        } else {
            JButton okBtn = button("OK");
            okBtn.addActionListener(e -> dialog.dispose());
            buttons.add(okBtn);
        }

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(center, BorderLayout.CENTER);
        card.add(buttons, BorderLayout.SOUTH);

        // Outer panel provides padding and a subtle dim
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(true);
        outer.setBackground(new Color(0, 0, 0, 110));
        outer.add(card);

        dialog.setContentPane(outer);

        // Apply the app theme to match the rest of the UI
        if (controller != null) {
            ThemeApplier.apply(dialog, controller.getSettings());
        }

        dialog.pack();
        dialog.setMinimumSize(new Dimension(Math.max(420, dialog.getWidth()), dialog.getHeight()));
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);

        return result[0];
    }

    // Section: Handle the logic for button.
    private static JButton button(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(0, 44));
        b.setFont(new Font("SansSerif", Font.BOLD, 16));
        b.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        return b;
    }

    // Section: Handle the logic for wrap.
    private static String wrap(String text) {
        if (text == null) return "";
        return "<html><div style='text-align:center; width:340px;'>" +
                escape(text) +
                "</div></html>";
    }

    // Section: Handle the logic for escape.
    private static String escape(String s) {
        return s
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br/>");
    }

    /**
     * A rounded outline border (black) that matches the card's corner radius.
     * This avoids the "square border on a rounded panel" look.
     */
    private static final class RoundedStrokeBorder extends AbstractBorder {
        private final int radius;
        private final int stroke;
        private final Color color;

        // Section: Handle the logic for rounded stroke border.
        private RoundedStrokeBorder(int radius, int stroke, Color color) {
            this.radius = radius;
            this.stroke = Math.max(1, stroke);
            this.color = color;
        }

        @Override
        // Section: Return the data used to border insets.
        public Insets getBorderInsets(Component c) {
            // keep some room for the stroke so it doesn't get clipped
            int s = stroke;
            return new Insets(s, s, s, s);
        }

        @Override
        // Section: Return the data used to border insets.
        public Insets getBorderInsets(Component c, Insets insets) {
            Insets i = getBorderInsets(c);
            insets.top = i.top;
            insets.left = i.left;
            insets.bottom = i.bottom;
            insets.right = i.right;
            return insets;
        }

        @Override
        // Section: Handle the logic for paint border.
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(stroke));

                // inset by half stroke so the outline is fully visible
                float inset = stroke / 2f;
                float w = width - stroke;
                float h = height - stroke;

                Shape rr = new RoundRectangle2D.Float(
                        x + inset,
                        y + inset,
                        w,
                        h,
                        radius * 2f,
                        radius * 2f
                );
                g2.draw(rr);
            } finally {
                g2.dispose();
            }
        }
    }
}
