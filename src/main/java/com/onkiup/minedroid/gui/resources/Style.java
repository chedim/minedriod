package com.onkiup.minedroid.gui.resources;

import com.onkiup.minedroid.gui.Context;
import com.onkiup.minedroid.gui.MineDroid;
import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.drawables.Drawable;
import com.onkiup.minedroid.gui.primitives.Rect;
import com.onkiup.minedroid.gui.views.ContentView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by chedim on 5/29/15.
 */
public class Style implements Context {

    protected HashMap<String, Object> props;
    protected ResourceLink source;
    protected Class r;
    protected String parentName;
    protected Style parent;
    private Style fallbackTheme;

    public Style(ResourceLink source, Class r) {
        this.source = source;
        this.r = r;
    }

    public Style(ResourceLink source, Class r, String parent) {
        this.source = source;
        this.r = r;
        this.parentName = parent;
    }

    protected void inflate() {
        XmlHelper helper = MineDroid.getXmlHelper(this, source);
        List<XmlHelper> items = helper.getChildren();
        props = new HashMap<String, Object>();

        for (XmlHelper item : items) {
            String name = item.getStringAttr(null, "name", null);
            if (name == null) continue;
            String value = item.getText();
            if (value.charAt(0) == '@') {
                props.put(name, ResourceManager.get(r, value));
            } else {
                props.put(name, value);
            }
        }

        if (parentName != null) {
            this.parent = (Style) ResourceManager.get(r, parentName);
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

    public Integer getInt(String property, Integer def) {
        Object val = get(property);
        if (val == null) return def;
        return Integer.valueOf(val.toString());
    }

    public Rect getRect(String property) {
        Integer all = getInt(property, 0);
        Rect rect = new Rect(all, all, all, all);
        Integer left = getInt(property+"-left", 0),
                top = getInt(property+"-top", 0),
                right = getInt(property+"-right", 0),
                bottom = getInt(property+"-bottom", 0);
        if (left != null) rect.left = left;
        if (top != null) rect.top = top;
        if (right != null) rect.right = right;
        if (bottom != null) rect.bottom = bottom;

        return rect;
    }

    public Style getStyle(String name) {
        Object val = getProperty(name);
        if (val == null) return null;
        return (Style) val;
    }

    @Override
    public Class R() {
        return r;
    }

    protected static Style getInstance() {
        throw new RuntimeException("Style.getInstance method SHOULD be overwritten!");
    }

    public void setFallbackTheme(Style fallbackTheme) {
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

    public ResourceLink getResource(String name, ResourceLink def) {
        Object val = getProperty(name);
        if (val == null) return def;

        return (ResourceLink) val;
    }
}
