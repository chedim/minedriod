package com.onkiup.minedroid.gui.views;

import java.awt.*;

import com.onkiup.minedroid.Context;
import com.onkiup.minedroid.gui.GuiManager;
import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.drawables.TrueTypeDrawable;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.resources.Style;
import com.onkiup.minedroid.gui.resources.ValueLink;

/**
 * Shows some text
 */
public class TextView extends ContentView {
    protected TrueTypeDrawable text = new TrueTypeDrawable("", 0);

    protected boolean multiline;

    public TextView(Context context) {
        super(context);
        text.setTextSize(GuiManager.getTheme(context).getStyle(getThemeStyleName()).getInt("fontSize", 14));
        vGravity = VGravity.TOP;
        hGravity = HGravity.LEFT;
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
        return text.getOriginalSize();
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
    public void setFontSize(int size) {
        text.setTextSize(size);
    }

    @Override
    public void drawContents(float partialTicks) {
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
        if (layout.width == Layout.WRAP_CONTENT) {
            result.setInnerWidth(textSize.x);
        } else if (layout.width == Layout.MATCH_PARENT && boundaries != null) {
            result.setOuterWidth(boundaries.x);
        }

        if (layout.height == Layout.WRAP_CONTENT) {
            result.setInnerHeight(Math.max(textSize.y, text.getMaxCharHeight()));
        } else if (layout.height == Layout.MATCH_PARENT && boundaries != null) {
            result.setOuterHeight(boundaries.y);
        }
        return result;
    }

    @Override
    public void setDebug(boolean debugDraw) {
        super.setDebug(debugDraw);
        text.setDebug(debugDraw);
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

        String text = node.getStringAttr(GuiManager.NS, "text", "");
        setText(text);
        setTextSize(node.getIntegerAttr(GuiManager.NS, "fontSize", style, 14));
        setColor(node.getColorAttr(GuiManager.NS, "color", style, 0l));
        setMultiline(node.getBoolAttr(GuiManager.NS, "multiline", style, false));
        setFontName(node.getStringAttr(GuiManager.NS, "fontName", style, "Verdana"));
    }

    public void setTextSize(int fontSize) {
        text.setTextSize(fontSize);
    }

    public void setColor(long color) {
        text.setColor(color);
    }

    public long getColor() {
        return text.getColor().getColor().raw();
    }

    @Override
    protected String getThemeStyleName() {
        return "text_view";
    }

    public boolean isMultiline() {
        return multiline;
    }

    public void setMultiline(boolean multiline) {
        this.multiline = multiline;
        text.setMultiline(multiline);
    }

    public void setFontName(String fontName) {
        text.setFontName(fontName);
    }

    public String getFontName() {
        return text.getFontName();
    }
}

