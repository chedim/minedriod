package com.onkiup.minedroid.gui.resources;

import com.onkiup.minedroid.Context;
import com.onkiup.minedroid.MineDroid;
import com.onkiup.minedroid.Modification;
import com.onkiup.minedroid.gui.GuiManager;
import com.onkiup.minedroid.gui.XmlHelper;
import com.onkiup.minedroid.gui.drawables.BitmapDrawable;
import com.onkiup.minedroid.gui.drawables.Drawable;
import com.onkiup.minedroid.gui.drawables.NinePatchDrawable;
import com.onkiup.minedroid.gui.views.View;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InvalidClassException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class ResourceManager {

    @SidedProxy(clientSide = "com.onkiup.minedroid.gui.resources.ClientEnvResolver", serverSide = "com.onkiup.minedroid.gui.resources.ServerEnvResolver")
    protected static EnvResolver envResolver;

    public static Object get(Context ctx, String link) {
        if (ctx == null) return null;
        if (link == null) return null;
        if (link.length() == 0) return "";
        if (!link.substring(0, 1).equals("@")) {
            if (link.length() < 2) return link;
            if (link.substring(0, 2).equals("\\@")) {
                link = link.substring(1);
            }
            return link;
        }

        Class R;

        link = link.substring(1);
        String[] parts = link.split("\\/");

        if (parts.length != 2) throw new RuntimeException("Invalid resource link: '@" + link + "'");
        if (parts[0].contains(":")) {
            String[] pack = parts[0].split(":");
            if (pack.length != 2) throw new RuntimeException("Invalid resource link: '@" + link + "'");
            R = Modification.R(pack[0]);
            parts[0] = pack[1];
        } else {
            R = Modification.R(ctx);
        }

        Object result = get(R, parts);

        if (result instanceof Exception) {
            if (!isMDR(R)) {
                result = get(MineDroid.getMineDroidR(), parts);
            }
        }

        if (result instanceof Exception) throw new RuntimeException("Resource '"+link+"' not found");
        return result;
    }

    private static Object get(Class R, String[] parts) {
        Object result = new Exception("Resource not found");
        Class type = getSubClass(R, parts[0]);
        if (type != null) {
            if (!parts[1].contains(".")) {
                result = getFieldValue(type, parts[1]);
            } else {
                String[] names = parts[1].split("\\.");
                for (int i = 0; i < names.length - 1; i++) {
                    type = getSubClass(type, names[i]);
                    if (type == null) return result;
                }
                result = getFieldValue(type, names[names.length - 1]);
            }
        }
        return result;
    }

    protected static boolean isMDR(Class R) {
        return R == MineDroid.getMineDroidR();
    }

    private static Class getSubClass(Class R, String name) {
        Class[] subs = R.getDeclaredClasses();
        for (Class sub : subs) {
            if (sub.getSimpleName().equals(name)) return sub;
        }

        return null;
    }

    private static Object getFieldValue(Class R, String name) {
        try {
            Object value = R.getField(name).get(null);
            return value;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Inflates Drawable from @ResourceLocation
     *
     * @param context Mod context
     * @param rl      Drawable context
     * @return Drawable
     */
    public static Drawable inflateDrawable(Context context, ResourceLocation rl, Style theme) {
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

    public static View inflateLayout(Context context, ResourceLocation source, Style theme) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document dom = db.parse(Minecraft.getMinecraft().getResourceManager().getResource(source).getInputStream());
            return processNode(context, dom.getChildNodes().item(0), theme);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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
     * Inflates view from a XmlHelper element
     *
     * @param node  Item node
     * @param theme theme with which View should be inflated
     * @return Inflated view
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvalidClassException
     */
    public static View processNode(XmlHelper node, Style theme) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvalidClassException, NoSuchMethodException, InvocationTargetException {
        String name = node.getNode().getNodeName();
        if (name.contains(":")) {
            name = name.substring(name.indexOf(":") + 1);
        }

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
     */
    public static View processNode(Context context, Node node, Style theme) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvalidClassException, NoSuchMethodException, InvocationTargetException {
        return processNode(new XmlHelper(context, node), theme);
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
     */
    public static Drawable processNodeDrawable(XmlHelper node) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvalidClassException {
        String name = node.getName();
        if (name.contains(":")) {
            name = name.substring(name.indexOf(":") + 1);
        }
        if (!name.contains(".")) {
            name = "com.onkiup.minedroid.gui.drawables." + name + "Drawable";
        }

        Class drawableClass = Class.forName(name);
        if (!Drawable.class.isAssignableFrom(drawableClass)) {
            throw new InvalidClassException("Class <" + name + "> is not a Drawable.");
        }

        Drawable drawable = (Drawable) drawableClass.newInstance();
        drawable.inflate(node, GuiManager.getTheme(node));

        return drawable;
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
     */
    public static Drawable processNodeDrawable(Context context, Node node) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvalidClassException {
        return processNodeDrawable(new XmlHelper(context, node));
    }

    /**
     * Current localizer for plurals
     */
    protected static PluralLocalizer pluralLocalizer;

    /**
     * Sets new PluralLocalizer
     *
     * @param localizer Localizer for current locale
     */
    public static void setPluralLocalizer(PluralLocalizer localizer) {
        pluralLocalizer = localizer;
    }


    /**
     * Returns current PluralLocalizer
     *
     * @return PluralLocalizer
     */
    public static PluralLocalizer getPluralLocalizer() {
        return pluralLocalizer;
    }

    /**
     * Returns environment qualifiers for resource managing
     *
     * @return EnvParams
     */
    public static EnvParams getEnvParams() {
        return envResolver.getEnvParams();
    }

    /**
     * Returns integer representation of Minecraft version
     *
     * @param v Minecraft version
     * @return Integer|null
     */
    public static Integer getMCVersion(String v) {
        if (v == null) return null;
        String[] version = v.split("\\.");
        int result = 0;
        for (int i = 0; i < Math.min(version.length, 3); i++) {
            result += Integer.valueOf(version[i]) * Math.pow(10, 3 - i);
        }
        return result;
    }


    protected static HashMap<Integer, EnvParams> playerEnvs;

    public static EnvParams getEnvParams(EntityPlayer player) {
        return playerEnvs.get(player.getEntityId());
    }

    public static void setEnvParams(EntityPlayer player, EnvParams params) {
        playerEnvs.put(player.getEntityId(), params);
    }
}
