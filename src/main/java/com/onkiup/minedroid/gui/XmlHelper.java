package com.onkiup.minedroid.gui;

import com.onkiup.minedroid.R;
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
public class XmlHelper implements Context {

    /**
     * Wrapped XML Node
     */
    protected Node node;
    /**
     * Current mod R class
     */
    protected Class r;

    public XmlHelper(Context context, Node node) {
        this.node = node;
        this.r = context.R();
    }

    /**
     * Returns node attribute
     * @param ns Attribute namespace URL
     * @param name Attribute name
     * @return Node Attribute value
     */
    public Node getAttr(String ns, String name) {
        return node.getAttributes().getNamedItemNS(ns, name);
    }

    /**
     * Reads string attribute or tries to get it from R class
     * @param ns Attribute namespace
     * @param name Attribute name
     * @param def Default value
     * @return String value
     */
    public String getStringAttr(String ns, String name, String def) {
        Node attr = getAttr(ns, name);

        if (attr == null) return def;

        String val = attr.getNodeValue();

        return ResourceManager.get(r, val).toString();
    }

    /**
     * Reads integer attribute or tries to get it from R class
     * @param ns Attribute namespace
     * @param name Attribute name
     * @param def Default value
     * @return Integer value
     */
    public Integer getIntegerAttr(String ns, String name, Integer def) {
        Node attr = getAttr(ns, name);
        if (attr == null) return def;
        String val = ResourceManager.get(r, attr.getNodeValue()).toString();
        return Integer.parseInt(val);
    }

    /**
     * Reads float attribute or tries to get it from R class
     * @param ns Attribute namespace URL
     * @param name Attribute name
     * @param def Default value
     * @return Float value
     */
    public Float getFloatAttr(String ns, String name, Float def) {
        Node attr = getAttr(ns, name);
        if (attr == null) return def;
        String val = ResourceManager.get(r, attr.getNodeValue()).toString();
        return Float.parseFloat(val);
    }

    /**
     * Reads drawable link and tries to get Drawable for it from R class
     * @param ns Attribute namespace URL
     * @param name Attribute name
     * @param def Default value
     * @return Drawable element
     */
    public Drawable getDrawableAttr(String ns, String name, Drawable def) {
        ResourceLocation rl = getResourceAttr(ns, name, null);
        if (rl == null) return def;

        return MineDroid.inflateDrawable(this, rl);
    }

    /**
     * Reads resource location link and gets it from R class
     * @param ns Attribute namespace URL
     * @param name Attribute name
     * @param o Default value
     * @return Resource location
     */
    public ResourceLocation getResourceAttr(String ns, String name, ResourceLocation o) {
        Node attr = getAttr(ns, name);
        if (attr == null) return o;

        return (ResourceLink) ResourceManager.get(r, attr.getNodeValue());
    }

    /**
     * Reads dimension attribute value. If it's a link, gets it from R class;
     * @param ns Attribute namespace URL
     * @param name Attribute name
     * @param def Default value
     * @return Dimension value
     */
    public Integer getDimenAttr(String ns, String name, Integer def) {
        Node attr = getAttr(ns, name);
        if (attr == null) return def;

        String val = attr.getNodeValue().toLowerCase();
        if (val.equals("match_parent")) return View.Layout.MATCH_PARENT;
        if (val.equals("wrap_content")) return View.Layout.WRAP_CONTENT;

        val = ResourceManager.get(r, val).toString();

        return Integer.parseInt(val);
    }

    /**
     * Reads rectangle information (name-left, name-top, name-right, and name-botom) from element attirbutes
     * @param ns Attributes namespace URL
     * @param name Attributes name
     * @param def Default value
     * @return Readed rectangle or default value
     */
    public Rect getRectAttr(String ns, String name, Rect def) {
        Integer all = getDimenAttr(ns, name, null);
        def = def.clone();

        if (all != null) {
            def.left = def.top = def.right = def.bottom = all;
        }

        Integer val = getDimenAttr(ns, name + "-left", null);
        if (val != null) def.left = val;
        val = getDimenAttr(ns, name + "-top", null);
        if (val != null) def.top = val;
        val = getDimenAttr(ns, name + "-right", null);
        if (val != null) def.right = val;
        val = getDimenAttr(ns, name + "-bottom", null);
        if (val != null) def.bottom = val;

        return def;
    }

    /**
     * The same as @XmlHelper.getStringAttr
     * @deprecated
     * @param ns Attribute namespace URL
     * @param name Attribute name
     * @param def Default value
     * @return String or default value
     */
    @Deprecated
    public String getLocalizedAttr(String ns, String name, String def) {
        return getStringAttr(ns, name, def);
    }

    /**
     * Reads Enum value from attribute
     * @param ns Attribute namespace URL
     * @param name Attribute name
     * @param def Default value, not null
     * @return Enum value or default value
     */
    public Enum getEnumAttr(String ns, String name, Enum def) {
        if (def == null) return def;

        String val = getStringAttr(ns, name, null);
        Class<Enum> clazz = (Class<Enum>) def.getClass();
        if (val != null) return Enum.valueOf(clazz, val.toUpperCase());
        return def;
    }

    /**
     * Returns node children list
     * @return Children list
     */
    public List<XmlHelper> getChildren() {
        List<XmlHelper> result = new ArrayList<XmlHelper>();
        NodeList list = node.getChildNodes();
        for (int x = 0; x < list.getLength(); x++) {
            Node childNode = list.item(x);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) result.add(new XmlHelper(this, childNode));
        }

        return result;
    }

    /**
     * Returns wrapped node
     * @return wrapped node
     */
    public Node getNode() {
        return node;
    }

    /**
     * Reads id link and fetches it from R class
     * @param ns Attribute namespace URL
     * @param name Attribute name
     * @return id value
     */
    public int getIdAttr(String ns, String name) {
        Node attr = getAttr(ns, name);
        if (attr == null) return -1;

        String id = attr.getNodeValue();
        if (id.equals("parent")) return 0;

        Object val = ResourceManager.get(r, id);

        if (val == null) return -1;
        if (val instanceof Integer) {
            return (Integer) val;
        }

        return Integer.valueOf(val.toString());
    }

    /**
     * Reads size values (width + height)
     * @param ns Attributes namespace URL
     * @param def Default value
     * @return Readed size or default value
     */
    public Point getSize(String ns, Point def) {

        def = def.clone();

        def.x = getDimenAttr(ns, "width", def.x);
        def.y = getDimenAttr(ns, "height", def.y);

        return def;
    }

    /**
     * Reads color attribute (and tries to fetch it from R class
     * @param ns Attribute namespace URL
     * @param name Attribute name
     * @param def Default value
     * @return Color or default value
     */
    public Long getColorAttr(String ns, String name, Long def) {
        Node attr = getAttr(ns, name);
        if (attr == null) return def;

        String value = attr.getNodeValue();
        value = ResourceManager.get(r, value).toString();
        return Long.parseLong(value, 16);
    }

    /**
     * Returns wrapped node name
     * @return node name
     */
    public String getName() {
        return node.getNodeName();
    }

    /**
     * Current mod R class
     * @return
     */
    @Override
    public Class R() {
        return r;
    }
}