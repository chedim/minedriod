package com.onkiup.minedroid.gui.views;

import com.onkiup.minedroid.Context;
import com.onkiup.minedroid.gui.GuiManager;
import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.events.KeyEvent;
import com.onkiup.minedroid.gui.events.MouseEvent;
import com.onkiup.minedroid.gui.resources.Style;

public class Focus extends LinearLayout {

    private int target;

    public Focus(Context context) {
        super(context);
    }

    @Override
    public void handleMouseEvent(MouseEvent event) {
        View target = findViewById(this.target);
        if (target != null) {
            target.handleMouseEvent(event);
            target.focus();
        }
    }

    @Override
    public void handleKeyboardEvent(KeyEvent event) {
        View target = findViewById(this.target);
        if (target != null) {
            target.handleKeyboardEvent(event);
            target.focus();
        }
    }

    @Override
    protected String getThemeStyleName() {
        return "focus";
    }

    @Override
    public void inflate(XmlHelper node, Style theme) {
        this.target = node.getIdAttr(GuiManager.NS, "target", theme);
        super.inflate(node, theme);
    }
}
