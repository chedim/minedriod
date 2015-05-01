package com.onkiup.minecraft.gui.views;

import com.onkiup.minecraft.gui.OnkiupGuiManager;
import com.onkiup.minecraft.gui.XmlHelper;
import com.onkiup.minecraft.gui.primitives.Point;
import com.onkiup.minecraft.gui.primitives.Rect;
import com.onkiup.minecraft.gui.themes.Theme;
import org.lwjgl.opengl.GL11;

/**
 * Created by chedim on 4/26/15.
 */
public abstract class ContentView extends View {
    protected HGravity hGravity = HGravity.LEFT;
    protected VGravity vGravity = VGravity.CENTER;

    public void setGravityHorizontal(HGravity gravity) {
        hGravity = gravity;
    }

    public void setGravityVertical(VGravity gravity) {
        vGravity = gravity;
    }

    public HGravity getGravityHorizontal() {
        return hGravity;
    }

    public VGravity hetGravityVertical() {
        return vGravity;
    }

    public Point getGravityOffset(Point size) {
        Point result = new Point(0, 0);
        Point me = resolvedLayout.getInnerSize();
        if (hGravity == HGravity.CENTER) result.x = me.x / 2 - size.x / 2;
        else if (hGravity == HGravity.RIGHT) result.x = me.x = size.x;

        if (vGravity == VGravity.CENTER) result.y = me.y / 2 - size.y / 2;
        else if (vGravity == VGravity.BOTTOM) result.y = me.y - size.y;

        return result;
    }

    @Override
    public void onDraw() {
        super.onDraw();

        setClipRect();
        drawContents();
        resetClipRect();
    }

    protected void setClipRect() {
        Rect inner = resolvedLayout.getInnerRect().move(position);
        OnkiupGuiManager.addClipRect(inner);
    }

    protected void resetClipRect() {
        OnkiupGuiManager.restoreClipRect();
    }

    public abstract void drawContents();

    public static enum HGravity {LEFT, CENTER, RIGHT};
    public static enum VGravity {TOP, CENTER, BOTTOM};

    @Override
    public void inflate(XmlHelper node, Theme theme) {
        super.inflate(node, theme);
        setGravityHorizontal((HGravity) node.getEnumAttr("mc", "horizontal-gravity", HGravity.CENTER));
        setGravityVertical((VGravity) node.getEnumAttr("mc", "vertical-gravity", VGravity.CENTER));
    }

}
