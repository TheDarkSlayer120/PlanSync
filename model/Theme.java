package model;


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
 * File purpose: This class supports the Theme part of PlanSync and documents the main responsibilities of the file.
 */

import java.awt.Color;

public class Theme {

    private final String name;
    private final Color lightColor;
    private final Color darkColor;

    public Theme(String name, Color lightColor, Color darkColor) {
        this.name = name;
        this.lightColor = lightColor;
        this.darkColor = darkColor;
    }

    // Section: Return the data used to name.
    public String getName() {
        return name;
    }

    // Section: Return the data used to light color.
    public Color getLightColor() {
        return lightColor;
    }

    // Section: Return the data used to dark color.
    public Color getDarkColor() {
        return darkColor;
    }
}
