package com.onkiup.minedroid.gui.resources;

/**
 * Helps to handle language-specific plurals rules
 */
public interface PluralLocalizer {
    /**
     * Calculates plural index for given value
     * @param what
     * @return plural index
     */
    int quantify(int what);
}
