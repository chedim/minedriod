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
 * Class that represents GUI Screen
 */
public abstract class Overlay extends GuiScreen implements Context {

    private static final Integer DBL_CLICK_TIMEOUT = getDblClickInterval();
    protected View contentView;
    protected RelativeLayout container = new RelativeLayout(this);
    protected State state = State.INITIALIZING;
    protected Drawable background;

    protected EntityPlayer player;
    protected World world;
    protected Point3D position;
    protected Class r;

    protected Overlay(Context context) {
        this.player = Minecraft.getMinecraft().thePlayer;
        this.world = Minecraft.getMinecraft().theWorld;
        r = context.R();
    }

    protected abstract ResourceLocation getContentLayout();
    protected abstract void fill(View content);

    protected Drawable getBackgroundDrawable() {
        return MineDroid.theme.getOverlayBackgroundDrawable().clone();
    }

    protected abstract void onStart();

    protected abstract void onStop();

    protected static final Point START_POINT = new Point(0, 0);

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        this.onStop();
        state = State.STOPPED;
    }

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
                contentView.setBackground(MineDroid.theme.getOverlayDrawable());
            }

            focusedItem = getNextFocusItem(0);

            state = State.CREATED;
        }

        System.out.println("New screen size: "+width+"x"+height);
    }

    public void focusItem(View view) {
        if (view.isFocusable()) focusedItem = view;
    }


    protected enum State {INITIALIZING, CREATED, STARTED, STOPPED}
    protected Timer clickWaiter;
    protected MouseEvent lastMouseDown;
    protected View focusedItem;

    protected boolean shiftPressed, ctrlPressed, altPressed, cmdPressed;

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

    public View getNextFocusItem(int offset) {
        int f = 0;
        List<View> focusables = null;
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

    @Override
    public void handleMouseInput() throws IOException {
        if (state != State.STARTED) return;
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

    protected class ClickWaiter extends TimerTask {
        protected MouseEvent up;

        public ClickWaiter(MouseEvent up) {
            this.up = up;
        }

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

    private static Integer getDblClickInterval() {
        Integer interval = (Integer) Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval");
        if (interval == null) interval = 200;
        return interval;
    }


    protected WindowKeyBinding getKeyBinding() {
        return null;
    }


    public void dismiss() {
        mc.thePlayer.closeScreen();
    }

    public boolean isCancelable() {
        return true;
    }

    protected View getContentView() {
        return container.inflateChild(MineDroid.getXmlHelper(this, getContentLayout()), MineDroid.theme);
    }

    @Override
    public Class R() {
        return r;
    }
}
