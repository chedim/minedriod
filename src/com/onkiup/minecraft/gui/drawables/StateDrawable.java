package com.onkiup.minecraft.gui.drawables;

import com.onkiup.minecraft.gui.XmlHelper;
import com.onkiup.minecraft.gui.primitives.Point;
import com.onkiup.minecraft.gui.themes.Theme;

/**
 * Created by chedim on 4/25/15.
 */
public class StateDrawable implements Drawable {
    protected Drawable[] drawables = new Drawable[5];
    protected State state = State.DEFAULT;

    public StateDrawable() {
    }

    @Override
    public void draw(Point where) {
        drawables[state.ordinal()].draw(where);
    }

    @Override
    public void setSize(Point size) {
        for (int i = 0; i<3; i++) {
            drawables[i].setSize(size);
        }
    }

    @Override
    public Point getSize() {
        return drawables[state.ordinal()].getSize();
    }

    @Override
    public Point getOriginalSize() {
        return drawables[state.ordinal()].getOriginalSize();
    }

    @Override
    public void inflate(XmlHelper node, Theme theme) {
        drawables[0] = node.getDrawableAttr("mc", "default", null);
        drawables[1] = node.getDrawableAttr("mc", "hover", drawables[0]);
        drawables[2] = node.getDrawableAttr("mc", "pressed", drawables[0]);
        drawables[3] = node.getDrawableAttr("mc", "selected", drawables[0]);
        drawables[4] = node.getDrawableAttr("mc", "focused", drawables[0]);
    }

    public void setDrawableForState(State state, Drawable drawable) {
        drawables[state.ordinal()] = drawable;
        if (state == State.DEFAULT) {
            for (int i=0; i<drawables.length; i++) {
                if (drawables[i] == null) drawables[i] = drawable;
            }
        }
    }

    public void setState(State state) {
        this.state = state;
    }
    public StateDrawable.State getState() {
        return state;
    }

    public enum State {
        DEFAULT, HOVER, PRESSED, SELECTED, FOCUSED
    }
}
