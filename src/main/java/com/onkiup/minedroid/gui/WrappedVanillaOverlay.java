package com.onkiup.minedroid.gui;

import com.onkiup.minedroid.Context;
import com.onkiup.minedroid.gui.views.VanillaGuiView;
import com.onkiup.minedroid.gui.views.View;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

/**
 * Created by chedim on 8/5/15.
 */
public class WrappedVanillaOverlay extends Overlay {

    protected GuiScreen screen;

    public WrappedVanillaOverlay(Context context, GuiScreen screen) {
        this(context);
        this.screen = screen;
    }

    public WrappedVanillaOverlay(Context context) {
        super(context);
    }

    @Override
    protected ResourceLocation getContentLayout() {
        return null;
    }

    @Override
    protected View createContentView() {
        View content = new VanillaGuiView(this, screen);
        content.getLayout().width = View.Layout.MATCH_PARENT;
        content.getLayout().height = View.Layout.MATCH_PARENT;
        return content;
    }

    @Override
    protected void fill(View content) {

    }

    @Override
    protected void onStart() {

    }

    @Override
    protected void onStop() {

    }
}
