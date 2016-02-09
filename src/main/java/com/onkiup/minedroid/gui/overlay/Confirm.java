package com.onkiup.minedroid.gui.overlay;

import com.onkiup.minedroid.MineDroid;
import com.onkiup.minedroid.Context;
import com.onkiup.minedroid.R;
import com.onkiup.minedroid.gui.Notification;
import com.onkiup.minedroid.gui.NotificationManager;
import com.onkiup.minedroid.gui.events.KeyEvent;
import com.onkiup.minedroid.gui.views.*;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

/**
 * Created by chedim on 8/5/15.
 */
public class Confirm extends Notification {

    protected String message;
    protected Handler handler;

    protected View.OnKeyPress keyListener = new View.OnKeyPress() {
        @Override
        public void handle(KeyEvent event) {
            if (event.keyCode == Keyboard.KEY_Y) {
                if (handler != null) handler.onAnswer(true);
                dismiss();
                return;
            }
            if (event.keyCode == Keyboard.KEY_N) {
                if (handler != null) handler.onAnswer(false);
                dismiss();
                return;
            }
        }
    };

    protected Confirm(Context context, String text) {
        super(context);
        message = text;
    }

    protected Confirm(Context context, String text, Handler handler) {
        this(context, text);
        setHandler(handler);
    }

    @Override
    protected ResourceLocation getContentLayout() {
        return R.layout.confirm;
    }

    @Override
    protected void fill(View content) {
        content.on(keyListener);
        TextView messageView = (TextView) content.findViewById(R.id.message);
        messageView.setText(message);
    }

    @Override
    protected void onStart() {

    }

    @Override
    protected void onStop() {

    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public interface Handler {
        public void onAnswer(boolean answer);
    }

    public static Confirm show(String format, Object... params) {
        String message = String.format(format, params);
        Confirm confirm = new Confirm(MineDroid.getMDContext(), message);
        NotificationManager.open(confirm);
        return confirm;
    }

    @Override
    public int getTimeLeft() {
        return -1;
    }
}
