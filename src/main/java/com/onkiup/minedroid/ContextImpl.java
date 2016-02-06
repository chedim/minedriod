package com.onkiup.minedroid;

/**
 * Created by chedim on 8/12/15.
 */
public class ContextImpl implements Context {

    protected final int context;

    public ContextImpl(int context) {
        this.context = context;
    }

    @Override
    public int contextId() {
        return context;
    }
}
