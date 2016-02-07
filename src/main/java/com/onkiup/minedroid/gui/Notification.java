package com.onkiup.minedroid.gui;

import com.onkiup.minedroid.Context;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.primitives.TimedPoint;
import com.onkiup.minedroid.gui.views.View;
import net.minecraft.client.Minecraft;

import java.io.IOException;

/**
 * Created by chedim on 8/10/15.
 */
public abstract class Notification extends Overlay {

    protected float animSpeed = 0.5f;
    protected TimedPoint position, targetPosition;
    protected int leftTime = 200;

    public Notification(Context context) {
        super(context);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        container.setPosition(position);
        super.drawScreen(mouseX, mouseY, partialTicks);
        float k = (targetPosition.time - position.time++) * animSpeed;
        if (k < 0) k = 1;
        if (position.x != targetPosition.x) {
            position.x += (int) ((targetPosition.x - position.x) / k);
        }

        if (position.y != targetPosition.y) {
            position.y += (int) ((targetPosition.y - position.y) / k);
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        container.getLayout().width = View.Layout.WRAP_CONTENT;
        container.getLayout().height = View.Layout.WRAP_CONTENT;
        View.Layout measured = container.measure(null);
        container.resolveLayout(measured);
        contentView.setElevation(4);
        container.setBackground(null);
        // out of the screen
        position = new TimedPoint(width, 0, 0);
    }

    public Point getPosition() {
        return new Point(position.x, position.y);
    }


    public void setTargetPosition(Point target) {
        position.time = 0;
        targetPosition = new TimedPoint(target, (int) (20 * animSpeed));
    }

    public void setPosition(Point p) {
        position = new TimedPoint(p, 0);
    }

    public int getTimeLeft() {
        return leftTime--;
    }

    public Point getTargetPosition() {
        return new Point(targetPosition.x, targetPosition.y);
    }

    public float getAnimationSpeed() {
        return animSpeed;
    }

    @Override
    public boolean isCancelable() {
        return false;
    }

    public int getWidth() {
        return contentView.getResolvedLayout().getOuterWidth();
    }

    public int getHeight() {
        return contentView.getResolvedLayout().getOuterHeight();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        this.mc = mc;


        this.width = width;
        this.height = height;
    }
}
