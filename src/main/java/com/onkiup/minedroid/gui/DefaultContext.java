package com.onkiup.minedroid.gui;

/**
 * Created by chedim on 5/22/15.
 */
public class DefaultContext implements Context {
    protected Class R;

    public DefaultContext(Class r) {
        R = r;
    }

    public Class R() {
        return R;
    }
}
