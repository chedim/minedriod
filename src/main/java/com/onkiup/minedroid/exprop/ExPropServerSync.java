package com.onkiup.minedroid.exprop;

import com.onkiup.minedroid.Context;
import com.onkiup.minedroid.Modification;
import com.onkiup.minedroid.net.ExPropDeltaPacket;
import com.onkiup.minedroid.net.ExPropDeltaRequestPacket;
import com.onkiup.minedroid.timer.Task;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

/**
 * Created by chedim on 8/13/15.
 */
public class ExPropServerSync implements Task.Server {

    @Override
    public void execute(Context ctx) {
        List<WeakReference<ExProps>> syncronizables = ExProps.getSyncronizables();
        if (syncronizables == null || syncronizables.size() == 0) return;
        for (WeakReference<ExProps> reference: syncronizables) {
            ExProps props = reference.get();
            if (props == null) continue;
            ExPropDeltaPacket packet = new ExPropDeltaPacket(props, true);
            if (packet.delta.size() == 0) continue;
            Entity e = props.getEntity();
            NetworkRegistry.TargetPoint point =
                    new NetworkRegistry.TargetPoint(e.dimension, e.posX, e.posY, e.posZ, 16);
            Modification.send(ctx, packet, point);
        }
    }
}
