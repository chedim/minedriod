package com.onkiup.minedroid.gui.primitives;

/**
 * Created by chedim on 5/15/15.
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

    public Point3D add(Point3D point) {
        Point3D result = (Point3D) super.add(point);
        result.z += point.z;

        return result;
    }

    @Override
    public String toString() {
        return "("+x+", "+y+", "+z+")";
    }

    public Point3D sub(Point3D point) {
        Point3D result = (Point3D) super.sub(point);
        result.z -= point.z;

        return result;
    }
}
