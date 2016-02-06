package com.onkiup.minedroid;

import com.onkiup.minedroid.exprop.ExPropClientSync;
import com.onkiup.minedroid.exprop.ExPropServerSync;
import com.onkiup.minedroid.net.ClientAlertPacket;
import com.onkiup.minedroid.net.EnvInfoPacket;
import com.onkiup.minedroid.net.ExPropDeltaPacket;
import com.onkiup.minedroid.net.ExPropDeltaRequestPacket;
import com.onkiup.minedroid.timer.Task;
import com.onkiup.minedroid.timer.TickHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * MineDroid Forge plugin
 */
@Mod(modid = MineDroid.MODID, version = MineDroid.VERSION)
public class MineDroid extends Modification {
    public static final String MODID = "minedroid";
    public static final String VERSION = "1.3.1";

    protected static MineDroid INSTANCE;


    public MineDroid() {
        super(R.class);
    }

    public void init() {
        INSTANCE = this;
    }

    @Override
    public void initNetwork() {
        super.initNetwork();
        registerPacket(EnvInfoPacket.class);
        registerPacket(ClientAlertPacket.class);
        registerPacket(ExPropDeltaPacket.class);
        registerPacket(ExPropDeltaRequestPacket.class);
    }

    public static Class getMineDroidR() {
        return R.class;
    }

    public static Context getMDContext() {
        return new ContextImpl(INSTANCE.contextId());
    }

    @Override
    public void initServer() {
        ExPropServerSync sync = new ExPropServerSync();
        tickHandler.repeat(1, sync);
    }

    @Override
    public void initClient() {
        tickHandler.repeat(30, new ExPropClientSync());
    }

    @Mod.EventHandler
    @SideOnly(Side.CLIENT)
    public void onClientInitializing(FMLInitializationEvent event) {
        isClient = true;
        Modification.initModules();
        Modification.initClients();
        clientThread = Thread.currentThread();
    }

    @Mod.EventHandler
    @SideOnly(Side.SERVER)
    public void onServerInitializing(FMLInitializationEvent event) {
        Modification.initModules();
    }

    @Mod.EventHandler
    public void onServerStart(FMLServerStartedEvent event) {
        serverThread = Thread.currentThread();
        isServer = true;
    }

    @Mod.EventHandler
    public void onServerStopped(FMLServerStoppedEvent event) {
        isServer = false;
    }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event) {
        Modification.stopServers();
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerAboutToStartEvent event) {
        initServers();
    }

    public static MineDroid getInstance() {
        return INSTANCE;
    }
}