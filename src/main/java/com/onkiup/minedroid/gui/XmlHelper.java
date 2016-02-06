package com.onkiup.minedroid.gui;

import com.onkiup.minedroid.Context;
import com.onkiup.minedroid.Contexted;
import com.onkiup.minedroid.gui.drawables.Drawable;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.primitives.Rect;
import com.onkiup.minedroid.gui.resources.ResourceLink;
import com.onkiup.minedroid.gui.resources.ResourceManager;
import com.onkiup.minedroid.gui.resources.Style;
import com.onkiup.minedroid.gui.views.View;
import net.minecraft.util.ResourceLocation;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Some useful methods for XML parsing
 */
public class XmlHelper extends Contexted {

    /**
     * Wrapped XML Node
     */
    protected Node node;

    public XmlHelper(Context context, Node node) {
        super(context);
        this.node = node;
    }

    /**
     * Returns node attribute
     *
     * @param ns   Attribute namespace URL
     * @param name Attribute name
     * @return Node Attribute value
     */
    public Node getAttr(String ns, String name) {
        if (ns == null) return node.getAttributes().getNamedItem(name);
        Node result = node.getAttributes().getNamedItemNS(ns, name);
        if (result == null) result = node.getAttributes().getNamedItem(name);
        return result;
    }

    /**
     * Reads string attribute or tries to get it from R class
     *
     * @param ns   Attribute namespace
     * @param name Attribute name
     * @param def  Default value
     * @return String value
     */
    public String getStringAttr(String ns, String name, String def) {
        Node attr = getAttr(ns, name);


        String val;
        if (attr == null) {
            val = def;
        } else {
            val = attr.getNodeValue();
        }

        Object v = ResourceManager.get(this, val);
        if (v == null) return def;
        return v.toString();
    }

    public String getStringAttr(String ns, String name, Style fb, String def) {
        return getStringAttr(ns, name, fb.getString(name, def));
    }

    /**
     * Reads integer attribute or tries to get it from R class
     *
     * @param ns   Attribute namespace
     * @param name Attribute name
     * @param def  Default value
     * @return Integer value
     */
    public Integer getIntegerAttr(String ns, String name, Integer def) {
        Node attr = getAttr(ns, name);
        if (attr == null) return def;
        String val = ResourceManager.get(this, attr.getNodeValue()).toString();
        if (val.length() == 0) return 0;
        return Integer.parseInt(val);
    }

    public Integer getIntegerAttr(String ns, String name, Style fb, Integer def) {
        return getIntegerAttr(ns, name, fb.getInt(name, def));
    }

    /**
     * Reads float attribute or tries to get it from R class
     *
     * @param ns   Attribute namespace URL
     * @param name Attribute name
     * @param def  Default value
     * @return Float value
     */
    public Float getFloatAttr(String ns, String name, Float def) {
        Node attr = getAttr(ns, name);
        if (attr == null) return def;
        String val = ResourceManager.get(this, attr.getNodeValue()).toString();
        return Float.parseFloat(val);
    }

    public Float getFloatAttr(String ns, String name, Style fb, Float def) {
        return getFloatAttr(ns, name, fb.getFloat(name, def));
    }

    /**
     * Reads drawable link and tries to get Drawable for it from R class
     *
     * @param ns   Attribute namespace URL
     * @param name Attribute name
     * @param def  Default value
     * @return Drawable element
     */
    public Drawable getDrawableAttr(String ns, String name, Drawable def) {
        ResourceLocation rl = getResourceAttr(ns, name, null);
        if (rl == null) return def;

        return GuiManager.inflateDrawable(this, rl);
    }

    public Drawable getDrawableAttr(String ns, String name, Style fb, Drawable def) {
        return getDrawableAttr(ns, name, fb.getDrawable(name, def));
    }

    /**
     * Reads resource location link and gets it from R class
     *
     * @param ns   Attribute namespace URL
     * @param name Attribute name
     * @param o    Default value
     * @return Resource location
     */
    public ResourceLocation getResourceAttr(String ns, String name, ResourceLocation o) {
        Node attr = getAttr(ns, name);
        if (attr == null) return o;

        return (ResourceLink) ResourceManager.get(this, attr.getNodeValue());
    }

    public ResourceLocation getResourceAttr(String ns, String name, Style fb, ResourceLink def) {
        return getResourceAttr(ns, name, fb.getResource(name, def));
    }

    /**
     * Reads dimension attribute value. If it's a link, gets it from R class;
     *
     * @param ns   Attribute namespace URL
     * @param name Attribute name
     * @param def  Default value
     * @return Dimension value
     */
    public Integer getDimenAttr(String ns, String name, Integer def) {
        Node attr = getAttr(ns, name);
        if (attr == null) return def;

        String val = attr.getNodeValue().toLowerCase();
        if (val.equals("match_parent")) return View.Layout.MATCH_PARENT;
        if (val.equals("wrap_content")) return View.Layout.WRAP_CONTENT;

        val = ResourceManager.get(this, val).toString();

        return Integer.parseInt(val);
    }

