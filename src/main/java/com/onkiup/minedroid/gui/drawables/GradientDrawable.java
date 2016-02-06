package com.onkiup.minedroid.gui.drawables;

import com.onkiup.minedroid.gui.GuiManager;
import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.primitives.Color;
import com.onkiup.minedroid.gui.primitives.GLColor;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.primitives.Rect;
import com.onkiup.minedroid.gui.resources.Style;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import org.lwjgl.opengl.GL11;

/**
 * Created by chedim on 8/9/15.
 */
public class GradientDrawable extends ColorDrawable {
    protected float angle;
    protected GLColor stop;

    public GradientDrawable() {
    }

    public GradientDrawable(Color color, Color stop, float angle) {
        super(color);
        this.stop = new GLColor(stop);
        this.angle = angle;
    }

    public GradientDrawable(GLColor color, GLColor stop, float angle) {
        super(color);
        this.stop = stop;
        this.angle = angle;
    }

    public GradientDrawable(int color, int stop, float angle) {
        super(color);
        this.stop = new GLColor(new Color(stop));
        this.angle = angle;
    }

    @Override
    public void draw(Point where) {
        GlStateManager.pushMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);

        GL11.glTranslated(where.x, where.y, 0);
        GuiManager.addClipRect(new Rect(where.x, where.y, where.x + size.x, where.y + size.y));

        Point lt = new Point(0, 0), rt = new Point(size.x, 0),
                rb = new Point(size.x, size.y), lb = new Point(0, size.y);



        int deg = (int) Math.floor(angle / 90);
        double rads = Math.toRadians(angle - 90 * deg);
        double cos = Math.cos(rads), sin = Math.sin(rads);
        double tan = Math.tan(rads);
        int x = Math.abs((int) (size.y * tan));
        int y = Math.abs((int) (size.x * tan));

        if (deg % 2 != 0) {
            GL11.glTranslated(size.x / 2, size.y / 2, 0);
            GL11.glRotatef(-90 * deg, 0, 0, 1);
            GL11.glTranslated(-size.y / 2, -size.x / 2, 0);
            // we have to rotate our rect
            rt = new Point(size.y, 0);
            rb = new Point(size.y, size.x);
            lb = new Point(0, size.y);
        } else {
            GL11.glTranslated(size.x / 2, size.y / 2, 0);
            GL11.glRotatef(-90 * deg, 0, 0, 1);
            GL11.glTranslated(-size.x / 2, -size.y / 2, 0);
        }

        lt.x -= y;
        rb.x += y;
        lb.x += y;
        lb.y += size.x;
        rt.x -= y;
        rt.y -= size.x;

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glColor4f(color.red, color.green, color.blue, color.alpha);
        GL11.glVertex2i(lt.x, lt.y);
        GL11.glVertex2i(lb.x, lb.y);
        GL11.glColor4f(stop.red, stop.green, stop.blue, stop.alpha);
        GL11.glVertex2i(rb.x, rb.y);
        GL11.glVertex2i(rt.x, rt.y);
        GL11.glEnd();
        GuiManager.restoreClipRect();

        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();

        GlStateManager.popMatrix();
//        FMLLog.info("Gradient: %s, %s", lt, rt);
//        FMLLog.info("Gradient: %s, %s", lb, rb);
//        FMLLog.info("-----------------");
//        GlStateManager.rotate((-angle), 0.5f, 0.5f, 0f);
    }

    @Override
    public void drawShadow(Point where, GLColor color, int w) {

    }

    @Override
    public void inflate(XmlHelper xmlHelper, Style theme) {
        super.inflate(xmlHelper, theme);
        stop = new GLColor(new Color(xmlHelper.getColorAttr(GuiManager.NS, "stop", theme, 0l)));
        angle = xmlHelper.getIntegerAttr(GuiManager.NS, "angle", theme, 0);
    }

    public void setStopColor(GLColor color) {
        stop = color;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }
}
