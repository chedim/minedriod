package com.onkiup.minecraft.gui.views;

import com.onkiup.minecraft.gui.OnkiupGuiManager;
import com.onkiup.minecraft.gui.Overlay;
import com.onkiup.minecraft.gui.XmlHelper;
import com.onkiup.minecraft.gui.drawables.BorderDrawable;
import com.onkiup.minecraft.gui.drawables.Drawable;
import com.onkiup.minecraft.gui.drawables.StateDrawable;
import com.onkiup.minecraft.gui.drawables.TextDrawable;
import com.onkiup.minecraft.gui.events.Event;
import com.onkiup.minecraft.gui.events.EventBase;
import com.onkiup.minecraft.gui.events.KeyEvent;
import com.onkiup.minecraft.gui.events.MouseEvent;
import com.onkiup.minecraft.gui.primitives.Color;
import com.onkiup.minecraft.gui.primitives.Point;
import com.onkiup.minecraft.gui.primitives.Rect;
import com.onkiup.minecraft.gui.themes.Theme;
import net.java.games.input.Mouse;

/**
 * Created by chedim on 4/25/15.
 */
public class View extends EventBase {
    protected int id;
    protected Point position;
    protected Layout layout = new Layout(), resolvedLayout;
    protected Drawable background;
    protected int layoutWeight = 0;
    protected boolean drawRect;
    protected Overlay screen;

    protected ViewHolder mHolder;

    protected ContentView parent;

    public View() {

    }

    public void onDraw() {
        if (background != null) {
            background.setSize(resolvedLayout.getSize());
            background.draw(position);
        }

        if (drawRect) {
            Color debugColor = new Color(0x66ff00ff);
            BorderDrawable border = new BorderDrawable(debugColor);
            border.setSize(resolvedLayout.getSize());
            border.draw(position);

            // size text
            TextDrawable sizeText = new TextDrawable(resolvedLayout.getSize().toString(), 0xffff00ff);
            sizeText.setTextSize(0.5f);


            sizeText.draw(position.add(new Point(2, sizeText.getSize().y * -1)));
        }
    }

    public void setBackground(Drawable background) {
        this.background = background;
    }

    /**
     * This method should determine the View size
     * should be called by parent ONLY if the child layout
     * width or height equals WRAP_CONTENT
     * otherwise parent SHOULD calculate item size by itself
     * <p/>
     * Result item size SHOULD be set by setSize after being calculated
     *
     * @param boundaries max element size
     * @return
     */
    public Layout measure(Point boundaries) {
        Layout result = layout.clone();
        if (background != null) {
            Point backgroundSize = background.getOriginalSize();
            if (result.width == Layout.WRAP_CONTENT)  result.width = backgroundSize.x;
            if (result.height == Layout.WRAP_CONTENT) result.height = backgroundSize.y;
        } else {
            if (result.width == Layout.WRAP_CONTENT) result.width = 0;
            if (result.height == Layout.WRAP_CONTENT) result.height = 0;
        }

        return result;
    }

    public void setLayoutWeight(int weight) {
        layoutWeight = weight;
    }

    public int getLayoutWeight() {
        return layoutWeight;
    }

    public Layout getResolvedLayout() {
        return resolvedLayout;
    }

    /**
     * This method is used by parent to set our size after we were measured
     *
     * @param layout
     */
    public void resolveLayout(Layout layout) {
        resolvedLayout = layout;
    }

    public void unresolveLayout() {
        resolvedLayout = null;
    }

