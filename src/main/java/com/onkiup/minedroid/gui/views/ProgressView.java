package com.onkiup.minedroid.gui.views;

import com.onkiup.minedroid.gui.Context;
import com.onkiup.minedroid.gui.MineDroid;
import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.drawables.Drawable;
import com.onkiup.minedroid.gui.drawables.PartialDrawable;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.resources.ResourceLink;
import com.onkiup.minedroid.gui.resources.Style;
import net.minecraft.util.ResourceLocation;

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
    public void drawContents() {
        float part = value / max;
        if (foreground instanceof PartialDrawable) {
            foreground.setSize(resolvedLayout.getInnerSize().clone());
            ((PartialDrawable) foreground).setPart(part);
        } else {
            Point size = resolvedLayout.getInnerSize().clone();
            size.x *= part;
            foreground.setSize(size);
        }
        Point p = getGravityOffset(foreground.getSize());
        foreground.draw(p);
    }

    @Override
    public void clear() {
        value = 0;
    }

    @Override
    public void inflate(XmlHelper node, Style theme) {
        super.inflate(node, theme);

        max = style.getInt("max", 100);
        value = style.getInt("value", 50);
        ResourceLocation fg = style.getResource("progress_drawable", null);

        max = node.getIntegerAttr(MineDroid.NS, "max", max);
        value = node.getIntegerAttr(MineDroid.NS, "value", value);
        fg = node.getResourceAttr(MineDroid.NS, "progress_drawable", fg);

        foreground = MineDroid.inflateDrawable(this, fg);
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
