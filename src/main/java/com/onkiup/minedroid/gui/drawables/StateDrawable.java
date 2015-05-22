package com.onkiup.minedroid.gui.drawables;

import com.onkiup.minedroid.gui.MineDroid;
import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.themes.Theme;

import java.util.List;

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
        List<XmlHelper> children = node.getChildren();
        for (XmlHelper child : children) {
            try {
                State childState = (State) child.getEnumAttr("mc", "state", State.DEFAULT);
                setDrawableForState(childState, MineDroid.processNodeDrawable(child));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
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

    public StateDrawable clone() {
        StateDrawable result = new StateDrawable();
        for (int i=0; i<drawables.length; i++)
            if (drawables[i] != null)
                result.drawables[i] = drawables[i].clone();

        result.state = state;
        return result;
    }
}
