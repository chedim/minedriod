package com.onkiup.minedroid.gui.betterfonts;

import java.awt.*;
import java.awt.image.BufferedImage;

import net.minecraft.client.Minecraft;

/**
 * Created by chedim on 8/7/15.
 */
public class FontRenderer {
    protected Font font;
    protected StringCache renderer;
    protected boolean antiAliasEnabled;

    public FontRenderer(StringCache renderer) {
        this.renderer = renderer;
    }

    public StringCache.Entry cacheString(String s) {
        renderer.setDefaultFont(font, antiAliasEnabled);
        return renderer.cacheString(s);
    }

    public StringCache.Entry renderString(String str, int startX, int startY, int initialColor, boolean shadowFlag) {
        renderer.setDefaultFont(font, antiAliasEnabled);
        return renderer.renderString(str, startX, startY, initialColor, shadowFlag);
    }

    public void setDefaultFont(Font f, boolean antiAlias) {
        f = font;
        antiAliasEnabled = antiAlias;
    }


    public void setDefaultFont(String name, int size, boolean antiAlias) {
        antiAliasEnabled = antiAlias;
        font = renderer.setDefaultFont(name, size, antiAlias);
    }

    public int sizeStringToWidth(String str, int width) {
        renderer.setDefaultFont(font, antiAliasEnabled);
        return renderer.sizeStringToWidth(str, width);
    }

    public String trimStringToWidth(String str, int width, boolean reverse) {
        renderer.setDefaultFont(font, antiAliasEnabled);
        return renderer.trimStringToWidth(str, width, reverse);
    }

    public Font getFont() {
        return font;
    }

    public FontMetrics getMetrics() {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = img.getGraphics();
        return graphics.getFontMetrics(getFont());
    }
}
