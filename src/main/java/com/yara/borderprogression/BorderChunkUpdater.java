package com.yara.borderprogression;

import java.util.HashMap;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import serverutils.data.ClaimedChunk;
import serverutils.data.ClaimedChunks;
import serverutils.lib.math.ChunkDimPos;

public class BorderChunkUpdater {

    // last safe position per player
    private static final HashMap<UUID, Double[]> lastSafePos = new HashMap<>();

    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;

        if (!player.worldObj.isRemote) {
            player.addChatMessage(new ChatComponentText("§a[Border] System active."));
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {

        if (event.phase != TickEvent.Phase.END) return;

        EntityPlayer player = event.player;
        World world = player.worldObj;

        if (world.isRemote) return;

        // ServerUtilities not loaded / inactive safeguard
        if (ClaimedChunks.instance == null) return;

        ChunkDimPos pos = new ChunkDimPos(player);
        ClaimedChunk chunk = ClaimedChunks.instance.getChunk(pos);

        UUID id = player.getUniqueID();

        boolean hasAnyClaims = !ClaimedChunks.instance.getAllChunks()
            .isEmpty();

        // ifno claims exist yet do nothing to prevent ehhh idk fuckiewuckies
        if (!hasAnyClaims) {
            saveSafe(player, id);
            return;
        }

        // claim does exist
        if (chunk != null) {
            saveSafe(player, id);
            return;
        }

        // if outside claim
        Double[] safe = lastSafePos.get(id);

        if (safe != null) {
            player.setPositionAndUpdate(safe[0], safe[1], safe[2]);

            player.addChatMessage(new net.minecraft.util.ChatComponentText("§cYou cannot leave claimed territory!"));

            System.out.println("[BORDER] Teleported player back to safe chunk");
        }
    }

    private void saveSafe(EntityPlayer player, UUID id) {
        lastSafePos.put(id, new Double[] { player.posX, player.posY, player.posZ });
    }
}
