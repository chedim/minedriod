package com.onkiup.minedroid.gui;

import com.onkiup.minedroid.*;
import net.minecraft.client.gui.GuiScreen;

public abstract class ConfigOverlay extends Overlay {

    private GuiScreen parent;

    public ConfigOverlay(GuiScreen parent) {
        super(com.onkiup.minedroid.MineDroid.getInstance());
        this.parent = parent;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.getState() == State.DISMISSED) {
            // Config UI dismisses a bit differently...
            parent.mc.displayGuiScreen(parent);
            return;
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
