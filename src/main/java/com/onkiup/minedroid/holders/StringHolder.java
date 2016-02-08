package com.onkiup.minedroid.holders;

import com.onkiup.minedroid.Context;
import com.onkiup.minedroid.R;
import com.onkiup.minedroid.gui.views.ListView;
import com.onkiup.minedroid.gui.views.TextView;
import com.onkiup.minedroid.gui.views.View;
import net.minecraft.util.ResourceLocation;

public class StringHolder extends ListView.Holder<String> {
    private TextView text;

    public StringHolder(Context context) {
        super(context);
    }

    @Override
    protected ResourceLocation getViewLocation() {
        return R.layout.holder_string;
    }

    @Override
    protected void fill(String object) {
        text.setText(object);
    }

    @Override
    protected void link(View view) {
        text = (TextView) view.findViewById(R.id.text);
    }
}
