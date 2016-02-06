package com.onkiup.minedroid.exprop;

import com.onkiup.minedroid.Context;
import com.onkiup.minedroid.MineDroid;
import com.onkiup.minedroid.Modification;
import com.onkiup.minedroid.net.ExPropDeltaRequestPacket;
import com.onkiup.minedroid.timer.Task;

import javax.swing.text.html.parser.Entity;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

/**
 * Created by chedim on 8/13/15.
 */
public class ExPropClientSync implements Task.Client {

    @Override
    public void execute(Context ctx) {
        List<WeakReference<ExProps>> syncronizables = ExProps.getSyncronizables();
        if (syncronizables == null || syncronizables.size() == 0) return;
        for (WeakReference<ExProps> reference: syncronizables) {
            ExProps props = reference.get();
            if (props == null) continue;
            if (props.hadChanged()) {
                props.fireEvent(ExProps.Changed.class, props);
            }
        }
    }
}
