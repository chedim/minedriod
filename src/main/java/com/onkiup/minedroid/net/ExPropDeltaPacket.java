package com.onkiup.minedroid.net;

import com.onkiup.minedroid.exprop.ExProps;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.HashMap;

/**
 * Created by chedim on 8/13/15.
 */
public class ExPropDeltaPacket extends APacket {
    public HashMap<String, Object> delta;
    public Class<? extends ExProps> type;
    public Integer entity;

    public ExPropDeltaPacket() {
    }

    public ExPropDeltaPacket(ExProps props, boolean clear) {
        this.delta = props.getDelta(clear);
        this.type = props.getClass();
        this.entity = props.getEntity().getEntityId();
    }

    public ExPropDeltaPacket(ExProps props, HashMap<String, Integer> hash) {
        this.delta = props.getDelta(hash);
        this.type = props.getClass();
        this.entity = props.getEntity().getEntityId();
    }

    public static class SHandler extends ServerHandler<ExPropDeltaPacket> {

        @Override
        public APacket handle(ExPropDeltaPacket packet, EntityPlayer sender, MessageContext messageContext) {
            FMLLog.info("Server got delta from %s for %s of %d", sender, packet.type.getSimpleName(), packet.entity);
            Entity entity = MinecraftServer.getServer().getEntityWorld().getEntityByID(packet.entity);
            if (entity == null) return null;
            ExProps props = (ExProps) entity.getExtendedProperties(packet.type.getName());
            if (props == null) {
                props = ExProps.create(entity, packet.type);
                if (props == null) return null;
                if (!props.allowClientCreation()) {
                    throw new RuntimeException("ExProps of type '"+packet.type.getSimpleName()+"' cannot be created by client");
                }
                props.register(entity);
            }
            props.applyDelta(packet.delta);
            return null;
        }
    }

    public static class CHandler extends ClientHandler<ExPropDeltaPacket> {

        @Override
        public APacket handle(ExPropDeltaPacket packet, MessageContext messageContext) {
            FMLLog.info("Client got delta for %s of %d", packet.type.getSimpleName(), packet.entity);
            Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(packet.entity);
            if (entity == null) return null;
            ExProps props = ExProps.get(entity, packet.type);
            if (props.applyDelta(packet.delta)) {
                props.fireEvent(ExProps.Changed.class, props);
            }
            return null;
        }
    }
}
