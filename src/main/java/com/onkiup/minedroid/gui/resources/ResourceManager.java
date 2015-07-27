package com.onkiup.minedroid.gui.resources;

import com.onkiup.minedroid.R;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;

@SideOnly(Side.CLIENT)
public class ResourceManager {

    public static Object get(Class R, String link) {
        if (link == null) return null;
        if (link.length() == 0) return "";
        if (!link.substring(0, 1).equals("@")) {
            if (link.length() < 2) return link;
            if (link.substring(0, 2).equals("\\@")) {
                link = link.substring(1);
            }
            return link;
        }

        link = link.substring(1);
        String[] parts = link.split("\\/");

        if (parts.length != 2) throw new RuntimeException("Invalid resource link: '@"+link+"'");
        if (parts[0].contains(":")) {
            String[] pack = parts[0].split(":");
            if (pack.length != 2) throw new RuntimeException("Invalid resource link: '@"+link+"'");
            if (!pack[0].equals("minedroid")) throw new RuntimeException("Invalid resources package: '"+pack[0]+"'");
            R = com.onkiup.minedroid.R.class;
            parts[0] = pack[1];
        }

        Class type = getSubClass(R, parts[0]);
        if (type == null) return null;

        if (!parts[1].contains(".")) {
            return getFieldValue(type, parts[1]);
        } else {
            String[] names = parts[1].split("\\.");
            for (int i=0; i < names.length - 1; i++) {
                type = getSubClass(type, names[i]);
                if (type == null) return null;
            }
            return getFieldValue(type, names[names.length - 1]);
        }
    }

    private static Class getSubClass(Class R, String name) {
        Class[] subs = R.getDeclaredClasses();
        for (Class sub: subs) {
            if (sub.getSimpleName().equals(name)) return sub;
        }

        return null;
    }

    private static Object getFieldValue(Class R, String name) {
        try {
            Object value = R.getField(name).get(null);
            return value;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
