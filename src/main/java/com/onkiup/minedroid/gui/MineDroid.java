package com.onkiup.minedroid.gui;

import com.onkiup.minedroid.R;
import com.onkiup.minedroid.gui.drawables.BitmapDrawable;
import com.onkiup.minedroid.gui.drawables.Drawable;
import com.onkiup.minedroid.gui.drawables.NinePatchDrawable;
import com.onkiup.minedroid.gui.overlay.Alert;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.primitives.Point3D;
import com.onkiup.minedroid.gui.primitives.Rect;
import com.onkiup.minedroid.gui.resources.EnvParams;
import com.onkiup.minedroid.gui.resources.PluralLocalizer;
import com.onkiup.minedroid.gui.resources.Style;
import com.onkiup.minedroid.gui.views.View;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InvalidClassException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Main MineDroid class
 */
@SideOnly(Side.CLIENT)
public class MineDroid implements IGuiHandler, Context {
    /**
     * Current MineDroid theme
     */
    public static HashMap<Class, Style> themes = new HashMap<Class, Style>();

    /**
     * Minecraft client instance
     */
    protected static Minecraft mc = Minecraft.getMinecraft();

    /**
     * Amounts of generated classes ids
     */
    protected final static HashMap<Class, Integer> amounts = new HashMap<Class, Integer>();

    /**
     * Timer controller
     */
    protected static TickHandler tickHandler;

    /**
     * Default MineDroid XML NameSpace
     */
    public static final String NS = "http://onkiup.com/minecraft/xml";

    /**
     * Utilizing module Context
     */
    protected Class r;

    /**
     * Initialized MineDroid
     * @param r Mod R class
     */
    public MineDroid(Class r) {
        this.r = r;
    }

    /**
     * Returns Mod's context
     * @return Mod's context
     */
    public Context getContext() {
        return this;
    }

    /**
     * Current localizer for plurals
     */
    protected static PluralLocalizer pluralLocalizer;

    static {
        tickHandler = new TickHandler();

        FMLCommonHandler.instance().bus().register(tickHandler);
    }

    /**
     * generates ids for Overlays
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
     * @return Point
     */
    public static Point getWindowSize() {
        Minecraft mc = Minecraft.getMinecraft();
        return new Point(mc.displayWidth, mc.displayHeight);
    }

    /**
     * Returns Minecraft's window scaled size
     * @return ScaledResolution
     */
    public static ScaledResolution getScale() {
        Minecraft mc = Minecraft.getMinecraft();
        return new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
    }

    /**
     * Scales coordinate
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
    protected static Rect clip = new Rect(new Point(0, 0), getWindowSize());

    /**
     * Stack of GL_SCISSOR clip rects
     */
    protected static ArrayList<Rect> clips = new ArrayList<Rect>();

    /**
     * Adds GL_SCISSOR area
     * @param newClip GL_SCISSOR area
     */
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

    /**
     * Drops last added GL_SCISSOR rect
     */
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

    /**
     * Inflates layout from XML file
     * @param context Mod context
     * @param source XML file location
     * @return View
     */
    public static View inflateLayout(Context context, ResourceLocation source) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document dom = db.parse(Minecraft.getMinecraft().getResourceManager().getResource(source).getInputStream());
            return processNode(context, dom.getChildNodes().item(0), getTheme(context));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads XML file into XMLParser, wraps it into XmlHelper and returns it
     * @param context Mod context
     * @param source Xml location
     * @return XmlHelper
     */
    public static XmlHelper getXmlHelper(Context context, ResourceLocation source) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document dom = db.parse(Minecraft.getMinecraft().getResourceManager().getResource(source).getInputStream());
            return new XmlHelper(context, dom.getChildNodes().item(0));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Inflates view from a XML Node element
     * @param context Mod context
     * @param node node element
     * @param theme theme with which View should be inflated
     * @return View
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvalidClassException
     */
    public static View processNode(Context context, Node node, Style theme) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvalidClassException, NoSuchMethodException, InvocationTargetException {
        return processNode(new XmlHelper(context, node), theme);
    }

