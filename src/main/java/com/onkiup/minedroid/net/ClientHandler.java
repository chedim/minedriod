package com.onkiup.minedroid.net;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by chedim on 5/18/15.
 */
public abstract class ClientHandler<REQ extends IMessage> implements IMessageHandler<REQ, IMessage> {
    @Override
    public IMessage onMessage(REQ req, MessageContext messageContext) {
        return this.handle(req, messageContext);
    }

    public abstract APacket handle(REQ req, MessageContext messageContext);
}
