package com.onkiup.minedroid.timer;

import com.onkiup.minedroid.Context;

/**
 * Created by chedim on 8/12/15.
 */
public abstract interface Task {

    /**
     * Called when timer is done
     */
    void execute(Context ctx);

    interface Client extends Task {

    }

    interface Server extends Task {

    }
}
