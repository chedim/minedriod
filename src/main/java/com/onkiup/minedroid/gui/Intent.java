package com.onkiup.minedroid.gui;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by chedim on 5/18/15.
 */
public class Intent implements Packet {

    public HashMap<String, Serializable> args;
    public int screenId;

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        screenId = buf.readInt();

    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {

    }

    @Override
    public void processPacket(INetHandler handler) {

    }
}
