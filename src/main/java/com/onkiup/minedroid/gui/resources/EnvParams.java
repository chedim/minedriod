package com.onkiup.minedroid.gui.resources;

import com.onkiup.minedroid.gui.MineDroid;

/**
 * Created by chedim on 5/21/15.
 */
public class EnvParams {
    public String lang;
    public Integer version;
    public Graphics graphics;
    public Mode mode;

    public EnvParams(String lang, String version, Graphics graphics, Mode mode) {
        this.lang = lang;
        this.version = MineDroid.getMCVersion(version);
        this.graphics = graphics;
        this.mode = mode;
    }

    public EnvParams() {

    }

    public static enum Graphics {FANCY, FAST}

    public static enum Mode {LOCAL, REMOTE}

    public int compareTo(EnvParams to) {
        int result = 0;
        if (version != null && version <= to.version) result++;
        if (lang != null && lang.equals(to.lang)) result++;
        if (graphics != null && graphics == to.graphics) result++;
        if (mode != null && mode == to.mode) result++;
        return result;
    }

    public String getPath() {
        String result = "";
        if (version != null) result += "-" + version;
        if (graphics != null) result += "-" + graphics.name().toLowerCase();
        if (mode != null) result += "-" + mode.name().toLowerCase();
        if (lang != null) result += "-" + lang;


        return result;
    }
}
