package com.onkiup.minedroid.gui.resources;

import com.onkiup.minedroid.gui.MineDroid;
import net.minecraft.util.ResourceLocation;

/**
 * Holds links to all environment-defined resource locations
 */
public class ResourceLink extends ResourceLocation {
    /**
     * resource location
     */
    protected String domain, path;


    public ResourceLink(String modid, String type, String name, EnvParams[] variants) {
        super("");
        domain = modid;
        if (variants != null && variants.length > 0) {
            int max = -1;
            EnvParams result = null;
            EnvParams env = MineDroid.getEnvParams();
            for (EnvParams variant : variants) {
                int cur = variant.compareTo(env);
                if (cur > max) {
                    result = variant;
                    max = cur;
                    if (max == 4) break;
                }
            }
            if (result != null) {
                path = type + result.getPath() + "/" + name;
            } else {
                path = "";
            }
        } else {
            path = type + "/" + name;
        }
    }

    public ResourceLink(String modid, String type, String name) {
        this(modid, type, name, null);
    }

    @Override
    public String getResourcePath() {
        return path;
    }

    @Override
    public String getResourceDomain() {
        return domain;
    }

    @Override
    public boolean equals(Object p_equals_1_) {
        if (!(p_equals_1_ instanceof ResourceLocation)) return false;
        return ((ResourceLocation) p_equals_1_).getResourceDomain().equals(getResourceDomain())
                && ((ResourceLocation) p_equals_1_).getResourcePath().equals(getResourcePath());
    }

    @Override
    public String toString() {
        return getResourceDomain() + ":" + getResourcePath();
    }

    @Override
    public int hashCode() {
        return 31 * getResourceDomain().hashCode() + getResourcePath().hashCode();
    }

}
