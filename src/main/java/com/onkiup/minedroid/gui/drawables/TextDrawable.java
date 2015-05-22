package com.onkiup.minedroid.gui.drawables;

import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.resources.ValueLink;
import com.onkiup.minedroid.gui.themes.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chedim on 4/25/15.
 */
public class TextDrawable implements Drawable {

    protected String text;
    protected long color;
    protected float fontSize = 1;
    protected Point size = new Point(0, 0), originalSize;
    protected boolean multiline;

    protected static int charHeight = 0;

    static {
        try
        {
//            Field f = Minecraft.getMinecraft().fontRendererObj.getClass().getDeclaredField("locationFontTexture");
//            f.setAccessible(true);
//            ResourceLocation fontLocation = (ResourceLocation) f.get(Minecraft.getMinecraft().fontRendererObj);
//            BufferedImage bufferedimage = TextureUtil.readBufferedImage(Minecraft.getMinecraft().getResourceManager().getResource(fontLocation).getInputStream());
//            charHeight = bufferedimage.getHeight() / 16;
            charHeight = Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT;
        }
        catch (Exception ioexception)
        {
            throw new RuntimeException(ioexception);
        }

    }

    public TextDrawable() {
    }

    public TextDrawable(String text, int color) {
        setText(text);
        this.color = color;
    }

    public void setTextSize(float fontSize) {
        this.fontSize = fontSize;
        size.x *= fontSize;
        size.y *= fontSize;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
        FontRenderer renderer = Minecraft.getMinecraft().getRenderManager().getFontRenderer();
        size.x = renderer.getStringWidth(text);
        size.y = charHeight;
        originalSize = size.clone();
    }

    public void setText(ValueLink text) {
        this.setText(text.toString());
    }

    public void setText(String s, Object... args) {
        setText(String.format(s, args));
    }

    public void setText(ValueLink s, Object... args) {
        setText(s.toString(), args);
    }

    public float getTextSize() {
        return fontSize;
    }

    @Override
    public void draw(Point where) {
        FontRenderer renderer = Minecraft.getMinecraft().getRenderManager().getFontRenderer();
        // setting global output scale
        if (size.x < 1) {
            return;
        }

        GL11.glPushMatrix();
        GL11.glScalef(fontSize, fontSize, fontSize);

        // scaling coordinates
        int left = (int) (where.x / fontSize);
        int top = (int) (where.y / fontSize);

        // drawing text
        List<String> lines = fitString(text, size.x);
        for (String line: lines) {
            renderer.drawString(line.replaceAll("\\n+$",""), left, top, (int) color);
            top += charHeight * fontSize;
        }

        // resetting output scale
        GL11.glScalef(1 / fontSize, 1 / fontSize, 1 / fontSize);
        GL11.glPopMatrix();
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
        Point osize = originalSize.clone();
        osize.x *= fontSize;
        osize.y *= fontSize;
        return osize;
    }

    @Override
    public void inflate(XmlHelper node, Theme theme) {
        color = node.getColorAttr("mc", "color", 0x00000000l);
        setText(node.getStringAttr("mc", "text", ""));
        setTextSize(node.getFloatAttr("mc", "size", 1f));
        setSize(node.getSize("mc", getOriginalSize()));
    }

    public int getCharHeight() {
        return (int) (charHeight * fontSize);
    }

    public Point calculateEndPoint(int width) {
        return calculateEndPoint(text, width);
    }

    public Point calculateEndPoint(String text, int width) {
        List<String> lines = fitString(text, width);
        int lastLineWidth = getStringWidth(lines.get(lines.size() - 1));
        return new Point(lastLineWidth, Math.round(charHeight * lines.size() * fontSize));
    }

    public Point calculatePosition(String text, int width, int position) {
        List<String> lines = fitString(text, width);
        int currentPosition = 0;
        Point result = new Point(0, 0);

        if (position != 0) {
            for (int y = 0; y < lines.size(); y++) {
                String line = lines.get(y);
                for (int x = 0; x < line.length(); x++) {
                    char c = line.charAt(x);
                    if (++currentPosition == position) {
                        if (c == '\n') {
                            result.x = 0;
                            result.y = Math.round(charHeight * (y+1) * fontSize);
                        } else {
                            result.x = getStringWidth(line.substring(0, x + 1));
                            result.y = Math.round(charHeight * y * fontSize);
                        }
                        break;
                    }
                }

                if (currentPosition == position) break;
            }
        }

        result.y += Math.round(charHeight * fontSize);

        return result;
    }

    public List<String> getTextLines(String text, int width) {
        return fitString(text, width);
    }

    public List<String>[] getSplitLines(String text, int width, int position) {
        List[] result = new List[] {new ArrayList<String>(), new ArrayList<String>()};
        List<String> lines = fitString(text, width);
        int index = 0;
        int currentPosition = 0;
        for (int i =0; i<lines.size(); i++) {
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
                    if (i < lines.size() -1) {
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

    public void setMultiline(boolean multiline) {
        this.multiline = multiline;
    }

    public int getStringWidth(String text) {
        int result = 0, currentLine = 0;
        FontRenderer renderer = Minecraft.getMinecraft().getRenderManager().getFontRenderer();

        for (int i=0; i<text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\n') {
                currentLine = 0;
            } else {
                currentLine += renderer.getCharWidth(c) * fontSize;
            }
            if (currentLine > result) result = currentLine;
        }

        return result;
    }

    public List<String> fitString(String text, int width) {
        FontRenderer renderer = Minecraft.getMinecraft().getRenderManager().getFontRenderer();
        List<String> result = new ArrayList<String>();
        int lineWidth = 0;
        String separators = " .,!:;)-=";
        StringBuilder line = new StringBuilder();
        if (text.length() == 0) {
            result.add("");
            return result;
        }

        for (int i=0; i<text.length(); i++) {
            char c = text.charAt(i);
            int charWidth = renderer.getCharWidth(c);
            if (c == '\n') {
                result.add(line.toString() + "\n");
                line.setLength(0);
                lineWidth = 0;
            } else if (lineWidth + charWidth <= width) {
                line.append(c);
                lineWidth += charWidth;
            } else {
                int separator = -1;
                for (int j = line.length(); j > 0; j--) {
                    if (separators.contains(String.valueOf(line.charAt(j - 1)))) {
                        separator = j;
                        break;
                    }
                }
                if (separator > -1) {
                    result.add(line.substring(0, separator));
                    line = new StringBuilder(line.substring(separator));
                } else {
                    result.add(line.toString());
                    line.setLength(0);
                }
                line.append(c);
                lineWidth = getStringWidth(line.toString());
            }
        }
        result.add(line.toString());
        return result;
    }

    @Override
    public TextDrawable clone() {
        TextDrawable result = new TextDrawable(text, (int) color);
        if (size != null) result.setSize(size);
        result.setTextSize(fontSize);

        return result;
    }
}
