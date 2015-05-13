package com.onkiup.minecraft.gui.views;

import com.onkiup.minecraft.gui.OnkiupGuiManager;
import com.onkiup.minecraft.gui.Overlay;
import com.onkiup.minecraft.gui.XmlHelper;
import com.onkiup.minecraft.gui.events.MouseEvent;
import com.onkiup.minecraft.gui.primitives.Rect;
import com.onkiup.minecraft.gui.themes.Theme;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chedim on 4/25/15.
 */
public abstract class ViewGroup extends ContentView {
    private List<View> children = new ArrayList<View>();

    public void addChild(View child) {
        children.add(child);
        child.setDebugDraw(drawRect);
        child.setParent(this);
        child.setOverlay(getOverlay());
    }

    public void addChildAt(int position, View child) {
        children.add(position, child);
        child.setDebugDraw(drawRect);
        child.setParent(this);
        child.setOverlay(getOverlay());
    }

    public int getChildrenCount() {
        return children.size();
    }

    public void removeChild(View child) {
        children.remove(child);
        child.setDebugDraw(false);
        child.setParent(null);
        child.setOverlay(null);
    }

    public void removeChildAt(int position) {
        View child = children.get(position);
        child.setDebugDraw(false);
        children.remove(position);
        child.setParent(null);
        child.setOverlay(null);
    }

    @Override
    public void setDebugDraw(boolean debugDraw) {
        super.setDebugDraw(debugDraw);
        for (View child : children) {
            child.setDebugDraw(debugDraw);
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

    public Layout createLayout() {
        return new Layout();
    }

    @Override
    public void inflate(XmlHelper node, Theme theme) {
        super.inflate(node, theme);
        NodeList children = node.getChildren();

        for (int x = 0; x < children.getLength(); x++) {
            Node childNode = children.item(x);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                View child = inflateChild(new XmlHelper(childNode), theme);
                if (child != null) addChild(child);
            }
        }
    }

    protected View inflateChild(XmlHelper node, Theme theme) {
        try {
            return OnkiupGuiManager.processNode(node.getNode(), theme);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

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

    public View findViewById(String id) {
        return findViewById(OnkiupGuiManager.getId(id));
    }

    public List<View> getFocusables() {
        List<View> result = new ArrayList<View>();
        for (int i = 0; i<getChildrenCount(); i++) {
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
        for (int i=0; i<getChildrenCount(); i++) {
            getChildAt(i).setOverlay(o);
        }
    }

    public void removeAllChildren() {
        children.clear();
    }
}
