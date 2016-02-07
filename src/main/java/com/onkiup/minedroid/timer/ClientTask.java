package com.onkiup.minedroid.timer;

/**
 * Interface for game ticks based timer
 */
public interface ClientTask {
    /**
     * Called when timer is done
     */
    void execute();
}
