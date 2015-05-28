package com.onkiup.minedroid.gui.primitives;

/**
 * Represents color with it's components stored as float values from 0 to 1
 */
public class GLColor {
    public float red, green, blue, alpha;

    public GLColor(Color color) {
        red = color.red / 255f;
        green = color.green / 255f;
        blue = color.blue / 255f;
        alpha = color.alpha / 255f;
    }

    public GLColor(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public GLColor(int red, int green, int blue, int alpha) {
        this.red = red / 255f;
        this.green = green / 255f;
        this.blue = blue / 255f;
        this.alpha = alpha / 255f;
    }

    @Override
    public GLColor clone() {
        return new GLColor(red, green, blue, alpha);
    }

    @Override
    public String toString() {
        return "rgba_gl("+red+", "+green+", "+blue+", "+alpha+")";
    }

    public Color getColor() {
        return new Color((int) (red * 255), (int) (green *255), (int) (blue * 255), (int) (alpha * 255));
    }
}
