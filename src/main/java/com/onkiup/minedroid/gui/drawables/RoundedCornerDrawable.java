package com.onkiup.minedroid.gui.drawables;

import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.primitives.Color;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.themes.Theme;

/**
 * Created by chedim on 4/30/15.
 */
public class RoundedCornerDrawable extends ColorDrawable {
    protected Point size;
    protected int radius;

    public RoundedCornerDrawable() {
        super();
    }

    public RoundedCornerDrawable(Color color, int radius) {
        super(color);
        this.radius = radius;
    }

    public RoundedCornerDrawable(int color, int radius) {
        super(color);
        this.radius = radius;
    }

    @Override
    public void draw(Point where) {
        Point ellipseSize = new Point(radius * 2, radius * 2);
        EllipseDrawable ellipse = new EllipseDrawable(color);
        ColorDrawable fill = new ColorDrawable(color);

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
    public void inflate(XmlHelper xmlHelper, Theme theme) {
        super.inflate(xmlHelper, theme);
        this.radius = xmlHelper.getDimenAttr("mc", "radius", 0);
    }

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

}
