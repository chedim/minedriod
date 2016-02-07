package com.onkiup.minedroid.gui.primitives;

/**
 * Represents a RGBA color
 */
public class Color {
    /**
     * Color components
     */
    public int alpha = 255, red = 0, green = 0, blue = 0;

    public Color(int red, int green, int blue, int alpha) {
        this.alpha = alpha;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public Color(long color) {
        blue = (int) (color & 255);
        green = (int) (color >> 8 & 255);
        red = (int) (color >> 16 & 255);
        if (color > 0xFFFFFF) {
            alpha = (int) (color >> 24 & 255);
        }
    }

    @Override
    public Color clone() throws CloneNotSupportedException {
        return new Color(red, green, blue, alpha);
    }

    @Override
    public String toString() {
        return "rgba("+red+", "+green+", "+blue+", "+alpha+")";
    }

    public long raw() {
        Long raw = 0l;
        raw = raw | alpha;
        raw = (raw << 8) | red;
        raw = (raw << 8) | green;
        raw = (raw << 8) | blue;
        return raw;
    }
}
