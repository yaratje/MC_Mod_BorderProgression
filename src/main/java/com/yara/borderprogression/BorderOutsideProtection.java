package com.yara.borderprogression;

import java.util.HashMap;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import serverutils.data.ClaimedChunk;
import serverutils.data.ClaimedChunks;
import serverutils.lib.math.ChunkDimPos;

public class BorderOutsideProtection {

    private static final HashMap<UUID, Boolean> isOutside = new HashMap<>();

    // Chunk checking

    private boolean isOutsideClaim(EntityPlayer player) {
        if (ClaimedChunks.instance == null) return false;

        ChunkDimPos pos = new ChunkDimPos(player);
        ClaimedChunk chunk = ClaimedChunks.instance.getChunk(pos);

        return chunk == null;
    }

    // state checking

    private void updateState(EntityPlayer player, boolean outside) {
        UUID id = player.getUniqueID();
        boolean wasOutside = isOutside.getOrDefault(id, false);

        if (outside != wasOutside) {
            if (outside) {
                player.addChatMessage(new ChatComponentText("§cYou left claimed land. You cannot build here."));
            } else {
                player.addChatMessage(new ChatComponentText("§aYou entered claimed land. Building enabled."));
            }
            isOutside.put(id, outside);
        }
    }

    // INTERACTIONS -----> BLOCK BREAK

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        EntityPlayer player = event.getPlayer();
        if (player == null || player.worldObj.isRemote) return;

        boolean outside = isOutsideClaim(player);
        updateState(player, outside);

        if (outside) {
            event.setCanceled(true);
        }
    }

    // INTERACTIONS ----> BLOCK PLACE

    @SubscribeEvent
    public void onBlockPlace(BlockEvent.PlaceEvent event) {
        EntityPlayer player = event.player;
        if (player == null || player.worldObj.isRemote) return;

        boolean outside = isOutsideClaim(player);
        updateState(player, outside);

        if (outside) {
            event.setCanceled(true);
        }
    }

    // INTERACTIONs ---- > RIGHT CLICK

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event) {
        EntityPlayer player = event.entityPlayer;
        if (player == null || player.worldObj.isRemote) return;

        boolean outside = isOutsideClaim(player);
        updateState(player, outside);

        if (outside) {

            // block block interaction
            if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
                event.setCanceled(true);
            }

            // block left-click interactions for modded tools etc
            // if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
            // event.setCanceled(true);
            // }
        }
    }

}
