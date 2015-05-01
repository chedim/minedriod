package com.onkiup.minecraft.gui.themes;

import com.onkiup.minecraft.gui.drawables.Drawable;
import com.onkiup.minecraft.gui.primitives.Rect;

/**
 * Created by chedim on 4/25/15.
 */
public interface Theme {
    public int getFontColor();
    public float getFontSize();
    public float getHeaderSize();

    public Drawable getOverlayDrawable();
    public Drawable getOverlayBackgroundDrawable();

    public Rect getDefaultPadding();
    public Rect getDefaultMargin();

    public Drawable getButtonBackground();
}
