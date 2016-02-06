package com.onkiup.minedroid.gui.events;

import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.primitives.Rect;
import com.onkiup.minedroid.gui.views.ContentView;
import com.onkiup.minedroid.gui.views.View;
import com.onkiup.minedroid.gui.views.ViewGroup;

/**
 * Created by chedim on 7/30/15.
 */
public class DragEvent {
    public Class type;
    public View source, target;
    public MouseEvent mouseEvent;
    public Boolean cancel = false;
    public Point viewMouseOffset;
    public Rect dragArea;
    public ContentView parent;
    public Integer parentPosition;

    public DragEvent clone() {
        DragEvent cloned = new DragEvent();
        cloned.type = type;
        cloned.source = source;
        cloned.target = target;
        cloned.mouseEvent = mouseEvent.clone();
        cloned.cancel = cancel;
        cloned.viewMouseOffset = viewMouseOffset;
        cloned.dragArea = dragArea;
        cloned.parent = parent;
        cloned.parentPosition = parentPosition;
        return cloned;
    }
}
