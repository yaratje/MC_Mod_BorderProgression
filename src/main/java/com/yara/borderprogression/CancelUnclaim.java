package com.yara.borderprogression;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import serverutils.data.ClaimedChunk;
import serverutils.events.chunks.ChunkModifiedEvent;
import serverutils.lib.data.ForgePlayer;
import serverutils.lib.math.ChunkDimPos;

public class CancelUnclaim {

    @SubscribeEvent
    public void onChunkUnclaim(ChunkModifiedEvent.Unclaimed event) {
        System.out.println("UNCLAIM EVENT FIRED");

        ForgePlayer fp = event.getPlayer();

        if (fp == null) {
            return;
        }

        EntityPlayer player = fp.getPlayer();

        if (player == null) return;

        ClaimedChunk chunk = event.getChunk();

        ChunkDimPos posToReclaim = chunk.getPos();

        ServerTickHandler.pending.add(new ServerTickHandler.ReclaimTask(fp, posToReclaim, 4));

        player.addChatMessage(new ChatComponentText("You cant unclaim chunks. :) "));

        return;

    }

}
