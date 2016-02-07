package com.onkiup.minedroid.gui;

import com.onkiup.minedroid.Context;
import net.minecraft.entity.Entity;

/**
 * Created by chedim on 8/14/15.
 */
public abstract class EntityHeadOverlay extends Overlay {
    protected Entity mEntity;

    public EntityHeadOverlay(Context context, Entity entity) {
        super(context);
        mEntity = entity;
    }
}
