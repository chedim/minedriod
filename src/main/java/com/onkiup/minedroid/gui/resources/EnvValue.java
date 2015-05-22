package com.onkiup.minedroid.gui.resources;

/**
 * Created by chedim on 5/22/15.
 */
public class EnvValue extends EnvParams {

    public Object value;

    public EnvValue(String lang, String version, Graphics graphics, Mode mode, Object value) {
        super(lang, version, graphics, mode);
        this.value = value;
    }

}
