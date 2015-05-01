package com.onkiup.minecraft.gui.drawables;

import com.onkiup.minecraft.gui.XmlHelper;
import com.onkiup.minecraft.gui.primitives.Point;
import com.onkiup.minecraft.gui.themes.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;

/**
 * Created by chedim on 4/25/15.
 */
public class TextDrawable implements Drawable {

    protected String text;
    protected long color;
    protected float fontSize = 1;
    protected Point size = new Point(0, 0), originalSize;

    protected static int charHeight = 0;

    static {
        try
        {
            Field f = Minecraft.getMinecraft().fontRendererObj.getClass().getDeclaredField("locationFontTexture");
            f.setAccessible(true);
            ResourceLocation fontLocation = (ResourceLocation) f.get(Minecraft.getMinecraft().fontRendererObj);
            BufferedImage bufferedimage = TextureUtil.readBufferedImage(Minecraft.getMinecraft().getResourceManager().getResource(fontLocation).getInputStream());
            charHeight = bufferedimage.getHeight() / 16;
        }
        catch (Exception ioexception)
        {
            throw new RuntimeException(ioexception);
        }

    }

    public TextDrawable() {
    }

    public TextDrawable(String text, int color) {
        setText(text);
        this.color = color;
    }

    public void setTextSize(float fontSize) {
        this.fontSize = fontSize;
        size.x *= fontSize;
        size.y *= fontSize;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
        FontRenderer renderer = Minecraft.getMinecraft().getRenderManager().getFontRenderer();
        size.x = renderer.getStringWidth(text);
        size.y = charHeight;
        originalSize = size.clone();
    }

    public float getTextSize() {
        return fontSize;
    }

    @Override
    public void draw(Point where) {
        FontRenderer renderer = Minecraft.getMinecraft().getRenderManager().getFontRenderer();
        // setting global output scale
        if (size.x < 1) {
            return;
        }

        GL11.glPushMatrix();
        GL11.glScalef(fontSize, fontSize, fontSize);

        // scaling coordinates
        int left = (int) (where.x / fontSize);
        int top = (int) (where.y / fontSize);

        // drawing text
        if (size.x != originalSize.x * fontSize) {
            renderer.drawSplitString(text, left, top, size.x, (int) color);
        } else {
            renderer.drawString(text, left, top, (int) color);
        }

        // resetting output scale
        GL11.glScalef(1 / fontSize, 1 / fontSize, 1 / fontSize);
        GL11.glPopMatrix();
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
        color = node.getColorAttr("mc", "color", 0x00000000l);
        setText(node.getStringAttr("mc", "text", ""));
        setTextSize(node.getFloatAttr("mc", "size", 1f));
        setSize(node.getSize("mc", getOriginalSize()));
    }
}
