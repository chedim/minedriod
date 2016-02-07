package com.onkiup.minedroid.gui.drawables;

import com.onkiup.minedroid.gui.GuiManager;
import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.primitives.GLColor;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.resources.Style;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Represents image files
 */
public class BitmapDrawable implements Drawable {

    /**
     * Image file location
     */
    protected ResourceLocation src;
    /**
     * Current drawable size
     */
    protected Point size;
    /**
     * Original image size
     */
    protected Point originalSize;
    private boolean debug;

    public BitmapDrawable(ResourceLocation src) throws IOException, OutOfMemoryError {
        setDrawable(src);
    }

    public BitmapDrawable() {

    }

    /**
     * Sets image location
     * @param src image location
     * @throws IOException
     * @throws OutOfMemoryError
     */
    public void setDrawable(ResourceLocation src) throws IOException, OutOfMemoryError {
        this.src = src;
        InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(src).getInputStream();
        BufferedImage img = ImageIO.read(is);
        originalSize = size = new Point(img.getWidth(), img.getHeight());
    }

    /**
     * Draws the image
     * @param where Where to draw the image
     */
    @Override
    public void draw(Point where) {
        int left = where.x;
        int top = where.y;
        int right = where.x + size.x;
        int bottom = where.y + size.y;

        Minecraft.getMinecraft().getTextureManager().bindTexture(src);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        worldrenderer.startDrawingQuads();
        worldrenderer.addVertexWithUV(right, bottom, 0, 1, 1);
        worldrenderer.addVertexWithUV(right, top, 0, 1, 0);
        worldrenderer.addVertexWithUV(left, top, 0, 0, 0);
        worldrenderer.addVertexWithUV(left, bottom, 0, 0, 1);
        tessellator.draw();

//        BorderDrawable overlay = new BorderDrawable(new Color(0x66ff0000));
//        overlay.setSize(size);
//        overlay.draw(where);
        GlStateManager.enableTexture2D();
        RenderHelper.enableStandardItemLighting();
    }

    /**
     * Sets drawable size
     * @param size New drawable size
     */
    @Override
    public void setSize(Point size) {
        this.size = size;
    }

    /**
     * Returns current drawable size
     * @return Drawable size
     */
    @Override
    public Point getSize() {
        return size;
    }

    /**
     * Returns original image size
     * @return image size
     */
    @Override
    public Point getOriginalSize() {
        return originalSize;
    }

    /**
     * Inflates Drawable from XML
     * @param node XML node
     * @param theme Theme with which it should be inflated
     */
    @Override
    public void inflate(XmlHelper node, Style theme) {
        try {
            setDrawable(node.getResourceAttr(GuiManager.NS, "background", null));
            size = new Point(0, 0);
            size.x = node.getDimenAttr(GuiManager.NS, "width", originalSize.x);
            size.y = node.getDimenAttr(GuiManager.NS, "height", originalSize.y);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BitmapDrawable clone() {
        try {
            BitmapDrawable result = new BitmapDrawable(src);
            if (size != null) result.setSize(size.clone());
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void drawShadow(Point where, GLColor color, int size) {
        return;
    }

    @Override
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
