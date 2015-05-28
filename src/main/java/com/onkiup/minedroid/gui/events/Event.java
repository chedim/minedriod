package com.onkiup.minedroid.gui.events;

/**
 * Base class for all MineDroid events
 */
public interface Event<Argument> {
    /**
     * Handles event
     * @param event event information
     */
    void handle(Argument event);
}
