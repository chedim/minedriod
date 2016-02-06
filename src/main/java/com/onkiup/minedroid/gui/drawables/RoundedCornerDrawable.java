package com.onkiup.minedroid.gui.drawables;

import com.onkiup.minedroid.gui.GuiManager;
import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.primitives.Color;
import com.onkiup.minedroid.gui.primitives.GLColor;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.resources.Style;

/**
 * Draws a rectangle with rounded corners
 */
public class RoundedCornerDrawable extends ColorDrawable {
    /**
     * Rectangle size
     */
    protected Point size;
    /**
     * Corners radius;
     */
    protected int radius;

    protected final static EllipseDrawable cornerDrawable = new EllipseDrawable();
    protected final static ColorDrawable fillDrawable = new ColorDrawable();
    protected final static GradientDrawable gradient = new GradientDrawable();

    public RoundedCornerDrawable() {
        super();
    }

    public RoundedCornerDrawable(Color color, int radius) {
        super(color);
        this.radius = radius;
    }

    public RoundedCornerDrawable(GLColor color, int radius) {
        super(color);
        this.radius = radius;
    }

    public RoundedCornerDrawable(int color, int radius) {
        super(color);
        this.radius = radius;
    }

    @Override
    public void draw(Point where) {
//        if (true) return;
        Point ellipseSize = new Point(radius * 2, radius * 2);
        EllipseDrawable ellipse = cornerDrawable;
        ellipse.setColor(color);
        ellipse.setCenter(null);
        ColorDrawable fill = fillDrawable;
        fill.setColor(color);

        ellipse.setSize(ellipseSize);
        Point mainFillSize = size.sub(new Point(0, radius * 2));
        Point topFillSize = new Point(size.x - radius * 2, radius);

        fill.setSize(mainFillSize);
        fill.draw(new Point(where.x, where.y + radius));
        fill.setSize(topFillSize);
        fill.draw(new Point(where.x + radius, where.y));
        fill.draw(new Point(where.x + radius, where.y + radius + mainFillSize.y));

        ellipse.setSize(new Point(radius * 2, radius * 2));

        ellipse.setArc(Math.PI, Math.PI * 1.5);
        ellipse.draw(where);
        ellipse.setArc(Math.PI * 0.5, Math.PI);
        ellipse.draw(where.add(new Point(0, size.y - radius * 2)));
        ellipse.setArc(0, Math.PI * 0.5);
        ellipse.draw(where.add(new Point(size.x - radius * 2, size.y - radius * 2)));
        ellipse.setArc(Math.PI * 1.5, Math.PI * 2);
        ellipse.draw(where.add(new Point(size.x - radius * 2, 0)));
    }

    @Override
    public void setSize(Point size) {
        this.size = size;
    }

    @Override
    public Point getSize() {
        return size;
    }

    @Override
    public Point getOriginalSize() {
        return new Point (radius * 2, radius * 2);
    }

    @Override
    public void inflate(XmlHelper xmlHelper, Style theme) {
        super.inflate(xmlHelper, theme);
        this.radius = xmlHelper.getDimenAttr(GuiManager.NS, "radius", 0);
    }

    /**
     * Sets corners radius
     * @param r
     */
    public void setRadius(int r) {
        radius = r;
    }

    public RoundedCornerDrawable clone() {
        RoundedCornerDrawable result = new RoundedCornerDrawable();
        result.setColor(color.clone());
        if (size != null) result.setSize(size.clone());
        result.setRadius(radius);

        return result;
    }

    @Override
    public void drawShadow(Point where, GLColor color, int w) {
        if (this.size == null) return;
        GLColor stop = color.clone();
//        stop.red = stop.blue = stop.green = (stop.red + stop.green + stop.blue) / 6;
        stop.alpha = 0;
        Point ellipseSize = new Point(w * 2, w * 2);
        cornerDrawable.setSize(ellipseSize);
        gradient.setColor(color);
        gradient.setStopColor(stop);

        // right
        gradient.setAngle(0);
        gradient.setSize(new Point(w, size.y - radius * 2));
        gradient.draw(new Point(where.x + size.x - radius, where.y + radius));
        // left
        gradient.setAngle(180);
        gradient.draw(new Point(where.x - w + radius, where.y + radius));
        // top
        gradient.setSize(new Point(size.x - radius * 2, w));
        gradient.setAngle(90);
        gradient.draw(new Point(where.x + radius, where.y + radius - w));
        // bottom
        gradient.setAngle(270);
        gradient.draw(new Point(where.x + radius, where.y + size.y - radius));

        // lt
        cornerDrawable.setSize(new Point(w * 2, w * 2));
        cornerDrawable.setColor(stop);
        cornerDrawable.setCenter(color);

        Point corner = new Point(where.x - w + radius, where.y - w + radius);
        cornerDrawable.setArc(Math.PI, Math.PI * 1.5);
        cornerDrawable.draw(corner);
        cornerDrawable.setArc(Math.PI * 0.5, Math.PI);
        cornerDrawable.draw(corner.add(new Point(0, size.y - radius * 2)));
        cornerDrawable.setArc(0, Math.PI * 0.5);
        cornerDrawable.draw(corner.add(new Point(size.x - radius * 2, size.y - radius * 2)));
        cornerDrawable.setArc(Math.PI * 1.5, Math.PI * 2);
        cornerDrawable.draw(corner.add(new Point(size.x - radius * 2, 0)));
    }
}
