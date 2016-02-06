package com.onkiup.minedroid.gui.views;

import com.onkiup.minedroid.Context;
import com.onkiup.minedroid.EventBase;
import com.onkiup.minedroid.gui.GuiManager;
import com.onkiup.minedroid.gui.Overlay;
import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.drawables.*;
import com.onkiup.minedroid.gui.events.*;
import com.onkiup.minedroid.gui.primitives.Color;
import com.onkiup.minedroid.gui.primitives.GLColor;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.primitives.Rect;
import com.onkiup.minedroid.gui.resources.Style;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;

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
    protected Context context;

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

    /**
     * Draggable flag
     */
    protected Boolean isDraggable = false;

    /**
     * View inside of this View can be dragged
     */
    protected Integer draggableRegion = -1;

    protected int elevation = 0;
    protected final static GLColor shadowColor = new GLColor(0f, 0f, 0f, 0.75f);

    public View(Context context) {
        this.context = context;
    }

    /**
     * Draws this view
     */
    public void onDraw(float partialTicks) {
//        FMLLog.info("Drawing '%s' at %s", this, resolvedLayout.getOuterRect().move(position));
        resetBlending();
        if (background != null) {
            background.setSize(resolvedLayout.getSize());
            shadowColor.alpha = 0.6f;
            if (elevation != 0) background.drawShadow(position, shadowColor, elevation);
            background.draw(position);
        }

        if (debug) {
            Color debugColor = new Color(0x66ff00ff);
            DebugDrawable border = new DebugDrawable(debugColor);
            border.setSize(resolvedLayout.getSize());
            border.draw(position);

            // size text
            TrueTypeDrawable sizeText = new TrueTypeDrawable(resolvedLayout.getSize().toString(), 0xffff00ff);
            sizeText.setTextSize(9);

            sizeText.draw(position.add(new Point(2, 2)));
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

    public boolean isPositioned() {
        return position != null;
    }

    @Override
    public int contextId() {
        return context.contextId();
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
            if (width < 0) return margin.left + margin.right;
            return width + margin.left + margin.right;
        }

        /**
         * @return View height with margins
         */
        public int getOuterHeight() {
            if (height < 0) return margin.top + margin.bottom;
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
            if (width < 0) return 0;
            return width - padding.left - padding.right;
        }

        /**
         * @return View height without paddings and margins
         */
        public int getInnerHeight() {
            if (height < 0) return 0;
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

        public Rect getOuterRect() {
            return new Rect(new Point(0, 0), getOuterSize());
        }

        public boolean relatesParent() {
            return width == MATCH_PARENT || height == MATCH_PARENT;
        }

        public void setParentSize(Point point) {
            if (width == MATCH_PARENT) {
                setOuterWidth(point.x);
            }
            if (height == MATCH_PARENT) {
                setOuterHeight(point.y);
            }
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

    public interface OnDragStart extends Event<DragEvent> {

    }

    public interface OnDragEnd extends Event<DragEvent> {

    }

    public interface OnDrag extends Event<DragEvent> {

    }

    public interface OnDrop extends Event<DragEvent> {

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
        style = node.getStyleAttr(GuiManager.NS, "style", theme.getStyle(getThemeStyleName()));

        setBackground(node.getDrawableAttr(GuiManager.NS, "background", style, null));
        Point size = new Point(Layout.WRAP_CONTENT, Layout.WRAP_CONTENT);
        if (background != null) {
            size = background.getOriginalSize();
        }

        Layout layout = new Layout();

        size = node.getSize(GuiManager.NS, style, size);

        layoutWeight = node.getIntegerAttr(GuiManager.NS, "weight", style, 1);

        layout.margin = node.getRectAttr(GuiManager.NS, "margin", style, null);
        layout.padding = node.getRectAttr(GuiManager.NS, "padding", style, null);
        layout.width = size.x;
        layout.height = size.y;
        if (isDraggable = node.getBoolAttr(GuiManager.NS, "draggable", style, false)) {
            draggableRegion = node.getIdAttr(GuiManager.NS, "dragArea", style);
        }
        setLayout(layout);
        this.id = node.getIdAttr(GuiManager.NS, "id");
        setDebug(node.getBoolAttr(GuiManager.NS, "debug", style, false));
        this.elevation = node.getIntegerAttr(GuiManager.NS, "elevation", style, 0);
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

    /**
     * @return true if element can be dragged
     */
    public boolean isDraggable() {
        return isDraggable;
    }

    /**
     * @return Id of View in which element can be dragged
     */
    public Integer getDraggableRegion() {
        return draggableRegion;
    }

    /**
     * Sets id of View in which element can be dragged
     * @param id View id
     */
    public void setDraggableRegion(Integer id) {
        draggableRegion = id;
    }

    public Point getPosition() {
        return position.clone();
    }

    /**
     * @return parent from which this element had been detached
     */
    public ContentView detach() {
        ContentView parent = this.parent;
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeChild(this);
        } else {
            parent.clear();
        }
        this.parent = null;
        return parent;
    }

    /**
     * Looks for a parent with given id
     * @param id
     * @return View if found, otherwise null
     */
    public View findParent(int id) {
        View parent = getParent();
        if (parent == null) return null;
        if (parent.getId() == id) return parent;
        return parent.findParent(id);
    }

    public void setElevation(int elevation) {
        this.elevation = elevation;
    }

    public int getElevation() {
        return elevation;
    }

    public static void resetBlending() {
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
    }
}
