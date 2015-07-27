package com.onkiup.minedroid.gui.views;

import com.onkiup.minedroid.gui.Context;
import com.onkiup.minedroid.gui.MineDroid;
import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.primitives.Rect;
import com.onkiup.minedroid.gui.resources.Style;

/**
 * Parent class for all GUI elements that have any content inside of them
 */
public abstract class ContentView extends View {
    /**
     * Content gravity on X axis
     */
    protected HGravity hGravity = HGravity.LEFT;
    /**
     * Content gravity on Y axis
     */
    protected VGravity vGravity = VGravity.CENTER;

    public ContentView(Context context) {
        super(context);
    }

    /**
     * Sets gravity on X axis
     * @param gravity new gravity
     */
    public void setGravityHorizontal(HGravity gravity) {
        hGravity = gravity;
    }

    /**
     * Sets gravity on Y axis
     * @param gravity new gravity
     */
    public void setGravityVertical(VGravity gravity) {
        vGravity = gravity;
    }

    /**
     *
     * @return current X axis gravity
     */
    public HGravity getGravityHorizontal() {
        return hGravity;
    }

    /**
     *
     * @return Current Y axis gravity
     */
    public VGravity hetGravityVertical() {
        return vGravity;
    }

    /**
     * Calculates element placement for current gravity
     * @param size Element size
     * @return Element drawing base point
     */
    public Point getGravityOffset(Point size) {
        Point result = new Point(0, 0);
        Point me = resolvedLayout.getInnerSize();
        if (hGravity == HGravity.CENTER) result.x = me.x / 2 - size.x / 2;
        else if (hGravity == HGravity.RIGHT) result.x = me.x = size.x;

        if (vGravity == VGravity.CENTER) result.y = me.y / 2 - size.y / 2;
        else if (vGravity == VGravity.BOTTOM) result.y = me.y - size.y;

        return result;
    }

    @Override
    public void onDraw() {
        super.onDraw();

        setClipRect();
        drawContents();
        resetClipRect();
    }

    /**
     * Sets GL_SCISSOR rectangle for View contents
     */
    protected void setClipRect() {
        Rect inner = resolvedLayout.getInnerRect().move(position);
        MineDroid.addClipRect(inner);
    }

    /**
     * Drops G_SCISSOR rectangle
     */
    protected void resetClipRect() {
        MineDroid.restoreClipRect();
    }

    /**
     * Draws view contents
     */
    public abstract void drawContents();

    /**
     * Clears view contents
     */
    public abstract void clear();

    /**
     * Horizontal gravity values;
     */
    public enum HGravity {LEFT, CENTER, RIGHT}

    /**
     * Vertical gravity values;
     */
    public enum VGravity {TOP, CENTER, BOTTOM}

    @Override
    public void inflate(XmlHelper node, Style theme) {
        super.inflate(node, theme);

        HGravity themeHGravity = (HGravity) style.getEnum("horizontal-gravity", HGravity.CENTER);
        VGravity themeVGravity = (VGravity) style.getEnum("vertical-gravity", VGravity.CENTER);

        setGravityHorizontal((HGravity) node.getEnumAttr(MineDroid.NS, "horizontal-gravity", themeHGravity));
        setGravityVertical((VGravity) node.getEnumAttr(MineDroid.NS, "vertical-gravity", themeVGravity));
    }

    @Override
    protected String getThemeStyleName() {
        return "content_view";
    }
}
