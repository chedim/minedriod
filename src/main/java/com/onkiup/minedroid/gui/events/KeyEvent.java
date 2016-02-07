package com.onkiup.minedroid.gui.events;

import com.onkiup.minedroid.gui.views.View;

import java.io.Serializable;

/**
 * Holds information about key events
 */
public class KeyEvent  {
    /**
     * Event type class
     */
    public Class type;
    /**
     * View that currently handles the event
     */
    public View target;
    /**
     * View that was an original target of the event
     */
    public View source;
    /**
     * Pressed key code
     */
    public int keyCode;
    /**
     * Pressed key char representation
     */
    public char keyChar;
    /**
     * Event cancelation flag
     */
    public boolean cancel = false;

    /**
     * Some keyboard flags
     */
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
