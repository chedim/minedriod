package com.onkiup.minedroid.gui.drawables;

import com.onkiup.minedroid.gui.primitives.Color;
import com.onkiup.minedroid.gui.primitives.Point;

/**
 * Draws debug border
 */
public class BorderDrawable extends ColorDrawable {
    public BorderDrawable(Color color) {
        super(color);
    }

    @Override
    public void draw(Point where) {
        ColorDrawable line = new ColorDrawable(color.getColor());

        // top
        line.setSize(new Point(1, size.y));
        line.draw(where);

        // bottom
        line.draw(where.add(new Point(size.x, 0)));

        // left
        line.setSize(new Point(size.x, 1));
        line.draw(where);

        // right
        line.draw(where.add(new Point(0, size.y)));

        ColorDrawable dot = new ColorDrawable(color.getColor());
        dot.setSize(new Point(3, 3));

        dot.draw(where.add(new Point(-1, -1)));                  // lt
        dot.draw(where.add(new Point(size.x - 1, -1)));          // rt
        dot.draw(where.add(new Point(size.x - 1, size.y - 1)));  // rb
        dot.draw(where.add(new Point(-1, size.y - 1)));          // lb
    }

    @Override
    public BorderDrawable clone() {
        BorderDrawable result = new BorderDrawable(color.getColor());
        if (size != null) result.setSize(size.clone());
        return result;
    }
}
