package com.onkiup.minedroid.gui;

import java.awt.*;
import java.io.InvalidClassException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import com.onkiup.minedroid.Context;
import com.onkiup.minedroid.Modification;
import com.onkiup.minedroid.R;
import com.onkiup.minedroid.gui.betterfonts.FontRenderer;
import com.onkiup.minedroid.gui.betterfonts.StringCache;
import com.onkiup.minedroid.gui.drawables.Drawable;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.primitives.Rect;
import com.onkiup.minedroid.gui.resources.EnvParams;
import com.onkiup.minedroid.gui.resources.ResourceManager;
import com.onkiup.minedroid.gui.resources.Style;
import com.onkiup.minedroid.gui.views.View;
import com.onkiup.minedroid.timer.Task;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.w3c.dom.Node;

/**
 * Main MineDroid class
 */
@SideOnly(Side.CLIENT)
public class GuiManager {
    /**
     * Current MineDroid theme
     */
    public static HashMap<Integer, Style> themes = new HashMap<Integer, Style>();

    /**
     * Minecraft client instance
     */
    @SideOnly(Side.CLIENT)
    protected static Minecraft mc = Minecraft.getMinecraft();

    /**
     * Amounts of generated classes ids
     */
    protected final static HashMap<Class, Integer> amounts = new HashMap<Class, Integer>();

    protected static Overlay inGameGui;

    /**
     * Default MineDroid XML NameSpace
     */
    public static final String NS = "http://onkiup.com/minecraft/xml";

    /**
     * Utilizing module Context
     */
    protected Class r;

    /**
     * Main Minecraft thread
     */
    protected static Thread mainThread;

    /**
     * Initialized MineDroid
     *
     * @param r Mod R class
     */
    public GuiManager(Class r) {
        this.r = r;
        mainThread = Thread.currentThread();
    }

    protected static StringCache fontRenderer = new StringCache();

    /**
     * generates ids for Overlays
     *
     * @param clazz Class for which should id be generated
     * @return int
     */
    public static int generateId(Class clazz) {
        if (!amounts.containsKey(clazz)) {
            amounts.put(clazz, 0);
        }

        int id = amounts.get(clazz);
        amounts.put(clazz, ++id);

        return id;
    }

    /**
     * Returns Minecraft window size
     *
     * @return Point
     */
    public static Point getWindowSize() {
        Minecraft mc = Minecraft.getMinecraft();
        return new Point(mc.displayWidth, mc.displayHeight);
    }

    /**
     * Returns Minecraft's window scaled size
     *
     * @return ScaledResolution
     */
    public static ScaledResolution getScale() {
        Minecraft mc = Minecraft.getMinecraft();
        return new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
    }

    /**
     * Scales coordinate
     *
     * @param x Coordinate value
     * @return int
     */
    public static int scale(int x) {
        ScaledResolution r = getScale();
        return x * r.getScaleFactor();
    }

    /**
     * Current GL_SCISSOR clip rect
     */
    protected static Rect clip;

    /**
     * Stack of GL_SCISSOR clip rects
     */
    protected static ArrayList<Rect> clips = new ArrayList<Rect>();

