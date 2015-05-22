package com.onkiup.minedroid.gui;

import com.onkiup.minedroid.gui.drawables.Drawable;
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
 * Created by chedim on 4/25/15.
 */
@SideOnly(Side.CLIENT)
public class MineDroid implements IGuiHandler {
    public static Theme theme;

    protected static Minecraft mc = Minecraft.getMinecraft();

    protected final static HashMap<Class, Integer> amounts = new HashMap<Class, Integer>();

    protected final static HashMap<String, Integer> ids = new HashMap<String, Integer>();
    protected static int idCount = 0;
    protected static TickHandler tickHandler;
    private static PluralLocalizer pluralLocalizer;

    static {
        theme = new DefaultTheme();
        tickHandler = new TickHandler();

        FMLCommonHandler.instance().bus().register(tickHandler);
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

    public static XmlHelper getXmlHelper(ResourceLocation source) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document dom = db.parse(Minecraft.getMinecraft().getResourceManager().getResource(source).getInputStream());
            return new XmlHelper(dom.getChildNodes().item(0));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static View processNode(Node node, Theme theme) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvalidClassException {
        return processNode(new XmlHelper(node), theme);
    }

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

    public static Drawable processNodeDrawable(Node node) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvalidClassException {
        return processNodeDrawable(new XmlHelper(node));
    }

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
            ids.put(id, ++idCount);
        }

        return ids.get(id);
    }

    protected static ArrayList<Class<? extends Overlay>> screens = new ArrayList<Class<? extends Overlay>>();

    public static int getScreenId(Class<? extends Overlay> c) {
        int id = screens.indexOf(c);
        if (id == -1) {
            id = screens.size();
            screens.add(c);
        }
        return id;
    }

    public static Class<? extends Overlay> getScreenClass(int id) {
        if (id < 0 || id > screens.size()) return null;
        return screens.get(id);
    }

    public static Overlay getScreen(int id) {
        Class<? extends Overlay> c = getScreenClass(id);
        if (c != null) try {
            return c.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addScreen(Class<? extends Overlay> c) {
        if (screens.contains(c)) return;
        screens.add(c);
    }

    public static PluralLocalizer getPluralLocalizer() {
        return pluralLocalizer;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

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

    public static void open(Overlay overlay) {
        Minecraft.getMinecraft().displayGuiScreen(overlay);
    }

    public static void alert(String s) {

    }

    public static EnvParams getEnvParams() {
        EnvParams result = new EnvParams();
        result.lang = mc.gameSettings.language.substring(0, 2);
        result.graphics = mc.gameSettings.fancyGraphics ? EnvParams.Graphics.FANCY : EnvParams.Graphics.FAST;
        result.mode = MinecraftServer.getServer().isDedicatedServer() ? EnvParams.Mode.REMOTE : EnvParams.Mode.LOCAL;
        result.version = getMCVersion(mc.getVersion());
        return result;
    }

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


    public static DelayedTask delay(float time, DelayedTask task) {
        tickHandler.add(new TaskInfo(task, (int) (time * 20), 1));
        return task;
    }

    public static DelayedTask repeat(float time, DelayedTask task) {
        tickHandler.add(new TaskInfo(task, (int) (time * 20), 0));
        return task;
    }

    public static DelayedTask repeat(float time, int times, DelayedTask task) {
        tickHandler.add(new TaskInfo(task, (int) (time * 20), times));
        return task;
    }

    public static void stop(DelayedTask task) {
       tickHandler.delete(task);
    }
}
