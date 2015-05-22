package com.onkiup.minedroid.gui.events;

/**
 * Created by chedim on 5/15/15.
 */
public class WindowKeyBinding {
    public String description, category;
    public int keyCode;

    public WindowKeyBinding(int keyCode, String description, String category) {
        this.description = description;
        this.category = category;
        this.keyCode = keyCode;
    }
}
