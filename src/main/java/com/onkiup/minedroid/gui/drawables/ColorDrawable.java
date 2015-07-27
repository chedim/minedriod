package com.onkiup.minedroid.gui.drawables;

import com.onkiup.minedroid.gui.MineDroid;
import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.primitives.Color;
import com.onkiup.minedroid.gui.primitives.GLColor;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.resources.Style;
import net.minecraft.client.renderer.GlStateManager;
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
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(color.red, color.green, color.blue, color.alpha);
        worldrenderer.startDrawingQuads();

        double left = where.x;
        double top = where.y;
        double right = where.x + size.x;
        double bottom = where.y + size.y;

        worldrenderer.addVertex(left, bottom, 0.0D);
        worldrenderer.addVertex(right, bottom, 0.0D);
        worldrenderer.addVertex(right, top, 0.0D);
        worldrenderer.addVertex(left, top, 0.0D);
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();

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
        setSize(xmlHelper.getSize(MineDroid.NS, new Point(0, 0)));
        setColor(xmlHelper.getColorAttr(MineDroid.NS, "color", 0x00000000l));
    }

    @Override
    public ColorDrawable clone() {
        ColorDrawable result = new ColorDrawable(color.clone());
        if (size != null) result.setSize(size.clone());
        return result;
    }
}
