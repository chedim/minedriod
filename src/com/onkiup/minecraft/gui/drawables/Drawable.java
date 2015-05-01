package com.onkiup.minecraft.gui.drawables;

import com.onkiup.minecraft.gui.XmlHelper;
import com.onkiup.minecraft.gui.primitives.Point;
import com.onkiup.minecraft.gui.themes.Theme;

/**
 * Created by chedim on 4/25/15.
 */
public interface    Drawable {
    public void draw(Point where);
    public void setSize(Point size);
    public Point getSize();
    public Point getOriginalSize();

    public void inflate(XmlHelper node, Theme theme);
}
