package com.onkiup.minedroid.gui.views;

import com.onkiup.minedroid.Context;
import com.onkiup.minedroid.gui.GuiManager;
import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.drawables.DebugDrawable;
import com.onkiup.minedroid.gui.primitives.Color;
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
    protected VGravity vGravity = VGravity.TOP;

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
        else if (hGravity == HGravity.RIGHT) result.x = me.x - size.x;

        if (vGravity == VGravity.CENTER) result.y = me.y / 2 - size.y / 2;
        else if (vGravity == VGravity.BOTTOM) result.y = me.y - size.y;

        return result;
    }

    @Override
    public void onDraw(float partialTicks) {
        super.onDraw(partialTicks);
        setClipRect();
        drawContents(partialTicks);
        resetClipRect();
    }

    /**
     * Sets GL_SCISSOR rectangle for View contents
     */
    protected void setClipRect() {
        Rect inner = resolvedLayout.getInnerRect().move(position);
        int f = GuiManager.getScale().getScaleFactor();
//        inner.left -= f;
//        inner.top -= f;
//        inner.bottom += f;
//        inner.right += f;
        if (debug) {
            DebugDrawable d = new DebugDrawable(new Color(0x6600ff00));
            d.setSize(inner.getSize());
            d.draw(inner.coords());
        } else {
            GuiManager.addClipRect(inner);
        }
    }

    /**
     * Drops G_SCISSOR rectangle
     */
    protected void resetClipRect() {
        if (!debug) GuiManager.restoreClipRect();
    }

    /**
     * Draws view contents
     */
    public abstract void drawContents(float partialTicks);

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

        setGravityHorizontal((HGravity) node.getEnumAttr(GuiManager.NS, "horizontal-gravity", style, HGravity.LEFT));
        setGravityVertical((VGravity) node.getEnumAttr(GuiManager.NS, "vertical-gravity", style, VGravity.TOP));
    }

    @Override
    protected String getThemeStyleName() {
        return "content_view";
    }
}
