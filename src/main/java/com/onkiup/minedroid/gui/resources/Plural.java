package com.onkiup.minedroid.gui.resources;

import com.onkiup.minedroid.gui.GuiManager;

/**
 * Holds information about plural strings
 */
public class Plural {
    protected String[] values = new String[6];

    public Plural() {

    }

    public Plural(String zero, String one, String two, String few, String many, String other) {
        values[0] = zero;
        values[1] = one;
        values[2] = two;
        values[3] = few;
        values[4] = many;
        values[5] = other;
    }

    /**
     * Gets plural value for given number
     * @param by
     * @return plural value
     */
    public String getValue(int by) {
        return values[ResourceManager.getPluralLocalizer().quantify(by)];
    }
}
