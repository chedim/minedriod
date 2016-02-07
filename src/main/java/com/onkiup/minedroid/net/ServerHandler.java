package com.onkiup.minedroid.net;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by chedim on 5/18/15.
 */
public abstract class ServerHandler<REQ extends IMessage> implements IMessageHandler<REQ, IMessage> {
    @Override
    public IMessage onMessage(REQ req, MessageContext messageContext) {
        return handle(req, messageContext.getServerHandler().playerEntity, messageContext);
    }

    public abstract APacket handle(REQ req, EntityPlayer sender, MessageContext messageContext);
}
