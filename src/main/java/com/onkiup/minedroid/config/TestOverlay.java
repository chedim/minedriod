package com.onkiup.minedroid.config;

import java.util.Arrays;

import com.onkiup.minedroid.Context;
import com.onkiup.minedroid.R;
import com.onkiup.minedroid.gui.events.MouseEvent;
import com.onkiup.minedroid.gui.views.CheckBox;
import com.onkiup.minedroid.gui.views.ListView;
import com.onkiup.minedroid.gui.views.ProgressView;
import com.onkiup.minedroid.gui.views.View;
import com.onkiup.minedroid.holders.StringHolder;
import com.onkiup.minedroid.timer.Task;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class TestOverlay extends com.onkiup.minedroid.gui.ConfigOverlay {

    public TestOverlay(GuiScreen parent) {
        super(parent);
    }

    private ProgressView pv;
    private ListView lv;

    private CheckBox.OnChange debugListener = new CheckBox.OnChange() {
        @Override
        public void handle(Boolean event) {
            TestOverlay.this.getContentView().setDebug(event);
        }
    };

    private View.OnClick closeListener = new View.OnClick() {
        @Override
        public void handle(MouseEvent event) {
            dismiss();
        }
    };

    private Task.Client progressor = new Task.Client() {
        @Override
        public void execute(Context ctx) {
            int val = pv.getValue();
            pv.setValue(++val % 100);
        }
    };

    @Override
    protected ResourceLocation getContentLayout() {
        return R.layout.minedroid_test;
    }

    @Override
    protected void fill(View content) {
        findViewById(R.id.debug).on(debugListener);
        findViewById(R.id.close).on(closeListener);

        pv = (ProgressView) findViewById(R.id.progress);

        lv = (ListView) findViewById(R.id.list);
        lv.setHolder(String.class, StringHolder.class);
        lv.setObjects(Arrays.asList("String1", "String2", "How", "many", "strings", "should", "I", "put?"));
    }

    @Override
    protected void onStart() {
        timer.repeat(0.5f, progressor);
    }

    @Override
    protected void onStop() {
        timer.stop(progressor);
    }
}
