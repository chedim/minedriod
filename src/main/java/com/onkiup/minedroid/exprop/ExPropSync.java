package com.onkiup.minedroid.exprop;

import com.onkiup.minedroid.MineDroid;

/**
 * Created by chedim on 8/13/15.
 */
public class ExPropSync {
    protected static boolean isStarted = false;

    public static void start() {
        if (isStarted) return;
        MineDroid.getInstance().getTickHandler().repeat(10, new ExPropClientSync());
//        MineDroid.getInstance().getTickHandler().repeat(10, new ExPropServerSync());
        isStarted = true;
    }
}
