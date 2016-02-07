package com.onkiup.minedroid.gui.drawables;

import com.onkiup.minedroid.gui.GuiManager;
import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.primitives.ColorPoint;
import com.onkiup.minedroid.gui.primitives.GLColor;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.resources.Style;
import com.onkiup.minedroid.gui.views.View;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import org.lwjgl.opengl.GL11;

import java.util.*;

/**
 * Created by chedim on 8/19/15.
 */
public class ShapeDrawable extends ColorDrawable implements List<Point> {
    protected ArrayList<Point> points = new ArrayList<Point>();
    protected Point originalSize;

    @Override
    public int size() {
        return points.size();
    }

    @Override
    public boolean isEmpty() {
        return points.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return points.contains(o);
    }

    @Override
    public Iterator<Point> iterator() {
        return points.iterator();
    }

    @Override
    public Object[] toArray() {
        return points.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return points.toArray(a);
    }

    @Override
    public boolean add(Point point) {
        originalSize = null;
        return points.add(point);
    }

    @Override
    public boolean remove(Object o) {
        originalSize = null;
        return points.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return points.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Point> c) {
        originalSize = null;
        return points.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Point> c) {
        originalSize = null;
        return points.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        originalSize = null;
        return points.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return points.retainAll(c);
    }

    @Override
    public void clear() {
        originalSize = null;
        points.clear();
    }

    @Override
    public Point get(int index) {
        return points.get(index);
    }

    @Override
    public Point set(int index, Point element) {
        originalSize = null;
        return points.set(index, element);
    }

    @Override
    public void add(int index, Point element) {
        originalSize = null;
        points.add(index, element);
    }

    @Override
    public Point remove(int index) {
        originalSize = null;
        return points.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return points.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return points.lastIndexOf(o);
    }

    @Override
    public ListIterator<Point> listIterator() {
        return points.listIterator();
    }

    @Override
    public ListIterator<Point> listIterator(int index) {
        return points.listIterator(index);
    }

    @Override
    public List<Point> subList(int fromIndex, int toIndex) {
        return points.subList(fromIndex, toIndex);
    }


    @Override
    public Point getOriginalSize() {
        if (originalSize == null) {
            originalSize = new Point(0, 0);
            for (Point point : points) {
                originalSize.max(point);
            }
        }

        return originalSize;
    }

    @Override
    public void draw(Point where) {
        View.resetBlending();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.startDrawing(GL11.GL_POLYGON);
        worldrenderer.setColorRGBA_F(color.red, color.green, color.blue, color.alpha);

        Point orig = getOriginalSize();

        for(Point point: points) {
            int pX = where.x + point.x * (size.x / orig.x);
            int pY = where.y + point.y * (size.y / orig.y);
            if (point instanceof ColorPoint) {
                GLColor color = ((ColorPoint) point).getColor();
                worldrenderer.setColorRGBA_F(color.red, color.green, color.blue, color.alpha);
            }
            worldrenderer.addVertex(pX, pY, 0);
        }
        tessellator.draw();
    }

    @Override
    public void drawShadow(Point where, GLColor color, int w) {

    }

    @Override
    public void inflate(XmlHelper xmlHelper, Style theme) {
        super.inflate(xmlHelper, theme);
        for (XmlHelper child: xmlHelper.getChildren()) {
            Long color = child.getColorAttr(GuiManager.NS, "color", null);
            int x = child.getDimenAttr(GuiManager.NS, "left", 0);
            int y = child.getDimenAttr(GuiManager.NS, "top", 0);
            if (color == null) {
                points.add(new Point(x, y));
            } else {
                points.add(new ColorPoint(x, y, color));
            }
        }
    }
}
