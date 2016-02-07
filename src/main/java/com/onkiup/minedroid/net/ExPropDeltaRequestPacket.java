package com.onkiup.minedroid.net;

import com.onkiup.minedroid.exprop.ExProps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.HashMap;

/**
 * Created by chedim on 8/13/15.
 */
public class ExPropDeltaRequestPacket extends APacket {
    public Integer entity;
    public Class<? extends ExProps> type;
    public HashMap<String, Integer> hash;

    public ExPropDeltaRequestPacket(ExProps props) {
        this.entity = props.getEntity().getEntityId();
        this.type = props.getClass();
        this.hash = props.getHashes();
    }

    public static class SHandler extends ServerHandler<ExPropDeltaRequestPacket> {

        @Override
        public APacket handle(ExPropDeltaRequestPacket packet, EntityPlayer sender, MessageContext messageContext) {
            FMLLog.info("Delta request from %s for %s of %d", sender, packet.type.getSimpleName(), packet.entity);
            Entity entity = MinecraftServer.getServer().getEntityWorld().getEntityByID(packet.entity);
            if (entity == null) return null;
            ExProps props = ExProps.getIfExists(entity, packet.type);
            if (props == null) return null;
            ExPropDeltaPacket response = new ExPropDeltaPacket(props, packet.hash);
            FMLLog.info("Delta response contains %d fields", response.delta.size());
            return response;
        }
    }
}
