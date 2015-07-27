package com.onkiup.minedroid.gui.views;

import com.onkiup.minedroid.gui.Context;
import com.onkiup.minedroid.gui.MineDroid;
import com.onkiup.minedroid.gui.Overlay;
import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.drawables.DebugDrawable;
import com.onkiup.minedroid.gui.drawables.Drawable;
import com.onkiup.minedroid.gui.drawables.StateDrawable;
import com.onkiup.minedroid.gui.drawables.TextDrawable;
import com.onkiup.minedroid.gui.events.Event;
import com.onkiup.minedroid.gui.events.EventBase;
import com.onkiup.minedroid.gui.events.KeyEvent;
import com.onkiup.minedroid.gui.events.MouseEvent;
import com.onkiup.minedroid.gui.primitives.Color;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.primitives.Rect;
import com.onkiup.minedroid.gui.resources.Style;

/**
 * Parent class for all View elements.
 */
public class View extends EventBase implements Context {
    /**
     * Element id
     */
    protected int id;
    /**
     * Position on screen
     */
    protected Point position;
    /**
     * Element margin, padding and size initial parameters
     */
    protected Layout layout = new Layout();
    /**
     * Element's actual margin, padding, and size
     */
    protected Layout resolvedLayout;
    /**
     * Element's background drawable
     */
    protected Drawable background;
    /**
     * Element weight for auto width calculation
     */
    protected int layoutWeight = 0;
    /**
     * Enforces element to draw it's border
     */
    protected boolean debug;
    /**
     * Screen that shows this element
     */
    protected Overlay screen;
    /**
     * Mod context
     */
    protected Class R;

    /**
     * Associated with this View ViewHolder instance
     */
    protected ViewHolder mHolder;

    /**
     * Parent view
     */
    protected ContentView parent;

    /**
     * Style of the element
     */
    protected Style style;

    public View(Context context) {
        R = context.R();
    }

    /**
     * Draws this view
     */
    public void onDraw() {
        if (background != null) {
            background.setSize(resolvedLayout.getSize());
            background.draw(position);
        }

        if (debug) {
            Color debugColor = new Color(0x66ff00ff);
            DebugDrawable border = new DebugDrawable(debugColor);
            border.setSize(resolvedLayout.getSize());
            border.draw(position);

            // size text
            TextDrawable sizeText = new TextDrawable(resolvedLayout.getSize().toString(), 0xffff00ff);
            sizeText.setTextSize(0.5f);


            sizeText.draw(position.add(new Point(2, sizeText.getSize().y * -1)));
        }
    }

    /**
     * Changes view background
     *
     * @param background New view background
     */
    public void setBackground(Drawable background) {
        this.background = background;
    }

    /**
     * This method should determine the View size and
     * should be called by parent ONLY if the child layout
     * width or height equals WRAP_CONTENT
     * otherwise parent SHOULD calculate item size by itself
     * <p/>
     * Result item size SHOULD be set by resolveLayout after it was calculated
     *
     * @param boundaries max element size
     * @return measured layout
     */
    public Layout measure(Point boundaries) {
        Layout result = layout.clone();
        if (background != null) {
            Point backgroundSize = background.getOriginalSize();
            if (result.width == Layout.WRAP_CONTENT) result.width = backgroundSize.x;
            if (result.height == Layout.WRAP_CONTENT) result.height = backgroundSize.y;
        } else {
            if (result.width == Layout.WRAP_CONTENT) result.width = 0;
            if (result.height == Layout.WRAP_CONTENT) result.height = 0;
        }

        return result;
    }

    /**
     * Set weight for automatic width/height calculation
     *
     * @param weight item weight
     */
    public void setLayoutWeight(int weight) {
        layoutWeight = weight;
    }

    /**
     * @return View's weight
     */
    public int getLayoutWeight() {
        return layoutWeight;
    }

    /**
     * @return View's resolved layout
     */
    public Layout getResolvedLayout() {
        return resolvedLayout;
    }

    /**
     * This method is used by parent to set view size after it was measured
     *
     * @param layout resolved layout
     */
    public void resolveLayout(Layout layout) {
        resolvedLayout = layout;
    }

    /**
     * Unsets previously resolved layout
     */
    public void unresolveLayout() {
        resolvedLayout = null;
    }

    /**
     * @return true if the view has resolved layout
     */
    public boolean isLayoutResolved() {
        return resolvedLayout != null;
    }

