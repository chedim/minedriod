package com.onkiup.minedroid.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

/**
 * Created by chedim on 8/4/15.
 */
public class APacket implements IMessage {

    public APacket() {
    }

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        try {
            Class s = getClass();
            Integer size = byteBuf.readInt();
            byte[] data = new byte[size];
            byteBuf.readBytes(data);
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream reader = new ObjectInputStream(in);
            HashMap<String, Serializable> values = (HashMap<String, Serializable>) reader.readObject();
            FMLLog.info("Packet '%s' values size: %d", s.getSimpleName(), values.size());

            Field[] fields = getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (Modifier.isPrivate(field.getModifiers())) continue;
                Serializable value = values.get(field.getName());
                FMLLog.info("APacket: get %s = '%s'", field.getName(), String.valueOf(value));
                field.setAccessible(true);
                field.set(this, value);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        Class s = getClass();
        HashMap<String, Serializable> values = new HashMap<String, Serializable>();
        FMLLog.info("Sending packet '%s'", s.getSimpleName());
        for (Field field : s.getFields()) {
            try {
                field.setAccessible(true);
                if (Modifier.isPrivate(field.getModifiers())) continue;
                Object value = field.get(this);
                values.put(field.getName(), (Serializable) value);
                FMLLog.info("APacket: Put %s = %s", field.getName(), String.valueOf(value));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream writer = new ObjectOutputStream(out);
            writer.writeObject(values);
            byteBuf.writeInt(out.size());
            byteBuf.writeBytes(out.toByteArray());
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static HandlersInfo getHandlers(Class message) {
        Class[] handlerCandidates = message.getDeclaredClasses();
        Class clientHandler = null, serverHandler = null;

        for (int x = 0; x < handlerCandidates.length; x++) {
            if (IMessageHandler.class.isAssignableFrom(handlerCandidates[x])) {
                if (ClientHandler.class.isAssignableFrom(handlerCandidates[x])) {
                    clientHandler = handlerCandidates[x];
                }

                if (ServerHandler.class.isAssignableFrom(handlerCandidates[x])) {
                    serverHandler = handlerCandidates[x];
                }

                if (serverHandler != null && clientHandler != null) break;
            }
        }

        HandlersInfo info = new HandlersInfo();
        info.client = clientHandler;
        info.server = serverHandler;

        return info;
    }

    public static class HandlersInfo {
        public Class client;
        public Class server;
    }
}
