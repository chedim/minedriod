package com.onkiup.minedroid.gui;

import com.onkiup.minedroid.Context;
import com.onkiup.minedroid.Modification;
import com.onkiup.minedroid.gui.drawables.Drawable;
import com.onkiup.minedroid.gui.events.DragEvent;
import com.onkiup.minedroid.gui.events.KeyEvent;
import com.onkiup.minedroid.gui.events.MouseEvent;
import com.onkiup.minedroid.gui.events.WindowKeyBinding;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.primitives.Point3D;
import com.onkiup.minedroid.gui.primitives.Rect;
import com.onkiup.minedroid.gui.views.*;
import com.onkiup.minedroid.timer.ClientTask;
import com.onkiup.minedroid.timer.Task;
import com.onkiup.minedroid.timer.TickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.Toolkit;
import java.io.IOException;
import java.util.List;

/**
 * Gui Screens parent class
 */
public abstract class Overlay extends GuiScreen implements Context {
    /**
     * Double clicks timeout
     */
    private static final Integer DBL_CLICK_TIMEOUT = getDblClickInterval();
    /**
     * Overlay content view
     */
    protected View contentView;
    /**
     * Overlay main container
     */
    protected RelativeLayout container = new RelativeLayout(this);
    /**
     * Overlay state
     */
    protected State state = State.INITIALIZING;
    /**
     * Overlay container background
     */
    protected Drawable background;
    /**
     * Current player
     */
    protected EntityPlayer player;
    /**
     * Current world
     */
    protected World world;
    /**
     * Window open position
     */
    protected Point3D position;

    /**
     * Current mod hash
     */
    protected int context;

    /**
     * Last drag event;
     */
    protected DragEvent lastDragEvent;

    /**
     * Parent of dragging element;
     */
    protected ViewGroup draggableParent;

    protected TickHandler timer;

    public Overlay(Context context) {
        this.player = Minecraft.getMinecraft().thePlayer;
        this.world = Minecraft.getMinecraft().theWorld;
        this.context = context.contextId();
        timer = Modification.getModule(this.context).getTickHandler();
    }

    /**
     * Should return Overlay content layout location
     *
     * @return Overlay content layout location
     */
    protected abstract ResourceLocation getContentLayout();

    /**
     * Should fill content view with data
     *
     * @param content content view
     */
    protected abstract void fill(View content);

    /**
     * Returns Overlay container background
     *
     * @return Background drawable
     */
    protected Drawable getBackgroundDrawable() {
        return GuiManager.inflateDrawable(this, GuiManager.getTheme(this)
                .getStyle("overlay").getResource("background", null));
    }

    /**
     * Called when Overlay is started
     */
    protected abstract void onStart();

    /**
     * Called when Overlay is dismissed
     */
    protected abstract void onStop();

    /**
     * Top left corner of the screen (for developer convinience :)
     */
    protected static final Point START_POINT = new Point(0, 0);

