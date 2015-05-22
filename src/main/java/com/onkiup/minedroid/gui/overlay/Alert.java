package com.onkiup.minedroid.gui.overlay;

import com.onkiup.minedroid.gui.Overlay;
import com.onkiup.minedroid.gui.events.MouseEvent;
import com.onkiup.minedroid.gui.views.*;
import net.minecraft.util.ResourceLocation;

/**
 * Created by chedim on 5/18/15.
 */
public class Alert extends Overlay {
    protected TextView mMessage = new TextView();
    protected Button mOkButton = new Button();
    protected RelativeLayout layout = new RelativeLayout();
    protected String text;

    public Alert(String text) {
        super();
    }

    protected View.OnClick mOk = new View.OnClick() {
        @Override
        public void handle(MouseEvent event) {
            dismiss();
        }
    };

    @Override
    protected ResourceLocation getContentLayout() {
        return null;
    }

    @Override
    protected View getContentView() {
        layout.addChild(mMessage);
        layout.addChild(mOkButton);
        mMessage.getLayout().width = RelativeLayout.Layout.MATCH_PARENT;
        mMessage.getLayout().height = RelativeLayout.Layout.WRAP_CONTENT;
        mOkButton.getLayout().width = RelativeLayout.Layout.WRAP_CONTENT;
        mOkButton.getLayout().height = RelativeLayout.Layout.WRAP_CONTENT;
        RelativeLayout.Layout cl = layout.getChildLayout(mOkButton);
        cl.below = mMessage;
        cl.alignCenter = layout;
        return layout;
    }

    @Override
    protected void fill(View content) {

    }

    @Override
    protected void onStart() {

    }

    @Override
    protected void onStop() {

    }
}
