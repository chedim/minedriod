package com.onkiup.minecraft.gui;

import com.onkiup.minecraft.gui.drawables.Drawable;
import com.onkiup.minecraft.gui.primitives.Point;
import com.onkiup.minecraft.gui.primitives.Rect;
import com.onkiup.minecraft.gui.views.View;
import net.minecraft.util.ResourceLocation;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by chedim on 4/30/15.
 */
public class XmlHelper {

    protected Node node;

    public XmlHelper(Node node) {
        this.node = node;
    }

    public Node getAttr(String ns, String name) {
        return node.getAttributes().getNamedItem(ns + ":" + name);
    }

    public String getStringAttr(String ns, String name, String def) {
        Node attr = getAttr(ns, name);

        if (attr == null) return def;

        return attr.getNodeValue();
    }

    public Integer getIntegerAttr(String ns, String name, Integer def) {
        Node attr = getAttr(ns, name);
        if (attr == null) return def;
        return Integer.parseInt(attr.getNodeValue());
    }

    public Float getFloatAttr(String ns, String name, Float def) {
        Node attr = getAttr(ns, name);
        if (attr == null) return def;
        return Float.parseFloat(attr.getNodeValue());
    }

    public Drawable getDrawableAttr(String ns, String name, Drawable def) {
        ResourceLocation rl = (ResourceLocation) getResourceAttr(ns, name, null);
        if (rl == null) return def;

        return OnkiupGuiManager.inflateDrawable(rl);
    }

    public ResourceLocation getResourceAttr(String ns, String name, ResourceLocation o) {
        Node attr = getAttr(ns, name);
        if (attr == null) return o;

        String[] location = attr.getNodeValue().split(":");
        ResourceLocation rl;
        if (location.length == 2) {
            rl = new ResourceLocation(location[0], location[1]);
        } else {
            rl = new ResourceLocation(location[0]);
        }

        return rl;
    }

    public Integer getDimenAttr(String ns, String name, Integer def) {
        Node attr = getAttr(ns, name);
        if (attr == null) return def;

        String val = attr.getNodeValue().toLowerCase();
        if (val.equals("match_parent")) return View.Layout.MATCH_PARENT;
        if (val.equals("wrap_content")) return View.Layout.WRAP_CONTENT;

        return Integer.parseInt(val);
    }

    public Rect getRectAttr(String ns, String name, Rect src) {
        Integer all = getDimenAttr(ns, name, null);
        src = src.clone();

        if (all != null) {
            src.left = src.top = src.right = src.bottom = all;
        }

        Integer val = getDimenAttr(ns, name + "-left", null);
        if (val != null) src.left = val;
        val = getDimenAttr(ns, name + "-top", null);
        if (val != null) src.top = val;
        val = getDimenAttr(ns, name + "-right", null);
        if (val != null) src.right = val;
        val = getDimenAttr(ns, name + "-bottom", null);
        if (val != null) src.bottom = val;

        return src;
    }

    public String getLocalizedAttr(String ns, String name, String def) {
        return getStringAttr(ns, name, def);
    }

    public Enum getEnumAttr(String ns, String name, Enum def) {
        if (def == null) return def;

        String val = getStringAttr(ns, name, null);
        Class<Enum> clazz = (Class<Enum>) def.getClass();
        if (val != null) return Enum.valueOf(clazz, val.toUpperCase());
        return def;
    }

    public NodeList getChildren() {
        return node.getChildNodes();
    }

    public Node getNode() {
        return node;
    }

    public int getIdAttr(String ns, String name) {
        String id = getStringAttr(ns, name, null);
        if (id == null) return -1;
        if (id.equals("parent")) return 0;
        return OnkiupGuiManager.getId(id);
    }

    public Point getSize(String ns, Point src) {

        src = src.clone();

        src.x = getDimenAttr(ns, "width", src.x);
        src.y = getDimenAttr(ns, "height", src.y);

        return src;
    }

    public Long getColorAttr(String ns, String name, Long def) {
        Node attr = getAttr(ns, name);
        if (attr == null) return def;

        String value = attr.getNodeValue();
        return Long.parseLong(value, 16);
    }
}