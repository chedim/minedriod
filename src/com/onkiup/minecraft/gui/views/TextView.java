package com.onkiup.minecraft.gui.views;

import com.onkiup.minecraft.gui.OnkiupGuiManager;
import com.onkiup.minecraft.gui.XmlHelper;
import com.onkiup.minecraft.gui.drawables.TextDrawable;
import com.onkiup.minecraft.gui.primitives.Point;
import com.onkiup.minecraft.gui.themes.Theme;

/**
 * Created by chedim on 4/25/15.
 */
public class TextView extends ContentView {
    protected TextDrawable text = new TextDrawable("", OnkiupGuiManager.theme.getFontColor());

    public TextView() {
        super();
        text.setTextSize(OnkiupGuiManager.theme.getFontSize());
        vGravity = VGravity.CENTER;
        hGravity = HGravity.CENTER;
    }

    public TextView(String text) {
        this();
        this.text.setText(text);
    }

    @Override
    public void drawContents() {
        Point textSize = text.getOriginalSize().clone();
        Point mySize = resolvedLayout.getInnerSize();
        if (textSize.x > mySize.x) textSize.x = mySize.x;
        if (textSize.y > mySize.y) textSize.y = mySize.y;
        text.setSize(textSize);
        Point offset = getGravityOffset(textSize);

        text.draw(position.add(offset).add(resolvedLayout.padding.coords()));
    }

    @Override
    public Layout measure(Point boundaries) {
        Layout result = super.measure(boundaries);
        Point textSize = text.getOriginalSize();
        result.setInnerWidth(Math.max(result.getInnerWidth(), textSize.x));
        result.setInnerHeight(Math.max(result.getInnerHeight(), textSize.y));
        return result;
    }

    public String getText() {
        return text.getText();
    }

    public void setText(String text) {
        this.text.setText(text);
    }

    @Override
    public void inflate(XmlHelper node, Theme theme) {
        super.inflate(node, theme);
        String text = node.getLocalizedAttr("mc", "text", "");
        setText(text);
    }
}

