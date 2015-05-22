package com.onkiup.minedroid.gui.events;

/**
 * Created by chedim on 4/26/15.
 */
public interface Event<Argument> {
    public void handle(Argument event);
}
