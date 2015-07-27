package com.onkiup.minedroid.gui.views;

import com.onkiup.minedroid.gui.Context;
import com.onkiup.minedroid.gui.MineDroid;
import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.drawables.TextDrawable;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.resources.Style;
import com.onkiup.minedroid.gui.resources.ValueLink;

/**
 * Shows some text
 */
public class TextView extends ContentView {
    protected TextDrawable text = new TextDrawable("", 0);

    public TextView(Context context) {
        super(context);
        text.setTextSize(MineDroid.getTheme(context).getStyle(getThemeStyleName()).getFloat("fontSize", 1f));
        vGravity = VGravity.CENTER;
        hGravity = HGravity.CENTER;
    }

    public TextView(Context context, String text) {
        this(context);
        this.text.setText(text);
    }

    /**
     *
     * @return Text size
     */
    protected Point getTextSize() {
        Point originalSize = text.getOriginalSize();
        int height = text.calculateEndPoint(resolvedLayout.getInnerWidth()).y;
        return new Point(Math.min(originalSize.x, resolvedLayout.getInnerWidth()), height);
    }

    /**
     * @return Returns current text boundaries
     */
    public float getFontSize() {
        return text.getTextSize();
    }

    /**
     * Sets font scale factor
     * @param size
     */
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

    /**
     *
     * @return text value of node
     */
    public String getText() {
        return text.getText();
    }

    /**
     * Sets text value of view
     * @param text
     */
    public void setText(String text) {
        this.text.setText(text);
    }

    /**
     * Sets formated text value of view
     * @param text format string
     * @param args format arguments
     */
    public void setText(String text, Object... args) {
        this.text.setText(text, args);
    }

    /**
     * Sets text value
     * @param text new value
     */
    public void setText(ValueLink text) {
        this.text.setText(text);
    }

    /**
     * Sets formated text value of view
     * @param text link to format string
     * @param args format arguments
     */
    public void setText(ValueLink text, Object... args) {
        this.text.setText(text, args);
    }

    @Override
    public void inflate(XmlHelper node, Style theme) {
        super.inflate(node, theme);

        float themeTextSize = style.getFloat("fontSize", 1f);
        int themeTextColor = style.getInt("color", 0);

        String text = node.getLocalizedAttr(MineDroid.NS, "text", "");
        setText(text);
        setTextSize(node.getFloatAttr(MineDroid.NS, "fontSize", themeTextSize));
        setColor(node.getIntegerAttr(MineDroid.NS, "color", themeTextColor));
    }

    public void setTextSize(Float fontSize) {
        text.setTextSize(fontSize);
    }

    public void setColor(long color) {
        text.setColor(color);
    }

    public long getColor() {
        return text.getColor();
    }

    @Override
    protected String getThemeStyleName() {
        return "text_view";
    }
}

