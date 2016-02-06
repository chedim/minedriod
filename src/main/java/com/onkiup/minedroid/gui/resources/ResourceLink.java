package com.onkiup.minedroid.gui.resources;

import com.onkiup.minedroid.gui.GuiManager;
import net.minecraft.util.ResourceLocation;

/**
 * Holds links to all environment-defined resource locations
 */
public class ResourceLink extends ResourceLocation {
    /**
     * resource location
     */
    protected String domain, path;
    protected boolean isResolved;
    protected String type, name;
    protected EnvParams[] variants;


    public ResourceLink(String modid, String type, String name, EnvParams[] variants) {
        super("");
        EnvParams env = ResourceManager.getEnvParams();
        domain = modid;
        this.type = type;
        this.name = name;
        this.variants = variants;

        if (env == null && variants != null && variants.length > 1) {
            // storing link information for future resolving
            return;
        }
        isResolved = true;
        resolve(env);
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

    public void resolve(EnvParams env) {
        if (variants != null && variants.length > 0) {
            int max = -1;
            EnvParams result = null;
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
}