    public Integer getDimenAttr(String ns, String name, Style fb, Integer def) {
        return getDimenAttr(ns, name, fb.getDimen(name, def));
    }

    /**
     * Reads rectangle information (name-left, name-top, name-right, and name-botom) from element attirbutes
     *
     * @param ns   Attributes namespace URL
     * @param name Attributes name
     * @param def  Default value
     * @return Readed rectangle or default value
     */
    public Rect getRectAttr(String ns, String name, Rect def) {
        Integer all = getDimenAttr(ns, name, null);
        if (all == null && def == null) def = new Rect(0, 0, 0, 0);
        else if (all != null) def = new Rect(all, all, all, all);
        else def = def.clone();

        def.left = getDimenAttr(ns, name + "-left", def.left);
        def.top = getDimenAttr(ns, name + "-top", def.top);
        def.right = getDimenAttr(ns, name + "-right", def.right);
        def.bottom = getDimenAttr(ns, name + "-bottom", def.bottom);

        return def;
    }

    public Rect getRectAttr(String ns, String name, Style fb, Rect def) {
        return getRectAttr(ns, name, fb.getRect(name, def));
    }

    /**
     * The same as @XmlHelper.getStringAttr
     *
     * @param ns   Attribute namespace URL
     * @param name Attribute name
     * @param def  Default value
     * @return String or default value
     * @deprecated
     */
    @Deprecated
    public String getLocalizedAttr(String ns, String name, String def) {
        return getStringAttr(ns, name, def);
    }

    /**
     * Reads Enum value from attribute
     *
     * @param ns   Attribute namespace URL
     * @param name Attribute name
     * @param def  Default value, not null
     * @return Enum value or default value
     */
    public Enum getEnumAttr(String ns, String name, Enum def) {
        String val = getStringAttr(ns, name, (String) null);
        if (val == null) return def;
        Class<Enum> clazz = (Class<Enum>) def.getClass();
        if (val != null) return Enum.valueOf(clazz, val.toUpperCase());
        return def;
    }

    public Enum getEnumAttr(String ns, String name, Style fb, Enum def) {
        return getEnumAttr(ns, name, fb.getEnum(name, def));
    }

    /**
     * Returns node children list
     *
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
     *
     * @return wrapped node
     */
    public Node getNode() {
        return node;
    }

    /**
     * Reads id link and fetches it from R class
     *
     * @param ns   Attribute namespace URL
     * @param name Attribute name
     * @return id value
     */
    public int getIdAttr(String ns, String name) {
        Node attr = getAttr(ns, name);
        if (attr == null) return -1;

        String id = attr.getNodeValue();
        if (id.equals("parent")) return 0;

        Object val = ResourceManager.get(this, id);

        if (val == null) return -1;
        if (val instanceof Integer) {
            return (Integer) val;
        }

        return Integer.valueOf(val.toString());
    }

    public int getIdAttr(String ns, String name, Style fb) {
        int id = getIdAttr(ns, name);
        if (id == -1) id = fb.getIdAttr(name);
        return id;
    }

    /**
     * Reads size values (width + height)
     *
     * @param ns  Attributes namespace URL
     * @param def Default value
     * @return Readed size or default value
     */
    public Point getSize(String ns, Point def) {
        if (def == null) def = new Point(0, 0);
        else def = def.clone();

        def.x = getDimenAttr(ns, "width", def.x);
        def.y = getDimenAttr(ns, "height", def.y);

        return def;
    }

    public Point getSize(String ns, Style fb, Point def) {
        return getSize(ns, fb.getSize(def));
    }

    /**
     * Reads color attribute (and tries to fetch it from R class
     *
     * @param ns   Attribute namespace URL
     * @param name Attribute name
     * @param def  Default value
     * @return Color or default value
     */
    public Long getColorAttr(String ns, String name, Long def) {
        Node attr = getAttr(ns, name);
        if (attr == null) return def;

        String value = attr.getNodeValue();
        value = ResourceManager.get(this, value).toString();
        return Long.parseLong(value, 16);
    }

    public Long getColorAttr(String ns, String name, Style fb, Long def) {
        return getColorAttr(ns, name, fb.getColor(name, def));
    }

    /**
     * @return wrapped node name
     */
    public String getName() {
        return node.getNodeName();
    }

    /**
     * @return text value of the node
     */
    public String getText() {
        return node.getTextContent();
    }

    public Style getStyleAttr(String ns, String name, Style def) {
        Node sName = getAttr(ns, name);
        if (sName == null) {
            return def;
        }

        Style result = (Style) ResourceManager.get(this, sName.getNodeValue());
        if (result == null) return def;

        result.setFallbackTheme(def);
        return result;
    }

    public Boolean getBoolAttr(String NS, String name, Boolean def) {
        String val = getStringAttr(NS, name, (String) null);
        if (val == null) return def;
        if (val.equals("true")) return true;
        try {
            return Integer.valueOf(val) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public Boolean getBoolAttr(String ns, String name, Style fb, Boolean def) {
        return getBoolAttr(ns, name, fb.getBool(name, def));
    }
}