package com.onkiup.minedroid;

import com.onkiup.minedroid.gui.GuiManager;
import com.onkiup.minedroid.net.*;
import com.onkiup.minedroid.timer.Task;
import com.onkiup.minedroid.timer.TickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import oracle.jrockit.jfr.Recording;
import sun.reflect.Reflection;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Created by chedim on 8/12/15.
 */
public abstract class Modification implements Context {

    protected TickHandler tickHandler;
    protected static HashMap<Integer, Modification> mods = new HashMap<Integer, Modification>();
    protected static HashMap<String, Integer> modIds = new HashMap<String, Integer>();
    protected static Thread clientThread, serverThread;

    protected Class r;
    protected static boolean isClient = false;
    protected static boolean isServer = false;

    protected SimpleNetworkWrapper NETWORK;
    protected static Modification INSTANCE;

    protected static boolean modulesInited, serversInited, clientsInited;

    @SideOnly(Side.CLIENT)
    protected GuiManager md;

    @SideOnly(Side.CLIENT)
    protected Minecraft client;

    @SideOnly(Side.SERVER)
    protected MinecraftServer server;

    public Modification(Class r) {
        INSTANCE = this;
        this.r = r;
        int hash = getClass().hashCode();
        String id = getModId();
        mods.put(hash, this);
        modIds.put(id, hash);
        tickHandler = new TickHandler(this);
    }

    /**
     * Used to initialize both server and client sides
     */
    public void init() {

    }

    public void initClient() {
        md = new GuiManager(r);
    }

    public void initServer() {
    }

    public void initNetwork() {
    }

    protected void initNetworkChannel() {
        String name = getClass().getName();
        if (name.length() > 20)  name = name.substring(name.length() - 20);
        NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(name);
    }

    protected void registerPacket(Class<? extends APacket> message) {
        APacket.HandlersInfo handlers = APacket.getHandlers(message);

        int hash = message.getName().hashCode();

        if (handlers.client != null) NETWORK.registerMessage(handlers.client, message, hash, Side.CLIENT);
        if (handlers.server != null) NETWORK.registerMessage(handlers.server, message, hash, Side.SERVER);
    }

    @Override
    public int contextId() {
        return getClass().hashCode();
    }

    public Class R() {
        return r;
    }

    public static Class R(Context context) {
        return mods.get(context.contextId()).R();
    }

    public static Class R(int hash) {
        return getModule(hash).R();
    }

    public static Class R(String id) {
        return getModule(id).R();
    }

    public static Modification getModule(Context context) {
        return mods.get(context.contextId());
    }

    public static Modification getModule(int hash) {
        return mods.get(hash);
    }

    public static Modification getModuleByR(Class r) {
        for (Modification mod : mods.values()) {
            if (mod.R() == r) return mod;
        }
        return null;
    }

    public static Modification getModule(String id) {
        Integer hash = modIds.get(id);
        if (hash == null) return null;
        return getModule(hash);
    }

    private String getModId() {
        try {
            Field f = getClass().getDeclaredField("MODID");
            return (String) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Module '" + this + "' didn't provide 'public final static String MODID' value;");
        }
    }

    public TickHandler getTickHandler() {
        return tickHandler;
    }

    public SimpleNetworkWrapper getNet() {
        return NETWORK;
    }

    public static TickHandler getTickHandler(Context ctx) {
        return getModule(ctx).getTickHandler();
    }

    public static TickHandler getTickHandler(int ctx) {
        return getModule(ctx).getTickHandler();
    }

    public static void send(Context ctx, APacket message) {
        send(ctx.contextId(), message);
    }

    public static void send(int ctx, final APacket message) {
        if (!isServer) {
            getModule(ctx).getNet().sendToServer(message);
        } else {
            Task.Server task = new Task.Server() {
                @Override
                public void execute(Context ctx) {
                    try {
                        final APacket.HandlersInfo handlers = APacket.getHandlers(message.getClass());
                        ServerHandler handler = (ServerHandler) handlers.server.newInstance();
                        APacket response = handler.handle(message, Minecraft.getMinecraft().thePlayer, null);
                        if (response != null) {
                            sendTo(ctx, Minecraft.getMinecraft().thePlayer, response);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            getModule(ctx).getTickHandler().delay(0, task);
        }
    }

    protected static void sendTo(int ctx, EntityPlayerMP player, APacket message) {
        getModule(ctx).getNet().sendTo(message, player);
    }

    public boolean isClient() {
        return isClient;
    }

    public static void sendTo(Context context, EntityPlayer player, APacket message) {
        sendTo(context.contextId(), player, message);
    }

    public static void sendTo(final int context, EntityPlayer player, final APacket message) {
        if (!isClient) {
            sendTo(context, (EntityPlayerMP) player, message);
        } else {
            Task.Client task = new Task.Client() {
                @Override
                public void execute(Context ctx) {
                    APacket.HandlersInfo handlers = APacket.getHandlers(message.getClass());
                    try {
                        ClientHandler ch = (ClientHandler) handlers.client.newInstance();
                        APacket response = ch.handle(message, null);
                        if (response != null) send(context, response);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            getModule(context).getTickHandler().delay(0, task);
        }
    }

    public static void send(Context ctx, final APacket packet, NetworkRegistry.TargetPoint point) {
        if (!isClient) {
            getModule(ctx).getNet().sendToAllAround(packet, point);
        } else {
                Task.Client task = new Task.Client() {
                    @Override
                    public void execute(Context ctx) {
                        try {
                            APacket.HandlersInfo handlers = APacket.getHandlers(packet.getClass());
                            final ClientHandler handler = (ClientHandler) handlers.client.newInstance();
                            APacket response = handler.handle(packet, null);
                            if (response != null) {
                                send(ctx, response);
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                };
                getModule(ctx).getTickHandler().delay(0, task);
        }
    }

    public boolean isServer() {
        return isServer;
    }

    protected static void initServers() {
        if (serversInited) return;
        for (Modification mod: mods.values()) mod.initServer();
        serversInited = true;
        FMLLog.info("Inited %d servers", mods.size());
    }

    protected static void initClients() {
        if (clientsInited) return;
        for (Modification mod: mods.values()) mod.initClient();
        clientsInited = true;
        FMLLog.info("Inited %d clients", mods.size());
    }

    protected static void initModules() {
        if (modulesInited) return;
        for (Modification mod: mods.values()) {
            mod.init();
            mod.initNetworkChannel();
            mod.initNetwork();
        }
        FMLLog.info("Inited %d modules", mods.size());
    }

    public static boolean isClientThread() {
        return Thread.currentThread() == clientThread;
    }

    public static boolean isServerThread() {
        return Thread.currentThread() == serverThread;
    }

    public static void stopServers() {
        for (Modification mod: mods.values()) mod.stopServer();
    }

    private void stopServer() {

    }

    private void nothing() {

    }
}
