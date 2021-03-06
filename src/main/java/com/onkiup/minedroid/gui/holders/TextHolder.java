package com.onkiup.minedroid.gui.holders;

import com.onkiup.minedroid.Context;
import com.onkiup.minedroid.gui.views.ContentView;
import com.onkiup.minedroid.gui.views.ListView;
import com.onkiup.minedroid.gui.views.TextView;
import com.onkiup.minedroid.gui.views.View;
import net.minecraft.util.ResourceLocation;

/**
 * Simple holder for string values
 */
public class TextHolder extends ListView.Holder<String>{
    /**
     * View that is used to display the value
     */
    protected TextView view;

    public TextHolder(Context context) {
        super(context);
    }

    @Override
    protected ResourceLocation getViewLocation() {
        return null;
    }

    @Override
    protected void fill(String object) {
        view.setText(object);
    }

    @Override
    protected void link(View view) {
        this.view = (TextView) view;
    }

    @Override
    public View getView() {
        if (mView == null) {
            TextView v = new TextView(this);
            v.setGravityVertical(ContentView.VGravity.CENTER);
            v.setGravityHorizontal(ContentView.HGravity.CENTER);
            v.setHolder(this);
        }

        return mView;
    }
}
