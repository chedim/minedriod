package com.onkiup.minedroid.gui.drawables;

import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.themes.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Created by chedim on 4/25/15.
 */
public class TextureDrawable implements Drawable {

    protected ResourceLocation src;
    protected Point size, originalSize;
    protected Point textureOffset;

    public TextureDrawable(ResourceLocation src) {
        this.src = src;
        this.size = new Point(255, 255);
        originalSize = size.clone();
    }

    @Override
    public void draw(Point where) {
        double left = where.x;
        double top = where.y;
        double right = where.x + size.x;
        double bottom = where.y + size.y;

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        Minecraft.getMinecraft().getTextureManager().bindTexture(src);
        GlStateManager.enableTexture2D();
        worldrenderer.startDrawingQuads();


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
        return originalSize;
    }

    @Override
    public void inflate(XmlHelper node, Theme theme) {

    }

    public TextureDrawable clone() {
        return null;
    }
}
