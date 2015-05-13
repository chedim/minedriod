package com.onkiup.minecraft.gui.views;

import com.onkiup.minecraft.gui.OnkiupGuiManager;
import com.onkiup.minecraft.gui.XmlHelper;
import com.onkiup.minecraft.gui.drawables.RoundedCornerDrawable;
import com.onkiup.minecraft.gui.events.MouseEvent;
import com.onkiup.minecraft.gui.primitives.Point;
import com.onkiup.minecraft.gui.primitives.Rect;
import com.onkiup.minecraft.gui.themes.Theme;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chedim on 4/30/15.
 */
public class ScrollView extends ContentView {
    protected View child;
    protected Point scroll = new Point(0, 0);

    public ScrollView() {

    }

    @Override
    public void resolveLayout(Layout layout) {
        super.resolveLayout(layout);
        Layout childLayout = child.getLayout().clone();

        if (childLayout.shouldBeMeasured())
            childLayout = child.measure(null);

        Rect margin = childLayout.margin;

        if (childLayout.width == Layout.MATCH_PARENT)
            childLayout.width = resolvedLayout.getInnerWidth() - margin.left - margin.right;
        if (childLayout.height == Layout.MATCH_PARENT)
            childLayout.height = resolvedLayout.getInnerHeight() - margin.top - margin.bottom;

        child.resolveLayout(childLayout);
    }

    @Override
    public void drawContents() {
        Layout childLayout = child.getResolvedLayout();

        if (scroll.y > childLayout.getOuterHeight() - resolvedLayout.getInnerHeight())
            scroll.y = childLayout.getOuterHeight() - resolvedLayout.getInnerHeight();
        if (scroll.y < 0) scroll.y = 0;
        if (childLayout.getOuterHeight() < resolvedLayout.getInnerHeight()) scroll.y = 0;

        child.setPosition(position.add(resolvedLayout.getInnerRect().coords().sub(scroll))
                .add(resolvedLayout.getInnerRect().coords()).add(childLayout.margin.coords()));
        child.onDraw();

        // drawing scroller
        int scrollerHeight = (int) (resolvedLayout.getInnerHeight() * 1f / childLayout.getOuterHeight()
                    * resolvedLayout.getInnerHeight());
        int scrollerPos = (int) (resolvedLayout.getInnerHeight() * 1f * (scroll.y * 1f / childLayout.getOuterHeight()));

        RoundedCornerDrawable scroller = new RoundedCornerDrawable(0x33000000, 1);

        scroller.setSize(new Point(2, scrollerHeight));
        Point scPosition = position.add(new Point(resolvedLayout.getInnerWidth() - 4, scrollerPos))
                .add(resolvedLayout.getInnerRect().coords());
        scroller.draw(scPosition);
     }

    @Override
    public Layout measure(Point boundaries) {
        Layout result = layout.clone();
        return result;
    }

    public void setChild(View view) {
        child = view;
        child.setParent(this);
    }

    public View getChild() {
        return child;
    }

    public void scrollTo(Point target) {
        this.scroll = target.clone();
    }

    public Point getScroll() {
        return this.scroll.clone();
    }

    @Override
    public void inflate(XmlHelper node, Theme theme) {
        super.inflate(node, theme);
        NodeList children = node.getChildren();

        for (int x = 0; x < children.getLength(); x++) {
            Node childNode = children.item(x);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                if (child != null) throw new RuntimeException("Too many children for a ScrollView");
                try {
                    setChild(OnkiupGuiManager.processNode(childNode, theme));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public void handleMouseEvent(MouseEvent event) {
        child.handleMouseEvent(event);
        if (event.type == OnScroll.class && !event.cancel) {
            this.scroll = this.scroll.sub(event.wheel);
        }
    }

    @Override
    public View findViewById(int id) {
        if (child.getId() == id) return child;
        return child.findViewById(id);
    }

    public List<View> getFocusables() {
        List<View> result = new ArrayList<View>();
        if (child.isFocusable()) result.add(child);
        if (child instanceof ViewGroup) result.addAll(((ViewGroup) child).getFocusables());

        return result;
    }
}
