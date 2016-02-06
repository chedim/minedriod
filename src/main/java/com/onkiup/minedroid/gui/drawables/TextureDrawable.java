package com.onkiup.minedroid.gui.drawables;

import com.onkiup.minedroid.gui.GuiManager;
import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.primitives.GLColor;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.resources.Style;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Should draw a texture from a texture pack.
 * Not realized yet
 */
public class TextureDrawable implements Drawable {

    protected ITextureObject texture;
    protected Point size, originalSize = new Point(16, 16);
    protected double tLeft, tTop, tRight, tBottom;


    public TextureDrawable(ITextureObject texture, double tLeft, double tTop, double tRight, double tBottom) {
        this.texture = texture;
        this.size = new Point(255, 255);
        originalSize = size.clone();
        this.tLeft = tLeft;
        this.tTop = tTop;
        this.tRight = tRight;
        this.tBottom = tBottom;
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
        GlStateManager.bindTexture(texture.getGlTextureId());
        GlStateManager.enableTexture2D();
        worldrenderer.startDrawingQuads();


        worldrenderer.addVertexWithUV(left, bottom, 0d, tLeft, tBottom);
        worldrenderer.addVertexWithUV(right, bottom, 0d, tRight, tBottom);
        worldrenderer.addVertexWithUV(right, top, 0.0D, tRight, tTop);
        worldrenderer.addVertexWithUV(left, top, 0.0D, tLeft, tTop);
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
    public void inflate(XmlHelper node, Style theme) {
        size = node.getSize(GuiManager.NS, originalSize.clone());
        try {
            Integer textureId = node.getIntegerAttr(GuiManager.NS, "texture", null);
        } catch (NumberFormatException e) {
            ResourceLocation src = node.getResourceAttr(GuiManager.NS, "texture", null);
            if (src != null) {
                SimpleTexture texture = new SimpleTexture(src);
//                texture.loadTexture();
            }
        }
    }

    public TextureDrawable clone() {
        return new TextureDrawable(texture, tLeft, tTop, tRight, tBottom);
    }

    @Override
    public void drawShadow(Point where, GLColor color, int size) {

    }

    public void setTexture(ITextureObject texture) {
        this.texture = texture;
    }

    public ITextureObject getTexture() {
        return texture;
    }
}
