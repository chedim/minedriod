package com.onkiup.minedroid.config;

import com.onkiup.minedroid.Context;
import com.onkiup.minedroid.gui.Overlay;
import com.onkiup.minedroid.gui.views.View;
import net.minecraft.util.ResourceLocation;

public class ConfigOverlay extends Overlay {
    public ConfigOverlay(Context context) {
        super(context);
    }

    @Override
    protected ResourceLocation getContentLayout() {
        return null;
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
