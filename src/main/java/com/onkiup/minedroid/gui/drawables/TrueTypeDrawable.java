package com.onkiup.minedroid.gui.drawables;

import java.awt.*;
import java.awt.image.BufferedImage;
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
import net.minecraft.client.renderer.Tessellator;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;

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
        float scale = 1;// GuiManager.getDisplayScale();
//        GL11.glScalef(1/scale, 1/scale, 1/scale);

        GlStateManager.enableTexture2D();
        // scaling coordinates
        int left = (int) Math.round(where.x);
        int top = (int) Math.round(where.y);

        // drawing text
        List<String> lines = splitString(text);
        setFont();
        DebugDrawable d = new DebugDrawable(new com.onkiup.minedroid.gui.primitives.Color(0x66ff0000));
        int maxHeight = getMaxCharHeight();
        int dBase = getBaseLine();
        for (String line : lines) {
            if (!line.equals("\n")) {
                String cleaned = line.replaceAll("\\n+$", "");
                StringCache.Entry entry = renderer.cacheString(cleaned);
                double base = entry.getScaledBase();
                int drawTop = top + (int) (dBase - base);
                entry = renderer.renderString(cleaned, Math.round(left * scale), Math.round(drawTop * scale), (int) color.getColor().raw(), true);
                if (entry != null && debug) {
                    d.setSize(new Point(entry.getScaledAdvance(), (int) entry.getScaledHeight()));
                }
            }
            if (debug) {
                d.draw(new Point(left, top));
            }
            top += maxHeight;
        }

//         resetting output scale
//        GL11.glScalef(scale, scale, scale);
//        GL11.glPopMatrix();
        GlStateManager.disableTexture2D();
    }

    private void setFont() {
        renderer.setDefaultFont(fontName, GuiManager.scale(fontSize), true);
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
        setFont();
        Point osize = new Point(0, 0);
        int lineHeight = getMaxCharHeight();

        if (text != null) {
            String[] lines = text.split("\n");
            for (String line : lines) {
                int width = getStringWidth(line);
                osize.x = Math.max(osize.x, width);
                osize.y += lineHeight;
            }
        }
        return osize;
    }

    public Integer getMaxCharHeight() {
        setFont();
        StringCache.Entry maxHeight = renderer.cacheString("|");
        return (int) maxHeight.getScaledHeight();
    }

    public Integer getBaseLine() {
        setFont();
        StringCache.Entry maxHeight = renderer.cacheString("|");
        return (int) maxHeight.getScaledBase();
    }

    @Override
    public void inflate(XmlHelper node, Style theme) {
        Style s = theme.getStyle("text");
        setColor(node.getColorAttr(GuiManager.NS, "color", Long.valueOf(s.getInt("color", 0))));
        setText(node.getStringAttr(GuiManager.NS, "text", ""));
        setTextSize(node.getIntegerAttr(GuiManager.NS, "size", s.getInt("size", 14)));
        setFontName(node.getStringAttr(GuiManager.NS, "font", s.getString("font", "Verdana")));
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
        List<String> lines = splitString(text);
        if (lines.size() == 0) return result;
        setFont();
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
        List<String> lines = splitString(text);
        int currentPosition = 0;
        setFont();
        int lineHeight = getMaxCharHeight();
        Point result = new Point(0, lineHeight);
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
                        result.x = (int) Math.ceil(getStringWidth(part));
                    }
                    break;
                }
            }
        }

        return result;
    }

    public int getStringWidth(String string) {
        setFont();
        FontMetrics metrics = renderer.getMetrics();
        float f = GuiManager.getScale().getScaleFactor();
        return (int) Math.ceil(metrics.stringWidth(string) / f);
    }

    public int getCharWidth(char c) {
        setFont();
        FontMetrics metrics = renderer.getMetrics();
        float f = GuiManager.getScale().getScaleFactor();
        return (int) Math.ceil(metrics.charWidth(c) / f);
    }

    /**
     * Splits text to fit given width
     *
     * @param text  Text to split
     * @param width Width limit
     * @return Text lines
     */
    public List<String> getTextLines(String text, int width) {
        return splitString(text);
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
        List<String> lines = splitString(text);
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

    public List<String> splitString(String text) {
        List<String> result = new ArrayList<String>();
        String[] lines = StringUtils.splitByWholeSeparatorPreserveAllTokens(text, "\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (i < lines.length - 1) {
                line += "\n";
            }
            result.add(line);
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
        renderer.setDefaultFont(fontName, GuiManager.scale((int) fontSize), true);
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

    public int getPosition(Point point) {
        List<String> lines = splitString(text);
        int charHeight = getMaxCharHeight();
        int bottom = charHeight;
        int position = 0;
        for (int i=0; i<lines.size(); i++) {
            String line = lines.get(i);
            if (bottom > point.y || i == lines.size() - 1) {
                // our line (or the last one)!
                int width = getStringWidth(line);
                if (width < point.x) {
                    if (line.endsWith("\n")) {
                        position--;
                    }
                    return position + line.length();    // -1 is for \n character
                } else if (point.x < 0) {
                    return position;
                } else {
                    char[] chars = line.toCharArray();
                    width = 0;
                    for (int j=0; j<chars.length; j++) {
                        int w = getCharWidth(chars[j]);
                        width += w;
                        if (width > point.x) {
                            int o = w - Math.abs(width - point.x);
                            return position + Math.round(o / (float)w);
                        }
                        position++;
                    }
                }
            } else {
                position += line.length();
            }
        }

        return position; // way out of the size of the string ^_^
    }


    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }
}
