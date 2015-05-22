package com.onkiup.minedroid.gui.primitives;

/**
 * Created by chedim on 4/25/15.
 */
public class Rect {
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

    public Point getSize() {
        return new Point(right - left, bottom - top);
    }

    public Point coords() {
        return new Point(left, top);
    }

    @Override
    public String toString() {
        return "[(" + left + ", " + top + ") — (" + right + ", " + bottom + ")]";
    }

    public boolean contains(Point point) {
        return point.x > left && point.x < right && point.y > top && point.y < bottom;
    }

    public Rect move(Point position) {
        Rect result = clone();
        result.left += position.x;
        result.top += position.y;
        result.bottom += position.y;
        result.right += position.x;
        return result;
    }

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

    public void setWidth(int width) {
        this.right = this.left + width;
    }

    public void setHeight(int height) {
        this.bottom = this.top + height;
    }
}
