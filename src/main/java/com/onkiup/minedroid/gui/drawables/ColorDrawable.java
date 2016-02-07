package com.onkiup.minedroid.gui.drawables;

import com.onkiup.minedroid.gui.GuiManager;
import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.primitives.Color;
import com.onkiup.minedroid.gui.primitives.GLColor;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.resources.Style;
import com.onkiup.minedroid.gui.views.View;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;

/**
 * Draws a colored rectangle
 */
public class ColorDrawable implements Drawable {

    /**
     * Rectangle color
     */
    protected GLColor color;

    /**
     * Rectangle size
     */
    protected Point size;
    private boolean debug;

    public ColorDrawable() {
    }

    public ColorDrawable(Color color) {
        this.color = new GLColor(color);
    }

    public ColorDrawable(GLColor color) {
        this.color = color;
    }

    public ColorDrawable(int color) {
        this(new Color(color));
    }

    @Override
    public void draw(Point where) {
        View.resetBlending();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.startDrawingQuads();

        double left = where.x;
        double top = where.y;
        double right = where.x + size.x;
        double bottom = where.y + size.y;

        worldrenderer.setColorRGBA_F(color.red, color.green, color.blue, color.alpha);
        worldrenderer.addVertex(left, bottom, 0.0D);
        worldrenderer.addVertex(right, bottom, 0.0D);
        worldrenderer.addVertex(right, top, 0.0D);
        worldrenderer.addVertex(left, top, 0.0D);
        tessellator.draw();
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
        return new Point(0, 0);
    }

    /**
     * Sets drawing color
     * @param color New color
     */
    public void setColor(long color) {
        setColor(new Color(color));
    }

    /**
     * Sets drawing color
     * @param color New color
     */
    public void setColor(Color color) {
        setColor(new GLColor(color));
    }

    /**
     * Sets drawing color
     * @param color New color
     */
    public void setColor(GLColor color) {
        this.color = color;
    }

    /**
     * Returns drawing color
     * @return drawing color
     */
    public GLColor getColor() {
        return this.color;
    }


    @Override
    public void inflate(XmlHelper xmlHelper, Style theme) {
        setSize(xmlHelper.getSize(GuiManager.NS, new Point(0, 0)));
        setColor(xmlHelper.getColorAttr(GuiManager.NS, "color", 0x00000000l));
    }

    @Override
    public ColorDrawable clone() {
        ColorDrawable result = new ColorDrawable(color.clone());
        if (size != null) result.setSize(size.clone());
        return result;
    }

    @Override
    public void drawShadow(Point where, GLColor color, int w) {
        if (this.size == null) return;
        GLColor stop = color.clone();
        stop.alpha = color.alpha / w;
        RoundedCornerDrawable shadow = new RoundedCornerDrawable(stop, 0);
        for (int i=1; i<w+1; i++) {
            shadow.setColor(stop);
            shadow.setRadius(i);
            shadow.setSize(size.add(new Point(i*2, i*2)));
            shadow.draw(where.add(new Point(-i, -i)));
        }
    }

    @Override
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
