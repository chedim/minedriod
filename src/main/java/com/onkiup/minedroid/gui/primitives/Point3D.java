package com.onkiup.minedroid.gui.primitives;

/**
 * Represents 3D point coordinates
 */
public class Point3D extends Point {

    public int z;

    public Point3D(int x, int y, int z) {
        super(x, y);
        this.z = z;
    }

    @Override
    public Point3D clone() {
        return new Point3D(x, y, z);
    }

    /**
     * Uses given point as an offset to create a moved point
     * @param point Offset
     * @return Moved point
     */
    public Point3D add(Point3D point) {
        Point3D result = (Point3D) super.add(point);
        result.z += point.z;

        return result;
    }

    @Override
    public String toString() {
        return "("+x+", "+y+", "+z+")";
    }

    /**
     * Uses given point as a negative offset to create a moved point
     * @param point Offset
     * @return Moved point
     */
    public Point3D sub(Point3D point) {
        Point3D result = (Point3D) super.sub(point);
        result.z -= point.z;

        return result;
    }
}