    /**
     * Sets view initial layout
     *
     * @param layout
     */
    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    /**
     * Sets view position on screen
     *
     * @param position new position
     */
    public void setPosition(Point position) {
        this.position = position;
    }

    /**
     * @return View's initial layout
     */
    public Layout getLayout() {
        return layout;
    }

    /**
     * @return View margin rectangle offsets
     */
    public Rect getMargin() {
        return resolvedLayout.margin;
    }

    /**
     * @return View's background drawable
     */
    public Drawable getBackground() {
        return background;
    }

    /**
     * @return View's size without margins
     */
    public Rect getRectangle() {
        return new Rect(position, position.add(resolvedLayout.getSize()));
    }

    /**
     * Handles mouse events for this view
     *
     * @param event Mouse event information
     */
    public void handleMouseEvent(MouseEvent event) {
        updateDrawableState(event);

        this.fireEvent(event.type, event);

        if (event.cancel) return;

        if (getParent() != null)
            getParent().fireEvent(event.type, event);
    }

    /**
     * Updates view's background state if it is a @StateDrawable instance
     *
     * @param event Mouse event that caused state change
     */
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

    /**
     * Searches all children view (and the view itself) by given ID
     *
     * @param id Id to search
     * @return Found view or null
     */
    public View findViewById(int id) {
        if (id == this.id) return this;
        return null;
    }

    /**
     * Handles keyboard events
     *
     * @param event Keyboard event information
     */
    public void handleKeyboardEvent(KeyEvent event) {
        this.fireEvent(event.type, event);
        if (!event.cancel) {
            event.target = getParent();
            getParent().fireEvent(event.type, event);
        }
    }

    @Override
    public Class R() {
        return R;
    }

    /**
     * View layout information holder
     */
    public static class Layout {
        /**
         * allows a child to resize accordingly to it's content
         */
        public static final int WRAP_CONTENT = -20000;

        /**
         * disallows a child from resizing and makes it to fill the whole parent inner area
         */
        public static final int MATCH_PARENT = -10000;

        /**
         * View margins
         */
        public Rect margin;
        /**
         * View paddings
         */
        public Rect padding;

        /**
         * View's size
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
            this(width, height, margin, new Rect(0, 0, 0, 0));
        }

        public Layout(int width, int height) {
            this(width, height, new Rect(0, 0, 0, 0));
        }

        @Override
        public Layout clone() {
            return new Layout(width, height, margin.clone(), padding.clone());
        }

        /**
         * Checks if this layout doesn't need to be resolved
         *
         * @return true if layout contains real width and height values
         */
        public boolean isResolved() {
            return !(width < 0 || height < 0);
        }

        /**
         * Checks if this view should be measured accordingly to it's contents
         *
         * @return true if width or height equals WRAP_CONTENT
         */
        public boolean shouldBeMeasured() {
            return width == WRAP_CONTENT || height == WRAP_CONTENT;
        }

        /**
         * @return View width with margins
         */
        public int getOuterWidth() {
            return width + margin.left + margin.right;
        }

        /**
         * @return View height with margins
         */
        public int getOuterHeight() {
            return height + margin.top + margin.bottom;
        }

        /**
         * Resize view to fit it (including margins) into given width
         *
         * @param outerWidth width limit
         */
        public void setOuterWidth(int outerWidth) {
            width = outerWidth - margin.left - margin.right;
        }

        /**
         * Resize view to fit it (including margins) into given height
         *
         * @param outerHeight height limit
         */
        public void setOuterHeight(int outerHeight) {
            height = outerHeight - margin.top - margin.bottom;
        }

        /**
         * Sets view's width without paddings and margins
         *
         * @param innerWidth inner width limit
         */
        public void setInnerWidth(int innerWidth) {
            width = innerWidth + padding.left + padding.right;
        }

        /**
         * Sets view's height without paddings and margins
         *
         * @param innerHeight inner height limit
         */
        public void setInnerHeight(int innerHeight) {
            height = innerHeight + padding.top + padding.bottom;
        }

        /**
         * @return View width without paddings and margins
         */
        public int getInnerWidth() {
            return width - padding.left - padding.right;
        }

        /**
         * @return View height without paddings and margins
         */
        public int getInnerHeight() {
            return height - padding.top - padding.bottom;
        }

