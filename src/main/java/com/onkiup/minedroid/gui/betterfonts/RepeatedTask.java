package com.onkiup.minedroid.gui.betterfonts;

import com.onkiup.minedroid.timer.ClientTask;

/**
 * Created by chedim on 8/5/15.
 */
public interface RepeatedTask extends ClientTask {
    public void onDone(boolean isCancelled);
}
