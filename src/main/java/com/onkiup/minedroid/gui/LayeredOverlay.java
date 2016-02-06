package com.onkiup.minedroid.gui;

import com.onkiup.minedroid.Context;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.primitives.Rect;
import com.onkiup.minedroid.gui.views.View;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by chedim on 8/5/15.
 */
public class LayeredOverlay extends GuiScreen {
    protected Class R;

    protected Overlay.State state = Overlay.State.INITIALIZING;
    protected ArrayList<Overlay> overlays = new ArrayList<Overlay>();


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (state == Overlay.State.DISMISSED) {
            if (this == mc.currentScreen) mc.thePlayer.closeScreen();
            return;
        }
        ArrayList<Overlay> remove = new ArrayList<Overlay>();

        for (int i=0; i<overlays.size(); i++) {
            Overlay overlay = overlays.get(i);
            if (overlay.getState() == Overlay.State.DISMISSED) {
                overlay.onGuiClosed();
                remove.add(overlay);
            } else if (overlay.getState() == Overlay.State.INITIALIZING) {
                overlay.setWorldAndResolution(mc, width, height);
                overlay.initGui();
                overlay.onStart();
            } else {
                if (i == overlays.size() - 1) {
                    overlay.getContentView().setElevation(4);
                } else {
                    overlay.getContentView().setElevation(2);
                }
                overlay.drawScreen(mouseX, mouseY, partialTicks);
            }
        }

        overlays.removeAll(remove);

        if (this.overlays.size() == 0 && this.state == Overlay.State.STARTED) {
            this.state = Overlay.State.DISMISSED;
            return;
        }

        this.state = Overlay.State.STARTED;
    }

    @Override
    public void initGui() {
        if (this.state == Overlay.State.INITIALIZING) {
            for (Overlay layer: overlays) {
                layer.initGui();
            }

            state = Overlay.State.CREATED;
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        NotificationManager.handleMouseFromLayeredOverlay();
        if (overlays.size() == 0) return;
        Point event = new Point(Mouse.getEventX() * this.width / this.mc.displayWidth,
            this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1
        );
        Boolean btnDown = Mouse.getEventButton() != -1 && Mouse.getEventButtonState();


        Overlay focus = null, oldFocus = overlays.get(overlays.size()-1);
        for (int i = overlays.size() - 1; i > -1; i--) {
            Overlay overlay = overlays.get(i);
            View content = overlay.getContentView();
            if (!(content.isLayoutResolved() && content.isPositioned())) continue;
            Rect area = content.getRectangle();
            if (area.contains(event)) {
                overlay.handleMouseInput();
                focus = overlay;
                break;
            }
            if (overlay.isModal()) break;
        }

        if (focus != null && btnDown && !oldFocus.isModal()) {
            overlays.remove(focus);
            overlays.add(focus);
        }
    }


    @Override
    public void handleKeyboardInput() throws IOException {
        if (overlays.size() > 0) {
            overlays.get(overlays.size()-1).handleKeyboardInput();
        }
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        super.setWorldAndResolution(mc, width, height);
        for (Overlay overlay: overlays) {
            overlay.setWorldAndResolution(mc, width, height);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        for (Overlay overlay: overlays)
            if (overlay.doesGuiPauseGame()) return true;

        return false;
    }



    public void dismiss() {
        for (Overlay overlay: overlays) {
            overlay.dismiss();
        }
    }

    public void addOverlay(Overlay overlay) {
        overlays.add(overlay);
    }
}
