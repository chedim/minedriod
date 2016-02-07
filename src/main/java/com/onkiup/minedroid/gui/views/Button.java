package com.onkiup.minedroid.gui.views;

import com.onkiup.minedroid.Context;

/**
 * Button GUI element
 */
public class Button extends TextView {
    public Button(Context context) {
        super(context);
    }

    public Button(Context context, String text) {
        super(context, text);
    }

    @Override
    protected String getThemeStyleName() {
        return "button";
    }
}