        /**
         * @return View size without paddings and margins
         */
        public Point getInnerSize() {
            return new Point(getInnerWidth(), getInnerHeight());
        }

        /**
         * @return View size with paddings but margins
         */
        public Point getSize() {
            return new Point(width, height);
        }

        /**
         * @return View size with paddings and margins
         */
        public Point getOuterSize() {
            return new Point(getOuterWidth(), getOuterHeight());
        }

        /**
         * @return Views inner rectangle
         */
        public Rect getInnerRect() {
            return new Rect(new Point(padding.left, padding.top), getInnerSize().add(padding.coords()));
        }
    }

    public interface OnMouseIn extends Event<MouseEvent> {
    }

    public interface OnMouseMove extends Event<MouseEvent> {
    }

    public interface OnClick extends Event<MouseEvent> {
    }

    public interface OnDblClick extends Event<MouseEvent> {

    }

    public interface OnMouseDown extends Event<MouseEvent> {
    }

    public interface OnMouseUp extends Event<MouseEvent> {

    }

    public interface OnMouseOut extends Event<MouseEvent> {
    }

    public interface OnKeyDown extends Event<KeyEvent> {

    }

    public interface OnKeyUp extends Event<KeyEvent> {

    }

    public interface OnKeyPress extends Event<KeyEvent> {

    }

    /**
     * Forces view to draw it's borders
     *
     * @param debugDraw true if view should drae borders
     */
    public void setDebug(boolean debugDraw) {
        debug = debugDraw;
    }

    /**
     * Sets view's parent view
     *
     * @param parent ContentView that holds this view
     */
    protected void setParent(ContentView parent) {
        this.parent = parent;
    }

    /**
     * @return Parent view
     */
    public ContentView getParent() {
        return parent;
    }

    /**
     * Loads this view from a XML Node.
     *
     * @param node  Source node
     * @param theme Theme to apply
     */
    public void inflate(XmlHelper node, Style theme) {
        setBackground(node.getDrawableAttr(MineDroid.NS, "background", null));
        Point size = new Point(0, 0);
        if (background != null) {
            size = background.getOriginalSize();
        }

        Layout layout = new Layout();

        layout.width = node.getDimenAttr(MineDroid.NS, "width", size.x);
        layout.height = node.getDimenAttr(MineDroid.NS, "height", size.y);

        theme = theme.getStyle(getThemeStyleName());

        style = node.getStyleAttr(null, "style", null);
        if (style != null) {
            style.setFallbackTheme(theme);
        } else {
            style = theme;
        }

        Rect margin = style.getRect("margin"), padding = style.getRect("padding");

        layout.margin = node.getRectAttr(MineDroid.NS, "margin", margin);
        layout.padding = node.getRectAttr(MineDroid.NS, "padding", padding);

        setLayout(layout);
        this.id = node.getIdAttr(MineDroid.NS, "id");
    }

    /**
     * @return theme style name for this class
     */
    protected String getThemeStyleName() {
        return "view";
    }

    /**
     * Sets view's id
     *
     * @param id new id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return id of the view
     */
    public int getId() {
        return id;
    }

    public interface OnScroll extends Event<MouseEvent> {
    }

    /**
     *
     * @return true if the view can gain focus
     */
    public boolean isFocusable() {
        return false;
    }

    /**
     * Called when the view gains or losts focus
     * @param focused true if focus gained
     */
    public void handleFocus(boolean focused) {
        if (background instanceof StateDrawable) {
            ((StateDrawable) background).setState(focused ? StateDrawable.State.FOCUSED : StateDrawable.State.DEFAULT);
        }
    }

    /**
     * Sets overlay that is drawing this view
     * @param o
     */
    public void setOverlay(Overlay o) {
        screen = o;
    }

    /**
     *
     * @return Overlay that is drawing the view
     */
    public Overlay getOverlay() {
        return screen;
    }

    /**
     * Forces the view to gain focus
     */
    public void focus() {
        getOverlay().focusItem(this);
    }

    /**
     * Associates a ViewHolder with the view
     * @param holder
     */
    public void setHolder(ViewHolder holder) {
        mHolder = holder;
        mHolder.setView(this);
    }

    /**
     *
     * @return Assocated with the view ViewHolder
     */
    public ViewHolder getHolder() {
        return mHolder;
    }

    /**
     * @return style of the element
     */
    public Style getStyle() {
        return style;
    }
}
