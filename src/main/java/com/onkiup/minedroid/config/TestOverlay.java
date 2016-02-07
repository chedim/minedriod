package com.onkiup.minedroid.config;

import com.onkiup.minedroid.R;
import com.onkiup.minedroid.gui.views.CheckBox;
import com.onkiup.minedroid.gui.views.View;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class TestOverlay extends com.onkiup.minedroid.gui.ConfigOverlay {
    public TestOverlay(GuiScreen parent) {
        super(parent);
    }

    private CheckBox.OnChange debugListener = new CheckBox.OnChange() {
        @Override
        public void handle(Boolean event) {
            TestOverlay.this.getContentView().setDebug(event);
        }
    };

    @Override
    protected ResourceLocation getContentLayout() {
        return R.layout.minedroid_test;
    }

    @Override
    protected void fill(View content) {
        findViewById(R.id.debug).on(debugListener);
    }

    @Override
    protected void onStart() {

    }

    @Override
    protected void onStop() {

    }
}
