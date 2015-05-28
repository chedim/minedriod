package com.onkiup.minedroid.gui.primitives;

/**
 * Represent a 2D point (or size) coordinates
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

    /**
     * Uses given point as an offset to create a moved point
     * @param point Offset
     * @return Moved point
     */
    public Point add(Point point) {
        Point result = clone();
        result.x += point.x;
        result.y += point.y;
        return result;
    }

    /**
     * Uses given point as a negative offset to create a moved point
     * @param point Offset
     * @return Moved point
     */
    public Point sub(Point point) {
        Point result = clone();
        result.x -= point.x;
        result.y -= point.y;
        return result;
    }
}
