package com.onkiup.minedroid.gui.primitives;

/**
 * This rect left+top points to the center of the rect
 * and right+bottom — to its right bottom point offset from the center.
 */
public class CenteredRect extends Rect {

    public CenteredRect() {

    }

    public CenteredRect(int left, int top, int right, int bottom) {
        super(left, top, right, bottom);
    }

    public CenteredRect(Point leftTop, Point rightBottom) {
        super(leftTop, rightBottom);
    }

    public CenteredRect(Rect o) {
        Point size = o.getSize();
        if (size.x >= 0) {
            left = size.x / 2;
        }

        if (size.y >= 0) {
            top = size.y / 2;
        }
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width / 2);
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height / 2);
    }

    public Rect getRect() {
        return new Rect(left - right, top - bottom, left + right, top + bottom);
    }

    @Override
    public Rect and(Rect other) {
        if (other instanceof CenteredRect) {
            other = ((CenteredRect) other).getRect();
        }
        return getRect().and(other);
    }

    @Override
    public Point getSize() {
        return new Point(right * 2, bottom * 2);
    }

    @Override
    public boolean contains(Point point) {
        int x = Math.abs(point.x - left);
        int y = Math.abs(point.y - top);
        return x < right && y < bottom;
    }

    @Override
    public Rect clone() {
        CenteredRect res = new CenteredRect();
        res.left = left;
        res.top = top;
        res.right = right;
        res.bottom = bottom;
        return res;
    }
}
