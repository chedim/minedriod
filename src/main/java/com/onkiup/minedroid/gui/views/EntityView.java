package com.onkiup.minedroid.gui.views;

import com.onkiup.minedroid.Context;
import com.onkiup.minedroid.gui.GuiManager;
import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.primitives.Rect;
import com.onkiup.minedroid.gui.resources.Style;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

/**
 * Created by chedim on 8/2/15.
 */
public class EntityView extends ContentView {

    protected Entity mEntity;

    public EntityView(Context context) {
        super(context);
    }

    @Override
    public void drawContents(float partialTicks) {
        try
        {
            if (mEntity == null) return;
            GL11.glPushMatrix();
//            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableTexture2D();
//            GlStateManager.enableLighting();
//            GlStateManager.disableBlend();
            Point p = position.add(new Point(resolvedLayout.margin.left, resolvedLayout.margin.top));
            Rect padding = resolvedLayout.padding;
            Point size = resolvedLayout.getInnerSize();
            GL11.glTranslatef(p.x + (size.x / 2) + padding.left,
                    p.y + size.y,
                    size.y);

            GL11.glScalef(size.x / 2, size.y / 2, size.y / 2);

            GL11.glRotatef(180, 0, 0, 1);
            GL11.glRotatef(rotation - 90, 0, 1, 0);

            RenderManager manager = Minecraft.getMinecraft().getRenderManager();

            RenderHelper.disableStandardItemLighting();
            manager.renderEntitySimple(mEntity, partialTicks);
            RenderHelper.disableStandardItemLighting();
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(32826 /* GL_RESCALE_NORMAL_EXT */);
            GL11.glTranslatef(0F, 0F, 0.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(32826 /* GL_RESCALE_NORMAL_EXT */);
            int i1 = 240;
            int k1 = 240;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,
                    i1 / 1.0F, k1 / 1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(32826 /* GL_RESCALE_NORMAL_EXT */);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(2896 /* GL_LIGHTING */);
            GL11.glDisable(2929 /* GL_DEPTH_TEST */);
            GL11.glPopMatrix();

            if (animate && ++rotation >= 360) rotation = 0;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void clear() {

    }

    public void setEntity(Entity entity) {
        mEntity = entity;
    }

    public Entity getEntity() {
        return mEntity;
    }

    protected Integer rotation = 0;
    protected Boolean animate = false;

    @Override
    public String getThemeStyleName() {
        return "entity_view";
    }

    @Override
    public void inflate(XmlHelper node, Style theme) {
        super.inflate(node, theme);
        rotation = node.getIntegerAttr(GuiManager.NS, "rotation", style, 0);
        animate = node.getBoolAttr(GuiManager.NS, "animate", style, false);
    }



}
