package com.onkiup.minedroid.gui.views;

import com.onkiup.minedroid.Context;
import com.onkiup.minedroid.gui.GuiManager;
import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.drawables.Drawable;
import com.onkiup.minedroid.gui.drawables.PartialDrawable;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.resources.Style;

/**
 * Created by chedim on 5/31/15.
 */
public class ProgressView extends ContentView {

    protected Drawable foreground;
    protected int max, value;

    public ProgressView(Context context) {
        super(context);
    }

    @Override
    public void drawContents(float partialTicks) {
        float part = value / (float) max;
        if (foreground instanceof PartialDrawable) {
            foreground.setSize(resolvedLayout.getInnerSize().clone());
            ((PartialDrawable) foreground).setPart(part);
        } else {
            Point size = resolvedLayout.getInnerSize().clone();
            size.x *= part;
            foreground.setSize(size);
        }
        Point p = position.add(resolvedLayout.padding.coords()).add(getGravityOffset(foreground.getSize()));
        foreground.draw(p);
    }

    @Override
    public void setDebug(boolean debugDraw) {
        super.setDebug(debugDraw);
        if (foreground != null) {
            foreground.setDebug(debugDraw);
        }
    }

    @Override
    public void clear() {
        value = 0;
    }

    @Override
    public void inflate(XmlHelper node, Style theme) {
        super.inflate(node, theme);

        max = node.getIntegerAttr(GuiManager.NS, "max", style, 100);
        value = node.getIntegerAttr(GuiManager.NS, "value", style, 50);

        foreground = node.getDrawableAttr(GuiManager.NS, "progress_drawable", style, null);
        foreground.setDebug(debug);
    }

    @Override
    protected String getThemeStyleName() {
        return "progress_view";
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getValue() {
        return value;
    }

    public int getMax() {
        return max;
    }
}
