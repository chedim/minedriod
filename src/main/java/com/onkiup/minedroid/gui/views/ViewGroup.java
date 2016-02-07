package com.onkiup.minedroid.gui.views;

import com.onkiup.minedroid.Context;
import com.onkiup.minedroid.gui.GuiManager;
import com.onkiup.minedroid.gui.Overlay;
import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.events.MouseEvent;
import com.onkiup.minedroid.gui.primitives.Rect;
import com.onkiup.minedroid.gui.resources.Style;

import java.util.ArrayList;
import java.util.List;

/**
 * Parent clas for all views that contain other views
 */
public abstract class ViewGroup extends ContentView {
    private List<View> children = new ArrayList<View>();

    public ViewGroup(Context r) {
        super(r);
    }

    /**
     * Adds a new child
     * @param child
     */
    public void addChild(View child) {
        children.add(child);
        child.setDebug(debug||child.debug);
        child.setParent(this);
        child.setOverlay(getOverlay());
    }

    @Override
    public void clear() {
        children.clear();
    }

    public void addChildAt(int position, View child) {
        children.add(position, child);
        child.setDebug(debug||child.debug);
        child.setParent(this);
        child.setOverlay(getOverlay());
    }

    /**
     * Returns count of added children
     * @return Children count
     */
    public int getChildrenCount() {
        return children.size();
    }

    public void removeChild(View child) {
        children.remove(child);
        child.setDebug(false);
        child.setParent(null);
        child.setOverlay(null);
    }

    public void removeChildAt(int position) {
        View child = children.get(position);
        child.setDebug(false);
        children.remove(position);
        child.setParent(null);
        child.setOverlay(null);
    }

    @Override
    public void setDebug(boolean debugDraw) {
        super.setDebug(debugDraw);
        for (View child : children) {
            child.setDebug(debugDraw);
        }
    }

    @Override
    public void handleMouseEvent(MouseEvent event) {

        updateDrawableState(event);

        MouseEvent childEvent = null;
        Rect inner = resolvedLayout.getInnerRect().move(position);
        for (int x = children.size() - 1; x > -1; x--) {
            View child = children.get(x);
            // do not allow rectangles to go out from my inner frame
            Rect childRect = child.getRectangle().and(inner);
            // skipping items outside of our viewport — they cannot receive any events
            if (childRect == null) continue;
            if (!childRect.contains(event.coords)) {
                if (childRect.contains(event.coords.sub(event.diff))) {
                    // MOUSE OUT
                    MouseEvent out = event.clone();
                    out.type = OnMouseOut.class;
                    out.target = child;
                    out.source = child;
                    child.handleMouseEvent(out);
                }
            } else {
                // do not send one event to multiple children
                // since children being processed from the end,
                // the latter child should be hidden by
                // the previous children
                if (childEvent == null) {
                    event.source = child;
                    event.target = child;
                    childEvent = event.clone();
                    if (event.type == OnMouseMove.class || event.type == OnMouseIn.class) {
                        if (!childRect.contains(event.coords.sub(event.diff))) {
                            childEvent.type = OnMouseIn.class;
                        }
                    }
                    child.handleMouseEvent(childEvent);
                }
            }
        }

        if (childEvent != null && childEvent.cancel) return;

        if (!event.cancel && getParent() != null) getParent().fireEvent(event.type, event);
    }

    public View getChildAt(int position) {
        return children.get(position);
    }

    @Override
    protected String getThemeStyleName() {
        return "view_group";
    }

    public Layout createLayout() {
        return new Layout();
    }

    @Override
    public void inflate(XmlHelper node, Style theme) {
        super.inflate(node, theme);
        List<XmlHelper> children = node.getChildren();

        for (XmlHelper childNode : children) {
            View child = inflateChild(childNode, theme);
            if (child != null) addChild(child);
        }
    }

    public View inflateChild(XmlHelper node, Style theme) {
        try {
            return GuiManager.processNode(node, theme);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public View findViewById(int id) {
        if (id == -1) return null;
        if (id == 0) return this;

        for (View child : children) {
            if (child.getId() == id) return child;
            View sub = child.findViewById(id);
            if (sub != null) return sub;
        }

        return null;
    }

    public List<View> getFocusables() {
        List<View> result = new ArrayList<View>();
        for (int i = 0; i < getChildrenCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof ViewGroup) {
                result.addAll(((ViewGroup) child).getFocusables());
            } else if (child.isFocusable()) {
                result.add(child);
            }
        }

        return result;
    }

    @Override
    public void setOverlay(Overlay o) {
        super.setOverlay(o);
        for (int i = 0; i < getChildrenCount(); i++) {
            getChildAt(i).setOverlay(o);
        }
    }

    public void removeAllChildren() {
        children.clear();
    }

    public Integer getChildPosition(View child) {
        return children.indexOf(child);
    }
}
