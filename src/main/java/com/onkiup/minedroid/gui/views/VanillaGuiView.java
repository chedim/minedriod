package com.onkiup.minedroid.gui.views;

import com.onkiup.minedroid.Context;
import com.onkiup.minedroid.gui.events.KeyEvent;
import com.onkiup.minedroid.gui.events.MouseEvent;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.io.IOException;

/**
 * Created by chedim on 8/5/15.
 */
public class VanillaGuiView extends ContentView {

    protected GuiScreen screen;

    public VanillaGuiView(Context context, GuiScreen screen) {
        this(context);
        this.screen = screen;
    }

    public VanillaGuiView(Context context) {
        super(context);
    }

    @Override
    public void drawContents(float partialTicks) {
        if (screen != null) {
            screen.setWorldAndResolution(screen.mc, resolvedLayout.getInnerWidth(), resolvedLayout.getInnerHeight());
            screen.drawScreen(Mouse.getX(), Mouse.getY(), partialTicks);
        }
    }

    @Override
    public void handleMouseEvent(MouseEvent event) {
        if (screen == null) return;
        try {
            screen.handleMouseInput();
        } catch (IOException e) {

        }
    }

    @Override
    public void handleKeyboardEvent(KeyEvent event) {
        if (screen == null) return;
        try {
            screen.handleKeyboardInput();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clear() {
        screen = null;
    }

    public GuiScreen getScreen() {
        return screen;
    }

    public void setScreen(GuiScreen screen) {
        this.screen = screen;
    }

}
