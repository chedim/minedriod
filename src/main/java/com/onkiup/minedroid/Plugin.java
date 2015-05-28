package com.onkiup.minedroid;

import com.onkiup.minedroid.gui.MineDroid;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

/**
 * MineDroid Forge plugin
 */
@Mod(modid = Plugin.MODID, version = Plugin.VERSION)
public class Plugin {
    public static final String MODID = "minedroid";
    public static final String VERSION = "1.0";

    /**
     * Initializes MineDroid
     * @param event Event object
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        try {
            Class.forName("net.minecraft.client.Minecraft");
        } catch (Exception e) {
            System.out.println("MineDroid is disabled due to dedicated server environment");
            return;
        }
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new MineDroid(R.class));
    }
}