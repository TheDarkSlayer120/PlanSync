package util;


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
 * File purpose: This class supports the SymbolIcon part of PlanSync and documents the main responsibilities of the file.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class SymbolIcon implements Icon {
    public enum Kind {
        TIMER, STOPWATCH, CALCULATOR, CALENDAR, ADD, CHECK, RECURRING, SETTINGS
    }

    private final Kind kind;
    private final int size;
    private final float strokeWidth;

    public SymbolIcon(Kind kind, int size) {
        // Section: Handle the logic for this.
        this(kind, size, Math.max(1.8f, size / 12f));
    }

    public SymbolIcon(Kind kind, int size, float strokeWidth) {
        this.kind = kind;
        this.size = size;
        this.strokeWidth = strokeWidth;
    }

    @Override
    // Section: Return the data used to icon width.
    public int getIconWidth() {
        return size;
    }

    @Override
    // Section: Return the data used to icon height.
    public int getIconHeight() {
        return size;
    }

    @Override
    // Section: Handle the logic for paint icon.
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.translate(x, y);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(resolveColor(c));
            switch (kind) {
                case TIMER -> paintTimer(g2);
                case STOPWATCH -> paintStopwatch(g2);
                case CALCULATOR -> paintCalculator(g2);
                case CALENDAR -> paintCalendar(g2);
                case ADD -> paintAdd(g2);
                case CHECK -> paintCheck(g2);
                case RECURRING -> paintRecurring(g2);
                case SETTINGS -> paintSettings(g2);
            }
        } finally {
            g2.dispose();
        }
    }

    // Section: Handle the logic for resolve color.
    private Color resolveColor(Component c) {
        if (c == null) return Color.BLACK;
        if (!c.isEnabled()) return UIManager.getColor("Button.disabledText") != null
                ? UIManager.getColor("Button.disabledText")
                : Color.GRAY;
        return c.getForeground() != null ? c.getForeground() : Color.BLACK;
    }

    // Section: Handle the logic for paint timer.
    private void paintTimer(Graphics2D g2) {
        double s = size;
        g2.draw(new Ellipse2D.Double(s * 0.16, s * 0.20, s * 0.68, s * 0.68));
        g2.draw(new Line2D.Double(s * 0.40, s * 0.10, s * 0.60, s * 0.10));
        g2.draw(new Line2D.Double(s * 0.50, s * 0.10, s * 0.50, s * 0.20));
        g2.draw(new Line2D.Double(s * 0.50, s * 0.54, s * 0.66, s * 0.38));
        g2.draw(new Line2D.Double(s * 0.50, s * 0.54, s * 0.50, s * 0.32));
    }

    // Section: Handle the logic for paint stopwatch.
    private void paintStopwatch(Graphics2D g2) {
        double s = size;
        g2.draw(new Ellipse2D.Double(s * 0.16, s * 0.24, s * 0.68, s * 0.60));
        g2.draw(new Line2D.Double(s * 0.42, s * 0.10, s * 0.58, s * 0.10));
        g2.draw(new Line2D.Double(s * 0.50, s * 0.10, s * 0.50, s * 0.24));
        g2.draw(new Line2D.Double(s * 0.62, s * 0.14, s * 0.72, s * 0.24));
        g2.draw(new Line2D.Double(s * 0.50, s * 0.54, s * 0.64, s * 0.42));
        g2.draw(new Line2D.Double(s * 0.50, s * 0.54, s * 0.50, s * 0.36));
    }

    // Section: Handle the logic for paint calculator.
    private void paintCalculator(Graphics2D g2) {
        double s = size;
        RoundRectangle2D body = new RoundRectangle2D.Double(s * 0.20, s * 0.12, s * 0.60, s * 0.76, s * 0.12, s * 0.12);
        g2.draw(body);
        g2.draw(new RoundRectangle2D.Double(s * 0.28, s * 0.20, s * 0.44, s * 0.16, s * 0.06, s * 0.06));
        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 3; c++) {
                double bx = s * (0.30 + c * 0.14);
                double by = s * (0.46 + r * 0.16);
                g2.draw(new RoundRectangle2D.Double(bx, by, s * 0.08, s * 0.08, s * 0.03, s * 0.03));
            }
        }
    }

    // Section: Handle the logic for paint calendar.
    private void paintCalendar(Graphics2D g2) {
        double s = size;
        RoundRectangle2D body = new RoundRectangle2D.Double(s * 0.16, s * 0.18, s * 0.68, s * 0.62, s * 0.10, s * 0.10);
        g2.draw(body);
        g2.draw(new Line2D.Double(s * 0.16, s * 0.34, s * 0.84, s * 0.34));
        g2.draw(new Line2D.Double(s * 0.30, s * 0.10, s * 0.30, s * 0.24));
        g2.draw(new Line2D.Double(s * 0.70, s * 0.10, s * 0.70, s * 0.24));
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 3; col++) {
                double cx = s * (0.28 + col * 0.16);
                double cy = s * (0.44 + row * 0.14);
                g2.fill(new Ellipse2D.Double(cx, cy, s * 0.05, s * 0.05));
            }
        }
    }

    // Section: Handle the logic for paint add.
    private void paintAdd(Graphics2D g2) {
        double s = size;
        g2.draw(new Ellipse2D.Double(s * 0.18, s * 0.18, s * 0.64, s * 0.64));
        g2.draw(new Line2D.Double(s * 0.50, s * 0.30, s * 0.50, s * 0.70));
        g2.draw(new Line2D.Double(s * 0.30, s * 0.50, s * 0.70, s * 0.50));
    }

    // Section: Handle the logic for paint check.
    private void paintCheck(Graphics2D g2) {
        double s = size;
        g2.draw(new Ellipse2D.Double(s * 0.18, s * 0.18, s * 0.64, s * 0.64));
        Path2D p = new Path2D.Double();
        p.moveTo(s * 0.32, s * 0.52);
        p.lineTo(s * 0.46, s * 0.66);
        p.lineTo(s * 0.70, s * 0.38);
        g2.draw(p);
    }

    // Section: Handle the logic for paint recurring.
    private void paintRecurring(Graphics2D g2) {
        double s = size;

        Path2D top = new Path2D.Double();
        top.moveTo(s * 0.22, s * 0.34);
        top.lineTo(s * 0.68, s * 0.34);
        top.lineTo(s * 0.60, s * 0.26);
        top.moveTo(s * 0.68, s * 0.34);
        top.lineTo(s * 0.60, s * 0.42);
        g2.draw(top);

        Arc2D rightTurn = new Arc2D.Double(s * 0.56, s * 0.34, s * 0.18, s * 0.24, 90, -90, Arc2D.OPEN);
        g2.draw(rightTurn);

        Path2D bottom = new Path2D.Double();
        bottom.moveTo(s * 0.78, s * 0.66);
        bottom.lineTo(s * 0.32, s * 0.66);
        bottom.lineTo(s * 0.40, s * 0.58);
        bottom.moveTo(s * 0.32, s * 0.66);
        bottom.lineTo(s * 0.40, s * 0.74);
        g2.draw(bottom);

        Arc2D leftTurn = new Arc2D.Double(s * 0.26, s * 0.42, s * 0.18, s * 0.24, 270, -90, Arc2D.OPEN);
        g2.draw(leftTurn);
    }

    // Section: Handle the logic for paint settings.
    private void paintSettings(Graphics2D g2) {
        double s = size;
        double cx = s * 0.5;
        double cy = s * 0.5;
        double outer = s * 0.30;
        double inner = s * 0.18;
        for (int i = 0; i < 8; i++) {
            double a = i * Math.PI / 4.0;
            double x1 = cx + Math.cos(a) * inner;
            double y1 = cy + Math.sin(a) * inner;
            double x2 = cx + Math.cos(a) * outer;
            double y2 = cy + Math.sin(a) * outer;
            g2.draw(new Line2D.Double(x1, y1, x2, y2));
        }
        g2.draw(new Ellipse2D.Double(s * 0.28, s * 0.28, s * 0.44, s * 0.44));
        g2.fill(new Ellipse2D.Double(s * 0.44, s * 0.44, s * 0.12, s * 0.12));
    }
}
