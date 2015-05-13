package com.onkiup.minecraft.gui.events;

import com.onkiup.minecraft.gui.views.View;

/**
 * Created by chedim on 5/3/15.
 */
public class KeyEvent {
    public Class type;
    public View target, source;
    public int keyCode;
    public char keyChar;
    public boolean cancel = false;

    public boolean shift, control, alt;

    @Override
    public KeyEvent clone() {
        KeyEvent result = new KeyEvent();
        result.type = type;
        result.target = target;
        result.source = source;
        result.keyCode = keyCode;
        result.keyChar = keyChar;
        result.shift = shift;
        result.control = control;
        result.alt = alt;
        result.cancel = cancel;

        return result;
    }
}
