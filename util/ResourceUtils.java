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
 * File purpose: This class supports the ResourceUtils part of PlanSync and documents the main responsibilities of the file.
 */

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;

public final class ResourceUtils {
    private ResourceUtils() {}

    // Section: Read and prepare the data used to image icon.
    public static ImageIcon loadImageIcon(String relativePath) {
        URL resource = ResourceUtils.class.getClassLoader().getResource(relativePath);
        if (resource != null) {
            return new ImageIcon(resource);
        }

        File file = AppPaths.getAppFile(relativePath);
        if (file.isFile()) {
            return new ImageIcon(file.getAbsolutePath());
        }

        return null;
    }

    // Section: Handle the logic for apply frame icon.
    public static void applyFrameIcon(JFrame frame, String relativePath) {
        if (frame == null) return;
        ImageIcon icon = loadImageIcon(relativePath);
        if (icon != null && icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
            frame.setIconImage(icon.getImage());
        }
    }

    // Section: Handle the logic for font can display.
    public static boolean fontCanDisplay(String text, Font font) {
        if (text == null || text.isEmpty() || font == null) return true;
        return font.canDisplayUpTo(text) == -1;
    }
}
