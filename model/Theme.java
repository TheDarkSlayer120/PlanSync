package model;

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

    public String getName() {
        return name;
    }

    public Color getLightColor() {
        return lightColor;
    }

    public Color getDarkColor() {
        return darkColor;
    }
}