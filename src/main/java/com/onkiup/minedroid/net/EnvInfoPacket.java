package com.onkiup.minedroid.net;

import com.onkiup.minedroid.gui.resources.EnvParams;
import com.onkiup.minedroid.gui.resources.ResourceManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by chedim on 8/12/15.
 */
public class EnvInfoPacket extends APacket {
    public EnvParams env;

    public static class SHandler extends ServerHandler<EnvInfoPacket> {

        @Override
        public APacket handle(EnvInfoPacket packet, EntityPlayer sender, MessageContext context) {
            ResourceManager.setEnvParams(sender, packet.env);
            return null;
        }
    }

    public static class CHandler extends DummyClientHandler {};
}
