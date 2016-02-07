package com.onkiup.minedroid.gui;

import com.onkiup.minedroid.gui.holders.EntityLabelHolder;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.views.View;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by chedim on 8/14/15.
 */
@SideOnly(Side.CLIENT)
public abstract class EntityLabelManager {
    static {
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    protected static HashMap<Class<? extends EntityLivingBase>, Class<? extends EntityLabelHolder>> holders
            = new HashMap<Class<? extends EntityLivingBase>, Class<? extends EntityLabelHolder>>();

    protected static HashMap<Class<? extends EntityLabelHolder>, List<View>> reusable
            = new HashMap<Class<? extends EntityLabelHolder>, List<View>>();

    protected static HashMap<Integer, EntityViewInformation> used = new HashMap<Integer, EntityViewInformation>();

    public static void  registerLabel(Class<? extends EntityLabelHolder> holder, Class<? extends EntityLivingBase>... forEntities) {
        for (Class<? extends EntityLivingBase> forEntity: forEntities) {
            holders.put(forEntity, holder);
        }
    }

    public static void unregisterLabel(Class<? extends EntityLivingBase> forEntities, Class<? extends EntityLabelHolder> holder) {
        holders.remove(forEntities);
    }

    public static class EventHandler {
        @SubscribeEvent
        public void onEntityRendering(RenderLivingEvent.Specials.Pre event) {
            EntityLivingBase entity = (EntityLiving) event.entity;
            if (!holders.containsKey(event.entity.getClass())) return;
            synchronized (used) {
                EntityViewInformation info = used.get(entity.getEntityId());
                if (info == null) {
                    info = new EntityViewInformation();
                    Class<? extends EntityLabelHolder> holderClass = holders.get(entity.getClass());
                    if (reusable.containsKey(holderClass) && reusable.get(holderClass).size() > 0) {
                        synchronized (reusable) {
                            info.view = reusable.get(holderClass).remove(0);
                        }
                    } else {
                        try {
                            EntityLabelHolder holder = holderClass.newInstance();
                            info.view = holder.getView();
                            info.view.setHolder(holder);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    info.view.getHolder().setObject(entity);
                    used.put(entity.getEntityId(), info);
                }
                info.used++;
                draw(entity, info.view);
            }
        }

        @SubscribeEvent
        public void onCLientTick(TickEvent.ClientTickEvent event) {
            synchronized (used) {
                ArrayList<Integer> unused = new ArrayList<Integer>();
                for (Integer key : used.keySet()) {
                    EntityViewInformation info = used.get(key);
                    if (--info.used < -100) {
                        unused.add(key);
                    }
                }

                for (Integer key : unused) {
                    EntityViewInformation info = used.remove(key);
                    Class<? extends EntityLabelHolder> holder = (Class<? extends EntityLabelHolder>) info.view.getHolder().getClass();
                    synchronized (reusable) {
                        if (!reusable.containsKey(holder)) {
                            reusable.put(holder, new ArrayList<View>());
                        }
                        List<View> holderReusables = reusable.get(holder);
                        if (holderReusables.size() < 10) {
                            holderReusables.add(info.view);
                        }
                    }
                }
            }
        }
    }


    protected static class EntityViewInformation {
        public View view;
        public int used = 0;
    }

    protected static void draw(EntityLivingBase entity, View v) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        double dX = (entity.prevPosX + (entity.posX - entity.prevPosX)) - (player.prevPosX + (player.posX - player.prevPosX));
        double dY = (entity.prevPosY + (entity.posY - entity.prevPosY)) - (player.prevPosY + (player.posY - player.prevPosY));
        double dZ = (entity.prevPosZ + (entity.posZ - entity.prevPosZ)) - (player.prevPosZ + (player.posZ - player.prevPosZ));

        GL11.glPushMatrix();

        GL11.glTranslatef((float) dX, (float) (dY + 0.1f), (float) dZ);
        GL11.glRotatef(90, 0, 1, 0);//noted 1
        GL11.glRotatef(-player.rotationYaw - 90, 0.0F, 1.0F, 0.0F);
        GL11.glScaled(0.01, 0.01, 0.01);

        // draw
        v.setElevation(0);
        View.Layout layout = v.getLayout().clone();
        if (layout.shouldBeMeasured()) {
            layout = v.measure(new Point(100, 100));
        }
        if (layout.relatesParent()) {
            layout.setParentSize(new Point(100, 100));
        }

        v.resolveLayout(layout);

        v.setPosition(new Point(layout.getOuterWidth() / -2, layout.getOuterHeight() * 2));
        v.onDraw(0);

        GL11.glPopMatrix();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableLighting();
    }
}
