package com.onkiup.minecraft.gui.primitives;

/**
 * Created by chedim on 4/25/15.
 */
public class Point {

    public int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public Point clone() {
        return new Point(x, y);
    }

    @Override
    public String toString() {
        return "("+x+", "+y+")";
    }

    public Point add(Point point) {
        Point result = clone();
        result.x += point.x;
        result.y += point.y;
        return result;
    }

    public Point sub(Point point) {
        Point result = clone();
        result.x -= point.x;
        result.y -= point.y;
        return result;
    }
}
