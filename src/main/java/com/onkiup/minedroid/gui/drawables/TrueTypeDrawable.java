package com.onkiup.minedroid.gui.drawables;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import com.onkiup.minedroid.gui.GuiManager;
import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.betterfonts.FontRenderer;
import com.onkiup.minedroid.gui.betterfonts.StringCache;
import com.onkiup.minedroid.gui.primitives.Color;
import com.onkiup.minedroid.gui.primitives.GLColor;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.resources.Style;
import com.onkiup.minedroid.gui.resources.ValueLink;
import net.minecraft.client.renderer.GlStateManager;
import org.apache.commons.lang3.StringUtils;

/**
 * Draws Text
 */
public class TrueTypeDrawable extends ColorDrawable {

    /**
     * Text to draw
     */
    protected String text;
    /**
     * Font texture scale factor
     */
    protected int fontSize = 14;

    /**
     * Font name
     */
    protected String fontName;

    /**
     * Current font size
     */
    protected Point size = new Point(0, 0);
    /**
     * Are line breaks allowed?
     */
    protected boolean multiline;

    protected FontRenderer renderer;
    protected static int[] colors;

    static {
        try {
//            Field f = Minecraft.getMinecraft().fontRendererObj.getClass().getDeclaredField("locationFontTexture");
//            f.setAccessible(true);
//            ResourceLocation fontLocation = (ResourceLocation) f.get(Minecraft.getMinecraft().fontRendererObj);
//            BufferedImage bufferedimage = TextureUtil.readBufferedImage(Minecraft.getMinecraft().getResourceManager().getResource(fontLocation).getInputStream());
//            charHeight = bufferedimage.getHeight() / 16;
        } catch (Exception ioexception) {
            throw new RuntimeException(ioexception);
        }

    }

    private boolean debug;

    public TrueTypeDrawable() {
        super();
        renderer = GuiManager.getBetterFonts();
        fontName = Font.MONOSPACED;
    }

    public TrueTypeDrawable(GLColor color) {
        this();
        setColor(color);
    }

    public TrueTypeDrawable(String text, GLColor color) {
        this(color);
        setText(text);
    }

    public TrueTypeDrawable(String text, Color color) {
        this(text, new GLColor(color));
    }

    public TrueTypeDrawable(String text, int raw) {
        this(text, new Color(raw));
    }

    /**
     * Sets text size
     *
     * @param fontSize new text size
     */
    public void setTextSize(int fontSize) {
        this.fontSize = fontSize;
    }

