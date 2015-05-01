package com.onkiup.minecraft.gui;

import com.onkiup.minecraft.gui.drawables.Drawable;
import com.onkiup.minecraft.gui.primitives.Point;
import com.onkiup.minecraft.gui.primitives.Rect;
import com.onkiup.minecraft.gui.themes.DefaultTheme;
import com.onkiup.minecraft.gui.themes.Theme;
import com.onkiup.minecraft.gui.views.View;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by chedim on 4/25/15.
 */
public class OnkiupGuiManager {
    public static Theme theme;

    protected final static HashMap<Class, Integer> amounts = new HashMap<Class, Integer>();

    protected final static HashMap<String, Integer> ids = new HashMap<String, Integer>();
    protected static int idCount = 0;

    static {
        theme = new DefaultTheme();
    }

    public static int generateId(Class clazz) {
        if (!amounts.containsKey(clazz)) {
            amounts.put(clazz, 0);
        }

        int id = amounts.get(clazz);
        amounts.put(clazz, ++id);

        return id;
    }

    public static Point getWindowSize() {
        Minecraft mc = Minecraft.getMinecraft();
        return new Point(mc.displayWidth, mc.displayHeight);
    }

    public static ScaledResolution getScale() {
        Minecraft mc = Minecraft.getMinecraft();
        return new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
    }

    public static int scale(int x) {
        ScaledResolution r = getScale();
        return x * r.getScaleFactor();
    }

    protected static Rect clip = new Rect(new Point(0, 0), getWindowSize());
    protected static ArrayList<Rect> clips = new ArrayList<Rect>();

    public static void addClipRect(Rect newClip) {
        clips.add(clip);
        newClip = newClip.and(clip);
        if (newClip == null) {
            // The new clip hasn't common pixels with the parent clip.
            // disallow ANY modification
            newClip = new Rect(0, 0, 0, 0);
        }

        clip = newClip;
        Point size = clip.getSize();
//        BorderDrawable border = new BorderDrawable(new Color(0x6600ff00));
//        border.setSize(size);
//        border.draw(clip.coords());

        Point windowSize = getWindowSize();

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(scale(clip.left), windowSize.y - scale(clip.bottom), scale(size.x), scale(size.y));

//        ColorDrawable d = new ColorDrawable(0x66000000);
//        d.setSize(getWindowSize());
//        d.draw(new Point(0, 0));
//        System.out.println("+Clip: "+newClip+" ("+clips.size()+")");
//        GL11.glViewport(newClip.left, newClip.top, newClip.right, newClip.bottom);
    }

    public static void restoreClipRect() {
//        System.out.println("-Clip: "+clip+" ("+clips.size()+")");
        if (clips.size() > 0) clip = clips.remove(clips.size() - 1);
//        GL11.glViewport(clip.left, clip.top, clip.right, clip.bottom);
        Point size = clip.getSize();
        GL11.glScissor(scale(clip.left), getWindowSize().y - scale(clip.bottom), scale(size.x), scale(size.y));
        if (clips.size() == 0) {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
    }

    public static View inflateLayout(ResourceLocation source) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document dom = db.parse(Minecraft.getMinecraft().getResourceManager().getResource(source).getInputStream());
            return processNode(dom.getChildNodes().item(0), theme);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static View processNode(Node node, Theme theme) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvalidClassException {
        String name = node.getNodeName();
        if (!name.contains(".")) {
            name = "com.onkiup.minecraft.gui.views."+name;
        }

        Class viewClass = Class.forName(name);
        if (!View.class.isAssignableFrom(viewClass)) {
            throw new InvalidClassException("Class <" + name + "> is not a View.");
        }

        View view = (View) viewClass.newInstance();
        view.inflate(new XmlHelper(node), theme);

        return view;
    }

    public static Drawable processNodeDrawable(Node node) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvalidClassException {
        String name = node.getNodeName();
        if (!name.contains(".")) {
            name = "com.onkiup.minecraft.gui.drawables."+name+"Drawable";
        }

        Class viewClass = Class.forName(name);
        if (!Drawable.class.isAssignableFrom(viewClass)) {
            throw new InvalidClassException("Class <" + name + "> is not a Drawable.");
        }

        Drawable drawable = (Drawable) viewClass.newInstance();
        drawable.inflate(new XmlHelper(node), theme);

        return drawable;
    }

    public static Drawable inflateDrawable(ResourceLocation rl) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document dom = db.parse(Minecraft.getMinecraft().getResourceManager().getResource(rl).getInputStream());
            return processNodeDrawable(dom.getChildNodes().item(0));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static int getId(String id) {
        if (!ids.containsKey(id)) {
            ids.put(id, idCount++);
        }

        return ids.get(id);
    }

}
