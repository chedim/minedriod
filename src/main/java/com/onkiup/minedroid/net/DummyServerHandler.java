package com.onkiup.minedroid.net;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by chedim on 8/13/15.
 */
public class DummyServerHandler extends ServerHandler {
    @Override
    public APacket handle(IMessage message, EntityPlayer sender, MessageContext messageContext) {
        return null;
    }
}
