package com.onkiup.minedroid.gui.views;

import com.onkiup.minedroid.gui.Context;
import com.onkiup.minedroid.gui.MineDroid;
import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.drawables.Drawable;
import com.onkiup.minedroid.gui.resources.ResourceLink;
import com.onkiup.minedroid.gui.resources.Style;

/**
 * Button GUI element
 */
public class Button extends TextView {
    public Button(Context context) {
        super(context);
        ResourceLink bg = MineDroid.getTheme(context).getStyle("button").getResource("background", null);
        if (bg != null) {
            setBackground(MineDroid.inflateDrawable(this, bg));
        }
    }

    @Override
    public void inflate(XmlHelper node, Style theme) {
        super.inflate(node, theme);
        if (background == null) {
            ResourceLink bg = style.getResource("background", null);
            if (bg != null) {
                background = MineDroid.inflateDrawable(this, bg);
            }
        }
    }

    public Button(Context context, String text) {
        super(context, text);
    }

    @Override
    protected String getThemeStyleName() {
        return "button";
    }
}
