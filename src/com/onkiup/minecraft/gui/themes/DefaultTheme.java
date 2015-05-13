package com.onkiup.minecraft.gui.themes;

import com.onkiup.minecraft.gui.drawables.ColorDrawable;
import com.onkiup.minecraft.gui.drawables.Drawable;
import com.onkiup.minecraft.gui.drawables.NinePatchDrawable;
import com.onkiup.minecraft.gui.drawables.StateDrawable;
import com.onkiup.minecraft.gui.primitives.Color;
import com.onkiup.minecraft.gui.primitives.Rect;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

/**
 * Created by chedim on 4/25/15.
 */
public class DefaultTheme implements Theme {

    protected static NinePatchDrawable overlayDrawable;
    protected static ColorDrawable overlayBackground = new ColorDrawable(new Color(0x66000000));
    protected static Rect defaultRectMarginPadding = new Rect(0, 0, 0, 0);
    protected static StateDrawable buttonBackground;

    static {
        try {
            overlayDrawable = new NinePatchDrawable(new ResourceLocation("korrin", "textures/ninepatch/panel"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        buttonBackground = new StateDrawable();
        buttonBackground.setDrawableForState(StateDrawable.State.DEFAULT, new ColorDrawable(0xff21aae0));
        buttonBackground.setDrawableForState(StateDrawable.State.HOVER, new ColorDrawable(0xff61c7f7));
        buttonBackground.setDrawableForState(StateDrawable.State.PRESSED, new ColorDrawable(0xff5faed5));

    }

    @Override
    public int getFontColor() {
        return 0xff000000;
    }

    @Override
    public float getFontSize() {
        return 1;
    }

    @Override
    public float getHeaderSize() {
        return 2;
    }

    @Override
    public Drawable getOverlayDrawable() {
        return overlayDrawable;
    }

    @Override
    public Drawable getOverlayBackgroundDrawable() {
        return overlayBackground;
    }

    @Override
    public Rect getDefaultPadding() {
        return defaultRectMarginPadding;
    }

    @Override
    public Rect getDefaultMargin() {
        return defaultRectMarginPadding;
    }

    @Override
    public Drawable getButtonBackground() {
        return buttonBackground;
    }
}
