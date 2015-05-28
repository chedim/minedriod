package com.onkiup.minedroid.gui.resources;

/**
 * Holds a string value for given environment params
 */
public class EnvValue extends EnvParams {

    public Object value;

    public EnvValue(String lang, String version, Graphics graphics, Mode mode, Object value) {
        super(lang, version, graphics, mode);
        this.value = value;
    }

}
