package com.onkiup.minedroid.gui.themes;

import com.onkiup.minedroid.gui.drawables.Drawable;
import com.onkiup.minedroid.gui.primitives.Rect;

/**
 * MineDroid views default attribute values
 */
public interface Theme {
    /**
     * Returns default font color
     * @return Default font color
     */
    int getFontColor();

    /**
     *
     * @return Default font size
     */
    float getFontSize();

    /**
     *
     * @return Default header font size
     */
    float getHeaderSize();

    /**
     *
     * @return Default overlay content view background
     */
    Drawable getOverlayDrawable();

    /**
     *
     * @return Default overlay container view background
     */
    Drawable getOverlayBackgroundDrawable();

    /**
     *
     * @return Default elements padding
     */
    Rect getDefaultPadding();

    /**
     *
     * @return Default elements margin
     */
    Rect getDefaultMargin();

    /**
     *
     * @return Default button background
     */
    Drawable getButtonBackground();
}
