package com.onkiup.minecraft.gui.holders;

import com.onkiup.minecraft.gui.views.ContentView;
import com.onkiup.minecraft.gui.views.ListView;
import com.onkiup.minecraft.gui.views.TextView;
import com.onkiup.minecraft.gui.views.View;
import net.minecraft.util.ResourceLocation;

/**
 * Created by chedim on 5/13/15.
 */
public class TextHolder extends ListView.Holder<String>{

    protected TextView view;

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
            TextView v = new TextView();
            v.setGravityVertical(ContentView.VGravity.CENTER);
            v.setGravityHorizontal(ContentView.HGravity.CENTER);
            v.setHolder(this);
        }

        return mView;
    }
}