    public boolean isLayoutResolved() {
        return resolvedLayout != null;
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public Layout getLayout() {
        return layout;
    }

    public Rect getMargin() {
        return resolvedLayout.margin;
    }

    public Drawable getBackground() {
        return background;
    }

    public Rect getRectangle() {
        return new Rect(position, position.add(resolvedLayout.getSize()));
    }

    public void handleMouseEvent(MouseEvent event) {
        updateDrawableState(event);

        this.fireEvent(event.type, event);

        if (event.cancel) return;

        if (getParent() != null)
            getParent().fireEvent(event.type, event);
    }

    protected void updateDrawableState(MouseEvent event) {
        if (background != null && background instanceof StateDrawable) {
            StateDrawable.State newState = null;
            if (event.type == OnMouseIn.class) {
                newState = StateDrawable.State.HOVER;
            } else if (event.type == OnMouseOut.class) {
                newState = StateDrawable.State.DEFAULT;
            } else if (event.type == OnMouseDown.class) {
                newState = StateDrawable.State.PRESSED;
            } else if (event.type == OnMouseUp.class) {
                newState = StateDrawable.State.HOVER;
            }

            if (newState == null && ((StateDrawable) background).getState() == StateDrawable.State.DEFAULT)
                newState = StateDrawable.State.HOVER;

            if (newState != null)
                ((StateDrawable) background).setState(newState);
        }
    }

    public View findViewById(String id) {
        return findViewById(OnkiupGuiManager.getId(id));
    }

    public View findViewById(int id) {
        if (id == this.id) return this;
        return null;
    }

    public void handleKeyboardEvent(KeyEvent event) {
        this.fireEvent(event.type, event);
        if (!event.cancel) {
            event.target = getParent();
            getParent().fireEvent(event.type, event);
        }
    }

    public static class Layout {
        // allows a child to resize to fit it's content
        public static final int WRAP_CONTENT = -20000;

        // disallows a child from resizing
        public static final int MATCH_PARENT = -10000;

        // margin for this element
        public Rect margin;
        public Rect padding;

        /**
         * During measure:
         * parent view should pass here it's size
         * or WRAP_CONTENT if size is unspecified.
         * THERE SHOULD NOT BE MATCH_PARENT VALUE SENT FROM PARENT
         * child view should return it's measured size here
         */
        public int width = WRAP_CONTENT, height = WRAP_CONTENT;

        public Layout() {
            this(WRAP_CONTENT, WRAP_CONTENT);
        }

        public Layout(int width, int height, Rect margin, Rect padding) {
            this.margin = margin;
            this.padding = padding;
            this.width = width;
            this.height = height;
        }

        public Layout(int width, int height, Rect margin) {
            this(width, height, margin, OnkiupGuiManager.theme.getDefaultPadding());
        }

        public Layout(int width, int height) {
            this(width, height, OnkiupGuiManager.theme.getDefaultMargin());
        }

        @Override
        public Layout clone() {
            return new Layout(width, height, margin.clone(), padding.clone());
        }

        public boolean isResolved() {
            return !(width < 0 || height < 0);
        }

        public boolean shouldBeMeasured() {
            return width == WRAP_CONTENT || height == WRAP_CONTENT;
        }

        public void applyParent(Layout parent) {
            if (width == MATCH_PARENT) {
                if (parent.width < 0) throw new RuntimeException("parent width should be resolved");
                width = parent.width - margin.left - margin.right;
            }

            if (height == MATCH_PARENT) {
                if (parent.height < 0) throw new RuntimeException("parent height should be resolved");
                height = parent.height - margin.top - margin.bottom;
            }
        }

        public int getOuterWidth() {
            return width + margin.left + margin.right;
        }

        public int getOuterHeight() {
            return height + margin.top + margin.bottom;
        }

        public void setOuterWidth(int outerWidth) {
            width = outerWidth - margin.left - margin.right;
        }

        public void setOuterHeight(int outerHeight) {
            height = outerHeight - margin.top - margin.bottom;
        }

        public void setInnerWidth(int innerWidth) {
            width = innerWidth + padding.left + padding.right;
        }

        public void setInnerHeight(int innerHeight) {
            height = innerHeight + padding.top + padding.bottom;
        }

        public int getInnerWidth() {
            return width - padding.left - padding.right;
        }

        public int getInnerHeight() {
            return height - padding.top - padding.bottom;
        }

        public Point getInnerSize() {
            return new Point(getInnerWidth(), getInnerHeight());
        }

        public Point getSize() {
            return new Point(width, height);
        }

        public Point getOuterSize() {
            return new Point(getOuterWidth(), getOuterHeight());
        }

        public Rect getInnerRect() {
            return new Rect(new Point(padding.left, padding.top), getInnerSize().add(padding.coords()));
        }
    }

    public static interface OnMouseIn extends Event<MouseEvent> {
    }

    public static interface OnMouseMove extends Event<MouseEvent> {
    }

    public static interface OnClick extends Event<MouseEvent> {
    }

    public static interface OnDblClick extends Event<MouseEvent> {

    }

    public static interface OnMouseDown extends Event<MouseEvent> {
    }

    public static interface OnMouseUp extends Event<MouseEvent> {

    }

    public static interface OnMouseOut extends Event<MouseEvent> {
    }

    public static interface OnKeyDown extends Event<KeyEvent> {

    }

    public static interface OnKeyUp extends Event<KeyEvent> {

    }

    public static interface OnKeyPress extends Event<KeyEvent> {

    }

    public void setDebugDraw(boolean debugDraw) {
        drawRect = debugDraw;
    }

    protected void setParent(ContentView parent) {
        this.parent = parent;
    }

    public ContentView getParent() {
        return parent;
    }

    public void inflate(XmlHelper node, Theme theme) {
        setBackground(node.getDrawableAttr("mc", "background", null));
        Point size = new Point(0, 0);
        if (background != null) {
            size = background.getOriginalSize();
        }

        Layout layout = new Layout();

        layout.width = node.getDimenAttr("mc", "width", size.x);
        layout.height = node.getDimenAttr("mc", "height", size.y);

        layout.margin = node.getRectAttr("mc", "margin", theme.getDefaultMargin());
        layout.padding = node.getRectAttr("mc", "padding", theme.getDefaultPadding());

        setLayout(layout);
        this.id = node.getIdAttr("mc", "id");
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static interface OnScroll extends Event<MouseEvent> {
    }

    public boolean isFocusable() {
        return false;
    }

    public void handleFocus(boolean focused) {
        if (background instanceof StateDrawable) {
            ((StateDrawable) background).setState(focused ? StateDrawable.State.FOCUSED : StateDrawable.State.DEFAULT);
        }
    }

    public void setOverlay(Overlay o) {
        screen = o;
    }

    public Overlay getOverlay() {
        return screen;
    }

    public void focus() {
        getOverlay().focusItem(this);
    }

    public void setHolder(ViewHolder holder) {
        mHolder = holder;
        mHolder.setView(this);
    }

    public ViewHolder getHolder() {
        return mHolder;
    }
}
