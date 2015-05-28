package com.onkiup.minedroid.gui.events;

import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.views.View;

/**
 * Holds information about mouse events
 */
public class MouseEvent {
    /**
     * Event type class
     */
    public Class type;
    /**
     * Event coordinates
     */
    public Point coords;
    /**
     * View that currently handles the event
     */
    public View target;
    /**
     * View that was an original target of the event
     */
    public View source;
    /**
     * Button numder (if pressed/released)
     */
    public int button;
    /**
     * Event coordinates offset of previous event
     */
    public Point diff;
    /**
     * Event cancelation flag
     */
    public boolean cancel = false;
    /**
     * Scroll amount values
     */
    public Point wheel;
    /**
     * Some useful keyboard flags
     */
    public boolean shift, control, alt;

    @Override
    public MouseEvent clone() {
        MouseEvent result = new MouseEvent();
        result.type = type;
        result.coords = coords;
        result.target = target;
        result.source = source;
        result.button = button;
        result.diff = diff;
        result.cancel = cancel;
        result.wheel = wheel.clone();
        result.shift = shift;
        result.control = control;
        result.alt = alt;

        return result;
    }
}
