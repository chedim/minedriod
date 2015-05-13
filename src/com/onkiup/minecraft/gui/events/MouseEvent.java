package com.onkiup.minecraft.gui.events;

import com.onkiup.minecraft.gui.primitives.Point;
import com.onkiup.minecraft.gui.views.View;

/**
 * Created by chedim on 4/26/15.
 */
public class MouseEvent {

    public Class type;

    public Point coords;
    public View target;
    public View source;
    public int button;

    public Point diff;

    public boolean cancel = false;
    public Point wheel;

    public boolean shift, control, alt;

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
