package com.onkiup.minedroid.gui.drawables;

import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.resources.Style;

/**
 * Basic class for all drawables
 */
public interface Drawable {
    /**
     * Draws the drawable
     * @param where Where to draw
     */
    void draw(Point where);
    /**
     * Sets drawable size
     * @param size New drawable size
     */
    void setSize(Point size);
    /**
     * Returns current drawable size
     * @return Drawable size
     */
    Point getSize();
    /**
     * Returns original drawable size
     * @return original size
     */
    Point getOriginalSize();

    /**
     * Inflates Drawable from XML
     * @param node XML node
     * @param theme Theme with which it should be inflated
     */
    void inflate(XmlHelper node, Style theme);

    /**
     * Clones the drawable
     * @return drawable clone
     */
    Drawable clone();

}
