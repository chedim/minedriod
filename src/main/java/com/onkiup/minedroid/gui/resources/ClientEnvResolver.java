package com.onkiup.minedroid.gui.resources;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;

/**
 * Created by chedim on 8/12/15.
 */
public class ClientEnvResolver extends EnvResolver {

    @Override
    public EnvParams getEnvParams() {
        EnvParams result = new EnvParams();
        Minecraft mc = Minecraft.getMinecraft();
        result.lang = mc.gameSettings.language.substring(0, 2);
        result.graphics = mc.gameSettings.fancyGraphics ? EnvParams.Graphics.FANCY : EnvParams.Graphics.FAST;
        result.mode = MinecraftServer.getServer().isDedicatedServer() ? EnvParams.Mode.REMOTE : EnvParams.Mode.LOCAL;
        result.version = ResourceManager.getMCVersion(mc.getVersion());
        return result;
    }
}
