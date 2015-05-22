package com.onkiup.minedroid.gui.resources;

import com.onkiup.minedroid.gui.MineDroid;

/**
 * Created by chedim on 5/22/15.
 */
public class ValueLink {
    protected Object value;

    public ValueLink(EnvValue[] variants) {
        if (variants != null && variants.length > 0) {
            int max = -1;
            EnvValue result = null;
            EnvParams env = MineDroid.getEnvParams();
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

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