    /**
     * Returns text value
     *
     * @return Text value
     */
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        linesCache = null;
    }

    /**
     * Sets text value from value link
     *
     * @param text New text value
     */
    public void setText(ValueLink text) {
        this.setText(text.toString());
    }

    /**
     * Sets formatted text value
     *
     * @param s    Format string
     * @param args Format arguments
     */
    public void setText(String s, Object... args) {
        setText(String.format(s, args));
    }

    /**
     * Sets formatted text value from value link
     *
     * @param s    Value link to format string
     * @param args Format arguments
     */
    public void setText(ValueLink s, Object... args) {
        setText(s.toString(), args);
    }

    /**
     * Returns current text boundaries
     *
     * @return text boundaries
     */
    public float getTextSize() {
        return fontSize;
    }

    @Override
    public void draw(Point where) {
        // setting global output scale
        if (size.x < 1) {
            return;
        }

//        GL11.glPushMatrix();
//        GL11.glScalef(fontSize, fontSize, fontSize);

        GlStateManager.enableTexture2D();
        // scaling coordinates
        int left = (int) Math.round(where.x);
        int top = (int) Math.round(where.y);

        // drawing text
        List<String> lines = fitString(text, size.x);
        renderer.setDefaultFont(fontName, fontSize, true);
        DebugDrawable d = new DebugDrawable(new com.onkiup.minedroid.gui.primitives.Color(0x66ff0000));
        int maxHeight = getMaxCharHeight();
        int dBase = getBaseLine();
        for (String line : lines) {
            if (!line.equals("\n")) {
                String cleaned = line.replaceAll("\\n+$", "");
                StringCache.Entry entry = renderer.cacheString(cleaned);
                double base = entry.getScaledBase();
                int drawTop = top + (int) (dBase - base);
                entry = renderer.renderString(cleaned, left, drawTop, (int) color.getColor().raw(), false);
                d.setSize(new Point(entry.getScaledAdvance(), (int) entry.getScaledHeight()));
            }
            if (debug) {
                d.draw(new Point(left, top));
            }
            top += maxHeight;
        }

//         resetting output scale
//        GL11.glScalef(1 / fontSize, 1 / fontSize, 1 / fontSize);
//        GL11.glPopMatrix();
        GlStateManager.disableTexture2D();
    }

    @Override
    public void setSize(Point size) {
        this.size = size;
    }

    @Override
    public Point getSize() {
        return size;
    }

    @Override
    public Point getOriginalSize() {
        renderer.setDefaultFont(fontName, fontSize, true);
        Point osize = new Point(0, 0);
        if (text != null) {
            String[] lines = text.split("\n");
            for (String line : lines) {
                StringCache.Entry entry = renderer.cacheString(line);
                osize.x = Math.max(entry.getScaledAdvance(), osize.x);
                osize.y += (int) entry.getScaledHeight();
            }
        }
        return osize;
    }

    public Integer getMaxCharHeight() {
        renderer.setDefaultFont(fontName, fontSize, true);
        StringCache.Entry maxHeight = renderer.cacheString("|");
        return (int) maxHeight.getScaledHeight();
    }

    public Integer getBaseLine() {
        renderer.setDefaultFont(fontName, fontSize, true);
        StringCache.Entry maxHeight = renderer.cacheString("|");
        return (int) maxHeight.getScaledBase();
    }

    @Override
    public void inflate(XmlHelper node, Style theme) {
        Style s = theme.getStyle("text");
        setColor(node.getColorAttr(GuiManager.NS, "color", Long.valueOf(s.getInt("color", 0))));
        setText(node.getStringAttr(GuiManager.NS, "text", ""));
        setTextSize(node.getIntegerAttr(GuiManager.NS, "size", s.getInt("fontSize", 14)));
        setSize(node.getSize(GuiManager.NS, getOriginalSize()));
    }

    /**
     * Calculates last text character bottom right point for given width
     *
     * @param width Width
     * @return Calculated end point
     */
    public Point calculateEndPoint(int width) {
        return calculateEndPoint(text, width);
    }

    /**
     * Calculates last character bottom right point for given text in given width
     *
     * @param text  Text
     * @param width Width
     * @return Calculated end point
     */
    public Point calculateEndPoint(String text, int width) {
        Point result = new Point(0, 0);
        if (text == null || text.length() == 0) return result;
        List<String> lines = fitString(text, width);
        if (lines.size() == 0) return result;
        renderer.setDefaultFont(fontName, fontSize, true);
        StringCache.Entry entry = null;
        for (String line : lines) {
            entry = renderer.cacheString(line);
            result.y += entry.getScaledHeight();
        }
        result.x = entry.getScaledAdvance();
        return result;
    }

    /**
     * Calculates coordinate for text position
     *
     * @param text     Text
     * @param width    width limit
     * @param position Text position
     * @return Calculated coordinate
     */
    public Point calculatePosition(String text, int width, int position) {
        List<String> lines = fitString(text, width);
        int currentPosition = 0;
        renderer.setDefaultFont(fontName, fontSize, true);
        int lineHeight = getMaxCharHeight();
        Point result = new Point(0, lineHeight);
        FontMetrics metrics = renderer.getMetrics();
        float f = GuiManager.getScale().getScaleFactor();

        if (position != 0) {
            for (int y = 0; y < lines.size(); y++) {
                String line = lines.get(y);
                if (currentPosition + line.length() < position) {
                    currentPosition += line.length();
                    result.y += lineHeight;
                } else {
                    String part = line.substring(0, position - currentPosition);
                    if (part.endsWith("\n")) {
                        part = part.substring(0, part.length() - 1);
                        result.y += lineHeight;
                        result.x = 0;
                    } else {
                        result.x = (int) Math.ceil(metrics.stringWidth(part) / f);
                    }
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Splits text to fit given width
     *
     * @param text  Text to split
     * @param width Width limit
     * @return Text lines
     */
    public List<String> getTextLines(String text, int width) {
        return fitString(text, width);
    }

    /**
     * Splits text until given position to fit given width
     *
     * @param text     Text to split
     * @param width    Width limit
     * @param position Position limit
     * @return Text lines
     */
    public List<String>[] getSplitLines(String text, int width, int position) {
        List[] result = new List[]{new ArrayList<String>(), new ArrayList<String>()};
        List<String> lines = fitString(text, width);
        int index = 0;
        int currentPosition = 0;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (index == 0) {
                if (currentPosition + line.length() > position) {
                    result[0].add(line.substring(0, position - currentPosition));
                    result[1].add(line.substring(position - currentPosition));
                    index = 1;
                    continue;
                } else if (currentPosition + line.length() == position) {
                    index = 1;
                    result[0].add(line);
                    if (i < lines.size() - 1) {
                        result[0].add("");
                    }
                    continue;
                }

                result[0].add(line);
                currentPosition += line.length();
            } else {
                result[1].add(line);
            }
        }

        return result;
    }

    /**
     * Allows/dissalows line breaks
     *
     * @param multiline line breaks flag
     */
    public void setMultiline(boolean multiline) {
        this.multiline = multiline;
    }

    public List<String> fitString(String text, int width) {
        List<String> result = new ArrayList<String>();
        String[] lines = StringUtils.splitByWholeSeparatorPreserveAllTokens(text, "\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            List<String> sub = fitString(line, width, fontSize);
            if (i < lines.length - 1) {
                if (sub.size() == 0) {
                    sub.add("\n");
                } else {
                    sub.set(sub.size() - 1, sub.get(sub.size() - 1) + "\n");
                }
            }
            result.addAll(sub);
        }
        return result;
    }

    protected List<String> linesCache;

    /**
     * Splits string to make it fit given width
     *
     * @param text  Text to split
     * @param width Width limit
     * @return Splitted text
     */
    public List<String> fitString(String text, int width, float fontSize) {
        if (linesCache != null) return linesCache;
        List<String> result = new ArrayList<String>();
        renderer.setDefaultFont(fontName, (int) fontSize, true);
        int left = 0;
        while (left < text.length()) {
            int fit = renderer.sizeStringToWidth(text.substring(left), width);
            if (fit == 0) break;
            result.add(text.substring(left, left + fit));
            left += fit;
        }
        return result;
    }

    @Override
    public TrueTypeDrawable clone() {
        TrueTypeDrawable result = new TrueTypeDrawable(text, color.clone());
        if (size != null) result.setSize(size);
        result.setTextSize(fontSize);

        return result;
    }

    @Override
    public void drawShadow(Point where, GLColor color, int size) {

    }

    @Override
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setColor(long color) {
        if (color == 0) color = 0xff000000;
        this.color = new GLColor(new Color(color));
    }
}
