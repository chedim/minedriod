package com.onkiup.minedroid.gui;

import com.onkiup.minedroid.gui.drawables.Drawable;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.primitives.Rect;
import com.onkiup.minedroid.gui.resources.ResourceLink;
import com.onkiup.minedroid.gui.resources.ResourceManager;
import com.onkiup.minedroid.gui.views.View;
import net.minecraft.util.ResourceLocation;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Some useful methods for XML parsing
 */
public class XmlHelper {

    /**
     * Wrapped XML Node
     */
    protected Node node;
    protected Context context;

    public XmlHelper(Context context, Node node) {
        this.node = node;
        this.context = context;
    }

    /**
     * Returns node attribute
     * @param ns Attribute namespace
     * @param name Attribute name
     * @return Node Attribute value
     */
    public Node getAttr(String ns, String name) {
        return node.getAttributes().getNamedItemNS(ns, name);
    }

    public String getStringAttr(String ns, String name, String def) {
        Node attr = getAttr(ns, name);

        if (attr == null) return def;

        String val = attr.getNodeValue();

        return ResourceManager.get(context.R(), val).toString();
    }

    public Integer getIntegerAttr(String ns, String name, Integer def) {
        Node attr = getAttr(ns, name);
        if (attr == null) return def;
        String val = ResourceManager.getValue(context.R(), attr.getNodeValue()).toString();
        return Integer.parseInt(val);
    }

    public Float getFloatAttr(String ns, String name, Float def) {
        Node attr = getAttr(ns, name);
        if (attr == null) return def;
        String val = ResourceManager.getValue(context.R(), attr.getNodeValue()).toString();
        return Float.parseFloat(val);
    }

    public Drawable getDrawableAttr(String ns, String name, Drawable def) {
        ResourceLocation rl = (ResourceLocation) getResourceAttr(ns, name, null);
        if (rl == null) return def;

        return MineDroid.inflateDrawable(context, rl);
    }

    public ResourceLocation getResourceAttr(String ns, String name, ResourceLocation o) {
        Node attr = getAttr(ns, name);
        if (attr == null) return o;

        return (ResourceLink) ResourceManager.get(context.R(), attr.getNodeValue());
    }

    public Integer getDimenAttr(String ns, String name, Integer def) {
        Node attr = getAttr(ns, name);
        if (attr == null) return def;

        String val = attr.getNodeValue().toLowerCase();
        if (val.equals("match_parent")) return View.Layout.MATCH_PARENT;
        if (val.equals("wrap_content")) return View.Layout.WRAP_CONTENT;

        val = ResourceManager.get(context.R(), val).toString();

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

    public List<XmlHelper> getChildren() {
        List<XmlHelper> result = new ArrayList<XmlHelper>();
        NodeList list = node.getChildNodes();
        for (int x = 0; x < list.getLength(); x++) {
            Node childNode = list.item(x);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) result.add(new XmlHelper(context, childNode));
        }

        return result;
    }

    public Node getNode() {
        return node;
    }

    public int getIdAttr(String ns, String name) {
        String id = getStringAttr(ns, name, null);
        if (id == null) return -1;
        if (id.equals("parent")) return 0;

        Object val = ResourceManager.get(context.R(), id);

        if (val == null) return -1;
        if (val instanceof Integer) {
            return (Integer) val;
        }

        return Integer.valueOf(val.toString());
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

    public String getName() {
        return node.getNodeName();
    }
}