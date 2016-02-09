package com.onkiup.minedroid.gui.overlay;

import com.onkiup.minedroid.MineDroid;
import com.onkiup.minedroid.Context;
import com.onkiup.minedroid.R;
import com.onkiup.minedroid.gui.Notification;
import com.onkiup.minedroid.gui.NotificationManager;
import com.onkiup.minedroid.gui.events.KeyEvent;
import com.onkiup.minedroid.gui.events.MouseEvent;
import com.onkiup.minedroid.gui.views.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

/**
 * Alert window
 */
@SideOnly(Side.CLIENT)
public class Alert extends Notification {
    protected TextView mMessage;
    protected Button mOkButton;
    protected RelativeLayout layout = new RelativeLayout(this);
    protected String text;
    protected View.OnKeyDown keyListener = new View.OnKeyDown() {
        @Override
        public void handle(KeyEvent event) {
            if (event.keyCode == Keyboard.KEY_Y) {
                dismiss();
            }
            FMLLog.info("Key pressed: %s", event.keyChar);
        }
    };

    protected View.OnClick clickListener = new View.OnClick() {
        @Override
        public void handle(MouseEvent event) {
            dismiss();
        }
    };

    protected Alert(Context context, String text) {
        super(context);
        this.text = text;
    }

    @Override
    protected ResourceLocation getContentLayout() {
        return R.layout.alert;
    }

    @Override
    public boolean isModal() {
        return true;
    }

    @Override
    protected void fill(View content) {
        mMessage = (TextView) findViewById(R.id.message);
        mMessage.setText(text);
        content.on(clickListener);
        content.on(keyListener);
    }

    @Override
    protected void onStart() {

    }

    @Override
    protected void onStop() {

    }

    public static Alert show(String format, Object... params) {
        String message = String.format(format, params);
        Alert alert = new Alert(MineDroid.getMDContext(), message);
        NotificationManager.open(alert);
        return alert;
    }

    @Override
    public int getTimeLeft() {
        return -1;
    }
}
