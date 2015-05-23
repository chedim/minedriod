package com.onkiup.minedroid.gui.resources;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.util.HashMap;

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

        Class[] subs = R.getDeclaredClasses();
        for (Class sub: subs) {
            if (sub.getSimpleName().equals(parts[0])) {
                try {
                    Field f = sub.getField(parts[1]);
                    return f.get(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

}