    /**
     * Inflates view from a XmlHelper element
     * @param node Item node
     * @param theme theme with which View should be inflated
     * @return Inflated view
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvalidClassException
     */
    public static View processNode(XmlHelper node, Style theme) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvalidClassException, NoSuchMethodException, InvocationTargetException {
        String name = node.getNode().getNodeName();
        if (!name.contains(".")) {
            name = "com.onkiup.minedroid.gui.views." + name;
        }

        Class viewClass = Class.forName(name);
        if (!View.class.isAssignableFrom(viewClass)) {
            throw new InvalidClassException("Class <" + name + "> is not a View.");
        }

        Constructor c = viewClass.getConstructor(Context.class);
        View view = (View) c.newInstance(node);
        view.inflate(node, theme);

        return view;
    }

    /**
     * Inflates drawable from XML Node
     * @param context Mod context
     * @param node Xml Node
     * @return Drawable
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvalidClassException
     */
    public static Drawable processNodeDrawable(Context context, Node node) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvalidClassException {
        return processNodeDrawable(new XmlHelper(context, node));
    }

    /**
     * Inflates Drawable from @XmlHelper
     * @param node wrapped XML Node
     * @return Drawable
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvalidClassException
     */
    public static Drawable processNodeDrawable(XmlHelper node) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvalidClassException {
        String name = node.getName();
        if (!name.contains(".")) {
            name = "com.onkiup.minedroid.gui.drawables." + name + "Drawable";
        }

        Class drawableClass = Class.forName(name);
        if (!Drawable.class.isAssignableFrom(drawableClass)) {
            throw new InvalidClassException("Class <" + name + "> is not a Drawable.");
        }

        Drawable drawable = (Drawable) drawableClass.newInstance();
        drawable.inflate(node, getTheme(node));

        return drawable;
    }

    /**
     * Inflates Drawable from @ResourceLocation
     * @param context Mod context
     * @param  rl Drawable context
     * @return Drawable
     */
    public static Drawable inflateDrawable(Context context, ResourceLocation rl) {
        String fname = rl.getResourcePath();
        if (fname.contains(".xml")) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);