    /**
     * Handles Overlay closed event
     */
    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        onStop();
        state = State.STOPPED;
    }

    /**
     * Draws overlay
     *
     * @param mouseX       Current mouse x coordinate
     * @param mouseY       Current mouse y coorfinate
     * @param partialTicks I have no idea what is it :)
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (state == State.DISMISSED) {
            if (mc.currentScreen == this) {
                if (mc.thePlayer != null) {
                    mc.thePlayer.closeScreen();
                } else {
                    FMLLog.warning("No player :(");
                }
            }
            return;
        }
        if (state == State.INITIALIZING) {
            initGui();
            onStart();
        }

        View.Layout resolved = container.getLayout();
        if (resolved.shouldBeMeasured()) resolved = container.measure(new Point(width, height));
        container.resolveLayout(resolved);

        container.onDraw(partialTicks);

        if (lastDragEvent != null) {
            // drawing draggable element
            Point position = lastDragEvent.mouseEvent.coords.sub(lastDragEvent.viewMouseOffset);
            if (lastDragEvent.dragArea != null) {
                GuiManager.addClipRect(lastDragEvent.dragArea);
            }
            lastDragEvent.source.setPosition(position);
            lastDragEvent.source.onDraw(partialTicks);
            if (lastDragEvent.dragArea != null) {
                GuiManager.restoreClipRect();
            }
        }
        state = State.STARTED;
    }

    /**
     * Inits Overlay components
     */
    @Override
    public void initGui() {
        if (this.state == State.INITIALIZING) {
            background = getBackgroundDrawable();

            container.setPosition(START_POINT);
            container.setLayout(new View.Layout(width, height));
            container.clear();

            contentView = createContentView();
            container.addChild(contentView);
            contentView.setElevation(4);

            fill(contentView);
            contentView.setOverlay(this);

            container.resolveLayout(container.measure(null));
            if (isModal()) {
                container.setBackground(background);
            }

            if (contentView.getBackground() == null) {
                Drawable back = GuiManager.getTheme(this).getStyle("overlay").getDrawable("content_background", null);
                contentView.setBackground(back);
            }

            focusedItem = getNextFocusItem(0);

            state = State.CREATED;
        }

        System.out.println("New screen size: " + width + "x" + height);
    }

    /**
     * Set focus on the view
     *
     * @param view View to be focused
     */
    public void focusItem(View view) {
        if (view.isFocusable()) focusedItem = view;
    }

    public State getState() {
        return state;
    }

    public View getFocusedItem() {
        return focusedItem;
    }

    /**
     * Represents Overlay states
     */
    protected enum State {
        INITIALIZING, CREATED, STARTED, DISMISSED, STOPPED
    }

    /**
     * timer for double clicks
     */
    protected Task.Client clickWaiter;
    /**
     * Stores last MouseDown event for double clicks
     */
    protected MouseEvent lastMouseDown;
    /**
     * Currently focused view
     */
    protected View focusedItem;

    /**
     * Some keyboard flags
     */
    protected boolean shiftPressed, ctrlPressed, altPressed, cmdPressed;

    /**
     * Handles keyboard events
     *
     * @param typedChar Char than had been typed
     * @param keyCode   Key code
     * @throws IOException
     */
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (state != State.STARTED) return;
        // delivering key
        boolean isDown = Keyboard.isKeyDown(keyCode);

        switch (keyCode) {
            case Keyboard.KEY_LSHIFT:
            case Keyboard.KEY_RSHIFT:
                shiftPressed = isDown;
                break;
            case Keyboard.KEY_LCONTROL:
            case Keyboard.KEY_RCONTROL:
                ctrlPressed = isDown;
                break;
            case Keyboard.KEY_LMENU:
            case Keyboard.KEY_RMENU:
                altPressed = isDown;
                break;
            case Keyboard.KEY_TAB:
                focusedItem = shiftPressed ? getNextFocusItem(-1) : getNextFocusItem(1);
                break;
        }

        KeyEvent event = new KeyEvent();
        event.keyChar = typedChar;
        event.keyCode = keyCode;
        event.shift = shiftPressed;
        event.control = ctrlPressed;
        event.alt = altPressed;

        if (focusedItem == null) {
            focusedItem = contentView;
        }

        if (focusedItem != null) {
            event.target = focusedItem;
            event.source = focusedItem;

            if (isDown) {
                event.type = View.OnKeyDown.class;
            } else {
                KeyEvent up = event.clone();
                up.type = View.OnKeyUp.class;
                focusedItem.handleKeyboardEvent(up);
                event.type = View.OnKeyPress.class;
            }

//            FMLLog.info("vent: %s %s", event.type.getSimpleName(), typedChar);
            focusedItem.handleKeyboardEvent(event);
        }

        if (!event.cancel) {
            if (event.keyCode == Keyboard.KEY_ESCAPE && isCancelable()) {
                dismiss();
            }
        }
    }

    /**
     * Returns view to be focused after current
     *
     * @param offset How much views to skip
     * @return View that should be focused
     */
    public View getNextFocusItem(int offset) {
        int f = 0;
        List<View> focusables;
        if (contentView instanceof ViewGroup) {
            focusables = ((ViewGroup) contentView).getFocusables();
        } else if (contentView instanceof ScrollView) {
            focusables = ((ScrollView) contentView).getFocusables();
        } else {
            return null;
        }

        if (focusables.size() == 0) return null;
        if (focusedItem != null) {
            f = focusables.indexOf(focusedItem);
        }
        f += offset;

        if (f >= focusables.size()) f -= focusables.size();
        if (f < 0) f += focusables.size();

        return focusables.get(f);
    }

    /**
     * Handles mouse inputs
     *
     * @throws IOException
     */
    @Override
    public void handleMouseInput() throws IOException {
        if (state != State.STARTED || contentView == null || !contentView.isLayoutResolved()) return;
        MouseEvent event = new MouseEvent();
        event.coords = new Point(0, 0);
        event.coords.x = Mouse.getEventX() * this.width / this.mc.displayWidth;
        event.coords.y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        event.diff = new Point(Mouse.getEventDX(), -Mouse.getEventDY());
        event.wheel = new Point(0, Mouse.getEventDWheel());
        event.alt = altPressed;
        event.shift = shiftPressed;
        event.control = ctrlPressed;

        if (Math.abs(event.wheel.y) >= 120) event.wheel.y /= 10;
        event.button = Mouse.getEventButton();
        // checking if mouse matches contentView rectangle
        Rect rect = contentView.getRectangle();
        if (rect.contains(event.coords)) {
            event.target = contentView;
            detectMouseEventType(event);
        } else if (rect.contains(event.coords.sub(event.diff))) {
            event.type = View.OnMouseOut.class;
        } else if (lastDragEvent != null &&
                (lastDragEvent.dragArea == null || lastDragEvent.dragArea.contains(event.coords))) {
            // dragging outside of contentView
            detectMouseEventType(event);
            lastDragEvent.mouseEvent = event;
            if (event.type == View.OnMouseUp.class) {
                lastDragEvent.type = View.OnDragEnd.class;
                lastDragEvent.target = null;
                setLastDraggingEvent(lastDragEvent);
                return;
            } else if (event.type == View.OnMouseMove.class) {
                lastDragEvent.type = View.OnDrag.class;
                lastDragEvent.target = null;
                setLastDraggingEvent(lastDragEvent);
                return;
            }
        } else {
            // this doesn't bother us
            return;
        }

        contentView.handleMouseEvent(event);

        if (event.type == View.OnMouseDown.class) {
            lastMouseDown = event;
            if (lastDragEvent != null) {
                lastDragEvent.mouseEvent = event;
                lastDragEvent.type = View.OnDragEnd.class;
                lastDragEvent.target = event.source;
                setLastDraggingEvent(lastDragEvent);
            }
        } else if (event.type == View.OnMouseUp.class && lastDragEvent != null && event.button == lastMouseDown.button) {
            lastDragEvent.mouseEvent = event;
            lastDragEvent.type = View.OnDragEnd.class;
            lastDragEvent.target = event.source;
            setLastDraggingEvent(lastDragEvent);
        } else if (event.type == View.OnMouseUp.class && lastMouseDown != null && event.button == lastMouseDown.button) {
            if (clickWaiter != null) {
                timer.stop(clickWaiter);
                clickWaiter = null;
                MouseEvent dblClick = event.clone();
                dblClick.type = View.OnDblClick.class;
                dblClick.target = contentView;
                dblClick.source = contentView;
                contentView.handleMouseEvent(dblClick);
//                System.out.println("DBLCLICK sended");
                lastMouseDown = null;
            } else {
                clickWaiter = new ClickWaiter(event);
                timer.delay(DBL_CLICK_TIMEOUT, clickWaiter);
//                System.out.println("Click scheduled");
            }
        } else if (event.type == View.OnMouseMove.class
                && event.target.isDraggable()
                && lastMouseDown != null
                && lastDragEvent == null) {

            if (clickWaiter != null) {
                timer.stop(clickWaiter);
                clickWaiter = null;
            }
            DragEvent dragEvent = new DragEvent();
            dragEvent.type = View.OnDragStart.class;
            dragEvent.mouseEvent = event;
            dragEvent.source = event.source;
            dragEvent.viewMouseOffset = event.coords.sub(event.source.getPosition());
            Integer dragAreaId = event.source.getDraggableRegion();
            if (dragAreaId != null && dragAreaId > 0) {
                View area = findViewById(dragAreaId);
                if (area != null) {
                    dragEvent.dragArea = area.getRectangle();
                }
            }

            event.source.fireEvent(View.OnDragStart.class, dragEvent);
            if (!dragEvent.cancel) {
                setLastDraggingEvent(dragEvent);
            }
        } else if (event.type == View.OnMouseMove.class && lastDragEvent != null) {
            lastDragEvent.mouseEvent = event;
            lastDragEvent.type = View.OnDrag.class;
            lastDragEvent.target = event.source;
            setLastDraggingEvent(lastDragEvent);
        }
    }

    protected void detectMouseEventType(MouseEvent event) {
        if (event.button != -1) {
            if (Mouse.getEventButtonState()) {
                // button state changed
                event.type = View.OnMouseDown.class;
            } else {
                event.type = View.OnMouseUp.class;
            }
        } else if (event.wheel.y != 0) {
            event.type = View.OnScroll.class;
        } else if (!contentView.getRectangle().contains(event.coords.sub(event.diff))) {
            event.type = View.OnMouseIn.class;
        } else {
            event.type = View.OnMouseMove.class;
        }
    }

    /**
     * Waiter for double clicks
     */
    protected class ClickWaiter implements Task.Client {
        protected MouseEvent up;

        public ClickWaiter(MouseEvent up) {
            this.up = up;
        }

        /**
         * If this is called, there is no double click
         */
        @Override
        public void execute(Context ctx) {
            MouseEvent click = up.clone();
            click.type = View.OnClick.class;
            click.source = contentView;
            contentView.handleMouseEvent(click);
//            System.out.println("CLICK sended");
            clickWaiter = null;
            lastMouseDown = null;
        }
    }

    /**
     * Returns system defined double click interval
     *
     * @return Double click interval
     */
    private static Integer getDblClickInterval() {
        Integer interval = (Integer) Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval");
        if (interval == null) interval = 200;
        return interval / 1000;
    }

    /**
     * Returns key binding that opens this window
     *
     * @return Key binding
     */
    protected WindowKeyBinding getKeyBinding() {
        return null;
    }


    /**
     * Dismisses the overlay
     */
    public void dismiss() {
        this.state = State.DISMISSED;
    }

    /**
     * Returns true if this window can be closed with Escape button
     *
     * @return cancalable flag
     */
    public boolean isCancelable() {
        return true;
    }

    /**
     * Returns content view
     *
     * @return Content view
     */
    protected View getContentView() {
        return contentView;
    }

    /**
     * Creates content view
     *
     * @return inflated content view
     */
    protected View createContentView() {
        return container.inflateChild(GuiManager.getXmlHelper(this, getContentLayout()), GuiManager.getTheme(this));
    }

    @Override
    public int contextId() {
        return context;
    }

    protected void setLastDraggingEvent(DragEvent event) {
        lastDragEvent = event;
        if (event.type == View.OnDragStart.class) {
            event.parent = event.source.getParent();
            if (event.parent instanceof ViewGroup) {
                event.parentPosition = ((ViewGroup) event.parent).getChildPosition(event.source);
            } else {
                throw new RuntimeException("Cannot start drag operation: parent is not a ViewGroup");
            }
            event.source.detach();
            System.out.println("Drag started");
        } else if (event.type == View.OnDrag.class) {
            event.source.fireEvent(event.type, event);
            lastDragEvent = event;
            System.out.println("Drag moved");
        } else if (event.type == View.OnDragEnd.class) {
            if (event.target != null) {
                DragEvent dropEvent = event.clone();
                dropEvent.type = View.OnDrop.class;
                Boolean handled = dropEvent.target.fireEvent(dropEvent.type, dropEvent);
                if (handled || !dropEvent.cancel) {
                    event.source.fireEvent(event.type, event);
                } else event.cancel = true;
            } else {
                Boolean handled = event.source.fireEvent(event.type, event);
                event.cancel |= !handled;
            }

            if (event.cancel) {
                // cancelling drag operation
                if (event.parent instanceof ViewGroup) {
                    ((ViewGroup) event.parent).addChildAt(event.parentPosition, event.source);
                }
                System.out.println("Drag cancelled");
            }

            lastDragEvent = null;
            lastMouseDown = null;
            System.out.println("Drag finished");
        }
    }

    /**
     * Finds view on this Overlay by its id
     *
     * @param id
     * @return View if found, otherwise null
     */
    public View findViewById(int id) {
        return contentView.findViewById(id);
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        this.mc = mc;

        container.getLayout().width = width;
        container.getLayout().height = height;

        this.width = width;
        this.height = height;
    }

    public boolean isModal() {
        return false;
    }
}
