package com.onkiup.minedroid.gui.views;

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
        result.setInnerWidth(Math.max(result.getInnerWidth(), textSize.x));
        result.setInnerHeight(Math.max(result.getInnerHeight(), textSize.y));
//        FMLLog.info("Measured size for %s: (%d, %d)", text.getText(), result.getOuterWidth(), result.getOuterHeight());
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

        String text = node.getLocalizedAttr(GuiManager.NS, "text", "");
        setText(text);
        setTextSize(node.getIntegerAttr(GuiManager.NS, "fontSize", style, 14));
        setColor(node.getColorAttr(GuiManager.NS, "color", style, 0l));
    }

    public void setTextSize(int fontSize) {
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

