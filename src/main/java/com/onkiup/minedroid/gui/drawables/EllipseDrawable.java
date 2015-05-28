package com.onkiup.minedroid.gui.drawables;

import com.onkiup.minedroid.gui.primitives.GLColor;
import com.onkiup.minedroid.gui.primitives.Point;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import org.lwjgl.opengl.GL11;

/**
 * Draws ellipse (or its part)
 */
public class EllipseDrawable extends ColorDrawable {

    /**
     * Ellipse arc start radian
     */
    protected double arcMin = 0;
    /**
     * Ellipse arc stop radian
     */
    protected double arcMax = 2 * Math.PI;

    @Override
    public EllipseDrawable clone() {
        EllipseDrawable result = new EllipseDrawable(color.clone());
        result.setArc(arcMin, arcMax);
        if (size != null) result.setSize(size.clone());
        return result;
    }

    public EllipseDrawable(GLColor color) {
        super(color);
    }

    public EllipseDrawable(int color) {
        super(color);
    }

    public EllipseDrawable() {
        super();
    }

    @Override
    public void draw(Point where) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(color.red, color.green, color.red, color.alpha);
        worldrenderer.startDrawingQuads();

        double rX = size.x / 2;
        double rY = size.y / 2;
        double a = rX, b = rY;
        if (rY > rX) {
            a = rY;
            b = rX;
        }

        GL11.glColor4f(color.red, color.green, color.blue, color.alpha);
        GL11.glBegin(GL11.GL_TRIANGLES);

        double step = Math.PI / (a + b) / 4;

        double centerX = where.x + rX, centerY = where.y + rY;
        boolean lastDraw = false;
        double lastI = 2 * Math.PI;
        for (double i = 2 * Math.PI - step; i > -step; i -= step) {
            if (i > arcMin && i < arcMax) {
                if (!lastDraw) {
                    triangle(centerX, centerY, rX, rY, arcMax, i);
                } else {
                    triangle(centerX, centerY, rX, rY, lastI, i);
                }
                lastDraw = true;
            } else if (lastDraw) {
                triangle(centerX, centerY, rX, rY, lastI, arcMin);
                break;
            }
//            triangle(centerX, centerY, rX, rY, lastI, i);
            lastI = i;
        }

        GL11.glEnd();

        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    /**
     * Draws ellipse triangle part
     * @param x1 center x
     * @param y1 center y
     * @param rX radius on X
     * @param rY radius on Y
     * @param i1 radians for first dot
     * @param i2 radians for second dot
     */
    protected void triangle(double x1, double y1, double rX, double rY, double i1, double i2) {
        double x2 = (x1 + Math.cos(i1) * rX);
        double y2 = (y1 + Math.sin(i1) * rY);
        double x3 = (x1 + Math.cos(i2) * rX);
        double y3 = (y1 + Math.sin(i2) * rY);

        GL11.glVertex2d(x1, y1);
        GL11.glVertex2d(x2, y2);
        GL11.glVertex2d(x3, y3);
    }

    /**
     * Sets arc limits
     * @param min Arc start rads
     * @param max Arc end rads
     */
    public void setArc(double min, double max) {
        arcMin = min;
        arcMax = max;
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
        return size;
    }

}
