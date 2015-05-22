package com.onkiup.minedroid.gui.resources;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;

@SideOnly(Side.CLIENT)
public class ResourceManager {
    protected static final String[] clientQualifiers = new String[4];
    protected static final String[] typeDirs = new String[] {
            "values", "drawables", "layouts", "textures", "images", "nines", "values"
    };
    protected static final String[] typeXmls = new String[] {
            "strings", null, null, null, null, null, "styles"
    };

    public static enum ResourceType {
        STRING, DRAWABLE, LAYOUT, TEXTURE, IMAGE, NINEPATCH, STYLE
    }


    static {
        clientQualifiers[0] = Minecraft.getMinecraft().getVersion();
        clientQualifiers[1] = Minecraft.getMinecraft().gameSettings.fancyGraphics ? "fancy" : "fast";
        clientQualifiers[2] = Minecraft.getMinecraft().isSingleplayer() ? "sp" : "mp";
        clientQualifiers[3] = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
    }

    protected String moduleId;
    protected HashMap<Integer, ResourceLocation> locationCache = new HashMap<Integer, ResourceLocation>();

    public ResourceManager(String moduleId) {
        this.moduleId = moduleId;
    }

    public ResourceLocation resolveId(Integer id) {
        if (locationCache.containsKey(id)) {
            return locationCache.get(id);
        }

        return null;
    }
}
