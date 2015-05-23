package com.onkiup.minedroid.gui;

import com.onkiup.minedroid.gui.drawables.Drawable;
import com.onkiup.minedroid.gui.overlay.Alert;
import com.onkiup.minedroid.gui.primitives.Point;
import com.onkiup.minedroid.gui.primitives.Point3D;
import com.onkiup.minedroid.gui.primitives.Rect;
import com.onkiup.minedroid.gui.resources.EnvParams;
import com.onkiup.minedroid.gui.resources.PluralLocalizer;
import com.onkiup.minedroid.gui.themes.DefaultTheme;
import com.onkiup.minedroid.gui.themes.Theme;
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
import java.io.InvalidClassException;
import java.lang.reflect.Constructor;
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
    public static Theme theme;

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
    protected Class R;

    public MineDroid(Class r) {
        R = r;
    }

    public Context getContext() {
        return this;
    }

    /**
     * Current localizer for plurals
     */
    protected static PluralLocalizer pluralLocalizer;

    static {
        theme = new DefaultTheme();
        tickHandler = new TickHandler();

        FMLCommonHandler.instance().bus().register(tickHandler);
    }

    /**
     * generates ids for Overlays
     * @param clazz
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
     * @param x
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
    protected static ArrayList<Rect> clips = new ArrayList<Rect>();

    /**
     * Adds GL_SCISSOR area
     * @param newClip
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
     * @param source
     * @return View
     */
    public static View inflateLayout(Context context, ResourceLocation source) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document dom = db.parse(Minecraft.getMinecraft().getResourceManager().getResource(source).getInputStream());
            return processNode(context, dom.getChildNodes().item(0), theme);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads XML file into XMLParser, wraps it into XmlHelper and returns it
     * @param source
     * @return XmlHelper
     */
    public static XmlHelper getXmlHelper(Context context, ResourceLocation source) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

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
     * @param node node element
     * @param theme theme with which View should be inflated
     * @return View
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvalidClassException
     */
    public static View processNode(Context context, Node node, Theme theme) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvalidClassException {
        return processNode(new XmlHelper(context, node), theme);
    }

    /**
     * Inflates view from a XmlHelper element
     * @param node
     * @param theme theme with which View should be inflated
     * @return
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvalidClassException
     */
    public static View processNode(XmlHelper node, Theme theme) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvalidClassException {
        String name = node.getNode().getNodeName();
        if (!name.contains(".")) {
            name = "com.onkiup.minedroid.gui.views." + name;
        }

        Class viewClass = Class.forName(name);
        if (!View.class.isAssignableFrom(viewClass)) {
            throw new InvalidClassException("Class <" + name + "> is not a View.");
        }

        View view = (View) viewClass.newInstance();
        view.inflate(node, theme);

        return view;
    }

    /**
     * Inflates drawable from XML Node
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

        Class viewClass = Class.forName(name);
        if (!Drawable.class.isAssignableFrom(viewClass)) {
            throw new InvalidClassException("Class <" + name + "> is not a Drawable.");
        }

        Drawable drawable = (Drawable) viewClass.newInstance();
        drawable.inflate(node, theme);

        return drawable;
    }

    /**
     * Inflates Drawable from @ResourceLocation
     * @param  rl
     * @return Drawable
     */
    public static Drawable inflateDrawable(Context context, ResourceLocation rl) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document dom = db.parse(Minecraft.getMinecraft().getResourceManager().getResource(rl).getInputStream());
            return processNodeDrawable(context, dom.getChildNodes().item(0));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     */
    protected static ArrayList<Class<? extends Overlay>> screens = new ArrayList<Class<? extends Overlay>>();

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
     * @param id
     * @return Class
     */
    public static Class<? extends Overlay> getScreenClass(int id) {
        if (id < 0 || id > screens.size()) return null;
        return screens.get(id);
    }

    /**
     * Creates Overlay
     * @param id
     * @return Overlay
     */
    public static Overlay getScreen(int id) {
        Class<? extends Overlay> c = getScreenClass(id);
        if (c != null) try {
            return c.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Registers Screen in MineDroid
     * @param c
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
     * @param localizer
     */
    public static void setPluralLocalizer(PluralLocalizer localizer) {
        pluralLocalizer = localizer;
    }

    /**
     * @param ID
     * @param player
     * @param world
     * @param x
     * @param y
     * @param z
     * @return
     */
    @Override
    @Deprecated
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    /**
     * Creates GUI for Forge
     * @param ID
     * @param player
     * @param world
     * @param x
     * @param y
     * @param z
     * @return
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
     * @param overlay
     */
    public static void open(Overlay overlay) {
        Minecraft.getMinecraft().displayGuiScreen(overlay);
    }

    /**
     * Displays alert
     * @param s
     */
    public static void alert(Context context, String s) {
        open(new Alert(context, s));
    }

    /**
     * Displays alert
     * @param s
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
     * @param v
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

    @Override
    public Class R() {
        return R;
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
                            continue;
                        }
                    }
                }
            }
        }

        public synchronized void add(TaskInfo info) {
            tasks.add(info);
        }

        public synchronized void delete(TaskInfo info) {
            remove.add(info);
        }

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
}
