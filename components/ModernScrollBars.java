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
 * File purpose: This class supports the ModernScrollBars part of PlanSync and documents the main responsibilities of the file.
 */

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public final class ModernScrollBars {

    private ModernScrollBars() {}

    // Section: Handle the logic for apply.
    public static void apply(JScrollPane sp, boolean darkMode, Color accent) {
        if (sp == null) return;

        sp.setWheelScrollingEnabled(true);
        sp.putClientProperty("JScrollPane.fastWheelScrolling", Boolean.TRUE);

        JScrollBar vsb = sp.getVerticalScrollBar();
        JScrollBar hsb = sp.getHorizontalScrollBar();

        if (vsb != null) styleBar(vsb, darkMode, accent, true);
        if (hsb != null) styleBar(hsb, darkMode, accent, false);

        // Clean bottom-right corner
        sp.setCorner(JScrollPane.LOWER_RIGHT_CORNER, new JPanel());
    }

    // Section: Handle the logic for style bar.
    private static void styleBar(JScrollBar bar, boolean darkMode, Color accent, boolean vertical) {
        bar.setOpaque(false);
        bar.setUnitIncrement(24);
        bar.setBlockIncrement(96);

        // Slim, modern thickness
        bar.setPreferredSize(vertical
                ? new Dimension(10, Integer.MAX_VALUE)
                // Section: Handle the logic for dimension.
                : new Dimension(Integer.MAX_VALUE, 10));

        Color thumb = (accent != null)
                ? new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), darkMode ? 190 : 160)
                : (darkMode ? new Color(255, 255, 255, 160) : new Color(0, 0, 0, 120));

        Color track = new Color(0, 0, 0, 0);

        bar.putClientProperty("modern.thumb", thumb);
        bar.putClientProperty("modern.track", track);

        bar.setUI(new BasicScrollBarUI() {

            @Override protected void configureScrollBarColors() {}

            @Override protected JButton createDecreaseButton(int orientation) { return zeroButton(); }
            @Override protected JButton createIncreaseButton(int orientation) { return zeroButton(); }

            // Section: Handle the logic for zero button.
            private JButton zeroButton() {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0, 0));
                b.setMinimumSize(new Dimension(0, 0));
                b.setMaximumSize(new Dimension(0, 0));
                b.setOpaque(false);
                b.setContentAreaFilled(false);
                b.setBorder(BorderFactory.createEmptyBorder());
                return b;
            }

            @Override
            // Section: Handle the logic for paint track.
            protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
                Color t = (Color) bar.getClientProperty("modern.track");
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(t != null ? t : new Color(0,0,0,0));
                g2.fillRect(r.x, r.y, r.width, r.height);
                g2.dispose();
            }

            @Override
            // Section: Handle the logic for paint thumb.
            protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
                if (r.isEmpty() || !bar.isEnabled()) return;

                Color thumb = (Color) bar.getClientProperty("modern.thumb");
                if (thumb == null) thumb = darkMode ? new Color(255,255,255,160) : new Color(0,0,0,120);

                int pad = 2;
                int arc = 12;

                int x = r.x + pad;
                int y = r.y + pad;
                int w = Math.max(1, r.width - (pad * 2));
                int h = Math.max(1, r.height - (pad * 2));

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(thumb);
                g2.fillRoundRect(x, y, w, h, arc, arc);

                g2.setColor(new Color(0, 0, 0, darkMode ? 85 : 55));
                g2.drawRoundRect(x, y, w, h, arc, arc);

                g2.dispose();
            }
        });
    }
}