    /**
     * Adds GL_SCISSOR area
     *
     * @param newClip GL_SCISSOR area
     */
    public static void addClipRect(Rect newClip) {
        if (clip == null) {
            clip = new Rect(new Point(0, 0), getWindowSize());
        } else {
            clips.add(clip);
        }
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

    /**
     * Drops last added GL_SCISSOR rect
     */
    public static void restoreClipRect() {
//        System.out.println("-Clip: "+clip+" ("+clips.size()+")");
        if (clips.size() > 0) {
            clip = clips.remove(clips.size() - 1);
            Point size = clip.getSize();
            GL11.glScissor(scale(clip.left), getWindowSize().y - scale(clip.bottom), scale(size.x), scale(size.y));
        } else {
            clip = null;
        }
//        GL11.glViewport(clip.left, clip.top, clip.right, clip.bottom);
        if (clip == null) {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
    }

    /**
     * Inflates layout from XML file
     *
     * @param context Mod context
     * @param source  XML file location
     * @return View
     * @see ResourceManager#inflateLayout(Context, ResourceLocation, Style)
     * @deprecated
     */
    public static View inflateLayout(Context context, ResourceLocation source) {
        return ResourceManager.inflateLayout(context, source, getTheme(context));
    }

    /**
     * Loads XML file into XMLParser, wraps it into XmlHelper and returns it
     *
     * @param context Mod context
     * @param source  Xml location
     * @return XmlHelper
     * @see ResourceManager#getXmlHelper(Context, ResourceLocation)
     * @deprecated
     */
    public static XmlHelper getXmlHelper(Context context, ResourceLocation source) {
        return ResourceManager.getXmlHelper(context, source);
    }

    /**
     * Inflates view from a XML Node element
     *
     * @param context Mod context
     * @param node    node element
     * @param theme   theme with which View should be inflated
     * @return View
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvalidClassException
     * @see ResourceManager#processNode(Context, Node, Style)
     * @deprecated
     */
    public static View processNode(Context context, Node node, Style theme) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvalidClassException, NoSuchMethodException, InvocationTargetException {
        return ResourceManager.processNode(new XmlHelper(context, node), theme);
    }

    /**
     * Inflates view from a XmlHelper element
     *
     * @param node  Item node
     * @param theme theme with which View should be inflated
     * @return Inflated view
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvalidClassException
     * @see ResourceManager#processNode(XmlHelper, Style)
     * @deprecated
     */
    public static View processNode(XmlHelper node, Style theme) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvalidClassException, NoSuchMethodException, InvocationTargetException {
        return ResourceManager.processNode(node, theme);
    }

    /**
     * Inflates drawable from XML Node
     *
     * @param context Mod context
     * @param node    Xml Node
     * @return Drawable
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvalidClassException
     * @see ResourceManager#processNodeDrawable(Context, Node)
     * @deprecated
     */
    public static Drawable processNodeDrawable(Context context, Node node) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvalidClassException {
        return ResourceManager.processNodeDrawable(new XmlHelper(context, node));
    }

    /**
     * Inflates Drawable from @XmlHelper
     *
     * @param node wrapped XML Node
     * @return Drawable
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvalidClassException
     * @see ResourceManager#processNodeDrawable(XmlHelper)
     * @deprecated
     */
    public static Drawable processNodeDrawable(XmlHelper node) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvalidClassException {
        return ResourceManager.processNodeDrawable(node);
    }

    /**
     * Inflates Drawable from @ResourceLocation
     *
     * @param context Mod context
     * @param rl      Drawable context
     * @return Drawable
     * @see ResourceManager#inflateDrawable(Context, ResourceLocation, Style)
     * @deprecated
     */
    public static Drawable inflateDrawable(Context context, ResourceLocation rl) {
        return ResourceManager.inflateDrawable(context, rl, getTheme(context));
    }

    /**
     * Opens Overlay
     *
     * @param overlay Overlay that should be open
     */
    public static void open(Context context, GuiScreen overlay) {
        if (overlay instanceof Notification) {
            NotificationManager.open((Notification) overlay);
        } else {
            OverlayRunnable run = new OverlayRunnable(overlay);
            Modification.getModule(context).getTickHandler().delay(0, run);
        }
    }

    public static void open(Overlay overlay) {
        open(overlay, overlay);
    }

    /**
     * Returns environment qualifiers for resource managing
     *
     * @return EnvParams
     */
    public static EnvParams getEnvParams() {
        EnvParams result = new EnvParams();
        result.lang = mc.gameSettings.language.substring(0, 2);
        result.graphics = mc.gameSettings.fancyGraphics ? EnvParams.Graphics.FANCY : EnvParams.Graphics.FAST;
        result.mode = MinecraftServer.getServer().isDedicatedServer() ? EnvParams.Mode.REMOTE : EnvParams.Mode.LOCAL;
        result.version = getMCVersion(mc.getVersion());
        return result;
    }

    /**
     * Returns integer representation of Minecraft version
     *
     * @param v Minecraft version
     * @return Integer|null
     * @see ResourceManager#getMCVersion(String)
     * @deprecated
     */
    public static Integer getMCVersion(String v) {
        return ResourceManager.getMCVersion(v);
    }

    public static void setTheme(Context ctx, Style theme) {
        themes.put(ctx.contextId(), theme);
    }

    public static Style getTheme(Context context) {
        Style theme = themes.get(context.contextId());
        if (theme == null) theme = R.style.theme;
        return theme;
    }

    public static int scaleFont(int fontSize) {
        return Math.round(scale(fontSize) * getDisplayScale());
    }

    private static class OverlayRunnable implements Task.Client {

        protected Overlay ovl;

        public OverlayRunnable(GuiScreen ovl) {
            this.ovl = (Overlay) ovl;
        }

        @Override
        public void execute(Context ctx) {
            LayeredOverlay layers = null;
            if (mc.currentScreen instanceof LayeredOverlay && ((LayeredOverlay) mc.currentScreen).state != Overlay.State.DISMISSED) {
                layers = (LayeredOverlay) mc.currentScreen;
            } else {
                layers = new LayeredOverlay();
                if (mc.currentScreen != null && mc.currentScreen instanceof Overlay) {
                    layers.addOverlay((Overlay) mc.currentScreen);
                }
                mc.displayGuiScreen(layers);
            }
            layers.addOverlay(ovl);
        }
    }

    public static FontRenderer getBetterFonts() {
        return new FontRenderer(fontRenderer);
    }

    public static float getDisplayScale() {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice device = env.getDefaultScreenDevice();

        try {
            Field field = device.getClass().getDeclaredField("scale");

            if (field != null) {
                field.setAccessible(true);
                Object scale = field.get(device);

                if (scale instanceof Integer) {
                    return ((Integer) scale) * 1f;
                }
            }
        } catch (Exception ignore) {
        }

        final Float scaleFactor = (Float) Toolkit.getDefaultToolkit().getDesktopProperty("apple.awt.contentScaleFactor");
        if (scaleFactor != null) {
            return scaleFactor;
        }

        return 1f;
    }
}
