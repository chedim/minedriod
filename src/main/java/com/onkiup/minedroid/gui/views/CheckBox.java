package com.onkiup.minedroid.gui.views;

import com.onkiup.minedroid.Context;
import com.onkiup.minedroid.gui.GuiManager;
import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.drawables.Drawable;
import com.onkiup.minedroid.gui.drawables.TextDrawable;
import com.onkiup.minedroid.gui.events.Event;
import com.onkiup.minedroid.gui.events.MouseEvent;
import com.onkiup.minedroid.gui.primitives.Rect;
import com.onkiup.minedroid.gui.resources.Style;

/**
 * Created by chedim on 5/31/15.
 */
public class CheckBox extends ContentView {

    protected Drawable check;
    protected boolean value;
    protected TextDrawable label;



    public CheckBox(Context context) {
        super(context);
    }

    @Override
    public void drawContents(float partialTicks) {
        if (value) {
            Rect inner = getResolvedLayout().getInnerRect();
            check.setSize(inner.getSize());
            check.draw(this.getPosition().add(inner.coords()));
        }
    }

    @Override
    public void clear() {

    }

    @Override
    protected String getThemeStyleName() {
        return "checkbox";
    }

    public boolean value() {
        return value;
    }

    public void value(boolean checked) {
        boolean old = value;
        value = checked;

        if (old != value) {
            fireEvent(OnChange.class, value);
        }
    }

    @Override
    public void inflate(XmlHelper node, Style theme) {
        super.inflate(node, theme);
        check = node.getDrawableAttr(GuiManager.NS, "check", style, null);
    }

    @Override
    public void handleMouseEvent(MouseEvent event) {
        super.handleMouseEvent(event);

        if (!event.cancel && event.type == View.OnMouseUp.class && event.button == 0) {
            value = !value;
            fireEvent(OnChange.class, value);
        }
    }

    public interface OnChange extends Event<Boolean> {

    }
}
