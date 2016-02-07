package com.onkiup.minedroid.net;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by chedim on 8/13/15.
 */
public class DummyClientHandler extends ClientHandler {
    @Override
    public APacket handle(IMessage message, MessageContext messageContext) {
        return null;
    }
}
