package com.onkiup.minedroid.gui;

import com.onkiup.minedroid.gui.overlay.Confirm;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.resources.ValueLink;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chedim on 8/12/15.
 */
@SideOnly(Side.CLIENT)
public class NotificationManager {

    protected NotificationManager() {
    }

    static {
        FMLCommonHandler.instance().bus().register(new EventHandler());
    }

    /**
     * Notifications list
     */
    private static List<Notification> notifications = new ArrayList<Notification>();

    public static void open(Notification notification) {
        synchronized (notifications) {
            notifications.add(notification);
        }
    }

    public static class EventHandler {

        protected EventHandler() {
        }

        @SubscribeEvent
        @SideOnly(Side.CLIENT)
        public void onEndTick(TickEvent.RenderTickEvent event) {
            List<Notification> remove = new ArrayList<Notification>();
            if (event.phase == TickEvent.Phase.START) {
                return;
            }

            Point next = new Point(0, 0);
            ScaledResolution resolution = GuiManager.getScale();
            synchronized (notifications) {
                int i = 0;
                for (Notification notification : notifications) {
                    i++;
                    notification.setWorldAndResolution(GuiManager.mc, resolution.getScaledWidth(), resolution.getScaledHeight());
                    int timeLeft = notification.getTimeLeft();
                    if (notification.getState() == Overlay.State.DISMISSED
                            || notification.getState() == Overlay.State.STOPPED) {
                        remove.add(notification);
                        notification.onGuiClosed();
                        FMLLog.info("%s -x-> void", notification);
                        continue;
                    } else if (timeLeft == 0) {
                        Point target = notification.getTargetPosition();
                        target.x = resolution.getScaledWidth();
                        notification.setTargetPosition(target);
                        FMLLog.info("%s -x-> %s", notification, target);
                    } else if (timeLeft != -1 && timeLeft < -20 * notification.getAnimationSpeed()) {
                        notification.dismiss();
                        FMLLog.info("%s ---> X", notification);
                        continue;
                    } else if (notification.getState() == Overlay.State.INITIALIZING) {
                        notification.initGui();
                        Point position = notification.getPosition();
                        position.y = next.y;
                        notification.setPosition(position);
                        position.x = resolution.getScaledWidth()
                                - notification.getContentView().getResolvedLayout().getOuterWidth();
                        notification.setTargetPosition(position);
                        FMLLog.info("%s -+-> %s (next.y = %d of %d/%d)", notification, position, next.y, i, notifications.size());
                    }
                    try {
                        notification.handleMouseInput();
                    } catch (Exception e) {}
                    notification.drawScreen(Mouse.getX(), Mouse.getY(), 0);
                    int top = notification.getPosition().y;
                    if (top == next.y) {
                        next.y = notification.getPosition().y + notification.getHeight();
                    }
                }

                notifications.removeAll(remove);
            }
        }

        @SubscribeEvent
        public void OnKeyEvent(InputEvent.KeyInputEvent event) {
            for (Notification notification : notifications) {
                if (notification.getState() == Overlay.State.STARTED) {
                    try {
                        notification.handleKeyboardInput();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    public static void handleMouseFromLayeredOverlay() {
        for (Notification notification : notifications) {
            if (notification.getState() == Overlay.State.STARTED) {
                try {
                    notification.handleMouseInput();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
