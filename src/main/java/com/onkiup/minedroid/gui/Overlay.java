package com.onkiup.minedroid.gui;

import com.onkiup.minedroid.gui.drawables.Drawable;
import com.onkiup.minedroid.gui.events.KeyEvent;
import com.onkiup.minedroid.gui.events.MouseEvent;
import com.onkiup.minedroid.gui.events.WindowKeyBinding;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.primitives.Point3D;
import com.onkiup.minedroid.gui.primitives.Rect;
import com.onkiup.minedroid.gui.views.RelativeLayout;
import com.onkiup.minedroid.gui.views.ScrollView;
import com.onkiup.minedroid.gui.views.View;
import com.onkiup.minedroid.gui.views.ViewGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.Toolkit;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
     * Current mod R class
     */
    protected Class r;

    public Overlay(Context context) {
        this.player = Minecraft.getMinecraft().thePlayer;
        this.world = Minecraft.getMinecraft().theWorld;
        r = context.R();
    }

    /**
     * Should return Overlay content layout location
     * @return Overlay content layout location
     */
    protected abstract ResourceLocation getContentLayout();

    /**
     * Should fill content view with data
     * @param content content view
     */
    protected abstract void fill(View content);

    /**
     * Returns Overlay container background
     * @return Background drawable
     */
    protected Drawable getBackgroundDrawable() {
        return MineDroid.inflateDrawable(this, MineDroid.getTheme(this)
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
        this.onStop();
        state = State.STOPPED;
    }

    /**
     * Draws overlay
     * @param mouseX Current mouse x coordinate
     * @param mouseY Current mouse y coorfinate
     * @param partialTicks I have no idea what is it :)
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (state != State.STARTED) {
            onStart();
        }

        container.setPosition(START_POINT);
        container.resolveLayout(container.measure(null));
        container.onDraw();
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

            contentView = getContentView();
            container.addChild(contentView);

            fill(contentView);
            contentView.setOverlay(this);

            container.resolveLayout(container.measure(null));
            container.setBackground(background);

            if (contentView.getBackground() == null) {
                contentView.setBackground(
                        MineDroid.inflateDrawable(this,
                                MineDroid.getTheme(this).getStyle("overlay").getResource("content_background", null)));
            }

            focusedItem = getNextFocusItem(0);

            state = State.CREATED;
        }

        System.out.println("New screen size: "+width+"x"+height);
    }

    /**
     * Set focus on the view
     * @param view View to be focused
     */
    public void focusItem(View view) {
        if (view.isFocusable()) focusedItem = view;
    }

    /**
     * Represents Overlay states
     */
    protected enum State {INITIALIZING, CREATED, STARTED, STOPPED}

    /**
     * timer for double clicks
     */
    protected Timer clickWaiter;
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
     * @param typedChar Char than had been typed
     * @param keyCode Key code
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
     * @throws IOException
     */
    @Override
    public void handleMouseInput() throws IOException {
        if (state != State.STARTED || contentView == null) return;
        MouseEvent event = new MouseEvent();
        event.coords = new Point(0, 0);
        event.coords.x = Mouse.getEventX() * this.width / this.mc.displayWidth;
        event.coords.y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        event.diff = new Point(Mouse.getEventDX(), -Mouse.getEventDY());
        event.wheel = new Point(0, Mouse.getEventDWheel());
        event.alt = altPressed;
        event.shift = shiftPressed;
        event.control = ctrlPressed;

        if (Math.abs(event.wheel.y) > 120) event.wheel.y /= 10;
        event.button = Mouse.getEventButton();
        // checking if mouse matches contentView rectangle
        Rect rect = contentView.getRectangle();
        if (rect.contains(event.coords)) {
            event.target = contentView;
            if (event.button != -1) {
                if (Mouse.getEventButtonState()) {
                    // button state changed
                    event.type = View.OnMouseDown.class;
                } else {
                    event.type = View.OnMouseUp.class;
                }
            } else if (event.wheel.y != 0) {
                event.type = View.OnScroll.class;
            } else if (!rect.contains(event.coords.sub(event.diff))) {
                event.type = View.OnMouseIn.class;
            } else {
                event.type = View.OnMouseMove.class;
            }
        } else if (rect.contains(event.coords.sub(event.diff))) {
            event.type = View.OnMouseOut.class;
        } else {
            // this doesn't bother us
            return;
        }

        contentView.handleMouseEvent(event);

        if (event.type == View.OnMouseDown.class) {
            lastMouseDown = event;
        } else if (event.type == View.OnMouseUp.class && lastMouseDown != null && event.button == lastMouseDown.button) {
            if (clickWaiter != null) {
                clickWaiter.cancel();
                clickWaiter = null;
                MouseEvent dblClick = event.clone();
                dblClick.type = View.OnDblClick.class;
                dblClick.target = contentView;
                dblClick.source = contentView;
                contentView.handleMouseEvent(dblClick);
                System.out.println("DBLCLICK sended");
            } else {
                clickWaiter = new Timer();
                clickWaiter.schedule(new ClickWaiter(event), DBL_CLICK_TIMEOUT);
                System.out.println("Click scheduled");
            }
        }
    }

    /**
     * Waiter for double clicks
     */
    protected class ClickWaiter extends TimerTask {
        protected MouseEvent up;

        public ClickWaiter(MouseEvent up) {
            this.up = up;
        }

        /**
         * If this is called, there is no double click
         */
        @Override
        public void run() {
            MouseEvent click = up.clone();
            click.type = View.OnClick.class;
            click.source = contentView;
            contentView.handleMouseEvent(click);
            System.out.println("CLICK sended");
            clickWaiter = null;
        }
    }

    /**
     * Returns system defined double click interval
     * @return Double click interval
     */
    private static Integer getDblClickInterval() {
        Integer interval = (Integer) Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval");
        if (interval == null) interval = 200;
        return interval;
    }

    /**
     * Returns key binding that opens this window
     * @return Key binding
     */
    protected WindowKeyBinding getKeyBinding() {
        return null;
    }


    /**
     * Dismisses the overlay
     */
    public void dismiss() {
        mc.thePlayer.closeScreen();
    }

    /**
     * Returns true if this window can be closed with Escape button
     * @return cancalable flag
     */
    public boolean isCancelable() {
        return true;
    }

    /**
     * Returns inflated content view
     * @return Content view
     */
    protected View getContentView() {
        return container.inflateChild(MineDroid.getXmlHelper(this, getContentLayout()), MineDroid.getTheme(this));
    }

    /**
     * Returns current Mod R class
     * @return R class
     */
    @Override
    public Class R() {
        return r;
    }
}
