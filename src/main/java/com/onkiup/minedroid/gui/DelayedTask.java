package com.onkiup.minedroid.gui;

/**
 * Interface for game ticks based timer
 */
public interface DelayedTask {
    /**
     * Called when timer is done
     */
    public void execute();
}
