package com.onkiup.minecraft.gui.views;

import com.onkiup.minecraft.gui.OnkiupGuiManager;
import com.onkiup.minecraft.gui.XmlHelper;
import com.onkiup.minecraft.gui.themes.Theme;

/**
 * Created by chedim on 4/26/15.
 */
public class Button extends TextView {
    public Button() {
        super();
        setBackground(OnkiupGuiManager.theme.getButtonBackground());
    }

    @Override
    public void inflate(XmlHelper node, Theme theme) {
        super.inflate(node, theme);
        if (background == null) background = theme.getButtonBackground();
    }

    public Button(String text) {
        this();
        setText(text);
    }
}
