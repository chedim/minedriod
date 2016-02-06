package com.onkiup.minedroid.gui.primitives;

/**
 * Created by chedim on 8/19/15.
 */
public class ColorPoint extends Point {

    protected GLColor color = new GLColor(0, 0, 0, 1);

    public ColorPoint(int x, int y) {
        super(x, y);
    }

    public ColorPoint(int x, int y, long color) {
        this(x, y);
        this.color = new GLColor(new Color(color));
    }

    public ColorPoint(int x, int y, GLColor color) {
        this(x, y);
        this.color = color;
    }

    public void setColor(GLColor color) {
        this.color = color;
    }

    public GLColor getColor() {
        return color;
    }

    @Override
    public Point clone() {
        return new ColorPoint(x, y, color);
    }
}
