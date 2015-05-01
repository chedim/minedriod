package com.onkiup.minecraft.gui.drawables;

import com.onkiup.minecraft.gui.XmlHelper;
import com.onkiup.minecraft.gui.primitives.Color;
import com.onkiup.minecraft.gui.primitives.GLColor;
import com.onkiup.minecraft.gui.primitives.Point;
import com.onkiup.minecraft.gui.themes.Theme;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;

/**
 * Created by chedim on 4/25/15.
 */
public class ColorDrawable implements Drawable {

    protected GLColor color;
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

    public void setColor(long color) {
        setColor(new Color(color));
    }

    public void setColor(Color color) {
        setColor(new GLColor(color));
    }

    public void setColor(GLColor color) {
        this.color = color;
    }

    public GLColor getColor() {
        return this.color;
    }


    @Override
    public void inflate(XmlHelper xmlHelper, Theme theme) {
        setSize(xmlHelper.getSize("mc", new Point(0, 0)));
        setColor(xmlHelper.getColorAttr("mc", "color", 0x00000000l));
    }
}
