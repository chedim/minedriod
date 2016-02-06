package com.onkiup.minedroid.gui.views;

import com.onkiup.minedroid.Context;
import com.onkiup.minedroid.gui.GuiManager;
import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.drawables.Drawable;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.primitives.Rect;
import com.onkiup.minedroid.gui.resources.Style;

/**
 * Created by chedim on 5/31/15.
 */
public class CheckBox extends View {

    protected Drawable check;
    protected boolean value;

    public CheckBox(Context context) {
        super(context);
    }

    @Override
    public void onDraw(float partialTicks) {
        // drawing background
        super.onDraw(partialTicks);
        if (value) {
            Rect inner = getResolvedLayout().getInnerRect();
            check.setSize(inner.getSize());
            check.draw(inner.coords());
        }
    }

    @Override
    protected String getThemeStyleName() {
        return "checkbox";
    }

    public boolean value() {
        return value;
    }

    public void value(boolean checked) {
        this.value = checked;
    }

    @Override
    public void inflate(XmlHelper node, Style theme) {
        super.inflate(node, theme);
        check = node.getDrawableAttr(GuiManager.NS, "check", style, null);
    }
}
