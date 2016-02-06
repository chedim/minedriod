package com.onkiup.minedroid.gui.resources;

import com.onkiup.minedroid.gui.GuiManager;

/**
 * Represents environment parameters
 */
public class EnvParams {
    /**
     * Language seting
     */
    public String lang;
    /**
     * Minecraft version
     */
    public Integer version;
    /**
     * Graphics setting
     */
    public Graphics graphics;
    /**
     * Online/ofline mode
     */
    public Mode mode;

    public EnvParams(String lang, String version, Graphics graphics, Mode mode) {
        this.lang = lang;
        this.version = GuiManager.getMCVersion(version);
        this.graphics = graphics;
        this.mode = mode;
    }

    public EnvParams() {

    }

    /**
     * Graphics setting values
     */
    public static enum Graphics {FANCY, FAST}

    /**
     * Online/offline mode values
     */
    public static enum Mode {LOCAL, REMOTE}

    /**
     * Return amount of parameters matched to given environment parameters
     * @param to environment to compare
     * @return amount of matched parameters
     */
    public int compareTo(EnvParams to) {
        int result = 0;
        if (version != null && version <= to.version) result++;
        if (lang != null && lang.equals(to.lang)) result++;
        if (graphics != null && graphics == to.graphics) result++;
        if (mode != null && mode == to.mode) result++;
        return result;
    }

    /**
     * returns environment quantifiers
     * @return quantifier string
     */
    public String getPath() {
        String result = "";
        if (version != null) result += "-" + version;
        if (graphics != null) result += "-" + graphics.name().toLowerCase();
        if (mode != null) result += "-" + mode.name().toLowerCase();
        if (lang != null) result += "-" + lang;


        return result;
    }
}
