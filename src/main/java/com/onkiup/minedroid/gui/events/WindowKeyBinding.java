package com.onkiup.minedroid.gui.events;

/**
 * Represents a window key binding
 */
public class WindowKeyBinding {
    /**
     * Key action description
     */
    public String description;
    /**
     * Key action category
     */
    public String category;
    /**
     * Binding key code
     */
    public int keyCode;

    public WindowKeyBinding(int keyCode, String description, String category) {
        this.description = description;
        this.category = category;
        this.keyCode = keyCode;
    }
}
