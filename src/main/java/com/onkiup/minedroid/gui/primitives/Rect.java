package com.onkiup.minedroid.gui.primitives;

/**
 * Represents a rectngle
 */
public class Rect {
    /**
     * Rectangle coordinates
     */
    public int left, top, right, bottom;

    public Rect(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public Rect(Point leftTop, Point rightBottom) {
        left = leftTop.x;
        top = leftTop.y;
        right = rightBottom.x;
        bottom = rightBottom.y;
    }

    public Rect() {
        this(0, 0, 0, 0);
    }

    @Override
    public Rect clone() {
        return new Rect(left, top, right, bottom);
    }

    /**
     * Calculates rectangle size
     * @return Calculated size
     */
    public Point getSize() {
        return new Point(right - left, bottom - top);
    }

    /**
     * Returns rectangle top left point coordinates
     * @return top left point coordinates
     */
    public Point coords() {
        return new Point(left, top);
    }

    @Override
    public String toString() {
        return "[(" + left + ", " + top + ") — (" + right + ", " + bottom + ")]";
    }

    /**
     * Checks if the point inside of the rectangle
     * @param point Test pont
     * @return check result
     */
    public boolean contains(Point point) {
        return point.x > left && point.x < right && point.y > top && point.y < bottom;
    }

    /**
     * Copies rectangle to the position
     * @param position new position
     * @return moved copy
     */
    public Rect move(Point position) {
        Rect result = clone();
        result.left += position.x;
        result.top += position.y;
        result.bottom += position.y;
        result.right += position.x;
        return result;
    }

    /**
     * returns rectangles intesection rectangle
     * @param other other rectangle
     * @return intersection area
     */
    public Rect and(Rect other) {
        Rect result = new Rect(0, 0, 0, 0);
        result.left = Math.max(left, other.left);
        result.top = Math.max(top, other.top);
        result.right = Math.min(right, other.right);
        result.bottom = Math.min(bottom, other.bottom);
        if (result.left > result.right || result.top > result.bottom) {
            // There is no intersection.
            return null;
        }
        return result;
    }

    /**
     * Moves rectangle bottom right point to fit given width
     * @param width new width
     */
    public void setWidth(int width) {
        this.right = this.left + width;
    }

    /**
     * Moves rectangle bottom right point to fit given height
     * @param height new height
     */
    public void setHeight(int height) {
        this.bottom = this.top + height;
    }
}
