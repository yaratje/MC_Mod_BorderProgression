package com.yara.borderprogression;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import serverutils.data.ClaimedChunk;
import serverutils.data.ClaimedChunks;
import serverutils.events.chunks.ChunkModifiedEvent;
import serverutils.lib.data.ForgePlayer;
import serverutils.lib.math.ChunkDimPos;

public class CancelUnclaim {

    @SubscribeEvent
    public void onChunkUnclaim(ChunkModifiedEvent.Unclaimed event) {

        ForgePlayer fp = event.getPlayer();

        if (fp == null) {
            return;
        }

        EntityPlayer player = fp.getPlayer();

        if (player == null) return;

        ClaimedChunk chunk = event.getChunk();

        ChunkDimPos posToReclaim = chunk.getPos();

        ClaimedChunks.instance.claimChunk(fp, posToReclaim);

        player.addChatMessage(new ChatComponentText("You cant unclaim chunks. :) "));

        return;

    }

}
