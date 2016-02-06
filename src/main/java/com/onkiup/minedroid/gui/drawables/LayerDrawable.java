package com.onkiup.minedroid.gui.drawables;

import com.onkiup.minedroid.MineDroid;
import com.onkiup.minedroid.gui.GuiManager;
import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.primitives.GLColor;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.resources.Style;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by chedim on 8/18/15.
 */
public class LayerDrawable extends ArrayList<LayerDrawable.Layer> implements Drawable {
    protected Point size;
    protected Point originalSize;

    public LayerDrawable(int initialCapacity) {
        super(initialCapacity);
    }

    public LayerDrawable() {
    }

    public LayerDrawable(Collection<? extends Layer> c) {
        super(c);
    }

    @Override
    public void draw(Point where) {
        getOriginalSize();
        for (Layer layer: this) {
            Point lw = where.add(new Point(layer.left, layer.top));
            layer.drawable.draw(lw);
        }
    }

    @Override
    public void setSize(Point size) {
        this.size = size.clone();
        for (Layer layer: this) {
            Point ls = size.sub(new Point(layer.left, layer.top)).sub(new Point(layer.right, layer.bottom));
            layer.drawable.setSize(ls);
        }
    }

    @Override
    public Point getSize() {
        return size.clone();
    }

    @Override
    public Point getOriginalSize() {
        if (originalSize == null) {
            originalSize = new Point(0, 0);
            for (Layer layer : this) {
                originalSize.max(layer.drawable.getOriginalSize());
            }
        }
        return originalSize;
    }

    @Override
    public void inflate(XmlHelper node, Style theme) {
        originalSize = null;
        setSize(node.getSize(GuiManager.NS, new Point(0, 0)));
        for (XmlHelper child: node.getChildren()) {
            Layer layer = new Layer();
            layer.drawable = child.getDrawableAttr(GuiManager.NS, "drawable", null);
            layer.top = child.getDimenAttr(GuiManager.NS, "top", 0);
            layer.right = child.getDimenAttr(GuiManager.NS, "right", 0);
            layer.bottom = child.getDimenAttr(GuiManager.NS, "bottom", 0);
            layer.left = child.getDimenAttr(GuiManager.NS, "left", 0);
            add(layer);
        }
    }

    @Override
    public Drawable clone() {
        LayerDrawable result = new LayerDrawable();
        result.setSize(size);
        for (Layer layer: this) {
            Layer n = new Layer(layer.drawable.clone(), layer.left, layer.top, layer.right, layer.bottom);
            result.add(n);
        }
        return result;
    }

    @Override
    public void drawShadow(Point where, GLColor color, int size) {

    }

    public static class Layer {
        public Drawable drawable;
        public int left;
        public int top;
        public int right;
        public int bottom;

        public Layer() {
        }

        public Layer(Drawable drawable, int left, int top, int right, int bottom) {
            this.drawable = drawable;
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }
    }
}
