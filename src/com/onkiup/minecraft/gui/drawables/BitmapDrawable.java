package com.onkiup.minecraft.gui.drawables;

import com.onkiup.minecraft.gui.XmlHelper;
import com.onkiup.minecraft.gui.primitives.Point;
import com.onkiup.minecraft.gui.themes.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by chedim on 4/25/15.
 */
public class BitmapDrawable implements Drawable {

    protected ResourceLocation src;
    protected Point size;
    protected Point originalSize;


    public BitmapDrawable(ResourceLocation src) throws IOException, OutOfMemoryError {
        setDrawable(src);
    }

    public BitmapDrawable() {

    }

    public void setDrawable(ResourceLocation src) throws IOException, OutOfMemoryError {
        this.src = src;
        InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(src).getInputStream();
        BufferedImage img = ImageIO.read(is);
        originalSize = size = new Point(img.getWidth(), img.getHeight());
    }

    @Override
    public void draw(Point where) {
        int left = where.x;
        int top = where.y;
        int right = where.x + size.x;
        int bottom = where.y + size.y;

        Minecraft.getMinecraft().getTextureManager().bindTexture(src);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.startDrawingQuads();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        worldrenderer.addVertexWithUV(right, bottom, 0, 1, 1);
        worldrenderer.addVertexWithUV(right, top, 0, 1, 0);
        worldrenderer.addVertexWithUV(left, top, 0, 0, 0);
        worldrenderer.addVertexWithUV(left, bottom, 0, 0, 1);
        tessellator.draw();

//        BorderDrawable overlay = new BorderDrawable(new Color(0x66ff0000));
//        overlay.setSize(size);
//        overlay.draw(where);
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
        return originalSize;
    }

    @Override
    public void inflate(XmlHelper node, Theme theme) {
        try {
            setDrawable((ResourceLocation) node.getResourceAttr("mc", "background", null));
            size = new Point(0, 0);
            size.x = node.getDimenAttr("mc", "width", originalSize.x);
            size.y = node.getDimenAttr("mc", "width", originalSize.y);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
