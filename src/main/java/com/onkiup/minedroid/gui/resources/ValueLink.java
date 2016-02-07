package com.onkiup.minedroid.gui.resources;

import com.onkiup.minedroid.gui.GuiManager;

/**
 * Holds information about all environment-specified values
 */
public class ValueLink {
    protected Object value;
    protected boolean isResolved;

    public ValueLink(EnvValue[] variants) {
        if (variants != null && variants.length > 0) {
            int max = -1;
            EnvValue result = null;
            EnvParams env = GuiManager.getEnvParams();
            for (EnvValue variant : variants) {
                int cur = variant.compareTo(env);
                if (cur > max) {
                    result = variant;
                    max = cur;
                    if (max == 4) break;
                }
            }

            if (result != null) {
                value = result.value;
            }
        }
    }

    /**
     * return value for current environment
     * @return
     */
    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
