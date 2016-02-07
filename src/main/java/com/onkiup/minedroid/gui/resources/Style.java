package com.onkiup.minedroid.gui.resources;

import com.onkiup.minedroid.Context;
import com.onkiup.minedroid.Contexted;
import com.onkiup.minedroid.Modification;
import com.onkiup.minedroid.gui.GuiManager;
import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.drawables.Drawable;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.primitives.Rect;
import com.onkiup.minedroid.gui.views.View;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;

/**
 * Created by chedim on 5/29/15.
 */
public class Style implements Context {

    protected HashMap<String, Object> props;
    protected ResourceLocation source;
    protected Integer modId;
    protected Class r;
    protected String parentName;
    protected Style parent;
    private Style fallbackTheme;

    public Style(ResourceLocation source, Class r) {
        this.source = source;
        this.r = r;
    }

    public Style(ResourceLocation source, Class r, String parent) {
        this.source = source;
        this.r = r;
        this.parentName = parent;
    }

    protected void inflate() {
        XmlHelper helper = GuiManager.getXmlHelper(this, source);
        List<XmlHelper> items = helper.getChildren();
        props = new HashMap<String, Object>();

        for (XmlHelper item : items) {
            String name = item.getStringAttr(null, "name", null);
            if (name == null) continue;
            String value = item.getText();
            if (!StringUtils.isEmpty(value) && value.charAt(0) == '@') {
                props.put(name, ResourceManager.get(this, value));
            } else {
                props.put(name, value);
            }
        }

        if (parentName == null) {
            parentName = helper.getStringAttr(GuiManager.NS, "parent", null);
        }

        if (parentName != null) {
            this.parent = (Style) ResourceManager.get(this, parentName);
        }

//        if (false && parent == null) {
//            Class p = getClass().getEnclosingClass();
//            if (Style.class.isAssignableFrom(p)) {
//                try {
//                    Method getInstance = p.getDeclaredMethod("getInstance");
//                    parent = (Style) getInstance.invoke(null);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }

    protected Object getProperty(String property) {
        if (props == null) inflate();
        Object result = null;
        if (!props.containsKey(property)) {
            if (parent != null) result = parent.get(property);
        } else {
            result = props.get(property);
        }
        if (result == null && fallbackTheme != null) result = fallbackTheme.getProperty(property);

        return result;
    }

    public Object get(String property) {
        return getProperty(property);
    }

    public String getString(String property, String def) {
        String val = (String) getProperty(property);
        if (val == null) return def;
        return val;
    }

    public Integer getInt(String property, Integer def) {
        Object val = get(property);
        if (val == null) return def;
        return Integer.valueOf(val.toString());
    }

    public Rect getRect(String property, Rect def) {
        Integer all = getInt(property, null);
        if (all == null && def == null) def = new Rect(0, 0, 0, 0);
        else if (all != null) def = new Rect(all, all, all, all);
        else def = def.clone();

        def.left = getInt(property + "-left", def.left);
        def.top = getInt(property + "-top", def.top);
        def.right = getInt(property + "-right", def.right);
        def.bottom = getInt(property + "-bottom", def.bottom);

        return def;
    }

    public Style getStyle(String name) {
        Style val = (Style) getProperty(name);

        return val;
    }

    public Style getStyle(String name, Style def) {
        Style v = getStyle(name);
        if (v == null) return def;
        return v;
    }

    protected static Style getInstance() {
        throw new RuntimeException("Style.getInstance method SHOULD be overwritten!");
    }

    public void setFallbackTheme(Style fallbackTheme) {
        if (this == fallbackTheme) return;
        this.fallbackTheme = fallbackTheme;
    }

    public Enum getEnum(String s, Enum def) {
        Object val = getProperty(s);
        if (val == null) return def;

        Class<Enum> clazz = (Class<Enum>) def.getClass();
        return Enum.valueOf(clazz, val.toString().toUpperCase());
    }

    public Float getFloat(String name, Float def) {
        Object val = getProperty(name);
        if (val == null) return def;

        return Float.valueOf(val.toString());
    }

    public ResourceLocation getResource(String name, ResourceLocation def) {
        Object val = getProperty(name);
        if (val == null) return def;

        return (ResourceLocation) val;
    }

    public Long getColor(String property, Long def) {
        Object val = get(property);
        if (val == null) return def;
        return Long.parseLong(val.toString(), 16);
    }

    public Drawable getDrawable(String name, Drawable o) {
        ResourceLocation l = getResource(name, null);
        if (l == null) return o;
        return GuiManager.inflateDrawable(this, l);
    }

    public int getIdAttr(String name) {
        String id = getString(name, null);
        if (id == null) return -1;
        if (id.equals("parent")) return 0;

        Object val = ResourceManager.get(this, id);
        if (val == null) return -1;
        if (val instanceof Integer) return (Integer) val;
        return Integer.valueOf(val.toString());
    }

    public Point getSize(Point def) {
        if (def == null) def = new Point(0, 0);
        else def = def.clone();

        def.x = getDimen("width", def.x);
        def.y = getDimen("height", def.y);
        return def;
    }

    public Integer getDimen(String name, Integer def) {
        String val = getString(name, null);
        if (val == null) return def;

        val = val.toLowerCase();
        if (val.equals("match_parent")) return View.Layout.MATCH_PARENT;
        if (val.equals("wrap_content")) return View.Layout.WRAP_CONTENT;

        val = ResourceManager.get(this, val).toString();

        return Integer.parseInt(val);
    }

    public Boolean getBool(String name, Boolean def) {
        String val = getString(name, null);
        if (val == null) return def;
        if (val.equals("true")) return true;
        try {
            return Integer.valueOf(val) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public int contextId() {
        if (modId == null) {
            modId = Modification.getModuleByR(r).hashCode();
        }
        return modId;
    }
}