            try {
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document dom = db.parse(Minecraft.getMinecraft().getResourceManager().getResource(rl).getInputStream());
                return processNodeDrawable(context, dom.getChildNodes().item(0));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (fname.contains("ninepatch")) {
            try {
                return new NinePatchDrawable(rl);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                return new BitmapDrawable(rl);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Registered Screens
     */
    protected static ArrayList<Class<? extends Overlay>> screens = new ArrayList<Class<? extends Overlay>>();

    /**
     * Returns Screen class id
     * @param c Screen class
     * @return
     */
    public static int getScreenId(Class<? extends Overlay> c) {
        int id = screens.indexOf(c);
        if (id == -1) {
            id = screens.size();
            screens.add(c);
        }
        return id;
    }

    /**
     * Return screen from id
     * @param id Screen id
     * @return Class
     */
    public static Class<? extends Overlay> getScreenClass(int id) {
        if (id < 0 || id > screens.size()) return null;
        return screens.get(id);
    }

    /**
     * Creates Overlay
     * @param context Mod context
     * @param id Overlay id
     * @return Overlay
     */
    public static Overlay getScreen(Context context, int id) {
        Class<? extends Overlay> c = getScreenClass(id);
        if (c != null) try {
            Constructor<? extends Overlay> constructor = c.getConstructor(Context.class);
            return constructor.newInstance(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Registers Screen in MineDroid
     * @param c Screen class
     */
    public static void addScreen(Class<? extends Overlay> c) {
        if (screens.contains(c)) return;
        screens.add(c);
    }

    /**
     * Returns current PluralLocalizer
     * @return PluralLocalizer
     */
    public static PluralLocalizer getPluralLocalizer() {
        return pluralLocalizer;
    }

    /**
     * Sets new PluralLocalizer
     * @param localizer Localizer for current locale
     */
    public static void setPluralLocalizer(PluralLocalizer localizer) {
        pluralLocalizer = localizer;
    }

    /**
     * @param ID Overlay id
     * @param player Player that opens Overlay
     * @param world Current world
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @return
     */
    @Override
    @Deprecated
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    /**
     * Creates GUI for Forge
     * @param ID Overlay id
     * @param player Player that opens Overlay
     * @param world Current world
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @return Registered for this id overlay
     */
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        Class<? extends Overlay> c = getScreenClass(ID);
        if (c != null) try {
            Constructor<? extends Overlay> constructor = c.getConstructor(EntityPlayer.class, World.class, Point3D.class);
            return constructor.newInstance(player, world, new Point3D(x, y, z));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Opens Overlay
     * @param overlay Overlay that should be open
     */
    public static void open(Overlay overlay) {
        Minecraft.getMinecraft().displayGuiScreen(overlay);
    }

    /**
     * Creates Overlay from class and opens it
     * @param overlayClass Overlay class that should be open
     */
    public void open(Class<? extends Overlay> overlayClass) {
        try {
            Constructor c = overlayClass.getConstructor(Context.class);
            Overlay overlay = (Overlay) c.newInstance(this);
            open(overlay);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Displays alert
     * @param context Mod context
     * @param s Alert message
     */
    public static void alert(Context context, String s) {
        open(new Alert(context, s));
    }

    /**
     * Displays alert
     * @param s Alert message
     */
    public void alert(String s) {
        open(new Alert(this, s));
    }

    /**
     * Returns environment qualifiers for resource managing
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
     * @param v Minecraft version
     * @return Integer|null
     */
    public static Integer getMCVersion(String v) {
        if (v == null) return null;
        String[] version = v.split("\\.");
        int result = 0;
        System.out.println("version[0] = "+version[0]);
        for (int i = 0; i < Math.min(version.length, 3); i++) {
            result += Integer.valueOf(version[i]) * Math.pow(10, 3 - i);
        }
        return result;
    }

    /**
     * Returns Mod R class
     * @return
     */
    @Override
    public Class R() {
        return r;
    }


    /**
     * Class for information about timer tasks
     */
    protected static class TaskInfo {
        DelayedTask task;
        int interval;
        int left;
        int repeatsLeft;

        public TaskInfo(DelayedTask task, int interval, int repeatsLeft) {
            this.task = task;
            this.interval = interval;
            this.left = interval;
            this.repeatsLeft = repeatsLeft;
        }
    }

    /**
     * World ticks handler that runs timer
     */
    protected static class TickHandler {
        private List<TaskInfo> tasks = new ArrayList<TaskInfo>();
        private List<TaskInfo> remove = new ArrayList<TaskInfo>();

        /**
         * Handles world tick
         * @param event Tick information
         */
        @SubscribeEvent
        public synchronized void onTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                tasks.removeAll(remove);
                remove.clear();

                for (TaskInfo task : tasks) {
                    if (--task.left == 0) {
                        task.task.execute();
                        if (task.repeatsLeft != 1) {
                            if (task.repeatsLeft > 0) {
                                task.repeatsLeft--;
                            }

                            task.left = task.interval;
                        } else {
                            remove.add(task);
                        }
                    }
                }
            }
        }

        /**
         * Adds a delayed task to tasks pool
         * @param info task info
         */
        public synchronized void add(TaskInfo info) {
            tasks.add(info);
        }

        /**
         * removes a delayed task from tasks pool
         * @param info task info
         */
        public synchronized void delete(TaskInfo info) {
            remove.add(info);
        }

        /**
         * deletes a delayed task from tasks pool
         * @param task delayed task
         */
        public synchronized void delete(DelayedTask task) {
            TaskInfo stop = null;
            for (TaskInfo info : tickHandler.tasks) {
                if (info.task == task) {
                    stop = info;
                    break;
                }
            }

            if (stop != null) {
                delete(stop);
            }
        }
    }

    /**
     * Schedules task for delayed one-time execution
     * @param time Delay interval in seconds
     * @param task Task to schedule
     * @return DelayedTask
     */
    public static DelayedTask delay(float time, DelayedTask task) {
        tickHandler.add(new TaskInfo(task, (int) (time * 20), 1));
        return task;
    }

    /**
     * Schedules task for unlimited repeated execution
     * @param time Task repeat interval
     * @param task Repeated task
     * @return DelayedTask
     */
    public static DelayedTask repeat(float time, DelayedTask task) {
        tickHandler.add(new TaskInfo(task, (int) (time * 20), 0));
        return task;
    }

    /**
     * Schedules task for limited times repeated execution
     * @param time Task repeat interval
     * @param times Amount of repeats
     * @param task Repeated task
     * @return DelayedTask
     */
    public static DelayedTask repeat(float time, int times, DelayedTask task) {
        tickHandler.add(new TaskInfo(task, (int) (time * 20), times));
        return task;
    }

    /**
     * Prevents future executions of delayed task and removes it
     * @param task Task to remove
     */
    public static void stop(DelayedTask task) {
       tickHandler.delete(task);
    }

    public void setTheme(Style theme) {
        themes.put(r, theme);
    }

    public static Style getTheme(Context context) {
        Style theme = themes.get(context.R());
        if (theme == null) theme = R.style.theme;
        return theme;
    }
}
