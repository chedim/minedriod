package com.onkiup.minedroid.gui.ingame;

import com.onkiup.minedroid.gui.Overlay;
import com.onkiup.minedroid.gui.resources.ResourceLink;
import com.onkiup.minedroid.gui.views.View;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.GuiIngameForge;
import org.lwjgl.input.Mouse;

/**
 * Created by chedim on 8/6/15.
 * @deprecated
 */
public abstract class GuiIngameDroid extends GuiIngame {

    protected View mHud;

    public GuiIngameDroid(Minecraft p_i46325_1_) {
        super(p_i46325_1_);
    }

    public abstract ResourceLink getLayout();
    public abstract void fillView(View content);

    @Override
    public void renderGameOverlay(float partialTicks) {
        mHud.onDraw(partialTicks);
    }


}
