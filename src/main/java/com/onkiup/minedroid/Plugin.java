package com.onkiup.minedroid;

import com.onkiup.minedroid.gui.MineDroid;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by chedim on 5/22/15.
 */
@Mod(modid = Plugin.MODID, version = Plugin.VERSION)
public class Plugin {
    public static final String MODID = "minedroid";
    public static final String VERSION = "1.0";

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        try {
            Class.forName("net.minecraft.client.Minecraft");
        } catch (Exception e) {
            System.out.println("MineDroid is disabled due to dedicated server environment");
            return;
        }
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new MineDroid());
    }
}