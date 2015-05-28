package com.onkiup.minedroid.gui;

/**
 * Default Context implementation
 */
public class DefaultContext implements Context {
    protected Class R;

    public DefaultContext(Class r) {
        R = r;
    }

    @Override
    public Class R() {
        return R;
    }
}
