package com.onkiup.minedroid.gui.views;

import com.onkiup.minedroid.gui.MineDroid;
import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.themes.Theme;

/**
 * Created by chedim on 4/26/15.
 */
public class Button extends TextView {
    public Button() {
        super();
        setBackground(MineDroid.theme.getButtonBackground());
    }

    @Override
    public void inflate(XmlHelper node, Theme theme) {
        super.inflate(node, theme);
        if (background == null) background = theme.getButtonBackground().clone();
    }

    public Button(String text) {
        this();
        setText(text);
    }
}
