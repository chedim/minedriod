package com.onkiup.minedroid.gui.primitives;

/**
 * Created by chedim on 8/11/15.
 */
public class TimedPoint extends Point {

    public int time;

    public TimedPoint(int x, int y, int time) {
        super(x, y);
        this.time = time;
    }

    public TimedPoint(Point src, int time) {
        this(src.x, src.y, time);
    }

    @Override
    public TimedPoint clone() {
        return new TimedPoint(x, y, time);
    }

    @Override
    public String toString() {
        return super.toString() + "@" + time;
    }

    @Override
    public TimedPoint add(Point point) {
        TimedPoint result = clone();
        result.x += point.x;
        result.y += point.y;
        return result;
    }

    public TimedPoint add(TimedPoint point) {
        TimedPoint result = add((Point) point);
        result.time += point.time;
        return result;
    }

    @Override
    public TimedPoint sub(Point point) {
        return (TimedPoint) super.sub(point);
    }

    public TimedPoint sub(TimedPoint point) {
        TimedPoint result = sub((Point) point);
        result.time -= point.time;
        return result;
    }
}
