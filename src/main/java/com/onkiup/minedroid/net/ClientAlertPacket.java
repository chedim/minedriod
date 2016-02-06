package com.onkiup.minedroid.net;

import com.onkiup.minedroid.gui.overlay.Alert;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by chedim on 8/11/15.
 */
public class ClientAlertPacket extends APacket {
    public ClientAlertPacket() {
    }

    public String message;

    public ClientAlertPacket(String message) {
        this.message = message;
    }

    public static class CHandler extends ClientHandler<ClientAlertPacket> {

        @Override
        @SideOnly(Side.CLIENT)
        public APacket handle(ClientAlertPacket packet, MessageContext messageContext) {
            Alert.show(packet.message);
            return null;
        }
    }

    public static class SHandler extends DummyServerHandler {};
}
