package com.onkiup.minedroid.gui.resources;

import com.onkiup.minedroid.gui.MineDroid;

/**
 * Created by chedim on 5/22/15.
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

    public String getValue(int by) {
        return values[MineDroid.getPluralLocalizer().quantify(by)];
    }
}
