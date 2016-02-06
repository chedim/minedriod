package com.onkiup.minedroid;

/**
 * Created by chedim on 8/12/15.
 */
public class Contexted implements Context {

    private Context context;

    public Contexted(Context context) {
        this.context = context;
    }

    @Override
    public int contextId() {
        return context.contextId();
    }

    public Context getContext() {
        return context;
    }
}
