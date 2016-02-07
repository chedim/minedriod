package com.onkiup.minedroid.gui.views;

import com.onkiup.minedroid.Context;
import com.onkiup.minedroid.gui.GuiManager;
import com.onkiup.minedroid.gui.Overlay;
import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.drawables.Drawable;
import com.onkiup.minedroid.gui.events.MouseEvent;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.primitives.Rect;
import com.onkiup.minedroid.gui.resources.Style;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows to scroll big views
 */
public class ScrollView extends ContentView {
    protected View child;
    protected Point scroll = new Point(0, 0);
    protected Drawable scrollDrawable;

    public ScrollView(Context context) {
        super(context);
        ResourceLocation rl = GuiManager.getTheme(context).getStyle("scroll_view").getResource("scrollbar_drawable", null);
        if (rl != null) {
            scrollDrawable = GuiManager.inflateDrawable(this, rl);
        }
    }

    @Override
    public void setDebug(boolean debugDraw) {
        super.setDebug(debugDraw);
        if (child != null) {
            child.setDebug(debugDraw);
        }
    }

    @Override
    public void resolveLayout(Layout layout) {
        super.resolveLayout(layout);
        if (child == null) return;

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
    public void setOverlay(Overlay o) {
        super.setOverlay(o);
        child.setOverlay(o);
    }

    @Override
    public void drawContents(float partialTicks) {
        if (child == null) return;

        Layout childLayout = child.getResolvedLayout();

        if (scroll.y > childLayout.getOuterHeight() - resolvedLayout.getInnerHeight())
            scroll.y = childLayout.getOuterHeight() - resolvedLayout.getInnerHeight();
        if (scroll.y < 0) scroll.y = 0;
        if (childLayout.getOuterHeight() < resolvedLayout.getInnerHeight()) scroll.y = 0;

        child.setPosition(position.add(resolvedLayout.getInnerRect().coords().sub(scroll))
                .add(resolvedLayout.getInnerRect().coords()).add(childLayout.margin.coords()));
        child.onDraw(partialTicks);

        // drawing scroller
        int scrollerHeight = (int) (resolvedLayout.getInnerHeight() * 1f / childLayout.getOuterHeight()
                * resolvedLayout.getInnerHeight());
        int scrollerPos = (int) (resolvedLayout.getInnerHeight() * 1f * (scroll.y * 1f / childLayout.getOuterHeight()));

        if (scrollDrawable != null) {
            scrollDrawable.setSize(new Point(2, scrollerHeight));
            Point scPosition = position.add(new Point(resolvedLayout.getInnerWidth() - 4, scrollerPos))
                    .add(resolvedLayout.getInnerRect().coords());
            scrollDrawable.draw(scPosition);
        }
    }

    @Override
    public void clear() {
        child = null;
    }

    @Override
    public Layout measure(Point boundaries) {
        return layout.clone();
    }

    public void setChild(View view) {
        child = view;
        child.setParent(this);
        child.setDebug(debug);
    }

    /**
     * @return Child element
     */
    public View getChild() {
        return child;
    }

    /**
     * Scrolls view to the given poition
     *
     * @param target position
     */
    public void scrollTo(Point target) {
        this.scroll = target.clone();
    }

    /**
     * @return current scroll value
     */
    public Point getScroll() {
        return this.scroll.clone();
    }

    @Override
    public void inflate(XmlHelper node, Style theme) {
        super.inflate(node, theme);
        List<XmlHelper> children = node.getChildren();

        for (XmlHelper childNode : children) {
            if (child != null) throw new RuntimeException("Too many children for a ScrollView");
            try {
                setChild(GuiManager.processNode(childNode, theme));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        scrollDrawable = node.getDrawableAttr(GuiManager.NS, "scrollbar_drawable", style, null);
    }

    @Override
    public void handleMouseEvent(MouseEvent event) {
        if (child != null) child.handleMouseEvent(event);
        if (event.type == OnScroll.class && !event.cancel) {
            this.scroll = this.scroll.sub(event.wheel);
        }
    }

    @Override
    public View findViewById(int id) {
        if (id == this.id) return this;
        if (child == null) return null;
        if (child.getId() == id) return child;
        return child.findViewById(id);
    }

    /**
     * @return List of focusables views
     */
    public List<View> getFocusables() {
        List<View> result = new ArrayList<View>();
        if (child != null) {
            if (child.isFocusable()) result.add(child);
            if (child instanceof ViewGroup) result.addAll(((ViewGroup) child).getFocusables());
        }

        return result;
    }

    @Override
    protected String getThemeStyleName() {
        return "scroll_view";
    }
}
