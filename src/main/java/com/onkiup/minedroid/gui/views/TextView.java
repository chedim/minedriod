package com.onkiup.minedroid.gui.views;

import com.onkiup.minedroid.gui.Context;
import com.onkiup.minedroid.gui.MineDroid;
import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.drawables.TextDrawable;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.resources.ValueLink;
import com.onkiup.minedroid.gui.themes.Theme;

/**
 * Created by chedim on 4/25/15.
 */
public class TextView extends ContentView {
    protected TextDrawable text = new TextDrawable("", MineDroid.theme.getFontColor());

    public TextView(Context context) {
        super(context);
        text.setTextSize(MineDroid.theme.getFontSize());
        vGravity = VGravity.CENTER;
        hGravity = HGravity.CENTER;
    }

    public TextView(Context context, String text) {
        this(context);
        this.text.setText(text);
    }

    protected Point getTextSize() {
        Point originalSize = text.getOriginalSize();
        int height = text.calculateEndPoint(resolvedLayout.getInnerWidth()).y;
        Point textSize = new Point(Math.min(originalSize.x, resolvedLayout.getInnerWidth()), height);
        return textSize;
    }

    public float getFontSize() {
        return text.getTextSize();
    }

    public void setFontSize(float size) {
        text.setTextSize(size);
    }

    @Override
    public void drawContents() {
        Point textSize = getTextSize();
        text.setSize(textSize);
        Point offset = getGravityOffset(textSize);
        if (textSize.y > resolvedLayout.getInnerHeight()) offset.y = resolvedLayout.getInnerHeight() - textSize.y;

        text.draw(position.add(offset).add(resolvedLayout.padding.coords()));
    }

    @Override
    public void clear() {
        text.setText("");
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

    public void setText(String text, Object... args) {
        this.text.setText(text, args);
    }

    public void setText(ValueLink text) {
        this.text.setText(text);
    }

    public void setText(ValueLink text, Object... args) {
        this.text.setText(text, args);
    }

    @Override
    public void inflate(XmlHelper node, Theme theme) {
        super.inflate(node, theme);
        String text = node.getLocalizedAttr(MineDroid.NS, "text", "");
        setText(text);
        this.text.setTextSize(node.getFloatAttr(MineDroid.NS, "fontSize", 1f));
    }
}

